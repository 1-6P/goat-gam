package com.sparta.goatgam.domain.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ReviewRequestDto {
    @Min(1)
    @Max(5)
    private int rate;

    @NotNull
    private String content;

    private String review_image;
}