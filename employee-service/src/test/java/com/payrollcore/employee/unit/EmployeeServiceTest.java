package com.payrollcore.employee.unit;

import com.payrollcore.employee.dto.request.CreateEmployeeRequest;
import com.payrollcore.employee.dto.request.SalaryStructureRequest;
import com.payrollcore.employee.dto.request.UpdateEmployeeRequest;
import com.payrollcore.employee.dto.response.EmployeeResponse;
import com.payrollcore.employee.model.*;
import com.payrollcore.employee.repository.EmployeeRepository;
import com.payrollcore.employee.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee mockEmployee;
    private CreateEmployeeRequest createRequest;

    @BeforeEach
    void setUp() {
        mockEmployee = Employee.builder()
                .id("emp123")
                .userId("user123")
                .employeeCode("EMP-0001")
                .firstName("John")
                .lastName("Doe")
                .email("john@payrollcore.com")
                .designation("Senior Software Engineer")
                .department(Department.ENGINEERING)
                .status(EmployeeStatus.ACTIVE)
                .contactInfo(ContactInfo.builder()
                        .phone("+1-619-123-4567")
                        .address("San Diego, CA")
                        .emergencyContact("Jane Doe")
                        .build())
                .joiningDate(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        createRequest = new CreateEmployeeRequest();
        createRequest.setFirstName("John");
        createRequest.setLastName("Doe");
        createRequest.setEmail("john@payrollcore.com");
        createRequest.setDesignation("Senior Software Engineer");
        createRequest.setDepartment(Department.ENGINEERING);
        createRequest.setPhone("+1-619-123-4567");
        createRequest.setAddress("San Diego, CA");
        createRequest.setEmergencyContact("Jane Doe");
    }

    // Create Employee Tests

    @Test
    void createEmployee_ShouldReturnEmployeeResponse_WhenValidRequest() {
        when(employeeRepository.existsByEmail(anyString())).thenReturn(false);
        when(employeeRepository.count()).thenReturn(0L);
        when(employeeRepository.save(any(Employee.class))).thenReturn(mockEmployee);

        EmployeeResponse response = employeeService.createEmployee(createRequest);

        assertNotNull(response);
        assertEquals("emp123", response.getId());
        assertEquals("EMP-0001", response.getEmployeeCode());
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getLastName());
        assertEquals("john@payrollcore.com", response.getEmail());
        assertEquals(Department.ENGINEERING, response.getDepartment());
        assertEquals(EmployeeStatus.ACTIVE, response.getStatus());

        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    void createEmployee_ShouldThrowException_WhenEmailAlreadyExists() {
        when(employeeRepository.existsByEmail(anyString())).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> employeeService.createEmployee(createRequest));

        assertEquals("Email already exists", exception.getMessage());
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    // Get Employee Tests

    @Test
    void getEmployeeById_ShouldReturnEmployee_WhenExists() {
        when(employeeRepository.findById(anyString())).thenReturn(Optional.of(mockEmployee));

        EmployeeResponse response = employeeService.getEmployeeById("emp123");

        assertNotNull(response);
        assertEquals("emp123", response.getId());
        assertEquals("John", response.getFirstName());
        verify(employeeRepository, times(1)).findById("emp123");
    }

    @Test
    void getEmployeeById_ShouldThrowException_WhenNotFound() {
        when(employeeRepository.findById(anyString())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> employeeService.getEmployeeById("emp123"));

        assertEquals("Employee not found", exception.getMessage());
    }

    @Test
    void getAllEmployees_ShouldReturnList_WhenEmployeesExist() {
        when(employeeRepository.findAll()).thenReturn(List.of(mockEmployee));

        List<EmployeeResponse> responses = employeeService.getAllEmployees();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("John", responses.getFirst().getFirstName());
        verify(employeeRepository, times(1)).findAll();
    }

    // Update Tests

    @Test
    void updateEmployee_ShouldUpdateFields_WhenValidRequest() {
        UpdateEmployeeRequest updateRequest = new UpdateEmployeeRequest();
        updateRequest.setDesignation("Lead Software Engineer");

        when(employeeRepository.findById(anyString())).thenReturn(Optional.of(mockEmployee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(mockEmployee);

        EmployeeResponse response = employeeService.updateEmployee("emp123", updateRequest);

        assertNotNull(response);
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    void updateEmployee_ShouldThrowException_WhenEmployeeNotFound() {
        when(employeeRepository.findById(anyString())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> employeeService.updateEmployee("emp123", new UpdateEmployeeRequest()));

        assertEquals("Employee not found", exception.getMessage());
    }

    // Salary Tests

    @Test
    void updateSalaryStructure_ShouldCalculateTotalGross_Correctly() {
        SalaryStructureRequest salaryRequest = new SalaryStructureRequest();
        salaryRequest.setBasicPay(5000.0);
        salaryRequest.setHra(2000.0);
        salaryRequest.setTravelAllowance(500.0);
        salaryRequest.setPerformanceBonus(1000.0);

        when(employeeRepository.findById(anyString())).thenReturn(Optional.of(mockEmployee));
        when(employeeRepository.save(any(Employee.class))).thenAnswer(i -> i.getArgument(0));

        EmployeeResponse response = employeeService.updateSalaryStructure("emp123", salaryRequest);

        assertNotNull(response);
        assertNotNull(response.getSalaryStructure());
        assertEquals(8500.0, response.getSalaryStructure().getTotalGross());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    // Delete Tests

    @Test
    void deleteEmployee_ShouldSetStatusTerminated_WhenExists() {
        when(employeeRepository.findById(anyString())).thenReturn(Optional.of(mockEmployee));
        when(employeeRepository.save(any(Employee.class))).thenAnswer(i -> i.getArgument(0));

        employeeService.deleteEmployee("emp123");

        verify(employeeRepository, times(1)).save(argThat(emp ->
                emp.getStatus() == EmployeeStatus.TERMINATED));
    }
}