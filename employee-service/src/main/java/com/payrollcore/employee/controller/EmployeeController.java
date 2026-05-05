package com.payrollcore.employee.controller;

import com.payrollcore.employee.dto.request.CreateEmployeeRequest;
import com.payrollcore.employee.dto.request.SalaryStructureRequest;
import com.payrollcore.employee.dto.request.UpdateEmployeeRequest;
import com.payrollcore.employee.dto.response.EmployeeResponse;
import com.payrollcore.employee.model.Department;
import com.payrollcore.employee.service.EmployeeService;
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
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN')")
    public ResponseEntity<EmployeeResponse> createEmployee(
            @Valid @RequestBody CreateEmployeeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(employeeService.createEmployee(request));
    }

    @GetMapping
    @PreAuthorize("hasRole('HR') or hasRole('FINANCE') or hasRole('ADMIN')")
    public ResponseEntity<List<EmployeeResponse>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('HR') or hasRole('FINANCE') or hasRole('ADMIN')")
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable String id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @GetMapping("/code/{employeeCode}")
    @PreAuthorize("hasRole('HR') or hasRole('FINANCE') or hasRole('ADMIN')")
    public ResponseEntity<EmployeeResponse> getEmployeeByCode(
            @PathVariable String employeeCode) {
        return ResponseEntity.ok(employeeService.getEmployeeByCode(employeeCode));
    }

    @GetMapping("/department/{department}")
    @PreAuthorize("hasRole('HR') or hasRole('FINANCE') or hasRole('ADMIN')")
    public ResponseEntity<List<EmployeeResponse>> getEmployeesByDepartment(
            @PathVariable Department department) {
        return ResponseEntity.ok(employeeService.getEmployeesByDepartment(department));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN')")
    public ResponseEntity<EmployeeResponse> updateEmployee(
            @PathVariable String id,
            @Valid @RequestBody UpdateEmployeeRequest request) {
        return ResponseEntity.ok(employeeService.updateEmployee(id, request));
    }

    @PutMapping("/{id}/salary")
    @PreAuthorize("hasRole('FINANCE') or hasRole('ADMIN')")
    public ResponseEntity<EmployeeResponse> updateSalaryStructure(
            @PathVariable String id,
            @Valid @RequestBody SalaryStructureRequest request) {
        return ResponseEntity.ok(employeeService.updateSalaryStructure(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEmployee(@PathVariable String id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}