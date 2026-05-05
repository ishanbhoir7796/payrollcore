package com.payrollcore.employee.service;

import com.payrollcore.employee.dto.request.CreateEmployeeRequest;
import com.payrollcore.employee.dto.request.SalaryStructureRequest;
import com.payrollcore.employee.dto.request.UpdateEmployeeRequest;
import com.payrollcore.employee.dto.response.EmployeeResponse;
import com.payrollcore.employee.model.*;
import com.payrollcore.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeResponse createEmployee(CreateEmployeeRequest request) {

        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        String employeeCode = generateEmployeeCode();

        Employee employee = Employee.builder()
                .userId(request.getUserId())
                .employeeCode(employeeCode)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .designation(request.getDesignation())
                .department(request.getDepartment())
                .status(EmployeeStatus.ACTIVE)
                .contactInfo(ContactInfo.builder()
                        .phone(request.getPhone())
                        .address(request.getAddress())
                        .emergencyContact(request.getEmergencyContact())
                        .build())
                .joiningDate(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Employee saved = employeeRepository.save(employee);
        return mapToResponse(saved);
    }

    public EmployeeResponse getEmployeeById(String id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        return mapToResponse(employee);
    }

    public EmployeeResponse getEmployeeByCode(String employeeCode) {
        Employee employee = employeeRepository.findByEmployeeCode(employeeCode)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        return mapToResponse(employee);
    }

    public List<EmployeeResponse> getAllEmployees() {
        return employeeRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<EmployeeResponse> getEmployeesByDepartment(Department department) {
        return employeeRepository.findByDepartment(department)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public EmployeeResponse updateEmployee(String id, UpdateEmployeeRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        if (request.getFirstName() != null) employee.setFirstName(request.getFirstName());
        if (request.getLastName() != null) employee.setLastName(request.getLastName());
        if (request.getDesignation() != null) employee.setDesignation(request.getDesignation());
        if (request.getDepartment() != null) employee.setDepartment(request.getDepartment());
        if (request.getStatus() != null) employee.setStatus(request.getStatus());

        if (request.getPhone() != null || request.getAddress() != null
                || request.getEmergencyContact() != null) {
            ContactInfo existing = employee.getContactInfo() != null
                    ? employee.getContactInfo() : new ContactInfo();
            if (request.getPhone() != null) existing.setPhone(request.getPhone());
            if (request.getAddress() != null) existing.setAddress(request.getAddress());
            if (request.getEmergencyContact() != null)
                existing.setEmergencyContact(request.getEmergencyContact());
            employee.setContactInfo(existing);
        }

        employee.setUpdatedAt(LocalDateTime.now());
        return mapToResponse(employeeRepository.save(employee));
    }

    public EmployeeResponse updateSalaryStructure(String id, SalaryStructureRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Double totalGross = request.getBasicPay() + request.getHra()
                + request.getTravelAllowance() + request.getPerformanceBonus();

        SalaryStructure salary = SalaryStructure.builder()
                .basicPay(request.getBasicPay())
                .hra(request.getHra())
                .travelAllowance(request.getTravelAllowance())
                .performanceBonus(request.getPerformanceBonus())
                .totalGross(totalGross)
                .effectiveFrom(LocalDateTime.now())
                .build();

        employee.setSalaryStructure(salary);
        employee.setUpdatedAt(LocalDateTime.now());
        return mapToResponse(employeeRepository.save(employee));
    }

    public void deleteEmployee(String id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        employee.setStatus(EmployeeStatus.TERMINATED);
        employee.setUpdatedAt(LocalDateTime.now());
        employeeRepository.save(employee);
    }

    private String generateEmployeeCode() {
        long count = employeeRepository.count() + 1;
        return String.format("EMP-%04d", count);
    }

    private EmployeeResponse mapToResponse(Employee employee) {
        return EmployeeResponse.builder()
                .id(employee.getId())
                .employeeCode(employee.getEmployeeCode())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .department(employee.getDepartment())
                .designation(employee.getDesignation())
                .status(employee.getStatus())
                .contactInfo(employee.getContactInfo())
                .salaryStructure(employee.getSalaryStructure())
                .joiningDate(employee.getJoiningDate())
                .createdAt(employee.getCreatedAt())
                .build();
    }
}