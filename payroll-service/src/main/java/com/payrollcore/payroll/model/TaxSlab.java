package com.payrollcore.payroll.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaxSlab {

    private Double minIncome;
    private Double maxIncome;
    private Double percentage;
}