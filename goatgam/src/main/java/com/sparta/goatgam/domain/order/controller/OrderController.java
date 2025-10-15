package com.sparta.goatgam.domain.order.controller;

import com.sparta.goatgam.domain.order.dto.*;
import com.sparta.goatgam.domain.order.service.OrderService;
import com.sparta.goatgam.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/order")
@Tag(name = "주문 내역 API", description = "주문 내역 관련 기능 API입니다.")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "내 주문 내역 목록 조회", description = "내 주문 내역 전체 목록을 조회합니다.")
    @GetMapping("")
    public ResponseEntity<PagedModel<OrderSummaryResponseDto>> getMyOrderSummary(
            @Parameter(description = "페이지 번호, 0부터 시작", example = "0")
            @RequestParam int page,

            @Parameter(description = "한 페이지 내 아이템 개수, 10/30/50 이외의 값은 10으로 고정", example = "10")
            @RequestParam int size,

            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return ResponseEntity.ok(orderService.getMyOrderSummary(page, size, userDetails.getUser()));
    }

    @GetMapping("/all")
    public ResponseEntity<PagedModel<AdminOrderSummaryResponseDto>> getUserOrderSummary(
            @Parameter(description = "조회할 userId. nullable", schema = @Schema(nullable = true))
            @RequestParam(required = false) Long userId,

            @Parameter(description = "페이지 번호, 0부터 시작", example = "0")
            @RequestParam int page,

            @Parameter(description = "한 페이지 내 아이템 개수, 10/30/50 이외의 값은 10으로 고정", example = "10")
            @RequestParam int size) {

        return ResponseEntity.ok(orderService.getUserOrderSummary(userId, page, size));
    }

    @Operation(summary = "주문 내역 상세 조회", description = "주문 내역 단건의 상세 정보를 조회합니다.")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailResponseDto> getOrderDetail(
            @Parameter(description = "주문 내역 ID")
            @PathVariable UUID orderId) {

        return ResponseEntity.ok(orderService.getOrderDetail(orderId));
    }

    @Operation(summary = "장바구니로 주문 등록", description = "장바구니 아이디로 주문요청을 한다.")
    @PostMapping("")
    public ResponseEntity<OrderSaveResponseDto> addOrder(@AuthenticationPrincipal UserDetailsImpl principal, @RequestBody OrderRequestContentDto body) {
        return ResponseEntity.ok(orderService.addOrder(principal.getUser(), body.request()));
    }
}