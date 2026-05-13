package com.payrollcore.payroll.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class DeductionConfigRequest {

    @NotBlank(message = "Type is required")
    private String type;

    private Double percentage;

    private List<TaxSlabRequest> slabs;

    @NotNull(message = "Active status is required")
    private Boolean isActive;
}