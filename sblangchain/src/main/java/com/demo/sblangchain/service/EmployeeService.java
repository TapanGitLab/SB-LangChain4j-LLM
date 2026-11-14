package com.demo.sblangchain.service;

import com.demo.sblangchain.dto.EmployeeDetails;
import com.demo.sblangchain.dto.SalaryComparison;
import com.demo.sblangchain.dto.SalaryIncreaseResult;
import com.demo.sblangchain.entity.Employee;
import com.demo.sblangchain.repository.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmployeeService {

    private static final BigDecimal MAX_RAISE_PERCENT = new BigDecimal("100");

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Transactional(readOnly = true)
    public EmployeeDetails findById(Long employeeId) {
        return employeeRepository.findById(employeeId)
                .map(EmployeeService::toDetails)
                .orElseThrow(() -> new EntityNotFoundException("Employee " + employeeId + " was not found."));
    }

    @Transactional(readOnly = true)
    public List<EmployeeDetails> findByDepartment(String department) {
        return employeeRepository.findByDepartmentIgnoreCaseOrderByNameAsc(requireDepartment(department))
                .stream()
                .map(EmployeeService::toDetails)
                .toList();
    }

    @Transactional(readOnly = true)
    public long countByDepartment(String department) {
        return employeeRepository.countByDepartmentIgnoreCase(requireDepartment(department));
    }

    @Transactional(readOnly = true)
    public SalaryComparison compareAverageSalaries(String firstDepartment, String secondDepartment) {
        String first = requireDepartment(firstDepartment);
        String second = requireDepartment(secondDepartment);
        return new SalaryComparison(
                first,
                employeeRepository.averageSalaryByDepartment(first),
                second,
                employeeRepository.averageSalaryByDepartment(second));
    }

    @Transactional
    public SalaryIncreaseResult increaseSalary(Long employeeId, BigDecimal percent) {
        if (percent == null || percent.signum() <= 0 || percent.compareTo(MAX_RAISE_PERCENT) > 0) {
            throw new IllegalArgumentException("Increase percent must be greater than 0 and no more than 100.");
        }
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee " + employeeId + " was not found."));
        BigDecimal previousSalary = employee.getSalary();
        BigDecimal newSalary = previousSalary
                .multiply(BigDecimal.ONE.add(percent.movePointLeft(2)))
                .setScale(2, RoundingMode.HALF_UP);
        employee.setSalary(newSalary);
        return new SalaryIncreaseResult(toDetails(employee), previousSalary, newSalary, percent);
    }

    private static String requireDepartment(String department) {
        if (department == null || department.isBlank()) {
            throw new IllegalArgumentException("Department must not be blank.");
        }
        return department.trim();
    }

    private static EmployeeDetails toDetails(Employee employee) {
        return new EmployeeDetails(employee.getId(), employee.getName(), employee.getDepartment(), employee.getSalary());
    }

}
