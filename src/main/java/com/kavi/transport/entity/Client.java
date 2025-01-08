package com.kavi.transport.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Data
@Table(name = "clients")
public class Client {
    // Getters and Setters
    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @Column(nullable = false)
    private String name;

    @Getter
    @Setter
    @Column
    private String contactInfo;

    @Getter
    @Setter
    @Column(nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @Getter
    @Setter
    @Column(nullable = false)
    private BigDecimal totalPaid = BigDecimal.ZERO;

    @Transient
    @Getter
    @Setter
    private BigDecimal unpaidSum;
}