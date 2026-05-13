package com.payrollcore.payroll.repository;

import com.payrollcore.payroll.model.PayrollRun;
import com.payrollcore.payroll.model.PayrollStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PayrollRunRepository extends MongoRepository<PayrollRun, String> {

    Optional<PayrollRun> findByMonthAndYear(String month, Integer year);

    List<PayrollRun> findByYear(Integer year);

    List<PayrollRun> findByStatus(PayrollStatus status);

    boolean existsByMonthAndYear(String month, Integer year);
}