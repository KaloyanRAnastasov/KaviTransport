package com.kavi.transport.service;

import com.kavi.transport.entity.DrivingLicenseType;
import com.kavi.transport.repository.DrivingLicenseTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DrivingLicenseTypeService {
    private final DrivingLicenseTypeRepository repository;

    public DrivingLicenseTypeService(DrivingLicenseTypeRepository repository) {
        this.repository = repository;
    }

    // Retrieve all license types
    public List<DrivingLicenseType> getAllLicenseTypes() {
        return repository.findAll();
    }

    // Save or update a license type
    public DrivingLicenseType saveLicenseType(DrivingLicenseType licenseType) {
        return repository.save(licenseType);
    }

    // Fetch a license type by ID
    public DrivingLicenseType getLicenseTypeById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("DrivingLicenseType not found"));
    }

    // Delete a license type by ID
    public void deleteLicenseType(Long id) {
        repository.deleteById(id);
    }
}
