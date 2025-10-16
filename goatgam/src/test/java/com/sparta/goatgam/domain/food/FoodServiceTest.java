package com.sparta.goatgam.domain.food;

import com.sparta.goatgam.domain.ai.service.AIService;
import com.sparta.goatgam.domain.owner.dto.FoodRequestDto;
import com.sparta.goatgam.domain.owner.dto.ResultResponseDto;
import com.sparta.goatgam.domain.owner.entity.Food;
import com.sparta.goatgam.domain.owner.entity.FoodStatus;
import com.sparta.goatgam.domain.owner.repository.FoodRepository;
import com.sparta.goatgam.domain.owner.service.FoodService;
import com.sparta.goatgam.domain.restaurant.entity.Restaurant;
import com.sparta.goatgam.domain.restaurant.repository.RestaurantRepository;
import com.sparta.goatgam.domain.user.entity.User;
import com.sparta.goatgam.global.exception.BusinessException;
import com.sparta.goatgam.global.exception.ExceptionCode;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) // unnecessary stubbing 경고 무시
class FoodServiceTest {

    @Mock private FoodRepository foodRepository;
    @Mock private RestaurantRepository restaurantRepository;
    @Mock private AIService aiService;
    @InjectMocks private FoodService foodService;

    // 공통 mock
    @Mock private User owner;
    @Mock private Restaurant restaurant;
    @Mock private Food food;

    private final UUID restaurantId = UUID.randomUUID();
    private final UUID foodId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        // --- 공통 관계 세팅 ---
        when(owner.getUserId()).thenReturn(10L);
        when(owner.getNickname()).thenReturn("사장님");

        when(restaurant.getRestaurantId()).thenReturn(restaurantId);
        when(restaurant.getUser()).thenReturn(owner);

        when(food.getId()).thenReturn(foodId);
        when(food.getRestaurant()).thenReturn(restaurant);
        when(food.getFoodStatus()).thenReturn(FoodStatus.Ok);
    }

    // ==================== AddFood ====================
    @Nested
    @DisplayName("addFood() - 메뉴 생성")
    class AddFood {

        @Test
        @DisplayName("성공: AI=false / 신규 메뉴 생성 성공")
        void success_createFoodWithoutAI() {
            FoodRequestDto dto = mockRequest("짜장면", BigDecimal.valueOf(8000), "img", "달콤한 짜장면", "Ok");

            given(restaurantRepository.findById(restaurantId)).willReturn(Optional.of(restaurant));
            given(foodRepository.findByRestaurantAndFoodName(restaurant, "짜장면")).willReturn(Optional.empty());
            given(foodRepository.save(any(Food.class))).willReturn(food);

            ResultResponseDto result = foodService.addFood(restaurantId, dto, false, owner);

            assertThat(result.getMessage()).isEqualTo("create success");
            verify(foodRepository).save(any(Food.class));
            verify(aiService, never()).createAiRequest(any(), any(), any());
        }

        @Test
        @DisplayName("실패: FOOD_DUPLICATED (이미 존재)")
        void fail_duplicatedFood() {
            Food existing = mock(Food.class);
            when(existing.getFoodStatus()).thenReturn(FoodStatus.Ok);

            given(restaurantRepository.findById(restaurantId)).willReturn(Optional.of(restaurant));
            given(foodRepository.findByRestaurantAndFoodName(restaurant, "짬뽕")).willReturn(Optional.of(existing));

            FoodRequestDto dto = mockRequest("짬뽕", BigDecimal.valueOf(9000), "img", "desc", "Ok");

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> foodService.addFood(restaurantId, dto, false, owner));

            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.FOOD_DUPLICATED);
        }

        @Test
        @DisplayName("성공: Deleted 상태 메뉴 복구")
        void success_restoreDeletedFood() {
            Food deleted = mock(Food.class);
            when(deleted.getFoodStatus()).thenReturn(FoodStatus.Deleted);
            when(deleted.getId()).thenReturn(foodId);

            given(restaurantRepository.findById(restaurantId)).willReturn(Optional.of(restaurant));
            given(foodRepository.findByRestaurantAndFoodName(restaurant, "짬뽕")).willReturn(Optional.of(deleted));

            FoodRequestDto dto = mockRequest("짬뽕", BigDecimal.valueOf(9000), "img", "desc", "Ok");

            ResultResponseDto result = foodService.addFood(restaurantId, dto, false, owner);

            assertThat(result.getMessage()).isEqualTo("restored success");
            verify(deleted).update(dto);
        }

        @Test
        @DisplayName("실패: 식당 없음 (RESTAURANT_NOT_FOUND)")
        void fail_restaurantNotFound() {
            given(restaurantRepository.findById(restaurantId)).willReturn(Optional.empty());
            FoodRequestDto dto = mockRequest("짬뽕", BigDecimal.valueOf(9000), "img", "desc", "Ok");

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> foodService.addFood(restaurantId, dto, false, owner));

            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.RESTAURANT_NOT_FOUND);
        }

        @Test
        @DisplayName("실패: 권한 없는 사용자 (FORBIDDEN_RESTAURANT)")
        void fail_forbiddenUser() {
            User other = mock(User.class);
            when(other.getUserId()).thenReturn(99L);

            given(restaurantRepository.findById(restaurantId)).willReturn(Optional.of(restaurant));
            FoodRequestDto dto = mockRequest("짬뽕", BigDecimal.valueOf(9000), "img", "desc", "Ok");

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> foodService.addFood(restaurantId, dto, false, other));

            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.FORBIDDEN_RESTAURANT);
        }
    }

    // ==================== UpdateFood ====================
    @Nested
    @DisplayName("updateFood() - 메뉴 수정")
    class UpdateFood {

        @Test
        @DisplayName("성공: 메뉴 정보 수정 성공")
        void success_updateFood() {
            FoodRequestDto dto = mockRequest("짬뽕", BigDecimal.valueOf(9500), "img", "수정된 설명", "Ok");

            given(restaurantRepository.findById(restaurantId)).willReturn(Optional.of(restaurant));
            given(foodRepository.findById(foodId)).willReturn(Optional.of(food));

            ResultResponseDto result = foodService.updateFood(restaurantId, foodId, dto, owner);

            assertThat(result.getMessage()).isEqualTo("update success");
            verify(food).update(dto);
        }
    }

    // ==================== DeleteFood ====================
    @Nested
    @DisplayName("deleteFood() - 메뉴 삭제")
    class DeleteFood {

        @Test
        @DisplayName("성공: 메뉴 삭제 처리")
        void success_deleteFood() {
            given(restaurantRepository.findById(restaurantId)).willReturn(Optional.of(restaurant));
            given(foodRepository.findById(foodId)).willReturn(Optional.of(food));

            ResultResponseDto result = foodService.deleteFood(restaurantId, foodId, owner);

            assertThat(result.getMessage()).isEqualTo("delete success");
            verify(food).changeStatus(FoodStatus.Deleted);
            verify(food).deleted(anyString());
        }
    }

    // ---------- helper ----------
    private FoodRequestDto mockRequest(String name, BigDecimal price, String image, String explain, String status) {
        FoodRequestDto dto = mock(FoodRequestDto.class);
        when(dto.getName()).thenReturn(name);
        when(dto.getPrice()).thenReturn(price);
        when(dto.getImage()).thenReturn(image);
        when(dto.getExplain()).thenReturn(explain);
        when(dto.getStatus()).thenReturn(status);
        return dto;
    }
}
