package com.demo.sblangchain.dto;

import java.math.BigDecimal;

public record SalaryIncreaseResult(
        EmployeeDetails employee,
        BigDecimal previousSalary,
        BigDecimal newSalary,
        BigDecimal increasePercent) {
}
