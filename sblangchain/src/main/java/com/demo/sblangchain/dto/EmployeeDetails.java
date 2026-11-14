package com.demo.sblangchain.dto;

import java.math.BigDecimal;

public record EmployeeDetails(Long id, String name, String department, BigDecimal salary) {
}
