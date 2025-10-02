package com.sparta.goatgam.domain.owner.entity;

import com.sparta.goatgam.domain.user.entity.User;
import com.sparta.goatgam.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "p_food")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Food extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "food_id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "food_name", nullable = false, length = 50)
    private String foodName;

    @Column(name = "food_price", nullable = false)
    private int foodPrice;

    @Column(name = "food_image")
    private String foodImage;

    @Column(name = "food_explain")
    private String foodExplain;

    @Enumerated(EnumType.STRING)
    @Column(name = "food_status", nullable = false)
    private FoodStatus foodStatus;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "restaurant_id", nullable = false)
//    private Restaurant restaurant;
}