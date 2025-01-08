package com.kavi.transport.service;

import com.kavi.transport.entity.Client;
import com.kavi.transport.entity.Company;
import com.kavi.transport.entity.Transport;
import com.kavi.transport.repository.ClientRepository;
import com.kavi.transport.repository.TransportRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransportService {
    private final TransportRepository transportRepository;
    private final ClientRepository clientRepository;

    public TransportService(TransportRepository transportRepository, ClientRepository clientRepository) {
        this.transportRepository = transportRepository;
        this.clientRepository = clientRepository;
    }

    public List<Transport> getAllTransports() {
        return transportRepository.findAll();
    }

    public Transport saveTransport(Transport transport, boolean isEdit) {
        Client client =  clientRepository.getReferenceById(transport.getClient().getId());  ;
        client.setBalance(client.getBalance().subtract(transport.getPrice()));
        clientRepository.save(client);

        // Save the transport
        return  transportRepository.save(transport);
    }

    public Transport getTransportById(Long id) {
        return transportRepository.findById(id).orElseThrow(() -> new RuntimeException("Transport not found"));
    }

    public void deleteTransport(Long id) {
        transportRepository.deleteById(id);
    }

    public Page<Transport> getFilteredTransports(Long clientId, Long vehicleId, Boolean isPaid, Pageable pageable) {
        if (clientId != null && vehicleId != null && isPaid != null) {
            return transportRepository.findByClientIdAndVehicleIdAndIsPaid(clientId, vehicleId, isPaid, pageable);
        } else if (clientId != null && vehicleId != null) {
            return transportRepository.findByClientIdAndVehicleId(clientId, vehicleId, pageable);
        } else if (clientId != null) {
            return transportRepository.findByClientId(clientId, pageable);
        } else if (vehicleId != null) {
            return transportRepository.findByVehicleId(vehicleId, pageable);
        } else if (isPaid != null) {
            return transportRepository.findByIsPaid(isPaid, pageable);
        } else {
            return transportRepository.findAll(pageable); // The Pageable object already handles sorting.
        }
    }

    public long countTransports() {
        return transportRepository.count();
    }
}
