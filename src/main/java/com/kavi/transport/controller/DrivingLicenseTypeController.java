package com.kavi.transport.controller;

import com.kavi.transport.entity.DrivingLicenseType;
import com.kavi.transport.service.DrivingLicenseTypeService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/driving-license-types")
public class DrivingLicenseTypeController {

    private final DrivingLicenseTypeService drivingLicenseTypeService;

    public DrivingLicenseTypeController(DrivingLicenseTypeService drivingLicenseTypeService) {
        this.drivingLicenseTypeService = drivingLicenseTypeService;
    }

    @GetMapping
    public String listLicenseTypes(Model model) {
        model.addAttribute("licenseTypes", drivingLicenseTypeService.getAllLicenseTypes());
        return "driving-license-type-list";
    }

    @GetMapping("/add")
    public String addLicenseTypeForm(Model model) {
        model.addAttribute("licenseType", new DrivingLicenseType());
        return "driving-license-type-form";
    }

    @GetMapping("/edit/{id}")
    public String editLicenseTypeForm(@PathVariable Long id, Model model) {
        model.addAttribute("licenseType", drivingLicenseTypeService.getLicenseTypeById(id));
        return "driving-license-type-form";
    }

    @PostMapping("/save")
    public String saveLicenseType(@ModelAttribute("licenseType") DrivingLicenseType licenseType) {
        drivingLicenseTypeService.saveLicenseType(licenseType);
        return "redirect:/driving-license-types";
    }

    @GetMapping("/delete/{id}")
    public String deleteLicenseType(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            drivingLicenseTypeService.deleteLicenseType(id);
            redirectAttributes.addFlashAttribute("successMessage", "License type deleted successfully.");
        } catch (DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete license type. It is referenced by other records.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "An unexpected error occurred while deleting the license type.");
        }
        return "redirect:/driving-license-types";
    }
}
