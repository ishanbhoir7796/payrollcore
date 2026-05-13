package com.payrollcore.payroll.repository;

import com.payrollcore.payroll.model.AuditLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends MongoRepository<AuditLog, String> {

    List<AuditLog> findByPerformedBy(String performedBy);

    List<AuditLog> findByTargetEntity(String targetEntity);

    List<AuditLog> findByAction(String action);
}