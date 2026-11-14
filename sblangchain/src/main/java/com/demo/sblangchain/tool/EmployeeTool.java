package com.demo.sblangchain.tool;

import com.demo.sblangchain.dto.EmployeeDetails;
import com.demo.sblangchain.dto.SalaryComparison;
import com.demo.sblangchain.dto.SalaryIncreaseResult;
import com.demo.sblangchain.service.EmployeeService;
import dev.langchain4j.exception.ToolErrorVisibleToLlm;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.function.Supplier;
import org.springframework.stereotype.Component;

@Component
public class EmployeeTool {

    private final EmployeeService employeeService;

    public EmployeeTool(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Tool("Find an employee's name, department, and salary using their employee ID")
    public EmployeeDetails findEmployeeById(@P("The numeric employee ID") Long employeeId) {
        return exposeExpectedErrorToLlm(() -> employeeService.findById(employeeId));
    }

    @Tool("List employees in a department")
    public List<EmployeeDetails> listEmployeesByDepartment(@P("The department name") String department) {
        return exposeExpectedErrorToLlm(() -> employeeService.findByDepartment(department));
    }

    @Tool("Count employees in a department")
    public long countEmployeesByDepartment(@P("The department name") String department) {
        return exposeExpectedErrorToLlm(() -> employeeService.countByDepartment(department));
    }

    @Tool("Compare the average salary of two departments")
    public SalaryComparison compareAverageSalaries(
            @P("The first department name") String firstDepartment,
            @P("The second department name") String secondDepartment) {
        return exposeExpectedErrorToLlm(
                () -> employeeService.compareAverageSalaries(firstDepartment, secondDepartment));
    }

    @Tool("Increase an employee's salary by a percentage; the permitted increase is greater than 0 and at most 100 percent")
    public SalaryIncreaseResult increaseSalary(
            @P("The numeric employee ID") Long employeeId,
            @P("The percentage increase, for example 10 for ten percent") BigDecimal percent) {
        return exposeExpectedErrorToLlm(() -> employeeService.increaseSalary(employeeId, percent));
    }

    private <T> T exposeExpectedErrorToLlm(Supplier<T> operation) {
        try {
            return operation.get();
        } catch (EntityNotFoundException | IllegalArgumentException exception) {
            throw ToolErrorVisibleToLlm.from(exception.getMessage());
        }
    }
}
