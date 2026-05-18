package com.payrollcore.payroll.controller;

import com.payrollcore.payroll.dto.request.DeductionConfigRequest;
import com.payrollcore.payroll.dto.request.PayrollRunRequest;
import com.payrollcore.payroll.dto.response.PayrollRunResponse;
import com.payrollcore.payroll.dto.response.PayslipResponse;
import com.payrollcore.payroll.model.DeductionConfig;
import com.payrollcore.payroll.service.DeductionConfigService;
import com.payrollcore.payroll.service.PayrollService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Payroll Management", description = "Endpoints for running payroll, managing payslips and deduction configurations")
public class PayrollController {

    private final PayrollService payrollService;
    private final DeductionConfigService deductionConfigService;

    // Payroll Run

    @PostMapping("/run")
    @PreAuthorize("hasRole('FINANCE') or hasRole('ADMIN')")
    @Operation(
            summary = "Run monthly payroll",
            description = "Processes payroll for all active employees for the given month and year. Fetches employee data from Employee Service, calculates gross salary, applies tax slab based deductions and generates individual payslips. Requires FINANCE or ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Payroll run completed successfully"),
            @ApiResponse(responseCode = "400", description = "Payroll already processed for this month and year"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<PayrollRunResponse> runPayroll(
            @Valid @RequestBody PayrollRunRequest request,
            HttpServletRequest httpRequest) {
        String authToken = httpRequest.getHeader("Authorization");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(payrollService.runPayroll(request, authToken));
    }

    @GetMapping("/runs")
    @PreAuthorize("hasRole('FINANCE') or hasRole('ADMIN')")
    @Operation(
            summary = "Get all payroll runs",
            description = "Returns a list of all payroll runs with summary totals. Requires FINANCE or ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of payroll runs returned successfully"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<List<PayrollRunResponse>> getAllPayrollRuns() {
        return ResponseEntity.ok(payrollService.getAllPayrollRuns());
    }

    // Payslips

    @GetMapping("/runs/{payrollRunId}/payslips")
    @PreAuthorize("hasRole('FINANCE') or hasRole('ADMIN')")
    @Operation(
            summary = "Get payslips by payroll run",
            description = "Returns all payslips generated in a specific payroll run. Requires FINANCE or ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of payslips returned successfully"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<List<PayslipResponse>> getPayslipsByRun(
            @PathVariable String payrollRunId) {
        return ResponseEntity.ok(payrollService.getPayslipsByPayrollRun(payrollRunId));
    }

    @GetMapping("/payslips/employee/{employeeId}")
    @PreAuthorize("hasRole('FINANCE') or hasRole('ADMIN') or hasRole('HR')")
    @Operation(
            summary = "Get all payslips for an employee",
            description = "Returns complete payslip history for a specific employee. Requires FINANCE, ADMIN or HR role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of payslips returned successfully"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<List<PayslipResponse>> getPayslipsByEmployee(
            @PathVariable String employeeId) {
        return ResponseEntity.ok(payrollService.getPayslipsByEmployee(employeeId));
    }

    @GetMapping("/payslips/employee/{employeeId}/{month}/{year}")
    @PreAuthorize("hasRole('FINANCE') or hasRole('ADMIN') or hasRole('HR')")
    @Operation(
            summary = "Get payslip by employee and month",
            description = "Returns a specific payslip for an employee for a given month and year. Requires FINANCE, ADMIN or HR role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payslip returned successfully"),
            @ApiResponse(responseCode = "400", description = "Payslip not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
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
    @Operation(
            summary = "Create deduction configuration",
            description = "Creates a new deduction config such as PROVIDENT_FUND, HEALTH_INSURANCE or INCOME_TAX with percentage or tax slabs. Requires FINANCE or ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Deduction config created successfully"),
            @ApiResponse(responseCode = "400", description = "Config already exists for this type"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<DeductionConfig> createDeductionConfig(
            @Valid @RequestBody DeductionConfigRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(deductionConfigService.createConfig(request));
    }

    @PutMapping("/deductions/{id}")
    @PreAuthorize("hasRole('FINANCE') or hasRole('ADMIN')")
    @Operation(
            summary = "Update deduction configuration",
            description = "Updates an existing deduction config. Can update percentage, tax slabs or active status. Requires FINANCE or ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deduction config updated successfully"),
            @ApiResponse(responseCode = "400", description = "Config not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<DeductionConfig> updateDeductionConfig(
            @PathVariable String id,
            @Valid @RequestBody DeductionConfigRequest request) {
        return ResponseEntity.ok(deductionConfigService.updateConfig(id, request));
    }

    @GetMapping("/deductions")
    @PreAuthorize("hasRole('FINANCE') or hasRole('ADMIN')")
    @Operation(
            summary = "Get all deduction configurations",
            description = "Returns all deduction configs including PF, Health Insurance and Income Tax slabs. Requires FINANCE or ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of deduction configs returned successfully"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<List<DeductionConfig>> getAllDeductionConfigs() {
        return ResponseEntity.ok(deductionConfigService.getAllConfigs());
    }
}