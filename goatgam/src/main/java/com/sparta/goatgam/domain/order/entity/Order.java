package com.sparta.goatgam.domain.order.entity;


import com.sparta.goatgam.domain.restaurant.entity.Restaurant;
import com.sparta.goatgam.domain.user.entity.User;
import com.sparta.goatgam.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "p_order")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private StatusEnum status;

    @Column(name = "status_by", nullable = false, length = 50)
    private String statusBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderFood> orderFoods;

    public Order(String address, int totalprice, String request, LocalDateTime now, StatusEnum statusEnum, String nickname, User user, Restaurant restaurant, List<OrderFood> orderFoods) {
        this.address = address;
        this.totalPrice = totalprice;
        this.request = request;
        this.orderTime = now;
        this.status = statusEnum;
        this.statusBy = nickname;
        this.user = user;
        this.restaurant = restaurant;
        this.orderFoods = orderFoods;
    }
    public void changeStatus(StatusEnum status){
        this.status = status;
    }
}
