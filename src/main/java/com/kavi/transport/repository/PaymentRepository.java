package com.kavi.transport.repository;

import com.kavi.transport.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    long countByIsPaidFalse();
}
