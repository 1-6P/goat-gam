package com.sparta.goatgam.domain.order;

import com.sparta.goatgam.domain.cart.entity.Cart;
import com.sparta.goatgam.domain.cart.entity.CartFood;
import com.sparta.goatgam.domain.cart.entity.CartFoodOption;
import com.sparta.goatgam.domain.cart.repository.CartRepository;
import com.sparta.goatgam.domain.order.dto.OrderDetailResponseDto;
import com.sparta.goatgam.domain.order.dto.OrderSaveResponseDto;
import com.sparta.goatgam.domain.order.entity.Order;
import com.sparta.goatgam.domain.order.repository.OrderRepository;
import com.sparta.goatgam.domain.order.service.OrderService;
import com.sparta.goatgam.domain.owner.entity.Food;
import com.sparta.goatgam.domain.owner.entity.FoodOption;
import com.sparta.goatgam.domain.restaurant.entity.Restaurant;
import com.sparta.goatgam.domain.user.entity.User;
import com.sparta.goatgam.domain.user.repository.UserRepository;
import com.sparta.goatgam.global.exception.BusinessException;
import com.sparta.goatgam.global.exception.ExceptionCode;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
public class OrderServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private CartRepository cartRepository;
    @InjectMocks
    private OrderService orderService;

    @Nested
    class AddOrder {
        @Test
        @DisplayName("주문 생성 성공: Cart에 담긴 상품으로 주문 저장")
        void order_add_success() {

            //given 세팅

            User user = mock(User.class);
            Cart cart = mock(Cart.class);
            CartFood cartFood = mock(CartFood.class);
            CartFoodOption cartFoodOption = mock(CartFoodOption.class);

            when(cartRepository.findByUserAndIsDeletedFalse(user))
                    .thenReturn(Optional.of(cart));
            when(cart.getCartFoods()).thenReturn(List.of(cartFood));
            when(cart.getRestaurant()).thenReturn(mock(Restaurant.class));

            //가격 및 옵션 설정
            var food = mock(Food.class);
            when(cartFood.getFood()).thenReturn(food);
            when(food.getFoodPrice()).thenReturn(BigDecimal.valueOf(10000));
            when(cartFood.getQuantity()).thenReturn(1);
            when(cartFood.getCartFoodOptions()).thenReturn(List.of(cartFoodOption));

            var foodOption = mock(FoodOption.class);
            when(cartFoodOption.getFoodOption()).thenReturn(foodOption);
            when(foodOption.getSurcharge()).thenReturn(BigDecimal.valueOf(1000));

            when(user.getAddress()).thenReturn("서울시 광화문로 113길 10-12");
            when(user.getNickname()).thenReturn("배고픈 테스터1");

            // when
            OrderSaveResponseDto response = orderService.addOrder(user, "테스트 요청사항");

            //then
            assertNotNull(response);
            assertEquals("주문내역이 저장되었습니다.", response.message());
            verify(orderRepository, times(1)).save(any());
            verify(cartRepository, times(1)).findByUserAndIsDeletedFalse(user);

        }

        @Test
        @DisplayName("주문 실패 -장바구니가 비어있을 경우엔 실패")
        void order_add_fail() {
            // given
            User user = mock(User.class);
            when(cartRepository.findByUserAndIsDeletedFalse(user))
                    .thenReturn(Optional.empty());

            // when & then
            assertThrows(BusinessException.class,
                    () -> orderService.addOrder(user, "요청사항"));

            verify(orderRepository, never()).save(any());

        }

    }

    //주문 상세보기
   @Nested
   class getOrderDetail {
        @Test
        @DisplayName("주문 상세보기")
        void getOrderDetail_success() {
            UUID orderId = UUID.randomUUID();
            Order order = mock(Order.class);
            Restaurant restaurant = mock(Restaurant.class);

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
            when(order.getOrderId()).thenReturn(orderId);
            when(order.getRestaurant()).thenReturn(restaurant);
            when(restaurant.getRestaurantName()).thenReturn("goat-gam 식당");
            when(order.getAddress()).thenReturn("서울시 종로구 광화문로");
            when(order.getTotalPrice()).thenReturn(BigDecimal.valueOf(12345));
            when(order.getOrderTime()).thenReturn(LocalDateTime.now());
            when(order.getOrderFoods()).thenReturn(List.of()); // 비어 있어도 DTO 생성 가능
            // when
            OrderDetailResponseDto dto = orderService.getOrderDetail(orderId);
            // then
            assertEquals(orderId, dto.getOrderId());
            assertEquals("goat-gam 식당", dto.getRestaurantName());
            assertEquals("서울시 종로구 광화문로", dto.getAddress());
            assertEquals(BigDecimal.valueOf(12345), dto.getTotalPrice());
            assertNotNull(dto.getOrderTime());

        }
        @Test
        @DisplayName("주문 상세 조회 실패 - 주문 없음")
        void getOrderDetail_fail_notFound() {
            // given
            UUID oid = UUID.randomUUID();
            when(orderRepository.findById(oid)).thenReturn(Optional.empty());

            // when & then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> orderService.getOrderDetail(oid));
            assertEquals(ExceptionCode.ORDER_NOT_FOUND, exception.getExceptionCode());
        }

    }

    //내 주문 보기
    @Nested
    class getMyOrderSummary {
        @Test
        @DisplayName("내 주문 요약 조회 성공 - 매핑 확인")
        void getMyOrderSummary_success() {
            User user = mock(User.class);
            Order o1 = mock(Order.class);
            Order o2 = mock(Order.class);
            Restaurant r1 = mock(Restaurant.class);
            Restaurant r2 = mock(Restaurant.class);

            when(o1.getOrderId()).thenReturn(UUID.randomUUID());
            when(o1.getRestaurant()).thenReturn(r1);
            when(r1.getRestaurantName()).thenReturn("식당A");
            when(o1.getAddress()).thenReturn("주소A");
            when(o1.getOrderTime()).thenReturn(LocalDateTime.now());
            when(o1.getTotalPrice()).thenReturn(BigDecimal.valueOf(1000));

            when(o2.getOrderId()).thenReturn(UUID.randomUUID());
            when(o2.getRestaurant()).thenReturn(r2);
            when(r2.getRestaurantName()).thenReturn("식당B");
            when(o2.getAddress()).thenReturn("주소B");
            when(o2.getOrderTime()).thenReturn(LocalDateTime.now());
            when(o2.getTotalPrice()).thenReturn(BigDecimal.valueOf(2000));

            Page<Order> page = new PageImpl<>(List.of(o1, o2), PageRequest.of(0, 2), 2);
            when(orderRepository.findAllByUser(eq(user), any(Pageable.class))).thenReturn(page);

            // when
            var result = orderService.getMyOrderSummary(0, 2, user);

            // then
            assertNotNull(result);
            assertEquals(2, result.getContent().size());
            assertEquals("식당A", result.getContent().get(0).getRestaurantName());
            assertEquals("식당B", result.getContent().get(1).getRestaurantName());
        }

        @Test
        @DisplayName("내 주문 조회 실패 - userid가 null")
        void getMyOrderSummary_fail() {
            assertThrows(RuntimeException.class,   // NPE or IllegalArgumentException 등 전파
                    () -> orderService.getMyOrderSummary(0, 10, null));
        }

        //getUserOrderSummary
        @Nested
        class getUserOrderSummary{
            @Test
            @DisplayName("관리자의 유저 주문 정보 조회 : -특정 사용자 ID ")
            void getUserOrderSummary_success() {
                // given
                Long id = 10L;
                User user = mock(User.class);
                when(userRepository.findById(id)).thenReturn(Optional.of(user));

                Order order = mock(Order.class);
                Restaurant restaurant = mock(Restaurant.class);
                when(order.getUser()).thenReturn(user);
                when(order.getOrderId()).thenReturn(UUID.randomUUID());
                when(order.getRestaurant()).thenReturn(restaurant);
                when(restaurant.getRestaurantName()).thenReturn("식당X");
                when(order.getAddress()).thenReturn("주소X");
                when(order.getOrderTime()).thenReturn(LocalDateTime.now());
                when(order.getTotalPrice()).thenReturn(BigDecimal.TEN);

                Page<Order> page = new PageImpl<>(List.of(order), PageRequest.of(0, 10), 1);
                when(orderRepository.findAllByUser(eq(user), any(Pageable.class))).thenReturn(page);

                // when
                var result = orderService.getUserOrderSummary(id, 0, 10);

                // then
                assertEquals(1, result.getContent().size());
                assertEquals("식당X", result.getContent().get(0).getRestaurantName());
                verify(userRepository, times(1)).findById(id);

            }

            @Test
            @DisplayName("관리자 주문 요약 조회 - userId 미지정(전체)")
            void getUserOrderSummary_all() {
                // given
                Order order = mock(Order.class);
                Restaurant restaurant = mock(Restaurant.class);
                User user = mock(User.class);

                when(order.getUser()).thenReturn(user);
                when(user.getUserId()).thenReturn(77L);
                when(order.getOrderId()).thenReturn(UUID.randomUUID());
                when(order.getRestaurant()).thenReturn(restaurant);
                when(restaurant.getRestaurantName()).thenReturn("식당Y");
                when(order.getAddress()).thenReturn("주소Y");
                when(order.getOrderTime()).thenReturn(LocalDateTime.now());
                when(order.getTotalPrice()).thenReturn(BigDecimal.ONE);

                Page<Order> page = new PageImpl<>(List.of(order), PageRequest.of(0, 10), 1);
                when(orderRepository.findAll(any(Pageable.class))).thenReturn(page);

                // when
                var result = orderService.getUserOrderSummary(null, 0, 10);

                // then
                assertEquals(1, result.getContent().size());
                assertEquals(77L, result.getContent().get(0).getUserId());
                verify(orderRepository, times(1)).findAll(any(Pageable.class));
                verify(userRepository, never()).findById(anyLong());
            }

            @Test
            @DisplayName("관리자 주문 요약 조회 실패 - 사용자 NOT FOUND")
            void getUserOrderSummary_fail_userNotFound() {
                // given
                Long uid = 999L;
                when(userRepository.findById(uid)).thenReturn(Optional.empty());
                // when & then
                BusinessException ex = assertThrows(BusinessException.class,
                        () -> orderService.getUserOrderSummary(uid, 0, 10));
                assertEquals(ExceptionCode.USER_NOT_FOUND, ex.getExceptionCode());
                verify(orderRepository, never()).findAllByUser(any(), any());
            }

        }


    }
}



