package com.sparta.goatgam.domain.cart.entity;

import com.sparta.goatgam.domain.restaurant.entity.Restaurant;
import com.sparta.goatgam.domain.restaurant.entity.RestaurantEnum;
import com.sparta.goatgam.domain.user.entity.User;
import com.sparta.goatgam.global.entity.BaseEntity;
import com.sparta.goatgam.global.exception.BusinessException;
import com.sparta.goatgam.global.exception.ExceptionCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "p_cart")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Cart extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "cart_id", nullable = false, updatable = false)
    private UUID cartId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("is_deleted = false")
    private List<CartFood> cartFoods;

    @Column(name = "is_deleted", nullable = false)
    @ColumnDefault("false")
    private boolean isDeleted;

    public static Cart create(User user, Restaurant restaurant) {
        // 사용자 검증
        if (!user.getStatus()) {
            throw new BusinessException(ExceptionCode.CART_DELETED_USER);
        }

        // 식당 검증
        if (!restaurant.isStatus()) {
            throw new BusinessException(ExceptionCode.CART_DELETED_RESTAURANT);
        }
        if (restaurant.getIsPublic() != RestaurantEnum.Open) {
            throw new BusinessException(ExceptionCode.CART_RESTAURANT_NOT_OPENED);
        }

        Cart cart = new Cart();

        cart.restaurant = restaurant;
        cart.user = user;
        cart.cartFoods = new ArrayList<>();

        return cart;
    }

    public void addCartFood(CartFood cartFood) {
        cartFoods.add(cartFood);
        cartFood.setCart(this);
    }

    public void delete(User user) {
        isDeleted = true;
        deleted(user.getNickname());

        for (CartFood cartFood : cartFoods) {
            cartFood.delete(user);
        }
    }

    // 명시적 접근자: 프로퍼티명을 "isDeleted"로 인식시키기 위함
    public boolean getIsDeleted() {
        return isDeleted;
    }
}
