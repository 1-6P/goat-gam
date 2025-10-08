package com.sparta.goatgam.domain.cart.entity;

import com.sparta.goatgam.domain.owner.entity.Food;
import com.sparta.goatgam.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "p_cart_food")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartFood extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "cart_food_id", nullable = false, updatable = false)
    private UUID cartFoodId;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "price", nullable = false)
    private int price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id")
    private Food food;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @OneToMany(mappedBy = "cartFood", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartFoodOption> cartFoodOptions;
}
