package com.payrollcore.payroll.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Earnings {

    private Double basicPay;
    private Double hra;
    private Double travelAllowance;
    private Double performanceBonus;
}