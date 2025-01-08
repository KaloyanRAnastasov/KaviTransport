package com.kavi.transport.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "employees")
public class Employee {

    // Getters and Setters
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    private String name;

    @Getter
    private String position;

    @Getter
    private BigDecimal salary;

    @Getter
    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToMany
    @JoinTable(
            name = "employee_license_types",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "license_type_id")
    )
    private Set<DrivingLicenseType> drivingLicenseTypes;


    public void setId(Long id) { this.id = id; }

    public void setName(String name) { this.name = name; }

    public void setPosition(String position) { this.position = position; }

    public void setSalary(BigDecimal salary) { this.salary = salary; }

    public void setCompany(Company company) { this.company = company; }

    public Set<DrivingLicenseType> getDrivingLicenseTypes() {
        System.out.println("Employee.java: getDrivingLicenseTypes() called");
        return drivingLicenseTypes;

    }
    public void setDrivingLicenseTypes(Set<DrivingLicenseType> drivingLicenseTypes) { this.drivingLicenseTypes = drivingLicenseTypes; }


    @Transient
    public List<DrivingLicenseType> getSortedLicenseTypes() {
        return drivingLicenseTypes.stream()
                .sorted(Comparator.comparing(DrivingLicenseType::getType))
                .collect(Collectors.toList());
    }

}
