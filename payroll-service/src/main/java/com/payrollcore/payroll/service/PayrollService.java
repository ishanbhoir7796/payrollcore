package com.payrollcore.payroll.service;

import com.payrollcore.payroll.dto.request.PayrollRunRequest;
import com.payrollcore.payroll.dto.response.PayrollRunResponse;
import com.payrollcore.payroll.dto.response.PayslipResponse;
import com.payrollcore.payroll.model.*;
import com.payrollcore.payroll.repository.AuditLogRepository;
import com.payrollcore.payroll.repository.DeductionConfigRepository;
import com.payrollcore.payroll.repository.PayrollRunRepository;
import com.payrollcore.payroll.repository.PayslipRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayrollService {

    private final PayrollRunRepository payrollRunRepository;
    private final PayslipRepository payslipRepository;
    private final DeductionConfigRepository deductionConfigRepository;
    private final AuditLogRepository auditLogRepository;
    private final EmployeeServiceClient employeeServiceClient;

    public PayrollRunResponse runPayroll(PayrollRunRequest request, String authToken) {

        // Check if payroll already run for this month/year
        if (payrollRunRepository.existsByMonthAndYear(request.getMonth(), request.getYear())) {
            throw new RuntimeException("Payroll already processed for "
                    + request.getMonth() + " " + request.getYear());
        }

        // Create payroll run record
        PayrollRun payrollRun = PayrollRun.builder()
                .month(request.getMonth())
                .year(request.getYear())
                .status(PayrollStatus.PROCESSING)
                .processedBy(getCurrentUserEmail())
                .createdAt(LocalDateTime.now())
                .build();

        payrollRun = payrollRunRepository.save(payrollRun);

        // Fetch all employees from Employee Service
        List<Map> employees = employeeServiceClient.getAllEmployees(authToken);

        // Fetch deduction configs
        DeductionConfig pfConfig = deductionConfigRepository
                .findByType("PROVIDENT_FUND")
                .orElseThrow(() -> new RuntimeException("Provident Fund config not found"));

        DeductionConfig hiConfig = deductionConfigRepository
                .findByType("HEALTH_INSURANCE")
                .orElseThrow(() -> new RuntimeException("Health Insurance config not found"));

        DeductionConfig taxConfig = deductionConfigRepository
                .findByType("INCOME_TAX")
                .orElseThrow(() -> new RuntimeException("Income Tax config not found"));

        // Generate payslips for each employee
        List<Payslip> payslips = new ArrayList<>();
        double totalGross = 0;
        double totalDeductions = 0;
        double totalNet = 0;

        for (Map employee : employees) {
            if (!"ACTIVE".equals(employee.get("status"))) continue;

            Map salaryStructure = (Map) employee.get("salaryStructure");
            if (salaryStructure == null) continue;

            // Extract salary components
            double basicPay = toDouble(salaryStructure.get("basicPay"));
            double hra = toDouble(salaryStructure.get("hra"));
            double travel = toDouble(salaryStructure.get("travelAllowance"));
            double bonus = toDouble(salaryStructure.get("performanceBonus"));
            double grossSalary = basicPay + hra + travel + bonus;

            // Calculate deductions
            double pf = (basicPay * pfConfig.getPercentage()) / 100;
            double hi = (grossSalary * hiConfig.getPercentage()) / 100;
            double tax = calculateIncomeTax(grossSalary, taxConfig);
            double totalDed = pf + hi + tax;
            double netPay = grossSalary - totalDed;

            // Build payslip
            Payslip payslip = Payslip.builder()
                    .payrollRunId(payrollRun.getId())
                    .employeeId((String) employee.get("id"))
                    .employeeCode((String) employee.get("employeeCode"))
                    .employeeName(employee.get("firstName") + " " + employee.get("lastName"))
                    .month(request.getMonth())
                    .year(request.getYear())
                    .earnings(Earnings.builder()
                            .basicPay(basicPay)
                            .hra(hra)
                            .travelAllowance(travel)
                            .performanceBonus(bonus)
                            .build())
                    .deductions(Deductions.builder()
                            .incomeTax(tax)
                            .providentFund(pf)
                            .healthInsurance(hi)
                            .leaveDeduction(0.0)
                            .build())
                    .grossSalary(grossSalary)
                    .totalDeductions(totalDed)
                    .netPay(netPay)
                    .status(PayslipStatus.GENERATED)
                    .generatedAt(LocalDateTime.now())
                    .build();

            payslips.add(payslip);
            totalGross += grossSalary;
            totalDeductions += totalDed;
            totalNet += netPay;
        }

        // Save all payslips
        payslipRepository.saveAll(payslips);

        // Update payroll run with summary
        payrollRun.setStatus(PayrollStatus.COMPLETED);
        payrollRun.setTotalEmployees(payslips.size());
        payrollRun.setTotalGrossPaid(totalGross);
        payrollRun.setTotalDeductions(totalDeductions);
        payrollRun.setTotalNetPaid(totalNet);
        payrollRun.setProcessedAt(LocalDateTime.now());
        payrollRunRepository.save(payrollRun);

        // Write audit log
        saveAuditLog("PAYROLL_RUN_COMPLETED", "PAYROLL_RUN", payrollRun.getId(),
                "Payroll run for " + request.getMonth() + " " + request.getYear()
                        + " completed with " + payslips.size() + " payslips generated");

        return mapToPayrollRunResponse(payrollRun);
    }

    public List<PayrollRunResponse> getAllPayrollRuns() {
        return payrollRunRepository.findAll()
                .stream()
                .map(this::mapToPayrollRunResponse)
                .collect(Collectors.toList());
    }

    public List<PayslipResponse> getPayslipsByPayrollRun(String payrollRunId) {
        return payslipRepository.findByPayrollRunId(payrollRunId)
                .stream()
                .map(this::mapToPayslipResponse)
                .collect(Collectors.toList());
    }

    public List<PayslipResponse> getPayslipsByEmployee(String employeeId) {
        return payslipRepository.findByEmployeeId(employeeId)
                .stream()
                .map(this::mapToPayslipResponse)
                .collect(Collectors.toList());
    }

    public PayslipResponse getPayslipByEmployeeAndMonth(String employeeId,
                                                        String month, Integer year) {
        Payslip payslip = payslipRepository
                .findByEmployeeIdAndMonthAndYear(employeeId, month, year)
                .orElseThrow(() -> new RuntimeException("Payslip not found"));
        return mapToPayslipResponse(payslip);
    }

    // Tax calculation engine
    private double calculateIncomeTax(double grossSalary, DeductionConfig taxConfig) {
        if (taxConfig.getSlabs() == null || taxConfig.getSlabs().isEmpty()) {
            return (grossSalary * taxConfig.getPercentage()) / 100;
        }

        for (TaxSlab slab : taxConfig.getSlabs()) {
            double min = slab.getMinIncome();
            double max = slab.getMaxIncome() != null ? slab.getMaxIncome() : Double.MAX_VALUE;
            if (grossSalary >= min && grossSalary <= max) {
                return (grossSalary * slab.getPercentage()) / 100;
            }
        }
        return 0.0;
    }


    private double toDouble(Object value) {
        if (value == null) return 0.0;
        return ((Number) value).doubleValue();
    }

    private String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "system";
    }

    private void saveAuditLog(String action, String entity,
                              String entityId, String description) {
        AuditLog log = AuditLog.builder()
                .action(action)
                .performedBy(getCurrentUserEmail())
                .targetEntity(entity)
                .targetId(entityId)
                .description(description)
                .performedAt(LocalDateTime.now())
                .build();
        auditLogRepository.save(log);
    }

    private PayrollRunResponse mapToPayrollRunResponse(PayrollRun run) {
        return PayrollRunResponse.builder()
                .id(run.getId())
                .month(run.getMonth())
                .year(run.getYear())
                .status(run.getStatus())
                .totalEmployees(run.getTotalEmployees())
                .totalGrossPaid(run.getTotalGrossPaid())
                .totalDeductions(run.getTotalDeductions())
                .totalNetPaid(run.getTotalNetPaid())
                .processedBy(run.getProcessedBy())
                .processedAt(run.getProcessedAt())
                .createdAt(run.getCreatedAt())
                .build();
    }

    private PayslipResponse mapToPayslipResponse(Payslip payslip) {
        return PayslipResponse.builder()
                .id(payslip.getId())
                .employeeId(payslip.getEmployeeId())
                .employeeCode(payslip.getEmployeeCode())
                .employeeName(payslip.getEmployeeName())
                .month(payslip.getMonth())
                .year(payslip.getYear())
                .earnings(payslip.getEarnings())
                .deductions(payslip.getDeductions())
                .grossSalary(payslip.getGrossSalary())
                .totalDeductions(payslip.getTotalDeductions())
                .netPay(payslip.getNetPay())
                .status(payslip.getStatus())
                .generatedAt(payslip.getGeneratedAt())
                .build();
    }
}