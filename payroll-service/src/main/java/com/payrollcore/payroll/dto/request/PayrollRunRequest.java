package com.payrollcore.payroll.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PayrollRunRequest {

    @NotBlank(message = "Month is required")
    private String month;

    @NotNull(message = "Year is required")
    @Min(value = 2000, message = "Year must be after 2000")
    @Max(value = 2100, message = "Year must be before 2100")
    private Integer year;
}