package com.payrollcore.payroll.dto.request;

import lombok.Data;

@Data
public class TaxSlabRequest {

    private Double minIncome;
    private Double maxIncome;
    private Double percentage;
}