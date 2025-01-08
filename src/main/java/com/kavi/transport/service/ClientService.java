package com.kavi.transport.service;

import com.kavi.transport.entity.Client;
import com.kavi.transport.repository.ClientRepository;
import com.kavi.transport.repository.TransportRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ClientService {
    private final ClientRepository clientRepository;
    private final TransportRepository transportRepository;

    public ClientService(ClientRepository clientRepository, TransportRepository transportRepository) {
        this.clientRepository = clientRepository;
        this.transportRepository = transportRepository;
    }

    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    public Client saveClient(Client client) {
        return clientRepository.save(client);
    }

    public Client getClientById(Long id) {
        return clientRepository.findById(id).orElseThrow(() -> new RuntimeException("Client not found"));
    }

    public void deleteClient(Long id) {
        clientRepository.deleteById(id);
    }

    public BigDecimal getUnpaidTransportSumForClient(Long clientId) {
        return transportRepository.findUnpaidTransportSumByClientId(clientId);
    }


}
