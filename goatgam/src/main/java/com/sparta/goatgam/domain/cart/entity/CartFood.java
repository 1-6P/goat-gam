package com.sparta.goatgam.domain.cart.entity;

import com.sparta.goatgam.domain.owner.entity.Food;
import com.sparta.goatgam.domain.user.entity.User;
import com.sparta.goatgam.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "p_cart_food")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
public class CartFood extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "cart_food_id", nullable = false, updatable = false)
    private UUID cartFoodId;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id")
    private Food food;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @OneToMany(mappedBy = "cartFood", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("is_deleted = false")
    private List<CartFoodOption> cartFoodOptions;

    @Column(name = "is_deleted", nullable = false)
    @ColumnDefault("false")
    private boolean isDeleted;

    public static CartFood create(Food food, int quantity) {
        CartFood cartFood = new CartFood();

        cartFood.quantity = quantity;
        cartFood.food = food;
        cartFood.cartFoodOptions = new ArrayList<>();

        return cartFood;
    }

    public void addCartFoodOption(CartFoodOption cartFoodOption) {
        cartFoodOptions.add(cartFoodOption);
        cartFoodOption.setCartFood(this);
    }

    // 명시적 접근자: 파생 쿼리에서 isDeleted 프로퍼티 인식용
    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void delete(User user) {
        isDeleted = true;
        deleted(user.getNickname());

        for (CartFoodOption cartFoodOption : cartFoodOptions) {
            cartFoodOption.delete(user);
        }
    }
}
