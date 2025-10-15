package com.sparta.goatgam.domain.owner.controller;

import com.sparta.goatgam.domain.order.dto.OrderDetailResponseDto;
import com.sparta.goatgam.domain.order.dto.OrderSummaryResponseDto;
import com.sparta.goatgam.domain.order.entity.StatusEnum;
import com.sparta.goatgam.domain.owner.service.OwnerService;
import com.sparta.goatgam.global.dto.MessageAndIdResponseDto;
import com.sparta.goatgam.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
    public ResponseEntity<PagedModel<OrderSummaryResponseDto>> getOrders(@PathVariable UUID restaurantId,
                                      @AuthenticationPrincipal UserDetailsImpl userDetails,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size,
                                      @RequestParam(defaultValue = "DESC") Sort.Direction sort,
                                      @RequestParam(required = false) StatusEnum status) {
        return ResponseEntity.ok(ownerService.getOrder(restaurantId, userDetails.getUser(), page, size, sort, status));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailResponseDto> getOrder(@PathVariable UUID restaurantId,
                                                           @PathVariable UUID orderId,
                                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(ownerService.getOrderDetail(restaurantId, orderId , userDetails.getUser()));
    }

    @PostMapping("/{orderId}/accept")
    public MessageAndIdResponseDto acceptOrder(@PathVariable UUID restaurantId,
                                               @PathVariable UUID orderId,
                                               @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ownerService.ChangeOrderStatus(restaurantId, orderId, userDetails.getUser(), StatusEnum.Accept);
    }

    @PostMapping("/{orderId}/reject")
    public MessageAndIdResponseDto rejectOrder(@PathVariable UUID restaurantId,
                                               @PathVariable UUID orderId,
                                               @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ownerService.ChangeOrderStatus(restaurantId, orderId, userDetails.getUser(), StatusEnum.Reject);
    }

    @PostMapping("/{orderId}/prepared")
    public MessageAndIdResponseDto prepareOrder(@PathVariable UUID restaurantId,
                                                @PathVariable UUID orderId,
                                                @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ownerService.ChangeOrderStatus(restaurantId, orderId, userDetails.getUser(), StatusEnum.Prepared);
    }

    @PostMapping("/{orderId}/delivery")
    public MessageAndIdResponseDto deliveryOrder(@PathVariable UUID restaurantId,
                                                 @PathVariable UUID orderId,
                                                 @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ownerService.ChangeOrderStatus(restaurantId, orderId, userDetails.getUser(), StatusEnum.OnDelivery);
    }

    @PostMapping("/{orderId}/complete")
    public MessageAndIdResponseDto completeOrder(@PathVariable UUID restaurantId,
                                                 @PathVariable UUID orderId,
                                                 @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ownerService.ChangeOrderStatus(restaurantId, orderId, userDetails.getUser(), StatusEnum.Completed);
    }
}
