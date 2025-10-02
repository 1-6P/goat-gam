package com.sparta.goatgam.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record UserDeleteResponseDto (
        String message,
        Long userId,
        int status,
        @JsonProperty("deletedAt")LocalDateTime deletedAt
        ) {}
