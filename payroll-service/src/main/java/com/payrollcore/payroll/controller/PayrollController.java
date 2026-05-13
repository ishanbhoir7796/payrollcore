package com.payrollcore.payroll.controller;

import com.payrollcore.payroll.dto.request.DeductionConfigRequest;
import com.payrollcore.payroll.dto.request.PayrollRunRequest;
import com.payrollcore.payroll.dto.response.PayrollRunResponse;
import com.payrollcore.payroll.dto.response.PayslipResponse;
import com.payrollcore.payroll.model.DeductionConfig;
import com.payrollcore.payroll.service.DeductionConfigService;
import com.payrollcore.payroll.service.PayrollService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payroll")
@RequiredArgsConstructor
public class PayrollController {

    private final PayrollService payrollService;
    private final DeductionConfigService deductionConfigService;

    // Payroll Run

    @PostMapping("/run")
    @PreAuthorize("hasRole('FINANCE') or hasRole('ADMIN')")
    public ResponseEntity<PayrollRunResponse> runPayroll(
            @Valid @RequestBody PayrollRunRequest request,
            HttpServletRequest httpRequest) {
        String authToken = httpRequest.getHeader("Authorization");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(payrollService.runPayroll(request, authToken));
    }

    @GetMapping("/runs")
    @PreAuthorize("hasRole('FINANCE') or hasRole('ADMIN')")
    public ResponseEntity<List<PayrollRunResponse>> getAllPayrollRuns() {
        return ResponseEntity.ok(payrollService.getAllPayrollRuns());
    }

    // Payslips

    @GetMapping("/runs/{payrollRunId}/payslips")
    @PreAuthorize("hasRole('FINANCE') or hasRole('ADMIN')")
    public ResponseEntity<List<PayslipResponse>> getPayslipsByRun(
            @PathVariable String payrollRunId) {
        return ResponseEntity.ok(payrollService.getPayslipsByPayrollRun(payrollRunId));
    }

    @GetMapping("/payslips/employee/{employeeId}")
    @PreAuthorize("hasRole('FINANCE') or hasRole('ADMIN') or hasRole('HR')")
    public ResponseEntity<List<PayslipResponse>> getPayslipsByEmployee(
            @PathVariable String employeeId) {
        return ResponseEntity.ok(payrollService.getPayslipsByEmployee(employeeId));
    }

    @GetMapping("/payslips/employee/{employeeId}/{month}/{year}")
    @PreAuthorize("hasRole('FINANCE') or hasRole('ADMIN') or hasRole('HR')")
    public ResponseEntity<PayslipResponse> getPayslipByEmployeeAndMonth(
            @PathVariable String employeeId,
            @PathVariable String month,
            @PathVariable Integer year) {
        return ResponseEntity.ok(
                payrollService.getPayslipByEmployeeAndMonth(employeeId, month, year));
    }

    // Deduction Config

    @PostMapping("/deductions")
    @PreAuthorize("hasRole('FINANCE') or hasRole('ADMIN')")
    public ResponseEntity<DeductionConfig> createDeductionConfig(
            @Valid @RequestBody DeductionConfigRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(deductionConfigService.createConfig(request));
    }

    @PutMapping("/deductions/{id}")
    @PreAuthorize("hasRole('FINANCE') or hasRole('ADMIN')")
    public ResponseEntity<DeductionConfig> updateDeductionConfig(
            @PathVariable String id,
            @Valid @RequestBody DeductionConfigRequest request) {
        return ResponseEntity.ok(deductionConfigService.updateConfig(id, request));
    }

    @GetMapping("/deductions")
    @PreAuthorize("hasRole('FINANCE') or hasRole('ADMIN')")
    public ResponseEntity<List<DeductionConfig>> getAllDeductionConfigs() {
        return ResponseEntity.ok(deductionConfigService.getAllConfigs());
    }
}