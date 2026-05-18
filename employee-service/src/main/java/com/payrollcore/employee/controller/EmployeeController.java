package com.payrollcore.employee.controller;

import com.payrollcore.employee.dto.request.CreateEmployeeRequest;
import com.payrollcore.employee.dto.request.SalaryStructureRequest;
import com.payrollcore.employee.dto.request.UpdateEmployeeRequest;
import com.payrollcore.employee.dto.response.EmployeeResponse;
import com.payrollcore.employee.model.Department;
import com.payrollcore.employee.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@Tag(name = "Employee Management", description = "Endpoints for managing employee records and salary structures")
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN')")
    @Operation(
            summary = "Create a new employee",
            description = "Creates a new employee record with contact info. Requires HR or ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Employee created successfully"),
            @ApiResponse(responseCode = "400", description = "Email already exists"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<EmployeeResponse> createEmployee(
            @Valid @RequestBody CreateEmployeeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(employeeService.createEmployee(request));
    }

    @GetMapping
    @PreAuthorize("hasRole('HR') or hasRole('FINANCE') or hasRole('ADMIN')")
    @Operation(
            summary = "Get all employees",
            description = "Returns a list of all employees. Requires HR, FINANCE or ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of employees returned successfully"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<List<EmployeeResponse>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('HR') or hasRole('FINANCE') or hasRole('ADMIN')")
    @Operation(
            summary = "Get employee by ID",
            description = "Returns a single employee by their MongoDB ID. Requires HR, FINANCE or ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee found"),
            @ApiResponse(responseCode = "400", description = "Employee not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable String id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @GetMapping("/code/{employeeCode}")
    @PreAuthorize("hasRole('HR') or hasRole('FINANCE') or hasRole('ADMIN')")
    @Operation(
            summary = "Get employee by employee code",
            description = "Returns a single employee by their auto-generated code e.g. EMP-0001. Requires HR, FINANCE or ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee found"),
            @ApiResponse(responseCode = "400", description = "Employee not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<EmployeeResponse> getEmployeeByCode(
            @PathVariable String employeeCode) {
        return ResponseEntity.ok(employeeService.getEmployeeByCode(employeeCode));
    }

    @GetMapping("/department/{department}")
    @PreAuthorize("hasRole('HR') or hasRole('FINANCE') or hasRole('ADMIN')")
    @Operation(
            summary = "Get employees by department",
            description = "Returns all employees in a given department. Requires HR, FINANCE or ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of employees returned successfully"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<List<EmployeeResponse>> getEmployeesByDepartment(
            @PathVariable Department department) {
        return ResponseEntity.ok(employeeService.getEmployeesByDepartment(department));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN')")
    @Operation(
            summary = "Update employee details",
            description = "Updates employee profile fields. Only provided fields are updated. Requires HR or ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee updated successfully"),
            @ApiResponse(responseCode = "400", description = "Employee not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<EmployeeResponse> updateEmployee(
            @PathVariable String id,
            @Valid @RequestBody UpdateEmployeeRequest request) {
        return ResponseEntity.ok(employeeService.updateEmployee(id, request));
    }

    @PutMapping("/{id}/salary")
    @PreAuthorize("hasRole('FINANCE') or hasRole('ADMIN')")
    @Operation(
            summary = "Update salary structure",
            description = "Updates employee salary components and recalculates total gross. Requires FINANCE or ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Salary structure updated successfully"),
            @ApiResponse(responseCode = "400", description = "Employee not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<EmployeeResponse> updateSalaryStructure(
            @PathVariable String id,
            @Valid @RequestBody SalaryStructureRequest request) {
        return ResponseEntity.ok(employeeService.updateSalaryStructure(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Delete employee",
            description = "Soft deletes an employee by setting their status to TERMINATED. Requires ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Employee deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Employee not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<Void> deleteEmployee(@PathVariable String id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}