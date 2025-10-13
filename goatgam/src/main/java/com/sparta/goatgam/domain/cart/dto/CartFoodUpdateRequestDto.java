package com.sparta.goatgam.domain.cart.dto;

import jakarta.annotation.Nullable;

import java.util.List;
import java.util.UUID;

public record CartFoodUpdateRequestDto(
        UUID cartFoodId,
        @Nullable
        List<UUID> changeOptionList
) {
}
