package com.payrollcore.payroll.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class EmployeeServiceClient {

    private final WebClient webClient;

    public EmployeeServiceClient(@Value("${employee-service.url}") String employeeServiceUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(employeeServiceUrl)
                .build();
    }

    public List<Map> getAllEmployees(String token) {
        try {
            return webClient.get()
                    .uri("/api/employees")
                    .header("Authorization", token)
                    .retrieve()
                    .bodyToFlux(Map.class)
                    .collectList()
                    .block();
        } catch (Exception e) {
            log.error("Error fetching employees from Employee Service: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch employees from Employee Service");
        }
    }

    public Map getEmployeeById(String employeeId, String token) {
        try {
            return webClient.get()
                    .uri("/api/employees/{id}", employeeId)
                    .header("Authorization", token)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (Exception e) {
            log.error("Error fetching employee {}: {}", employeeId, e.getMessage());
            throw new RuntimeException("Failed to fetch employee from Employee Service");
        }
    }
}