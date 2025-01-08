package com.kavi.transport.controller;

import com.kavi.transport.entity.Client;
import com.kavi.transport.service.ClientService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/clients")
public class ClientController {
    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public String listClients(Model model) {
        List<Client> clients = clientService.getAllClients();
        clients.forEach(client -> {
            BigDecimal unpaidSum = clientService.getUnpaidTransportSumForClient(client.getId());
            client.setUnpaidSum(unpaidSum); // Assuming you add an unpaidSum field in the Client entity
        });
        model.addAttribute("clients", clients);
        return "clients";
    }

    @GetMapping("/add")
    public String addClientForm(Model model) {
        model.addAttribute("client", new Client());
        return "client-form";
    }

    @PostMapping("/save")
    public String saveClient(@ModelAttribute("client") Client client) {
        clientService.saveClient(client);
        return "redirect:/clients";
    }

    @GetMapping("/edit/{id}")
    public String editClientForm(@PathVariable Long id, Model model) {
        model.addAttribute("client", clientService.getClientById(id));
        return "client-form";
    }

    @GetMapping("/delete/{id}")
    public String deleteClient(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            clientService.deleteClient(id);
            redirectAttributes.addFlashAttribute("successMessage", "Client deleted successfully.");
        } catch (DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete client. It is referenced by other records.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "An unexpected error occurred while deleting the client.");
        }
        return "redirect:/clients";
    }
}
