package com.sparta.goatgam.domain.Pay;


import com.sparta.goatgam.domain.order.entity.Order;
import com.sparta.goatgam.domain.order.entity.StatusEnum;
import com.sparta.goatgam.domain.order.repository.OrderRepository;
import com.sparta.goatgam.domain.pay.dto.PaymentConfirmRequestDto;
import com.sparta.goatgam.domain.pay.dto.PaymentVerifyRequestDto;
import com.sparta.goatgam.domain.pay.entity.Payment;
import com.sparta.goatgam.domain.pay.entity.paymentStatusEnum;
import com.sparta.goatgam.domain.pay.repository.PaymentRepository;
import com.sparta.goatgam.domain.pay.service.PaymentService;
import com.sparta.goatgam.domain.restaurant.entity.Restaurant;
import com.sparta.goatgam.domain.user.entity.User;
import com.sparta.goatgam.global.dto.MessageAndIdResponseDto;
import com.sparta.goatgam.global.exception.BusinessException;
import com.sparta.goatgam.global.exception.ExceptionCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PaymentServiceTest {

    @Mock private PaymentRepository paymentRepository;
    @Mock private OrderRepository orderRepository;
    @InjectMocks private PaymentService paymentService;

    private final UUID orderId = UUID.randomUUID();
    private final User user = mock(User.class);
    private final Restaurant restaurant = mock(Restaurant.class);
    private final Order order = mock(Order.class);
    private final Payment payment = mock(Payment.class);

    @BeforeEach
    void setUp() {
        when(user.getUserId()).thenReturn(1L);
        when(order.getUser()).thenReturn(user);
        when(order.getOrderId()).thenReturn(orderId);
        when(order.getRestaurant()).thenReturn(restaurant);
        when(restaurant.getUser()).thenReturn(user);
        when(payment.getApprovedAt()).thenReturn(LocalDateTime.now());
        when(payment.getAmount()).thenReturn(BigDecimal.valueOf(10000));
    }

    // ================================================================
    @Nested
    @DisplayName("varifyAmount() - 결제 금액 검증")
    class VerifyAmount {

        @Test
        @DisplayName("성공: 금액 일치")
        void success_verify() {
            PaymentVerifyRequestDto dto = new PaymentVerifyRequestDto(orderId, BigDecimal.valueOf(10000));

            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
            when(order.getTotalPrice()).thenReturn(BigDecimal.valueOf(10000));

            MessageAndIdResponseDto result = paymentService.verifyAmount(dto, user);

            assertThat(result.getMessage()).isEqualTo("varify success");
        }

        @Test
        @DisplayName("실패: 잘못된 사용자")
        void fail_invalidUser() {
            User other = mock(User.class);
            when(other.getUserId()).thenReturn(99L);
            PaymentVerifyRequestDto dto = new PaymentVerifyRequestDto(orderId, BigDecimal.valueOf(10000));

            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> paymentService.verifyAmount(dto, other));

            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.INVALID_USER);
        }

        @Test
        @DisplayName("실패: 금액 불일치 (PG_AMOUNT_INCORRECT)")
        void fail_amountIncorrect() {
            PaymentVerifyRequestDto dto = new PaymentVerifyRequestDto(orderId, BigDecimal.valueOf(9999));

            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
            when(order.getTotalPrice()).thenReturn(BigDecimal.valueOf(10000));

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> paymentService.verifyAmount(dto, user));

            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.PG_AMOUNT_INCORRECT);
        }
    }

    // ================================================================
    @Nested
    @DisplayName("confirmPayment() - 결제 승인")
    class ConfirmPayment {

        @Test
        @DisplayName("성공: 결제 성공(Mock 처리)")
        void success_confirmPayment() {
            PaymentConfirmRequestDto dto = new PaymentConfirmRequestDto("MOCK_KEY", orderId, BigDecimal.valueOf(10000));

            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
            when(order.getTotalPrice()).thenReturn(BigDecimal.valueOf(10000));
            given(paymentRepository.save(any(Payment.class))).willAnswer(inv -> inv.getArgument(0));

            MessageAndIdResponseDto result = paymentService.confirmPayment(dto, user);

            assertThat(result.getMessage()).isEqualTo("mock payment success");
            verify(paymentRepository).save(any(Payment.class));
        }

        @Test
        @DisplayName("실패: 사용자 불일치 (INVALID_USER)")
        void fail_invalidUser() {
            User other = mock(User.class);
            when(other.getUserId()).thenReturn(99L);

            PaymentConfirmRequestDto dto = new PaymentConfirmRequestDto("MOCK_KEY", orderId, BigDecimal.valueOf(10000));
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> paymentService.confirmPayment(dto, other));

            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.INVALID_USER);
        }

        @Test
        @DisplayName("실패: 금액 불일치 (PG_AMOUNT_INCORRECT)")
        void fail_amountIncorrect() {
            PaymentConfirmRequestDto dto = new PaymentConfirmRequestDto("MOCK_KEY", orderId, BigDecimal.valueOf(9000));
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
            when(order.getTotalPrice()).thenReturn(BigDecimal.valueOf(10000));

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> paymentService.confirmPayment(dto, user));

            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.PG_AMOUNT_INCORRECT);
        }
    }

    // ================================================================
    @Nested
    @DisplayName("cancelPayment() - 결제 취소")
    class CancelPayment {

        @Test
        @DisplayName("성공: 5분 이내 취소")
        void success_cancelPayment() {
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
            given(paymentRepository.findPaymentByOrder(order)).willReturn(Optional.of(payment));

            MessageAndIdResponseDto result = paymentService.cancelPayment(orderId, user);

            assertThat(result.getMessage()).isEqualTo("payment cancel success");
            verify(payment).updateStatus(paymentStatusEnum.Canceled);
            verify(order).changeStatus(StatusEnum.Cancel);
        }

        @Test
        @DisplayName("실패: 결제 정보 없음 (PG_NOT_FOUND)")
        void fail_paymentNotFound() {
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
            given(paymentRepository.findPaymentByOrder(order)).willReturn(Optional.empty());

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> paymentService.cancelPayment(orderId, user));

            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.PG_NOT_FOUND);
        }

        @Test
        @DisplayName("실패: 잘못된 사용자 (FORBIDDEN_ORDER)")
        void fail_forbiddenUser() {
            User other = mock(User.class);
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
            given(paymentRepository.findPaymentByOrder(order)).willReturn(Optional.of(payment));
            when(order.getUser()).thenReturn(user);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> paymentService.cancelPayment(orderId, other));

            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.FORBIDDEN_ORDER);
        }

        @Test
        @DisplayName("실패: 5분 초과 (PG_CANCEL_TIMEOUT)")
        void fail_cancelTimeout() {
            Payment oldPayment = mock(Payment.class);
            when(oldPayment.getApprovedAt()).thenReturn(LocalDateTime.now().minusMinutes(10));

            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
            given(paymentRepository.findPaymentByOrder(order)).willReturn(Optional.of(oldPayment));

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> paymentService.cancelPayment(orderId, user));

            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.PG_CANCEL_TIMEOUT);
        }
    }

    // ================================================================
    @Nested
    @DisplayName("refundPayment() - 결제 환불")
    class RefundPayment {

        @Test
        @DisplayName("성공: 환불 성공")
        void success_refund() {
            PaymentVerifyRequestDto dto = new PaymentVerifyRequestDto(orderId, BigDecimal.valueOf(5000));

            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
            given(paymentRepository.findPaymentByOrder(order)).willReturn(Optional.of(payment));
            when(payment.getPaymentStatus()).thenReturn(paymentStatusEnum.Done);
            when(payment.getAmount()).thenReturn(BigDecimal.valueOf(10000));

            MessageAndIdResponseDto result = paymentService.refundPayment(dto, user);

            assertThat(result.getMessage()).isEqualTo("refund success");
            verify(payment).updateStatus(paymentStatusEnum.Refunded);
            verify(order).changeStatus(StatusEnum.Refund);
        }

        @Test
        @DisplayName("실패: 권한 없음 (FORBIDDEN_ORDER_REFUND)")
        void fail_forbiddenUser() {
            User other = mock(User.class);
            when(restaurant.getUser()).thenReturn(user);
            when(other.getUserId()).thenReturn(99L);

            PaymentVerifyRequestDto dto = new PaymentVerifyRequestDto(orderId, BigDecimal.valueOf(5000));
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
            given(paymentRepository.findPaymentByOrder(order)).willReturn(Optional.of(payment));

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> paymentService.refundPayment(dto, other));

            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.FORBIDDEN_ORDER_REFUND);
        }

        @Test
        @DisplayName("실패: 상태가 Done이 아님 (PG_CANT_REFUND)")
        void fail_wrongStatus() {
            PaymentVerifyRequestDto dto = new PaymentVerifyRequestDto(orderId, BigDecimal.valueOf(5000));
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
            given(paymentRepository.findPaymentByOrder(order)).willReturn(Optional.of(payment));
            when(payment.getPaymentStatus()).thenReturn(paymentStatusEnum.Canceled);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> paymentService.refundPayment(dto, user));

            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.PG_CANT_REFUND);
        }

        @Test
        @DisplayName("실패: 환불 금액 초과 (PG_REFUND_AMOUNT_INCORRECT)")
        void fail_refundAmountIncorrect() {
            PaymentVerifyRequestDto dto = new PaymentVerifyRequestDto(orderId, BigDecimal.valueOf(15000));
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
            given(paymentRepository.findPaymentByOrder(order)).willReturn(Optional.of(payment));
            when(payment.getPaymentStatus()).thenReturn(paymentStatusEnum.Done);
            when(payment.getAmount()).thenReturn(BigDecimal.valueOf(10000));

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> paymentService.refundPayment(dto, user));

            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.PG_REFUND_AMOUNT_INCORRECT);
        }
    }
}

