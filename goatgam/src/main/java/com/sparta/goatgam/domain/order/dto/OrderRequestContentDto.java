package com.sparta.goatgam.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record OrderRequestContentDto(
        @Schema(description = "주문 요청사항", example = "고수 빼주세요. 리뷰이벤트 할게요.")
        String request
) {
}
