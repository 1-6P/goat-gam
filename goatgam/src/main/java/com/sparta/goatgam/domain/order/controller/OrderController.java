package com.sparta.goatgam.domain.order.controller;

import com.sparta.goatgam.domain.order.dto.OrderSummaryResponseDto;
import com.sparta.goatgam.domain.order.service.OrderService;
import com.sparta.goatgam.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/order")
@Tag(name = "주문 내역 API", description = "주문 내역 관련 기능 API입니다.")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "내 주문 내역 목록 조회", description = "내 주문 내역 전체 목록을 조회합니다.")

    @GetMapping("")
    public PagedModel<OrderSummaryResponseDto> getMyOrderSummary(
            @Parameter(description = "페이지 번호, 0부터 시작", example = "0")
            @RequestParam int page,

            @Parameter(description = "한 페이지 내 아이템 개수, 10/30/50 이외의 값은 10으로 고정", example = "10")
            @RequestParam int size,

            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return orderService.getMyOrderSummary(page, size, userDetails.getUser());
    }

    @GetMapping("/all")
    public ResponseEntity<PagedModel<OrderSummaryResponseDto>> getUserOrderSummary(
            @Parameter(description = "조회할 userId. nullable", schema = @Schema(nullable = true))
            @RequestParam(required = false) Long userId,

            @Parameter(description = "페이지 번호, 0부터 시작", example = "0")
            @RequestParam int page,

            @Parameter(description = "한 페이지 내 아이템 개수, 10/30/50 이외의 값은 10으로 고정", example = "10")
            @RequestParam int size) {

        return ResponseEntity.ok(orderService.getUserOrderSummary(userId, page, size));
    }
}
