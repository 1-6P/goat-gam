package com.sparta.goatgam.domain.pay.repository;

import com.sparta.goatgam.domain.order.entity.Order;
import com.sparta.goatgam.domain.pay.entity.Payment;
import com.sparta.goatgam.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Optional<Payment> findPaymentByOrder(Order order);

    Page<Payment> findAllByOrderUser(User user, Pageable pageable);
}
