package com.sparta.goatgam.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserUpdateResponseDto {
    private String message;
    private Long userId;
}
