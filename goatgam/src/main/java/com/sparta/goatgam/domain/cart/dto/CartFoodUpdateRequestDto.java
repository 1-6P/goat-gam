package com.sparta.goatgam.domain.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record CartFoodUpdateRequestDto(
        @Schema(description = "증감 옵션입니다. \"INCREASE\" 또는 \"DECREASE\"를 입력하세요.", example = "INCREASE")
        QuantityUpdateTypeEnum updateType
) {
}