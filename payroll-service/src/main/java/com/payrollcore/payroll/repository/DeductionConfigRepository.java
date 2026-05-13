package com.payrollcore.payroll.repository;

import com.payrollcore.payroll.model.DeductionConfig;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeductionConfigRepository extends MongoRepository<DeductionConfig, String> {

    Optional<DeductionConfig> findByType(String type);

    List<DeductionConfig> findByIsActive(boolean isActive);
}