package com.sparta.goatgam.domain.address.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AddressDeleteResponseDto {
    private Long userId;
    private UUID id;
    private String message;

    public AddressDeleteResponseDto(Long userId, UUID id, String message) {
        this.userId = userId;
        this.id = id;
        this.message = message;
    }
}
