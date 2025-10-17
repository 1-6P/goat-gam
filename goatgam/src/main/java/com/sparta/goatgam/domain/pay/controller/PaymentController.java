package com.sparta.goatgam.domain.pay.controller;

import com.sparta.goatgam.domain.pay.dto.PaymentConfirmRequestDto;
import com.sparta.goatgam.domain.pay.dto.PaymentResponseDto;
import com.sparta.goatgam.domain.pay.dto.PaymentVerifyRequestDto;
import com.sparta.goatgam.domain.pay.service.PaymentService;
import com.sparta.goatgam.global.dto.MessageAndIdResponseDto;
import com.sparta.goatgam.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/payment")
@Tag(name = "결제 API", description = "결제 관련 기능 API입니다.")
public class PaymentController {
    private final PaymentService paymentService;

    @Operation(summary = "결제 검증", description = "주문금액과 결제 금액이 같은지 확인")
    @PostMapping("/verify-amount")
    public MessageAndIdResponseDto verifyAmount(@RequestBody PaymentVerifyRequestDto paymentVerifyRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return paymentService.verifyAmount(paymentVerifyRequestDto, userDetails.getUser());
    }

    @Operation(summary = "결제 승인", description = "결제를 승인")
    @PostMapping("/confirm")
    public MessageAndIdResponseDto confirmPayment(@RequestBody PaymentConfirmRequestDto paymentConfirmRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return paymentService.confirmPayment(paymentConfirmRequestDto, userDetails.getUser());
    }

    @Operation(summary = "결제 취소", description = "결제 후 5분 내로 취소 가능(주문자와 동일한 경우)")
    @PatchMapping("/cancel")
    public MessageAndIdResponseDto cancelPayment(@RequestParam UUID orderId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return paymentService.cancelPayment(orderId, userDetails.getUser());
    }

    @Operation(summary = "환불", description = "결제를 환불처리(주문한 가게의 사장님만)")
    @PatchMapping("/refund")
    public MessageAndIdResponseDto refundPayment(@RequestBody PaymentVerifyRequestDto dto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return paymentService.refundPayment(dto, userDetails.getUser());
    }

    @Operation(summary = "결제 내역 조희", description = "결제 내역을 조회합니다.")
    @GetMapping("")
    public ResponseEntity<PagedModel<PaymentResponseDto>> getPaymentList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ASC") String direction,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return ResponseEntity.ok(paymentService.getPaymentList(page, size, direction, userDetails.getUser()));
    }
}