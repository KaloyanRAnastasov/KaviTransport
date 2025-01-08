package com.kavi.transport.controller;

import com.kavi.transport.entity.VehicleType;
import com.kavi.transport.service.VehicleTypeService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/vehicle-types")
public class VehicleTypeController {

    private final VehicleTypeService vehicleTypeService;

    public VehicleTypeController(VehicleTypeService vehicleTypeService) {
        this.vehicleTypeService = vehicleTypeService;
    }

    @GetMapping
    public String listVehicleTypes(Model model) {
        model.addAttribute("vehicleTypes", vehicleTypeService.getAllVehicleTypes());
        return "vehicle-type-list";
    }

    @GetMapping("/add")
    public String addVehicleTypeForm(Model model) {
        model.addAttribute("vehicleType", new VehicleType());
        return "vehicle-type-form";
    }

    @GetMapping("/edit/{id}")
    public String editVehicleTypeForm(@PathVariable Long id, Model model) {
        model.addAttribute("vehicleType", vehicleTypeService.getVehicleTypeById(id));
        return "vehicle-type-form";
    }

    @PostMapping("/save")
    public String saveVehicleType(@ModelAttribute("vehicleType") VehicleType vehicleType) {
        vehicleTypeService.saveVehicleType(vehicleType);
        return "redirect:/vehicle-types";
    }

    @GetMapping("/delete/{id}")
    public String deleteVehicleType(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            vehicleTypeService.deleteVehicleType(id);
            redirectAttributes.addFlashAttribute("successMessage", "Vehicle type deleted successfully.");
        } catch (DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete vehicle type. It is referenced by other records (e.g., vehicles).");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "An unexpected error occurred while deleting the vehicle type.");
        }
        return "redirect:/vehicle-types";
    }
}
