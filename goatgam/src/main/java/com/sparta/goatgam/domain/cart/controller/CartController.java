package com.sparta.goatgam.domain.cart.controller;

import com.sparta.goatgam.domain.cart.dto.CartFoodRequestDto;
import com.sparta.goatgam.domain.cart.service.CartService;
import com.sparta.goatgam.global.dto.MessageAndIdResponseDto;
import com.sparta.goatgam.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cart")
@Tag(name = "장바구니 API", description = "장바구니 관련 기능 API입니다.")
public class CartController {

    private final CartService cartService;

    @Operation(summary = "장바구니에 음식 추가", description = "장바구니에 음식을 추가합니다. 하나의 가게의 음식만 담을 수 있고 " +
            "새로운 가게의 음식을 담으면 이전 장바구니는 삭제되고 새로운 장바구니가 생성됩니다.")
    @PostMapping("")
    public ResponseEntity<MessageAndIdResponseDto> addCartFood(
            @Valid @RequestBody CartFoodRequestDto cartFoodRequestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return ResponseEntity.ok(cartService.addCartFood(cartFoodRequestDto, userDetails.getUser()));
    }
}
