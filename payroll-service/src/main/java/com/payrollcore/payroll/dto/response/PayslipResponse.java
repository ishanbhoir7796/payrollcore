package com.payrollcore.payroll.dto.response;

import com.payrollcore.payroll.model.Deductions;
import com.payrollcore.payroll.model.Earnings;
import com.payrollcore.payroll.model.PayslipStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayslipResponse {

    private String id;
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