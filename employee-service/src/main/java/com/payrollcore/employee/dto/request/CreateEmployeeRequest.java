package com.payrollcore.employee.dto.request;

import com.payrollcore.employee.model.Department;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateEmployeeRequest {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Designation is required")
    private String designation;

    @NotNull(message = "Department is required")
    private Department department;

    private String phone;
    private String address;
    private String emergencyContact;

    private String userId;
}