package com.sparta.goatgam.domain.owner.controller;

import com.sparta.goatgam.domain.order.dto.OrderDetailResponseDto;
import com.sparta.goatgam.domain.order.dto.OrderSummaryResponseDto;
import com.sparta.goatgam.domain.order.entity.StatusEnum;
import com.sparta.goatgam.domain.owner.service.OwnerService;
import com.sparta.goatgam.global.dto.MessageAndIdResponseDto;
import com.sparta.goatgam.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/restaurant/{restaurantId}/order")
@RequiredArgsConstructor
@Tag(name = "주문 처리 API", description = "사장님의 주문 처리 관련 기능 API입니다.")
public class OwnerController {
    private final OwnerService ownerService;

    @GetMapping()
    @Operation(summary = "주문 목록 조회", description = "주인이 본인 가게의 주문 목록을 확인 가능")
    public ResponseEntity<PagedModel<OrderSummaryResponseDto>> getOrders(@PathVariable UUID restaurantId,
                                      @AuthenticationPrincipal UserDetailsImpl userDetails,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size,
                                      @RequestParam(defaultValue = "DESC") Sort.Direction sort,
                                      @RequestParam(required = false) StatusEnum status) {
        return ResponseEntity.ok(ownerService.getOrder(restaurantId, userDetails.getUser(), page, size, sort, status));
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "주문 단건 조회", description = "주인이 본인 가게의 주문의 상세정보를 확인 가능")
    public ResponseEntity<OrderDetailResponseDto> getOrder(@PathVariable UUID restaurantId,
                                                           @PathVariable UUID orderId,
                                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(ownerService.getOrderDetail(restaurantId, orderId , userDetails.getUser()));
    }

    @PatchMapping("/{orderId}/accept")
    @Operation(summary = "주문 수락", description = "request상태의 주문을 수락 처리")
    public MessageAndIdResponseDto acceptOrder(@PathVariable UUID restaurantId,
                                               @PathVariable UUID orderId,
                                               @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ownerService.ChangeOrderStatus(restaurantId, orderId, userDetails.getUser(), StatusEnum.Accept);
    }

    @PatchMapping("/{orderId}/reject")
    @Operation(summary = "주문 거절", description = "request상태의 주문을 거절 처리")
    public MessageAndIdResponseDto rejectOrder(@PathVariable UUID restaurantId,
                                               @PathVariable UUID orderId,
                                               @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ownerService.ChangeOrderStatus(restaurantId, orderId, userDetails.getUser(), StatusEnum.Reject);
    }

    @PatchMapping("/{orderId}/prepared")
    @Operation(summary = "준비 완료", description = "accept상태의 주문을 준비완료 처리")
    public MessageAndIdResponseDto prepareOrder(@PathVariable UUID restaurantId,
                                                @PathVariable UUID orderId,
                                                @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ownerService.ChangeOrderStatus(restaurantId, orderId, userDetails.getUser(), StatusEnum.Prepared);
    }

    @PatchMapping("/{orderId}/delivery")
    @Operation(summary = "배달중", description = "prepared상태의 주문을 배달중 처리")
    public MessageAndIdResponseDto deliveryOrder(@PathVariable UUID restaurantId,
                                                 @PathVariable UUID orderId,
                                                 @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ownerService.ChangeOrderStatus(restaurantId, orderId, userDetails.getUser(), StatusEnum.OnDelivery);
    }

    @PatchMapping("/{orderId}/complete")
    @Operation(summary = "배달 완료", description = "delivery상태의 주문을 배달 완료 처리")
    public MessageAndIdResponseDto completeOrder(@PathVariable UUID restaurantId,
                                                 @PathVariable UUID orderId,
                                                 @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ownerService.ChangeOrderStatus(restaurantId, orderId, userDetails.getUser(), StatusEnum.Completed);
    }
}
