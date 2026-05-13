package com.payrollcore.payroll.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "payslips")
public class Payslip {

    @Id
    private String id;

    private String payrollRunId;
    private String employeeId;
    private String employeeCode;
    private String employeeName;

    private String month;
    private Integer year;

    private Earnings earnings;
    private Deductions deductions;

    private Double grossSalary;
    private Double totalDeductions;
    private Double netPay;

    private PayslipStatus status;
    private LocalDateTime generatedAt;
}