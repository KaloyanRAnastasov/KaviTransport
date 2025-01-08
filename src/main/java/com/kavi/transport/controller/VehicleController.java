package com.kavi.transport.controller;

import com.kavi.transport.entity.Employee;
import com.kavi.transport.entity.Vehicle;
import com.kavi.transport.service.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/vehicles")
public class VehicleController {
    private final VehicleService vehicleService;
    private final CompanyService companyService;
    private final VehicleTypeService vehicleTypeService;
    private final DrivingLicenseTypeService drivingLicenseTypeService;
    private final EmployeeService employeeService;

    public VehicleController(VehicleService vehicleService, CompanyService companyService, VehicleTypeService vehicleTypeService, DrivingLicenseTypeService drivingLicenseTypeService, EmployeeService employeeService) {
        this.vehicleService = vehicleService;
        this.companyService = companyService;
        this.vehicleTypeService = vehicleTypeService;
        this.drivingLicenseTypeService = drivingLicenseTypeService;
        this.employeeService = employeeService;
    }

    @GetMapping
    public String listVehicles(Model model) {
        model.addAttribute("vehicles", vehicleService.getAllVehicles());
        return "vehicles";
    }

    @GetMapping("/{vehicleId}/eligible-drivers")
    @ResponseBody
    public List<Employee> getEligibleDrivers(@PathVariable Long vehicleId) {
        Vehicle vehicle = vehicleService.getVehicleById(vehicleId);
        List<Employee> e =  employeeService.getDriversWithMatchingLicenses(vehicle.getRequiredLicenseTypes());
        return e;
    }

    @GetMapping("/add")
    public String addVehicleForm(Model model) {
        model.addAttribute("vehicle", new Vehicle());
        model.addAttribute("companies", companyService.getAllCompanies());
        model.addAttribute("vehicleTypes", vehicleTypeService.getAllVehicleTypes());
        model.addAttribute("licenseTypes", drivingLicenseTypeService.getAllLicenseTypes());
        return "vehicle-form";
    }

    @PostMapping("/save")
    public String saveVehicle(@ModelAttribute("vehicle") Vehicle vehicle) {
        vehicleService.saveVehicle(vehicle);
        return "redirect:/vehicles";
    }

    @GetMapping("/edit/{id}")
    public String editVehicleForm(@PathVariable Long id, Model model) {
        model.addAttribute("vehicle", vehicleService.getVehicleById(id));
        model.addAttribute("companies", companyService.getAllCompanies());
        model.addAttribute("vehicleTypes", vehicleTypeService.getAllVehicleTypes());
        model.addAttribute("licenseTypes", drivingLicenseTypeService.getAllLicenseTypes());
        return "vehicle-form";
    }

    @GetMapping("/delete/{id}")
    public String deleteVehicle(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            vehicleService.deleteVehicle(id);
            redirectAttributes.addFlashAttribute("successMessage", "Vehicle deleted successfully.");
        } catch (DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete vehicle. It is referenced by other records (e.g., transports).");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "An unexpected error occurred while deleting the vehicle.");
        }
        return "redirect:/vehicles";
    }
}
