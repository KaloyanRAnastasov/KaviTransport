package com.kavi.transport.service;

import com.kavi.transport.entity.DrivingLicenseType;
import com.kavi.transport.entity.Vehicle;
import com.kavi.transport.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class VehicleService {
    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    @Transactional
    public Vehicle saveVehicle(Vehicle vehicle) {
        // Save the vehicle first to ensure it has a valid ID
        Vehicle savedVehicle = vehicleRepository.save(vehicle);

        // Now save the associated license types
        Set<DrivingLicenseType> licenseTypes = vehicle.getRequiredLicenseTypes();
        if (licenseTypes != null && !licenseTypes.isEmpty()) {
            savedVehicle.setRequiredLicenseTypes(licenseTypes);
            vehicleRepository.save(savedVehicle); // Update the vehicle with license types
        }

        return savedVehicle;
    }

    public Vehicle getVehicleById(Long id) {
        return vehicleRepository.findById(id).orElseThrow(() -> new RuntimeException("Vehicle not found"));
    }

    public void deleteVehicle(Long id) {
        vehicleRepository.deleteById(id);
    }

    public List<Vehicle> getVehiclesByCompany(Long companyId) {
        return vehicleRepository.findByCompanyId(companyId);
    }

    public long countVehicles() {
        return vehicleRepository.count();
    }
}
