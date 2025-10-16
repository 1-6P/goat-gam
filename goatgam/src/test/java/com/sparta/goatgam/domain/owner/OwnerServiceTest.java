package com.sparta.goatgam.domain.owner;

import com.sparta.goatgam.domain.order.dto.OrderDetailResponseDto;
import com.sparta.goatgam.domain.order.entity.Order;
import com.sparta.goatgam.domain.order.entity.StatusEnum;
import com.sparta.goatgam.domain.order.repository.OrderRepository;
import com.sparta.goatgam.domain.owner.service.OwnerService;
import com.sparta.goatgam.domain.restaurant.entity.Restaurant;
import com.sparta.goatgam.domain.restaurant.repository.RestaurantRepository;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OwnerServiceTest {

    @Mock private RestaurantRepository restaurantRepository;
    @Mock private OrderRepository orderRepository;
    @InjectMocks private OwnerService ownerService;

    private final UUID restaurantId = UUID.randomUUID();
    private final UUID orderId = UUID.randomUUID();
    private final User owner = mock(User.class);
    private final Restaurant restaurant = mock(Restaurant.class);
    private final Order order = mock(Order.class);

    @BeforeEach
    void setUp() {
        when(owner.getUserId()).thenReturn(10L);
        when(restaurant.getUser()).thenReturn(owner);
        when(restaurant.getRestaurantId()).thenReturn(restaurantId);
        when(order.getRestaurant()).thenReturn(restaurant);
        when(order.getOrderId()).thenReturn(orderId);
    }

    // ================================================================
    @Nested
    @DisplayName("getOrder() - 주문 목록 조회")
    class GetOrder {

        @Test
        @DisplayName("성공: 상태 전체 조회")
        void success_getAllOrders() {
            Page<Order> orderPage = new PageImpl<>(List.of(order));
            given(restaurantRepository.findById(restaurantId)).willReturn(Optional.of(restaurant));
            given(orderRepository.findOrderByRestaurant(eq(restaurant), any(Pageable.class))).willReturn(orderPage);

            var result = ownerService.getOrder(restaurantId, owner, 0, 10, Sort.Direction.DESC, null);

            assertThat(result).isNotNull();
            verify(orderRepository).findOrderByRestaurant(eq(restaurant), any(Pageable.class));
        }

        @Test
        @DisplayName("성공: 특정 상태 조회 (StatusEnum.Accept)")
        void success_getOrdersByStatus() {
            Page<Order> orderPage = new PageImpl<>(List.of(order));
            given(restaurantRepository.findById(restaurantId)).willReturn(Optional.of(restaurant));
            given(orderRepository.findByRestaurantAndStatus(eq(restaurant), eq(StatusEnum.Accept), any(Pageable.class)))
                    .willReturn(orderPage);

            var result = ownerService.getOrder(restaurantId, owner, 0, 10, Sort.Direction.DESC, StatusEnum.Accept);

            assertThat(result).isNotNull();
            verify(orderRepository).findByRestaurantAndStatus(eq(restaurant), eq(StatusEnum.Accept), any(Pageable.class));
        }

        @Test
        @DisplayName("실패: 식당 없음 (RESTAURANT_NOT_FOUND)")
        void fail_restaurantNotFound() {
            given(restaurantRepository.findById(restaurantId)).willReturn(Optional.empty());

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> ownerService.getOrder(restaurantId, owner, 0, 10, Sort.Direction.DESC, null));

            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.RESTAURANT_NOT_FOUND);
        }

        @Test
        @DisplayName("실패: 권한 없음 (FORBIDDEN_RESTAURANT)")
        void fail_forbiddenRestaurant() {
            User other = mock(User.class);
            when(other.getUserId()).thenReturn(99L);
            given(restaurantRepository.findById(restaurantId)).willReturn(Optional.of(restaurant));

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> ownerService.getOrder(restaurantId, other, 0, 10, Sort.Direction.DESC, null));

            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.FORBIDDEN_RESTAURANT);
        }
    }

    // ================================================================
    @Nested
    @DisplayName("getOrderDetail() - 주문 상세 조회")
    class GetOrderDetail {

        @Test
        @DisplayName("성공: 주문 상세 조회 성공")
        void success_getOrderDetail() {
            given(restaurantRepository.findById(restaurantId)).willReturn(Optional.of(restaurant));
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
            when(order.getRestaurant()).thenReturn(restaurant);

            OrderDetailResponseDto result = ownerService.getOrderDetail(restaurantId, orderId, owner);

            assertThat(result).isNotNull();
            verify(orderRepository).findById(orderId);
        }

        @Test
        @DisplayName("실패: 식당 없음 (RESTAURANT_NOT_FOUND)")
        void fail_restaurantNotFound() {
            given(restaurantRepository.findById(restaurantId)).willReturn(Optional.empty());

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> ownerService.getOrderDetail(restaurantId, orderId, owner));

            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.RESTAURANT_NOT_FOUND);
        }

        @Test
        @DisplayName("실패: 주문 없음 (ORDER_NOT_FOUND)")
        void fail_orderNotFound() {
            given(restaurantRepository.findById(restaurantId)).willReturn(Optional.of(restaurant));
            given(orderRepository.findById(orderId)).willReturn(Optional.empty());

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> ownerService.getOrderDetail(restaurantId, orderId, owner));

            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.ORDER_NOT_FOUND);
        }

        @Test
        @DisplayName("실패: 주문이 다른 식당 소속 (FOOD_INPUT_ERROR)")
        void fail_wrongRestaurant() {
            Restaurant anotherRestaurant = mock(Restaurant.class);
            when(anotherRestaurant.getRestaurantId()).thenReturn(UUID.randomUUID());

            given(restaurantRepository.findById(restaurantId)).willReturn(Optional.of(restaurant));
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
            when(order.getRestaurant()).thenReturn(anotherRestaurant);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> ownerService.getOrderDetail(restaurantId, orderId, owner));

            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.FOOD_INPUT_ERROR);
        }

        @Test
        @DisplayName("실패: 권한 없음 (FORBIDDEN_RESTAURANT)")
        void fail_forbiddenUser() {
            User other = mock(User.class);
            when(other.getUserId()).thenReturn(99L);

            given(restaurantRepository.findById(restaurantId)).willReturn(Optional.of(restaurant));
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
            when(order.getRestaurant()).thenReturn(restaurant);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> ownerService.getOrderDetail(restaurantId, orderId, other));

            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.FORBIDDEN_RESTAURANT);
        }
    }

    // ================================================================
    @Nested
    @DisplayName("changeOrderStatus() - 주문 상태 변경")
    class ChangeOrderStatus {

        @Test
        @DisplayName("성공: 주문 상태 변경 (Request → Accept)")
        void success_changeOrderStatus() {
            when(order.getStatus()).thenReturn(StatusEnum.Request);

            given(restaurantRepository.findById(restaurantId)).willReturn(Optional.of(restaurant));
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

            MessageAndIdResponseDto result = ownerService.ChangeOrderStatus(restaurantId, orderId, owner, StatusEnum.Accept);

            assertThat(result.getMessage()).isEqualTo("success");
            verify(order).changeStatus(StatusEnum.Accept);
        }

        @Test
        @DisplayName("실패: 권한 없음 (FORBIDDEN_RESTAURANT)")
        void fail_forbiddenUser() {
            User other = mock(User.class);
            when(other.getUserId()).thenReturn(99L);

            given(restaurantRepository.findById(restaurantId)).willReturn(Optional.of(restaurant));
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> ownerService.ChangeOrderStatus(restaurantId, orderId, other, StatusEnum.Accept));

            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.FORBIDDEN_RESTAURANT);
        }

        @Test
        @DisplayName("실패: 상태 전이 불가 (ORDER_ALREADY_PROCESSED)")
        void fail_alreadyProcessed() {
            when(order.getStatus()).thenReturn(StatusEnum.Accept);
            given(restaurantRepository.findById(restaurantId)).willReturn(Optional.of(restaurant));
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> ownerService.ChangeOrderStatus(restaurantId, orderId, owner, StatusEnum.Accept));

            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.ORDER_ALREADY_PROCESSED);
        }

        @Test
        @DisplayName("실패: Prepared 이전 단계 없음 (ORDER_ACCEPT_REQUIRED)")
        void fail_preparedWithoutAccept() {
            when(order.getStatus()).thenReturn(StatusEnum.Request);
            given(restaurantRepository.findById(restaurantId)).willReturn(Optional.of(restaurant));
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> ownerService.ChangeOrderStatus(restaurantId, orderId, owner, StatusEnum.Prepared));

            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.ORDER_ACCEPT_REQUIRED);
        }

        @Test
        @DisplayName("실패: OnDelivery 이전 단계 없음 (ORDER_PREPARED_REQUIRED)")
        void fail_deliveryWithoutPrepared() {
            when(order.getStatus()).thenReturn(StatusEnum.Accept);
            given(restaurantRepository.findById(restaurantId)).willReturn(Optional.of(restaurant));
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> ownerService.ChangeOrderStatus(restaurantId, orderId, owner, StatusEnum.OnDelivery));

            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.ORDER_PREPARED_REQUIRED);
        }

        @Test
        @DisplayName("실패: Completed 이전 단계 없음 (ORDER_DELIVERY_REQUIRED)")
        void fail_completedWithoutDelivery() {
            when(order.getStatus()).thenReturn(StatusEnum.Prepared);
            given(restaurantRepository.findById(restaurantId)).willReturn(Optional.of(restaurant));
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> ownerService.ChangeOrderStatus(restaurantId, orderId, owner, StatusEnum.Completed));

            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.ORDER_DELIVERY_REQUIRED);
        }
    }
}

