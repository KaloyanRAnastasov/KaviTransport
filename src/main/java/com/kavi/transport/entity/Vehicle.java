package com.kavi.transport.entity;

import jakarta.persistence.*;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "vehicles")
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String licensePlate;

    private Double capacity;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne
    @JoinColumn(name = "vehicle_type_id", nullable = false)
    private VehicleType vehicleType;

    private Integer maxPeopleCount;
    private Double maxWeight;
    private Double maxVolume;

    @ManyToMany
    @JoinTable(
            name = "vehicle_license_types",
            joinColumns = @JoinColumn(name = "vehicle_id"),
            inverseJoinColumns = @JoinColumn(name = "license_type_id")
    )
    private Set<DrivingLicenseType> requiredLicenseTypes;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
    public Double getCapacity() { return capacity; }
    public void setCapacity(Double capacity) { this.capacity = capacity; }
    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }
    public VehicleType getVehicleType() { return vehicleType; }
    public void setVehicleType(VehicleType vehicleType) { this.vehicleType = vehicleType; }
    public Integer getMaxPeopleCount() { return maxPeopleCount; }
    public void setMaxPeopleCount(Integer maxPeopleCount) { this.maxPeopleCount = maxPeopleCount; }
    public Double getMaxWeight() { return maxWeight; }
    public void setMaxWeight(Double maxWeight) { this.maxWeight = maxWeight; }
    public Double getMaxVolume() { return maxVolume; }
    public void setMaxVolume(Double maxVolume) { this.maxVolume = maxVolume; }


    public Set<DrivingLicenseType> getRequiredLicenseTypes() {
        return requiredLicenseTypes;
    }

    public void setRequiredLicenseTypes(Set<DrivingLicenseType> requiredLicenseTypes) {
        this.requiredLicenseTypes = requiredLicenseTypes;
    }

    @Transient
    public List<DrivingLicenseType> getSortedLicenseTypes() {
        return requiredLicenseTypes.stream()
                .sorted(Comparator.comparing(DrivingLicenseType::getType))
                .collect(Collectors.toList());
    }
}
