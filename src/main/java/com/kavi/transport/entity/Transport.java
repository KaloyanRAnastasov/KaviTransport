package com.kavi.transport.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "transports")
public class Transport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Start point is required.")
    @Size(min = 2, max = 255, message = "Start point must be between 2 and 255 characters.")
    private String startPoint;

    @NotNull(message = "End point is required.")
    @Size(min = 2, max = 255, message = "End point must be between 2 and 255 characters.")
    private String endPoint;

    @NotNull(message = "Departure date is required.")
    private LocalDateTime departureDate;

    @NotNull(message = "Arrival date is required.")
    private LocalDateTime arrivalDate;

    @Size(max = 255, message = "Cargo description must not exceed 255 characters.")
    private String cargoDescription;

    @Positive(message = "Total weight must be positive.")
    private BigDecimal totalWeight;

    @Positive(message = "Price must be positive.")
    private BigDecimal price;

    @Positive(message = "Volume must be positive.")
    private BigDecimal volume;

    private Integer peopleCount;

    @NotNull(message = "Vehicle is required.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @NotNull(message = "Driver is required.")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private Employee driver;

    @NotNull(message = "Client is required.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Setter
    @Getter
    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;



    @Setter
    @Getter
    @OneToMany( cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "transport_id")
    private List<Payment> payments;


    @Transient
    public boolean getIsPaid() {
        payments = getPayments();
        System.out.println("Transport.java: getIsPaid() called");
        return !payments.isEmpty() && payments.stream().anyMatch(Payment::isPaid);
    }




}
