package com.sparta.goatgam.domain.review.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ReviewRequestDto {
    private int rate;
    private String content;
    private String review_image;
    private UUID restaurantId;
}