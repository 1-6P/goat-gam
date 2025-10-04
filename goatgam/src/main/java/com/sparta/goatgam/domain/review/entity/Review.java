package com.sparta.goatgam.domain.review.entity;

import com.sparta.goatgam.domain.restaurant.entity.Restaurant;
import com.sparta.goatgam.domain.user.entity.User;
import com.sparta.goatgam.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "p_review")
@Builder
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID reviewId;

    @Column(nullable = false)
    private int rate;

    @Column(nullable = false)
    private String content;

    private String review_image;

    private Boolean status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    public void deleteReview(String nickname) {
        if (this.status){
            this.status = false;
            this.deleted(nickname);
        }
    }

    public void restoreReview(String nickname) {
        this.status = true;
        this.restore();
    }
}