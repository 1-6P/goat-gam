package com.sparta.goatgam.domain.address.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AddressDeleteResponseDto {
    private Long userId;
    private UUID id;
    private boolean status;
    private LocalDateTime deletedAt;

    public AddressDeleteResponseDto(Long userId, UUID id, boolean b, LocalDateTime deletedAt) {
        this.userId = userId;
        this.id = id;
        this.status = b;
        this.deletedAt = deletedAt;
    }
}
