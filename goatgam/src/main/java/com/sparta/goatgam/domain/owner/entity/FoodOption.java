package com.sparta.goatgam.domain.owner.entity;

import com.sparta.goatgam.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "p_food_option")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FoodOption extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "option_id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "option_contents",  nullable = false, length = 50)
    private String contents;

    @Column(name = "option_surcharge",  nullable = false)
    private int surcharge;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id", nullable = false, updatable = false)
    private Food food;

    @Column(name = "option_deleted",  nullable = false)
    private boolean deleted;
}
