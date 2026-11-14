package com.demo.sblangchain.dto;

import java.math.BigDecimal;

public record SalaryComparison(
        String firstDepartment,
        BigDecimal firstAverageSalary,
        String secondDepartment,
        BigDecimal secondAverageSalary) {
}
