package com.payrollcore.payroll.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Deductions {

    private Double incomeTax;
    private Double providentFund;
    private Double healthInsurance;
    private Double leaveDeduction;
}