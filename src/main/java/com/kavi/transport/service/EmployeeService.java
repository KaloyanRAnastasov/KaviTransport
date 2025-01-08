package com.kavi.transport.service;

import com.kavi.transport.entity.DrivingLicenseType;
import com.kavi.transport.entity.Employee;
import com.kavi.transport.repository.EmployeeRepository;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class EmployeeService {

    private final EmployeeRepository repository;

    public EmployeeService(EmployeeRepository repository) {
        this.repository = repository;
    }

    public List<Employee> getAllEmployees() {
        return repository.findAll();
    }

    public Employee saveEmployee(Employee employee) {
        return repository.save(employee);
    }

    public Employee getEmployeeById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    public void deleteEmployee(Long id) {
        repository.deleteById(id);
    }

    public List<Employee> getDriversWithMatchingLicenses(Set<DrivingLicenseType> requiredLicenseTypes) {
        // Debugging: Print the required license types
        System.out.println("Required License Types: " + requiredLicenseTypes);

        if (requiredLicenseTypes == null || requiredLicenseTypes.isEmpty()) {
            System.out.println("No required license types provided.");
            return List.of(); // Return an empty list if no requirements are provided
        }

        return repository.findAll().stream()
                .filter(driver -> {
                    // Debugging: Print each driver's licenses
                    System.out.println("Driver: " + driver.getName());
                    System.out.println("Driver's License Types: " + driver.getDrivingLicenseTypes());

                    // Ensure driver's license types are not null
                    if (driver.getDrivingLicenseTypes() == null) {
                        System.out.println("Driver " + driver.getName() + " has no licenses.");
                        return false;
                    }

                    // Check if driver's licenses match the required licenses
                    boolean hasMatchingLicenses = driver.getDrivingLicenseTypes().containsAll(requiredLicenseTypes);
                    System.out.println("Driver " + driver.getName() + " matches: " + hasMatchingLicenses);

                    return hasMatchingLicenses;
                })
                .toList();
    }

    public long countEmployees() {
        return repository.count();
    }
}
