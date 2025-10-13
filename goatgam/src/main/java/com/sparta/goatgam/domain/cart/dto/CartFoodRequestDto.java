package com.sparta.goatgam.domain.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;
import java.util.UUID;

public record CartFoodRequestDto(
        @Schema(description = "주문할 식당 ID", example = "44d76b89-8452-4db2-af63-f709531b8d5a")
        @NotNull(message = "restaurantId는 필수입니다.")
        UUID restaurantId,

        @Schema(description = "장바구니에 담을 음식 ID", example = "c9ad3757-acef-42ff-af9d-229487d039a1")
        @NotNull(message = "foodId는 필수입니다.")
        UUID foodId,

        @Schema(description = "음식의 양", example = "1")
        @Positive(message = "수량은 1개 이상이어야 합니다.")
        int quantity,

        @Schema(description = "리스트 형태의 옵션 ID", example = "[\"d762ba88-e18f-4663-8ea8-c48506e28d52\", \"66ada86a-dc3f-4808-963b-0dfc141afe83\"]")
        List<UUID> options
) {
}