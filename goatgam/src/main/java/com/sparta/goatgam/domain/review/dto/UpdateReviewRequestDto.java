package com.sparta.goatgam.domain.review.dto;

import lombok.Data;

@Data
public class UpdateReviewRequestDto {
    private int rate;
    private String content;
    private String review_image;
}
