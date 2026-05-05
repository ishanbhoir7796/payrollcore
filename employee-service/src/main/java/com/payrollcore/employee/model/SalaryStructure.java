package com.payrollcore.employee.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalaryStructure {

    private Double basicPay;
    private Double hra;
    private Double travelAllowance;
    private Double performanceBonus;
    private Double totalGross;
    private LocalDateTime effectiveFrom;
}