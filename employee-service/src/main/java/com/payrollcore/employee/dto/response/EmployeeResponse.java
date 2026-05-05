package com.payrollcore.employee.dto.response;

import com.payrollcore.employee.model.ContactInfo;
import com.payrollcore.employee.model.Department;
import com.payrollcore.employee.model.EmployeeStatus;
import com.payrollcore.employee.model.SalaryStructure;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponse {

    private String id;
    private String employeeCode;
    private String firstName;
    private String lastName;
    private String email;
    private Department department;
    private String designation;
    private EmployeeStatus status;
    private ContactInfo contactInfo;
    private SalaryStructure salaryStructure;
    private LocalDateTime joiningDate;
    private LocalDateTime createdAt;
}