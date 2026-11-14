package com.demo.sblangchain.repository;

import com.demo.sblangchain.entity.Employee;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    List<Employee> findByDepartmentIgnoreCaseOrderByNameAsc(String department);

    long countByDepartmentIgnoreCase(String department);

    @Query(value = "select avg(salary) from employees where lower(department) = lower(:department)", nativeQuery = true)
    BigDecimal averageSalaryByDepartment(@Param("department") String department);
}
