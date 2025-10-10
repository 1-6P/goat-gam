package com.sparta.goatgam.domain.address.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AddressDeleteResponseDto {
    private Long userId;
    private UUID id;
    private boolean status;
    private String message;

    public AddressDeleteResponseDto(Long userId, UUID id, boolean status, String message) {
        this.userId = userId;
        this.id = id;
        this.status = status;
        this.message = message;
    }
}
