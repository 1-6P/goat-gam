package com.sparta.goatgam.domain.pay.repository;

import com.sparta.goatgam.domain.order.entity.Order;
import com.sparta.goatgam.domain.pay.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Optional<Payment> findPaymentByOrder(Order order);
}
