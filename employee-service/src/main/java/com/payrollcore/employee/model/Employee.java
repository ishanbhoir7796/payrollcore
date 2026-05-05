package com.payrollcore.employee.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "employees")
public class Employee {

    @Id
    private String id;

    private String userId;

    @Indexed(unique = true)
    private String employeeCode;

    private String firstName;
    private String lastName;

    @Indexed(unique = true)
    private String email;

    private Department department;
    private String designation;
    private EmployeeStatus status;

    private ContactInfo contactInfo;
    private SalaryStructure salaryStructure;

    private LocalDateTime joiningDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}