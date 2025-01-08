package com.kavi.transport.service;

import com.kavi.transport.entity.Payment;
import com.kavi.transport.repository.PaymentRepository;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public void savePayment(Payment payment) {
        paymentRepository.save(payment);
    }

    public long countPendingPayments() {
        return paymentRepository.countByIsPaidFalse();
    }
}
