package com.payrollcore.employee.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class SalaryStructureRequest {

    @NotNull(message = "Basic pay is required")
    @Positive(message = "Basic pay must be positive")
    private Double basicPay;

    @NotNull(message = "HRA is required")
    @Positive(message = "HRA must be positive")
    private Double hra;

    @NotNull(message = "Travel allowance is required")
    @Positive(message = "Travel allowance must be positive")
    private Double travelAllowance;

    @NotNull(message = "Performance bonus is required")
    @Positive(message = "Performance bonus must be positive")
    private Double performanceBonus;
}