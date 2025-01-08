package com.kavi.transport.controller;

import com.kavi.transport.entity.Employee;
import com.kavi.transport.entity.DrivingLicenseType;
import com.kavi.transport.service.CompanyService;
import com.kavi.transport.service.EmployeeService;
import com.kavi.transport.service.DrivingLicenseTypeService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final DrivingLicenseTypeService licenseTypeService;
    private final CompanyService companyService;

    public EmployeeController(EmployeeService employeeService, DrivingLicenseTypeService licenseTypeService , CompanyService companyService) {
        this.employeeService = employeeService;
        this.companyService = companyService;
        this.licenseTypeService = licenseTypeService;
    }

    @GetMapping
    public String listEmployees(Model model) {
        model.addAttribute("employees", employeeService.getAllEmployees());
        model.addAttribute("companies", companyService.getAllCompanies());
        return "employees";
    }

    @GetMapping("/add")
    public String addEmployeeForm(Model model) {
        model.addAttribute("employee", new Employee());
        model.addAttribute("companies", companyService.getAllCompanies());
        model.addAttribute("licenseTypes", licenseTypeService.getAllLicenseTypes());
        return "employee-form";
    }

    @PostMapping("/save")
    public String saveEmployee(@ModelAttribute Employee employee) {
        employeeService.saveEmployee(employee);
        return "redirect:/employees";
    }

    @GetMapping("/edit/{id}")
    public String editEmployeeForm(@PathVariable Long id, Model model) {
        model.addAttribute("employee", employeeService.getEmployeeById(id));
        model.addAttribute("companies", companyService.getAllCompanies());
        model.addAttribute("licenseTypes", licenseTypeService.getAllLicenseTypes());
        return "employee-form";
    }

    @GetMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            employeeService.deleteEmployee(id);
            redirectAttributes.addFlashAttribute("successMessage", "Employee deleted successfully.");
        } catch (DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete this employee as it is referenced by other records.");
        }
        return "redirect:/employees";
    }
}
