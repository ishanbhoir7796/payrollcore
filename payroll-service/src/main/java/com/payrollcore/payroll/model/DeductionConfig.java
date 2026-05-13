package com.payrollcore.payroll.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "deduction_config")
public class DeductionConfig {

    @Id
    private String id;

    private String type;
    private Double percentage;
    private List<TaxSlab> slabs;
    private boolean isActive;
    private LocalDateTime updatedAt;
}