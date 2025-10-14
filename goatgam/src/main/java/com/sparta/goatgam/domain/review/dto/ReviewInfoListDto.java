package com.sparta.goatgam.domain.review.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ReviewInfoListDto {
    private UUID reviewId;
    private UUID restaurantId;
    private String nickname;
    private String content;
    private String reviewImage;
    private int rate;
    private LocalDateTime createdAt;
}
