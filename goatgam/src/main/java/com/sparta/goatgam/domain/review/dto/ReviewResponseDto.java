package com.sparta.goatgam.domain.review.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class ReviewResponseDto {
    private UUID reviewId;
    private String message;
}
