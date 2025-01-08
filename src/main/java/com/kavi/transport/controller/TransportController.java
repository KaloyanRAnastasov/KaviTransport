package com.kavi.transport.controller;

import com.kavi.transport.entity.Employee;
import com.kavi.transport.entity.Payment;
import com.kavi.transport.entity.Transport;
import com.kavi.transport.entity.Vehicle;
import com.kavi.transport.service.*;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/transports")
public class TransportController {

    private final TransportService transportService;
    private final VehicleService vehicleService;
    private final EmployeeService employeeService;
    private final ClientService clientService;
    private final CompanyService companyService;
    private final PaymentService paymentService;


    public TransportController(TransportService transportService,
                               VehicleService vehicleService,
                               EmployeeService employeeService,
                               ClientService clientService,
                               CompanyService companyService,
                               PaymentService paymentService
    ) {
        this.transportService = transportService;
        this.vehicleService = vehicleService;
        this.employeeService = employeeService;
        this.clientService = clientService;
        this.companyService = companyService;
        this.paymentService = paymentService;

    }

    @GetMapping
    public String listTransports(
            @RequestParam(value = "clientId", required = false) Long clientId,
            @RequestParam(value = "vehicleId", required = false) Long vehicleId,
            @RequestParam(value = "isPaid", required = false) Boolean isPaid,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "sortOrder", required = false) String sortOrder,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model) {

        Pageable pageable;

        if (sort == null || sort.isEmpty()) {
            // No sorting applied, use default unsorted pageable
            pageable = PageRequest.of(page, 10);
        } else {
            Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder) ? Sort.Direction.DESC : Sort.Direction.ASC;
            pageable = PageRequest.of(page, 10, Sort.by(direction, sort));
        }

        Page<Transport> transports = transportService.getFilteredTransports(clientId, vehicleId, isPaid, pageable);

        model.addAttribute("transports", transports.getContent());
        model.addAttribute("pageable", transports);
        model.addAttribute("clients", clientService.getAllClients());
        model.addAttribute("vehicles", vehicleService.getAllVehicles());
        model.addAttribute("sort", sort);
        model.addAttribute("sortOrder", sortOrder);

        return "transport-list";
    }

    @GetMapping("/add")
    public String addTransportForm(Model model) {
        model.addAttribute("transport", new Transport());
        model.addAttribute("vehicles", vehicleService.getAllVehicles());
        model.addAttribute("employees", employeeService.getAllEmployees());
        model.addAttribute("clients", clientService.getAllClients());
        model.addAttribute("companies", companyService.getAllCompanies());
        return "transport-form";
    }

    @GetMapping("/pay/{id}")
    public String payTransport(@PathVariable Long id) {
        Transport transport = transportService.getTransportById(id);


        if (!transport.getIsPaid()) {
            Payment payment = new Payment();
            payment.setTransport(transport);
            payment.setAmount(transport.getPrice());
            payment.setPaymentDate(LocalDateTime.now());
            payment.setPaid(true);
            paymentService.savePayment(payment);
            transport.getCompany().setRevenue(transport.getCompany().getRevenue().add(transport.getPrice()));
            transport.getClient().setBalance(transport.getClient().getBalance().subtract(transport.getPrice()));
            transport.getClient().setTotalPaid(transport.getClient().getTotalPaid().add(transport.getPrice()));

        }
        return "redirect:/transports";
    }

    @PostMapping("/save")
    public String saveTransport(@Valid @ModelAttribute("transport") Transport transport, BindingResult result, Model model) {
        // Validate that arrival time is after departure time
        if (transport.getDepartureDate() != null && transport.getArrivalDate() != null) {
            if (!transport.getArrivalDate().isAfter(transport.getDepartureDate())) {
                result.rejectValue("arrivalDate", "error.arrivalDate", "Arrival time must be after departure time.");
                populateModelForTransportForm(model, transport);
                return "transport-form";
            }
        }


        // Validation logic for driver licenses and vehicle capacity
        Vehicle selectedVehicle = vehicleService.getVehicleById(transport.getVehicle().getId());
        Employee selectedDriver = employeeService.getEmployeeById(transport.getDriver().getId());

        if (!selectedDriver.getDrivingLicenseTypes().containsAll(selectedVehicle.getRequiredLicenseTypes())) {
            result.rejectValue("driver", "error.driver", "Driver does not have the required license for this vehicle.");
            populateModelForTransportForm(model, transport);
            return "transport-form";
        }

        if (selectedVehicle.getVehicleType().getLoadType() == 0 && (transport.getPeopleCount() > selectedVehicle.getMaxPeopleCount())) {
            result.rejectValue("peopleCount", "error.peopleCount", "People count exceeds the maximum capacity of the vehicle.");
            populateModelForTransportForm(model, transport);
            return "transport-form";
        }

        if (selectedVehicle.getVehicleType().getLoadType() == 1) {
            if (transport.getTotalWeight() != null && transport.getTotalWeight().compareTo(BigDecimal.valueOf(selectedVehicle.getMaxWeight())) > 0) {
                result.rejectValue("totalWeight", "error.totalWeight", "Total weight exceeds the maximum capacity of the vehicle.");
                populateModelForTransportForm(model, transport);
                return "transport-form";
            }
            if (transport.getVolume() != null && transport.getVolume().compareTo(BigDecimal.valueOf(selectedVehicle.getMaxVolume())) > 0) {
                result.rejectValue("volume", "error.volume", "Volume exceeds the maximum capacity of the vehicle.");
                populateModelForTransportForm(model, transport);
                return "transport-form";
            }
        }

        transportService.saveTransport(transport, transport.getId() != null);
        return "redirect:/transports";
    }

    private void populateModelForTransportForm(Model model, Transport transport) {
        if (transport.getId() != null) {
            model.addAttribute("transport", transport);
        }
        model.addAttribute("employees", employeeService.getAllEmployees());
        model.addAttribute("clients", clientService.getAllClients());
        model.addAttribute("companies", companyService.getAllCompanies());
        model.addAttribute("vehicles", new ArrayList<>()); // Empty initially, updated by JS
        model.addAttribute("drivers", new ArrayList<>());
    }


    @GetMapping("/edit/{id}")
    public String editTransportForm(@PathVariable Long id, Model model) {
        model.addAttribute("transport", transportService.getTransportById(id));
        model.addAttribute("employees", employeeService.getAllEmployees());
        model.addAttribute("clients", clientService.getAllClients());
        model.addAttribute("companies", companyService.getAllCompanies());
        model.addAttribute("vehicles", new ArrayList<>()); // Empty initially, updated by JS
        model.addAttribute("drivers", new ArrayList<>());
        return "transport-form";
    }

    @GetMapping("/delete/{id}")
    public String deleteTransport(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            transportService.deleteTransport(id);
            redirectAttributes.addFlashAttribute("successMessage", "Transport deleted successfully.");
        } catch (DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete transport. It is referenced by other records (e.g., payments).");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "An unexpected error occurred while deleting the transport.");
        }
        return "redirect:/transports";
    }




}
