package com.example.demo.controller;

import com.example.demo.entity.Employee;
import com.example.demo.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    // ---------- 1. Create ----------
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("employee", new Employee());
        return "create";
    }

    @PostMapping("/create")
    public String processCreateForm(@ModelAttribute("employee") Employee employee, Model model) {
        // === Validate Name ===
        String name = employee.getName();
        if (name == null || name.trim().isEmpty()) {
            model.addAttribute("error", "Name cannot be empty.");
            return "create";
        }
        // Remove leading/trailing spaces for validation
        String trimmed = name.trim();
        int spaceCount = 0;
        boolean validName = true;
        for (char c : trimmed.toCharArray()) {
            if (c == ' ') {
                spaceCount++;
            } else if (!Character.isLetter(c)) {
                validName = false;
                break;
            }
        }
        if (!validName || spaceCount > 2) {
            model.addAttribute("error", "Name must contain only letters (A-Z, a-z) and at most 2 spaces. No numbers or special characters allowed.");
            return "create";
        }        // Store trimmed name (remove extra spaces)
        employee.setName(trimmed);

        // === Validate Age ===
        int age = employee.getAge();
        if (age < 18 || age > 60) {
            model.addAttribute("error", "Age must be between 18 and 60.");
            return "create";
        }

        // === Validate Designation ===
        String designation = employee.getDesignation();
        if (!employeeService.isValidDesignation(designation)) {
            model.addAttribute("error", "Designation must be: trainer, tester, or programmer.");
            return "create";
        }

        // Set salary based on designation
        employee.setSalary(employeeService.getSalaryForDesignation(designation));
        model.addAttribute("employee", employee);
        return "confirm";
    }

    @PostMapping("/confirm")
    public String confirmCreate(@ModelAttribute("employee") Employee employee,
                                @RequestParam("action") String action,
                                Model model) {
        if ("yes".equalsIgnoreCase(action)) {
            employeeService.saveEmployee(employee);
            model.addAttribute("message", "Employee created successfully!");
            return "success";
        }
        return "redirect:/";
    }

    // ---------- 2. Display ----------
    @GetMapping("/display")
    public String displayEmployees(Model model) {
        model.addAttribute("employees", employeeService.getAllEmployees());
        return "display";
    }

    // ---------- 3. Raise Salary ----------
    @GetMapping("/raise")
    public String showRaiseForm(Model model) {
        List<Employee> employees = employeeService.getAllEmployees();
        if (employees.isEmpty()) {
            model.addAttribute("error", "No employees found. Create one first.");
        }
        model.addAttribute("employees", employees);
        model.addAttribute("raiseRequest", new RaiseRequest());
        return "raise";
    }

    @PostMapping("/raise")
    public String processRaise(@ModelAttribute("raiseRequest") RaiseRequest request,
                               Model model) {
        try {
            Employee updated = employeeService.raiseSalary(request.getEmployeeId(), request.getPercentage());
            model.addAttribute("message", "Salary raised! New salary: " + updated.getSalary());
            return "success";
        } catch (Exception e) {
            model.addAttribute("error", "Error: " + e.getMessage());
            model.addAttribute("employees", employeeService.getAllEmployees());
            return "raise";
        }
    }

    // ---------- 4. Exit with Thank You ----------
    @GetMapping("/exit")
    public String exit() {
        return "goodbye";   // shows thank‑you page
    }

    // Helper class for raise form
    public static class RaiseRequest {
        private Integer employeeId;
        private double percentage;
        public Integer getEmployeeId() { return employeeId; }
        public void setEmployeeId(Integer employeeId) { this.employeeId = employeeId; }
        public double getPercentage() { return percentage; }
        public void setPercentage(double percentage) { this.percentage = percentage; }
    }
}
