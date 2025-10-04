package com.sparta.goatgam.domain.order.entity;

import com.sparta.goatgam.domain.owner.entity.Food;
import com.sparta.goatgam.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "p_order_food")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    @Column(name = "count", nullable = false, updatable = false)
    private int count;

    @Column(name = "price", nullable = false, updatable = false)
    private int price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id")
    private Food food;
}
