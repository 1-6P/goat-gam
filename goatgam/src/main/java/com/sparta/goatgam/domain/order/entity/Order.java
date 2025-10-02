package com.sparta.goatgam.domain.order.entity;


import com.sparta.goatgam.domain.user.entity.User;
import com.sparta.goatgam.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "p_order")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_id", nullable = false, updatable = false)
    private UUID orderId;

    @Column(name = "address", nullable = false, updatable = false)
    private String address;

    @Column(name = "total_price", nullable = false, updatable = false)
    private int totalPrice;

    @Column(name = "request", updatable = false)
    private String request;

    @Column(name = "order_time", nullable = false, updatable = false)
    private LocalDateTime orderTime;

    @Column(name = "status", nullable = false, updatable = false)
    private StatusEnum status;

    @Column(name = "status_by", nullable = false, updatable = false, length = 50)
    private String statusBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "restaurant_id")
//    private Restaurant restaurant;
}
