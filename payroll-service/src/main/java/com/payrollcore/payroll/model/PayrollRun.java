package com.payrollcore.payroll.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "payroll_runs")
public class PayrollRun {

    @Id
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