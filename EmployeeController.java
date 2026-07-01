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

    // ---------- Main Menu ----------
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
    public String processCreateForm(@ModelAttribute("employee") Employee employee,
                                    Model model) {
        String designation = employee.getDesignation();
        if (!employeeService.isValidDesignation(designation)) {
            model.addAttribute("error", "Designation must be exactly: trainer, tester, or programmer (case-insensitive).");
            return "create";
        }

        double salary = employeeService.getSalaryForDesignation(designation);
        employee.setSalary(salary);

        model.addAttribute("employee", employee);
        return "confirm";   // show confirmation page
    }

    @PostMapping("/confirm")
    public String confirmCreate(@ModelAttribute("employee") Employee employee,
                                @RequestParam("action") String action,
                                Model model) {
        if ("yes".equalsIgnoreCase(action)) {
            employeeService.saveEmployee(employee);
            model.addAttribute("message", "Employee created successfully!");
            return "success";
        } else {
            return "redirect:/";
        }
    }

    // ---------- 2. Display ----------
    @GetMapping("/display")
    public String displayEmployees(Model model) {
        List<Employee> employees = employeeService.getAllEmployees();
        model.addAttribute("employees", employees);
        return "display";
    }

    // ---------- 3. Raise Salary ----------
    @GetMapping("/raise")
    public String showRaiseForm(Model model) {
        List<Employee> employees = employeeService.getAllEmployees();
        if (employees.isEmpty()) {
            model.addAttribute("error", "No employees found. Create one first.");
            return "raise";
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
            model.addAttribute("message", "Salary raised successfully! New salary: " + updated.getSalary());
            return "success";
        } catch (Exception e) {
            model.addAttribute("error", "Error: " + e.getMessage());
            model.addAttribute("employees", employeeService.getAllEmployees());
            return "raise";
        }
    }

    // ---------- 4. Exit ----------
    @GetMapping("/exit")
    public String exit() {
        return "redirect:/";
    }

    // Helper class for raise form binding
    public static class RaiseRequest {
        private Long employeeId;
        private double percentage;

        public Long getEmployeeId() { return employeeId; }
        public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

        public double getPercentage() { return percentage; }
        public void setPercentage(double percentage) { this.percentage = percentage; }
    }
}
