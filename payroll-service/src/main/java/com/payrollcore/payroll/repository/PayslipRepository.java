package com.payrollcore.payroll.repository;

import com.payrollcore.payroll.model.Payslip;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PayslipRepository extends MongoRepository<Payslip, String> {

    List<Payslip> findByPayrollRunId(String payrollRunId);

    List<Payslip> findByEmployeeId(String employeeId);

    Optional<Payslip> findByEmployeeIdAndMonthAndYear(String employeeId,
                                                      String month,
                                                      Integer year);

    List<Payslip> findByMonthAndYear(String month, Integer year);
}