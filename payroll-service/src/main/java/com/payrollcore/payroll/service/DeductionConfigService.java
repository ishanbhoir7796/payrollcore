package com.payrollcore.payroll.service;

import com.payrollcore.payroll.dto.request.DeductionConfigRequest;
import com.payrollcore.payroll.model.DeductionConfig;
import com.payrollcore.payroll.model.TaxSlab;
import com.payrollcore.payroll.repository.DeductionConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeductionConfigService {

    private final DeductionConfigRepository deductionConfigRepository;

    public DeductionConfig createConfig(DeductionConfigRequest request) {

        if (deductionConfigRepository.findByType(request.getType()).isPresent()) {
            throw new RuntimeException("Config already exists for type: " + request.getType());
        }

        DeductionConfig config = DeductionConfig.builder()
                .type(request.getType())
                .percentage(request.getPercentage())
                .slabs(request.getSlabs() == null ? null :
                        request.getSlabs().stream()
                        .map(s -> TaxSlab.builder()
                                  .minIncome(s.getMinIncome())
                                  .maxIncome(s.getMaxIncome())
                                  .percentage(s.getPercentage())
                                  .build())
                        .collect(Collectors.toList()))
                .isActive(request.getIsActive())
                .updatedAt(LocalDateTime.now())
                .build();

        return deductionConfigRepository.save(config);
    }

    public DeductionConfig updateConfig(String id, DeductionConfigRequest request) {

        DeductionConfig config = deductionConfigRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Config not found"));

        if (request.getPercentage() != null) config.setPercentage(request.getPercentage());
        if (request.getIsActive() != null) config.setActive(request.getIsActive());
        if (request.getSlabs() != null) {
            config.setSlabs(request.getSlabs().stream()
                    .map(s -> TaxSlab.builder()
                            .minIncome(s.getMinIncome())
                            .maxIncome(s.getMaxIncome())
                            .percentage(s.getPercentage())
                            .build())
                    .collect(Collectors.toList()));
        }
        config.setUpdatedAt(LocalDateTime.now());
        return deductionConfigRepository.save(config);
    }

    public List<DeductionConfig> getAllConfigs() {
        return deductionConfigRepository.findAll();
    }

    public DeductionConfig getConfigByType(String type) {
        return deductionConfigRepository.findByType(type)
                .orElseThrow(() -> new RuntimeException("Config not found for type: " + type));
    }
}