package com.payrollcore.payroll.unit;

import com.payrollcore.payroll.dto.request.PayrollRunRequest;
import com.payrollcore.payroll.dto.response.PayrollRunResponse;
import com.payrollcore.payroll.dto.response.PayslipResponse;
import com.payrollcore.payroll.model.*;
import com.payrollcore.payroll.repository.AuditLogRepository;
import com.payrollcore.payroll.repository.DeductionConfigRepository;
import com.payrollcore.payroll.repository.PayrollRunRepository;
import com.payrollcore.payroll.repository.PayslipRepository;
import com.payrollcore.payroll.service.EmployeeServiceClient;
import com.payrollcore.payroll.service.PayrollService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PayrollServiceTest {

    @Mock
    private PayrollRunRepository payrollRunRepository;

    @Mock
    private PayslipRepository payslipRepository;

    @Mock
    private DeductionConfigRepository deductionConfigRepository;

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private EmployeeServiceClient employeeServiceClient;

    @InjectMocks
    private PayrollService payrollService;

    private PayrollRunRequest payrollRunRequest;
    private PayrollRun mockPayrollRun;
    private Payslip mockPayslip;
    private DeductionConfig pfConfig;
    private DeductionConfig hiConfig;
    private DeductionConfig taxConfig;
    private Map<String, Object> mockEmployee;

    @BeforeEach
    void setUp() {
        payrollRunRequest = new PayrollRunRequest();
        payrollRunRequest.setMonth("MAY");
        payrollRunRequest.setYear(2026);

        mockPayrollRun = PayrollRun.builder()
                .id("run123")
                .month("MAY")
                .year(2026)
                .status(PayrollStatus.COMPLETED)
                .totalEmployees(1)
                .totalGrossPaid(8500.0)
                .totalDeductions(2470.0)
                .totalNetPaid(6030.0)
                .processedBy("finance@payrollcore.com")
                .processedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();

        mockPayslip = Payslip.builder()
                .id("pay123")
                .payrollRunId("run123")
                .employeeId("emp123")
                .employeeCode("EMP-0001")
                .employeeName("John Doe")
                .month("MAY")
                .year(2026)
                .earnings(Earnings.builder()
                        .basicPay(5000.0)
                        .hra(2000.0)
                        .travelAllowance(500.0)
                        .performanceBonus(1000.0)
                        .build())
                .deductions(Deductions.builder()
                        .incomeTax(1700.0)
                        .providentFund(600.0)
                        .healthInsurance(170.0)
                        .leaveDeduction(0.0)
                        .build())
                .grossSalary(8500.0)
                .totalDeductions(2470.0)
                .netPay(6030.0)
                .status(PayslipStatus.GENERATED)
                .generatedAt(LocalDateTime.now())
                .build();

        pfConfig = DeductionConfig.builder()
                .id("pf123")
                .type("PROVIDENT_FUND")
                .percentage(12.0)
                .isActive(true)
                .build();

        hiConfig = DeductionConfig.builder()
                .id("hi123")
                .type("HEALTH_INSURANCE")
                .percentage(2.0)
                .isActive(true)
                .build();

        taxConfig = DeductionConfig.builder()
                .id("tax123")
                .type("INCOME_TAX")
                .percentage(20.0)
                .isActive(true)
                .build();

        mockEmployee = new HashMap<>();
        mockEmployee.put("id", "emp123");
        mockEmployee.put("employeeCode", "EMP-0001");
        mockEmployee.put("firstName", "John");
        mockEmployee.put("lastName", "Doe");
        mockEmployee.put("status", "ACTIVE");

        Map<String, Object> salaryStructure = new HashMap<>();
        salaryStructure.put("basicPay", 5000.0);
        salaryStructure.put("hra", 2000.0);
        salaryStructure.put("travelAllowance", 500.0);
        salaryStructure.put("performanceBonus", 1000.0);
        mockEmployee.put("salaryStructure", salaryStructure);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getName()).thenReturn("finance@payrollcore.com");
        SecurityContextHolder.setContext(securityContext);
    }

    // Payroll Run Tests

    @Test
    void runPayroll_ShouldReturnPayrollRunResponse_WhenValidRequest() {
        when(payrollRunRepository.existsByMonthAndYear(anyString(), anyInt()))
                .thenReturn(false);
        when(payrollRunRepository.save(any(PayrollRun.class)))
                .thenReturn(mockPayrollRun);
        when(employeeServiceClient.getAllEmployees(anyString()))
                .thenReturn(List.of(mockEmployee));
        when(deductionConfigRepository.findByType("PROVIDENT_FUND"))
                .thenReturn(Optional.of(pfConfig));
        when(deductionConfigRepository.findByType("HEALTH_INSURANCE"))
                .thenReturn(Optional.of(hiConfig));
        when(deductionConfigRepository.findByType("INCOME_TAX"))
                .thenReturn(Optional.of(taxConfig));
        when(payslipRepository.saveAll(anyList())).thenReturn(List.of(mockPayslip));
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(null);

        PayrollRunResponse response = payrollService.runPayroll(
                payrollRunRequest, "Bearer token123");

        assertNotNull(response);
        assertEquals("run123", response.getId());
        assertEquals("MAY", response.getMonth());
        assertEquals(2026, response.getYear());
        assertEquals(PayrollStatus.COMPLETED, response.getStatus());

        verify(payrollRunRepository, times(2)).save(any(PayrollRun.class));
        verify(payslipRepository, times(1)).saveAll(anyList());
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void runPayroll_ShouldThrowException_WhenPayrollAlreadyProcessed() {
        when(payrollRunRepository.existsByMonthAndYear(anyString(), anyInt()))
                .thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> payrollService.runPayroll(payrollRunRequest, "Bearer token123"));

        assertTrue(exception.getMessage().contains("Payroll already processed"));
        verify(payrollRunRepository, never()).save(any(PayrollRun.class));
    }

    @Test
    void runPayroll_ShouldThrowException_WhenPFConfigNotFound() {
        when(payrollRunRepository.existsByMonthAndYear(anyString(), anyInt()))
                .thenReturn(false);
        when(payrollRunRepository.save(any(PayrollRun.class)))
                .thenReturn(mockPayrollRun);
        when(employeeServiceClient.getAllEmployees(anyString()))
                .thenReturn(List.of(mockEmployee));
        when(deductionConfigRepository.findByType("PROVIDENT_FUND"))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> payrollService.runPayroll(payrollRunRequest, "Bearer token123"));

        assertEquals("Provident Fund config not found", exception.getMessage());
    }

    // Get Payroll Runs Tests

    @Test
    void getAllPayrollRuns_ShouldReturnList_WhenRunsExist() {
        when(payrollRunRepository.findAll()).thenReturn(List.of(mockPayrollRun));

        List<PayrollRunResponse> responses = payrollService.getAllPayrollRuns();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("MAY", responses.getFirst().getMonth());
        assertEquals(2026, responses.getFirst().getYear());
        verify(payrollRunRepository, times(1)).findAll();
    }

    // Payslip Tests

    @Test
    void getPayslipsByEmployee_ShouldReturnList_WhenPayslipsExist() {
        when(payslipRepository.findByEmployeeId(anyString()))
                .thenReturn(List.of(mockPayslip));

        List<PayslipResponse> responses = payrollService.getPayslipsByEmployee("emp123");

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("John Doe", responses.getFirst().getEmployeeName());
        assertEquals(8500.0, responses.getFirst().getGrossSalary());
        assertEquals(6030.0, responses.getFirst().getNetPay());
        verify(payslipRepository, times(1)).findByEmployeeId("emp123");
    }

    @Test
    void getPayslipByEmployeeAndMonth_ShouldReturnPayslip_WhenExists() {
        when(payslipRepository.findByEmployeeIdAndMonthAndYear(
                anyString(), anyString(), anyInt()))
                .thenReturn(Optional.of(mockPayslip));

        PayslipResponse response = payrollService
                .getPayslipByEmployeeAndMonth("emp123", "MAY", 2026);

        assertNotNull(response);
        assertEquals("emp123", response.getEmployeeId());
        assertEquals("MAY", response.getMonth());
        assertEquals(2026, response.getYear());
        assertEquals(PayslipStatus.GENERATED, response.getStatus());
    }

    @Test
    void getPayslipByEmployeeAndMonth_ShouldThrowException_WhenNotFound() {
        when(payslipRepository.findByEmployeeIdAndMonthAndYear(
                anyString(), anyString(), anyInt()))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> payrollService.getPayslipByEmployeeAndMonth(
                        "emp123", "MAY", 2026));

        assertEquals("Payslip not found", exception.getMessage());
    }
}