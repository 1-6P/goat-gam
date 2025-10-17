package com.sparta.goatgam.domain.cart;

import com.sparta.goatgam.domain.cart.dto.CartFoodRequestDto;
import com.sparta.goatgam.domain.cart.entity.Cart;
import com.sparta.goatgam.domain.cart.entity.CartFood;
import com.sparta.goatgam.domain.cart.repository.CartFoodRepository;
import com.sparta.goatgam.domain.cart.repository.CartRepository;
import com.sparta.goatgam.domain.cart.service.CartService;
import com.sparta.goatgam.domain.owner.entity.Food;
import com.sparta.goatgam.domain.owner.entity.FoodStatus;
import com.sparta.goatgam.domain.owner.repository.FoodOptionRepository;
import com.sparta.goatgam.domain.owner.repository.FoodRepository;
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
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class CartServiceTest {

	@Mock CartRepository cartRepository;
	@Mock CartFoodRepository cartFoodRepository;
	@Mock RestaurantRepository restaurantRepository;
	@Mock FoodRepository foodRepository;
	@Mock FoodOptionRepository foodOptionRepository;

	@InjectMocks CartService cartService;

	private User user;
	private Restaurant restaurant;
	private Food food;
	private Cart cart;

	@BeforeEach
	void setup() {
		user = mock(User.class);
		lenient().when(user.getUserId()).thenReturn(1L);

		restaurant = mock(Restaurant.class);
		UUID rid = UUID.randomUUID();
		lenient().when(restaurant.getRestaurantId()).thenReturn(rid);
		lenient().when(restaurant.isStatus()).thenReturn(true);

		food = mock(Food.class);
		lenient().when(food.getId()).thenReturn(UUID.randomUUID());
		lenient().when(food.getFoodStatus()).thenReturn(FoodStatus.Ok);

		cart = mock(Cart.class);
		lenient().when(cart.getCartId()).thenReturn(UUID.randomUUID());
		lenient().when(cart.getUser()).thenReturn(user);
		lenient().when(cart.getRestaurant()).thenReturn(restaurant);

		lenient().when(cartRepository.findByUserAndIsDeletedFalse(any(User.class)))
				 .thenReturn(Optional.of(cart));
		lenient().when(cartRepository.save(any(Cart.class))).thenReturn(cart);
	}

	// ===== getCartInfo() =====
	@Test
	@DisplayName("성공: 장바구니 조회 성공")
	void success_getCartInfo() {
		given(cartRepository.findByUserAndIsDeletedFalse(user)).willReturn(Optional.of(cart));

		var result = cartService.getCartInfo(user);

		assertThat(result).isNotNull();
		verify(cartRepository).findByUserAndIsDeletedFalse(user);
	}

	@Test
	@DisplayName("실패: CART_NOT_FOUND 예외")
	void fail_getCartInfo_notFound() {
		given(cartRepository.findByUserAndIsDeletedFalse(user)).willReturn(Optional.empty());

		BusinessException ex = assertThrows(BusinessException.class, () -> cartService.getCartInfo(user));

		assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.CART_NOT_FOUND);
	}

	// ===== addCartFood() =====
	@Nested
	@DisplayName("addCartFood() - 장바구니 음식 추가")
	class AddCartFoodTest {

		@Test
		@DisplayName("성공: 기존 카트 존재 시 같은 음식 추가 → 수량 증가")
		void success_addExistingFood() {
			UUID restaurantId = UUID.randomUUID();
			UUID foodId = UUID.randomUUID();
			CartFoodRequestDto req = mock(CartFoodRequestDto.class);

			// 요청 DTO
			given(req.restaurantId()).willReturn(restaurantId);
			given(req.foodId()).willReturn(foodId);
			given(req.quantity()).willReturn(1);
			given(req.options()).willReturn(List.of());

			// 레스토랑과 푸드
			given(restaurantRepository.findById(restaurantId)).willReturn(Optional.of(restaurant));
			given(foodRepository.findByIdAndRestaurant_RestaurantId(foodId, restaurantId))
				.willReturn(Optional.of(food));

			// 기존 카트 존재
			given(cartRepository.findByUserAndIsDeletedFalse(any(User.class)))
				.willReturn(Optional.of(cart));

			// cart 내부 레스토랑 ID도 동일하게 설정
			given(restaurant.getRestaurantId()).willReturn(restaurantId);
			given(cart.getRestaurant()).willReturn(restaurant);

			// 기존 음식 mock
			CartFood existing = mock(CartFood.class);
			when(existing.getFood()).thenReturn(food);
			when(existing.getCartFoodOptions()).thenReturn(List.of());
			when(existing.getQuantity()).thenReturn(1);

			given(cartFoodRepository.findAllByCartAndFoodAndIsDeletedFalse(cart, food))
				.willReturn(List.of(existing));

			// 실행
			var result = cartService.addCartFood(req, user);

			// 검증
			assertThat(result).isInstanceOf(MessageAndIdResponseDto.class);
			assertThat(result.getMessage()).contains("성공적으로 담았습니다");
		}

		@Test
		@DisplayName("실패: 존재하지 않는 식당 → RESTAURANT_NOT_FOUND")
		void fail_restaurantNotFound() {
			CartFoodRequestDto req = mock(CartFoodRequestDto.class);
			given(req.restaurantId()).willReturn(UUID.randomUUID());
			given(restaurantRepository.findById(any())).willReturn(Optional.empty());

			BusinessException ex = assertThrows(BusinessException.class,
												() -> cartService.addCartFood(req, user));

			assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.RESTAURANT_NOT_FOUND);
		}

		@Test
		@DisplayName("실패: FOOD_NOT_FOUND")
		void fail_foodNotFound() {
			UUID restaurantId = UUID.randomUUID();
			UUID foodId = UUID.randomUUID();
			CartFoodRequestDto req = mock(CartFoodRequestDto.class);
			given(req.restaurantId()).willReturn(restaurantId);
			given(req.foodId()).willReturn(foodId);

			given(restaurantRepository.findById(restaurantId)).willReturn(Optional.of(restaurant));
			given(foodRepository.findByIdAndRestaurant_RestaurantId(foodId, restaurantId))
				.willReturn(Optional.empty());

			BusinessException ex = assertThrows(BusinessException.class,
												() -> cartService.addCartFood(req, user));

			assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.FOOD_NOT_FOUND);
		}

		@Test
		@DisplayName("실패: FOOD_NOT_SELL")
		void fail_foodNotSell() {
			UUID restaurantId = UUID.randomUUID();
			UUID foodId = UUID.randomUUID();
			CartFoodRequestDto req = mock(CartFoodRequestDto.class);

			Food soldOutFood = mock(Food.class);
			when(soldOutFood.getFoodStatus()).thenReturn(FoodStatus.Hidden);

			given(req.restaurantId()).willReturn(restaurantId);
			given(req.foodId()).willReturn(foodId);

			given(restaurantRepository.findById(restaurantId)).willReturn(Optional.of(restaurant));
			given(foodRepository.findByIdAndRestaurant_RestaurantId(foodId, restaurantId))
				.willReturn(Optional.of(soldOutFood));
			given(cartRepository.findByUserAndIsDeletedFalse(user)).willReturn(Optional.of(cart));

			BusinessException ex = assertThrows(BusinessException.class,
												() -> cartService.addCartFood(req, user));

			assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.FOOD_NOT_SELL);
		}
	}
}
