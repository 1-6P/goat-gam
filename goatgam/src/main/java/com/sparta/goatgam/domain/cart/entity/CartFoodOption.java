package com.sparta.goatgam.domain.cart.entity;

import com.sparta.goatgam.domain.owner.entity.FoodOption;
import com.sparta.goatgam.domain.user.entity.User;
import com.sparta.goatgam.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.UUID;

@Entity
@Table(name = "p_cart_food_option")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CartFoodOption extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "cart_food_option_id", nullable = false, updatable = false)
    private UUID cartFoodOptionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_option_id")
    private FoodOption foodOption;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_food_id")
    private CartFood cartFood;

    @Column(name = "is_deleted", nullable = false)
    @ColumnDefault("false")
    private boolean isDeleted;

    public static CartFoodOption create(FoodOption foodOption) {
        CartFoodOption cartFoodOption = new CartFoodOption();

        cartFoodOption.foodOption = foodOption;

        return cartFoodOption;
    }

    public void delete(User user) {
        isDeleted = true;
        deleted(user.getNickname());
    }
}
