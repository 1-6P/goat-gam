package com.sparta.goatgam.domain.follow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FollowResponseDto {
    private boolean success;
    private String message;
}
