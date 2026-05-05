package com.payrollcore.employee.repository;

import com.payrollcore.employee.model.Department;
import com.payrollcore.employee.model.Employee;
import com.payrollcore.employee.model.EmployeeStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends MongoRepository<Employee, String> {

    Optional<Employee> findByEmail(String email);

    Optional<Employee> findByEmployeeCode(String employeeCode);

    Optional<Employee> findByUserId(String userId);

    List<Employee> findByDepartment(Department department);

    List<Employee> findByStatus(EmployeeStatus status);

    List<Employee> findByDepartmentAndStatus(Department department, EmployeeStatus status);

    boolean existsByEmail(String email);

    boolean existsByEmployeeCode(String employeeCode);
}