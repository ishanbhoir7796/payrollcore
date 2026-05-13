package com.payrollcore.payroll.dto.response;

import com.payrollcore.payroll.model.PayrollStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayrollRunResponse {

    private String id;
    private String month;
    private Integer year;
    private PayrollStatus status;
    private Integer totalEmployees;
    private Double totalGrossPaid;
    private Double totalDeductions;
    private Double totalNetPaid;
    private String processedBy;
    private LocalDateTime processedAt;
    private LocalDateTime createdAt;
}