package com.demo.sblangchain;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.demo.sblangchain.entity.Employee;
import com.demo.sblangchain.repository.EmployeeRepository;
import com.demo.sblangchain.service.EmployeeService;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
class SblangchainApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private EmployeeService employeeService;

	@Test
	void contextLoads() {
	}

	@Test
	void chatEndpointExplainsWhenNoProviderIsEnabled() throws Exception {
		mockMvc.perform(post("/api/assistant/chat")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"message":"Find employee 101"}
								"""))
				.andExpect(status().isServiceUnavailable());
	}

	@Test
	@Transactional
	void employeeOperationsQueryAndUpdateTheDatabase() {
		employeeRepository.saveAll(List.of(
				new Employee(101L, "Asha", "IT", new BigDecimal("10000.00")),
				new Employee(102L, "Ravi", "IT", new BigDecimal("8000.00")),
				new Employee(201L, "Maya", "HR", new BigDecimal("6000.00"))));

		var itEmployees = employeeService.findByDepartment("it");
		var comparison = employeeService.compareAverageSalaries("IT", "HR");
		var raise = employeeService.increaseSalary(101L, new BigDecimal("10"));

		org.assertj.core.api.Assertions.assertThat(itEmployees).hasSize(2);
		org.assertj.core.api.Assertions.assertThat(employeeService.countByDepartment("hr")).isEqualTo(1);
		org.assertj.core.api.Assertions.assertThat(comparison.firstAverageSalary())
				.isEqualByComparingTo("9000.00");
		org.assertj.core.api.Assertions.assertThat(comparison.secondAverageSalary())
				.isEqualByComparingTo("6000.00");
		org.assertj.core.api.Assertions.assertThat(raise.newSalary()).isEqualByComparingTo("11000.00");
	}
}
