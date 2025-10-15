package com.sparta.goatgam.domain.order.entity;

import com.sparta.goatgam.domain.cart.entity.CartFood;
import com.sparta.goatgam.domain.cart.entity.CartFoodOption;
import com.sparta.goatgam.domain.owner.entity.Food;
import com.sparta.goatgam.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "p_order_food")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderFood extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_food_id", nullable = false, updatable = false)
    private UUID orderFoodId;

    @Column(name = "food_name", nullable = false, updatable = false, length = 100)
    private String foodName;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "option_list", columnDefinition = "jsonb", updatable = false)
    private List<String> optionList;

    @Column(name = "quantity", nullable = false, updatable = false)
    private int quantity;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id")
    private Food food;

    public static OrderFood fromCartFood(CartFood cartFood) {
        List<String> options = cartFood.getCartFoodOptions()
                .stream()
                .map(opt -> opt.getFoodOption().getContents())
                .toList();

        BigDecimal price = cartFood.getFood().getFoodPrice();
        for (CartFoodOption option : cartFood.getCartFoodOptions()) {
            price = price.add(option.getFoodOption().getSurcharge());
        }

        return OrderFood.builder()
                .foodName(cartFood.getFood().getFoodName())
                .optionList(options)
                .quantity(cartFood.getQuantity())
                .price(price)
                .food(cartFood.getFood()).build();
    }
}
