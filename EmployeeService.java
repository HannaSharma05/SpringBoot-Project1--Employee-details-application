package com.example.demo.service;

import com.example.demo.entity.Employee;
import com.example.demo.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    private static final double TRAINER_SALARY = 30000.0;
    private static final double TESTER_SALARY = 35000.0;
    private static final double PROGRAMMER_SALARY = 40000.0;

    public double getSalaryForDesignation(String designation) {
        return switch (designation.toLowerCase()) {
            case "trainer" -> TRAINER_SALARY;
            case "tester" -> TESTER_SALARY;
            case "programmer" -> PROGRAMMER_SALARY;
            default -> throw new IllegalArgumentException("Invalid designation: " + designation);
        };
    }

    public boolean isValidDesignation(String designation) {
        if (designation == null) return false;
        String d = designation.toLowerCase();
        return d.equals("trainer") || d.equals("tester") || d.equals("programmer");
    }

    public Employee saveEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Optional<Employee> findEmployeeById(Integer id) {
        return employeeRepository.findById(id);
    }

    public Employee raiseSalary(Integer id, double percentage) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
        double increment = employee.getSalary() * (percentage / 100.0);
        employee.setSalary(employee.getSalary() + increment);
        return employeeRepository.save(employee);
    }
}
