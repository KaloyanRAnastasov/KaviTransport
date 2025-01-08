package com.kavi.transport.controller;

import com.kavi.transport.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final UserService userService;
    private final VehicleService vehicleService;
    private final TransportService transportService;
    private final EmployeeService employeeService;
    private final CompanyService companyService;
    private final PaymentService paymentService;

    public HomeController(UserService userService, VehicleService vehicleService, TransportService transportService, EmployeeService employeeService, CompanyService companyService, PaymentService paymentService) {
        this.userService = userService;
        this.vehicleService = vehicleService;
        this.transportService = transportService;
        this.employeeService = employeeService;
        this.companyService = companyService;
        this.paymentService = paymentService;
    }
    

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalUsers", userService.countUsers());
        model.addAttribute("totalVehicles", vehicleService.countVehicles());
        model.addAttribute("totalTransports", transportService.countTransports());
        model.addAttribute("totalEmployees", employeeService.countEmployees());
        model.addAttribute("companyWithMaxRevenue", companyService.getCompanyWithMaxRevenue());
        model.addAttribute("pendingPayments", paymentService.countPendingPayments());
        return "dashboard";
    }

}