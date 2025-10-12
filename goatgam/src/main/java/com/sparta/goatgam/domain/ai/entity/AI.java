package com.sparta.goatgam.domain.ai.entity;

import com.sparta.goatgam.domain.owner.entity.Food;
import com.sparta.goatgam.domain.user.entity.User;
import com.sparta.goatgam.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "p_ai_request")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AI extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ai_request_id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "prompt",  nullable = false, updatable = false)
    private String input;

    @Column(name = "answer", nullable = false, updatable = false)
    private String answer;

    @Column(name = "status", nullable = false)
    private boolean status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id")
    private Food food;
}
