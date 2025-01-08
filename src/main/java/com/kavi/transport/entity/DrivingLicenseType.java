package com.kavi.transport.entity;

import jakarta.persistence.*;

@Entity
public class DrivingLicenseType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String type; // e.g., "B", "C", "D", etc.

    @Column(nullable = false)
    private String description; // e.g., "Passenger cars", "Heavy trucks", etc.

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
