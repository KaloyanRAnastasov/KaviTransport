package com.kavi.transport.controller;

import com.kavi.transport.entity.Company;
import com.kavi.transport.entity.Vehicle;
import com.kavi.transport.service.CompanyService;
import com.kavi.transport.service.VehicleService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/companies")
public class CompanyController {
    private final CompanyService companyService;
    private final VehicleService vehicleService;

    public CompanyController(CompanyService companyService, VehicleService vehicleService) {
        this.companyService = companyService;
        this.vehicleService = vehicleService;
    }

    @GetMapping
    public String listCompanies(Model model) {
        List<Company> s = companyService.getAllCompanies();
        model.addAttribute("companies", s);
        return "companies";
    }

    @GetMapping("/add")
    public String addCompanyForm(Model model) {
        model.addAttribute("company", new Company());
        return "company-form";
    }

    @PostMapping("/save")
    public String saveCompany(@ModelAttribute("company") Company company) {
        companyService.saveCompany(company);
        return "redirect:/companies";
    }

    @GetMapping("/edit/{id}")
    public String editCompanyForm(@PathVariable Long id, Model model) {
        model.addAttribute("company", companyService.getCompanyById(id));
        return "company-form";
    }

    @GetMapping("/delete/{id}")
    public String deleteCompany(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            companyService.deleteCompany(id);
            redirectAttributes.addFlashAttribute("successMessage", "Company deleted successfully.");
        } catch (DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete company. It is referenced by other records.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "An unexpected error occurred while deleting the company.");
        }
        return "redirect:/companies";
    }

    @GetMapping("/{companyId}/vehicles")
    @ResponseBody
    public List<Vehicle> getVehiclesByCompany(@PathVariable Long companyId) {
        return vehicleService.getVehiclesByCompany(companyId);
    }
}
