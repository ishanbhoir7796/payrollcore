package com.payrollcore.employee.dto.request;

import com.payrollcore.employee.model.Department;
import com.payrollcore.employee.model.EmployeeStatus;
import lombok.Data;

@Data
public class UpdateEmployeeRequest {

    private String firstName;
    private String lastName;
    private String designation;
    private Department department;
    private EmployeeStatus status;
    private String phone;
    private String address;
    private String emergencyContact;
}