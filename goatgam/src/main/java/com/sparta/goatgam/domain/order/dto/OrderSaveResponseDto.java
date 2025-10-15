package com.sparta.goatgam.domain.order.dto;

import java.util.UUID;

public record OrderSaveResponseDto(UUID orderId, String message) {}
