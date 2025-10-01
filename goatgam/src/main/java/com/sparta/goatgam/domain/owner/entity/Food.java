package com.sparta.goatgam.domain.owner.entity;

import com.sparta.goatgam.domain.user.entity.User;
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
public class Food {
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    // 생성일자
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 생성자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", updatable = false)
    private User createdBy;

    // 수정일자
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 수정자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy;

    // 삭제일자
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // 삭제자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deleted_by")
    private User deletedBy;

    public Food(String foodName, int foodPrice, String foodImage, String foodExplain, FoodStatus foodStatus, Restaurant restaurant) {
        this.foodName = foodName;
        this.foodPrice = foodPrice;
        this.foodImage = foodImage;
        this.foodExplain = foodExplain;
        this.foodStatus = foodStatus;
        this.restaurant = restaurant;
    }
}