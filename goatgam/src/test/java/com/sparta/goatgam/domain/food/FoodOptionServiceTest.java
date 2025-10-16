package com.sparta.goatgam.domain.food;

import com.sparta.goatgam.domain.owner.dto.FoodOptionRequestDto;
import com.sparta.goatgam.domain.owner.dto.ResultResponseDto;
import com.sparta.goatgam.domain.owner.entity.Food;
import com.sparta.goatgam.domain.owner.entity.FoodOption;
import com.sparta.goatgam.domain.owner.repository.FoodOptionRepository;
import com.sparta.goatgam.domain.owner.service.FoodOptionService;
import com.sparta.goatgam.domain.owner.service.FoodService;
import com.sparta.goatgam.domain.restaurant.entity.Restaurant;
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
@MockitoSettings(strictness = Strictness.LENIENT)
class FoodOptionServiceTest {

    @Mock private FoodOptionRepository foodOptionRepository;
    @Mock private FoodService foodService;
    @InjectMocks private FoodOptionService foodOptionService;

    private final UUID restaurantId = UUID.randomUUID();
    private final UUID menuId = UUID.randomUUID();
    private final UUID optionId = UUID.randomUUID();
    private final User owner = mock(User.class);
    private final Food food = mock(Food.class);
    private final FoodOption foodOption = mock(FoodOption.class);

    @BeforeEach
    void setUp() {
        when(owner.getUserId()).thenReturn(10L);
        when(owner.getNickname()).thenReturn("사장님");
        when(foodOption.getId()).thenReturn(optionId);
    }

    // ================================================================
    @Nested
    @DisplayName("addOption() - 옵션 추가")
    class AddOption {

        @Test
        @DisplayName("성공: 신규 옵션 생성")
        void success_createOption() {
            FoodOptionRequestDto dto = new FoodOptionRequestDto("곱빼기", BigDecimal.valueOf(1000));

            given(foodService.validateRestaurantOwner(restaurantId, owner)).willReturn(mock(Restaurant.class));
            given(foodService.validateFoodInRestaurant(menuId, restaurantId)).willReturn(food);
            given(foodOptionRepository.findByFoodAndContents(food, "곱빼기")).willReturn(Optional.empty());
            given(foodOptionRepository.save(any(FoodOption.class))).willReturn(foodOption);

            ResultResponseDto result = foodOptionService.addOption(restaurantId, menuId, dto, owner);

            assertThat(result.getMessage()).isEqualTo("create success");
            verify(foodOptionRepository).save(any(FoodOption.class));
        }

        @Test
        @DisplayName("성공: 기존 Deleted 옵션 복구")
        void success_restoreOption() {
            FoodOptionRequestDto dto = new FoodOptionRequestDto("곱빼기", BigDecimal.valueOf(1000));
            FoodOption deleted = mock(FoodOption.class);
            when(deleted.isDeleted()).thenReturn(true);
            when(deleted.getId()).thenReturn(optionId);

            given(foodService.validateRestaurantOwner(restaurantId, owner)).willReturn(mock(Restaurant.class));
            given(foodService.validateFoodInRestaurant(menuId, restaurantId)).willReturn(food);
            given(foodOptionRepository.findByFoodAndContents(food, "곱빼기")).willReturn(Optional.of(deleted));

            ResultResponseDto result = foodOptionService.addOption(restaurantId, menuId, dto, owner);

            assertThat(result.getMessage()).isEqualTo("restored success");
            verify(deleted).changeStatus(false);
            verify(deleted).update(dto);
        }

        @Test
        @DisplayName("실패: 중복된 옵션 (OPTION_DUPLICATED)")
        void fail_duplicateOption() {
            FoodOptionRequestDto dto = new FoodOptionRequestDto("곱빼기", BigDecimal.valueOf(1000));
            FoodOption existing = mock(FoodOption.class);
            when(existing.isDeleted()).thenReturn(false);

            given(foodService.validateRestaurantOwner(restaurantId, owner)).willReturn(mock(Restaurant.class));
            given(foodService.validateFoodInRestaurant(menuId, restaurantId)).willReturn(food);
            given(foodOptionRepository.findByFoodAndContents(food, "곱빼기")).willReturn(Optional.of(existing));

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> foodOptionService.addOption(restaurantId, menuId, dto, owner));

            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.OPTION_DUPLICATED);
        }
    }

    // ================================================================
    @Nested
    @DisplayName("updateOption() - 옵션 수정")
    class UpdateOption {

        @Test
        @DisplayName("성공: 옵션 정보 수정 성공")
        void success_updateOption() {
            FoodOptionRequestDto dto = new FoodOptionRequestDto("면추가", BigDecimal.valueOf(500));

            given(foodService.validateRestaurantOwner(restaurantId, owner)).willReturn(mock(Restaurant.class));
            given(foodService.validateFoodInRestaurant(menuId, restaurantId)).willReturn(food);
            given(foodOptionRepository.findById(optionId)).willReturn(Optional.of(foodOption));

            ResultResponseDto result = foodOptionService.updateOption(restaurantId, menuId, optionId, dto, owner);

            assertThat(result.getMessage()).isEqualTo("update success");
            verify(foodOption).update(dto);
        }

        @Test
        @DisplayName("실패: 옵션 존재하지 않음 (OPTION_NOT_FOUND)")
        void fail_optionNotFound() {
            FoodOptionRequestDto dto = new FoodOptionRequestDto("면추가", BigDecimal.valueOf(500));

            given(foodService.validateRestaurantOwner(restaurantId, owner)).willReturn(mock(Restaurant.class));
            given(foodService.validateFoodInRestaurant(menuId, restaurantId)).willReturn(food);
            given(foodOptionRepository.findById(optionId)).willReturn(Optional.empty());

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> foodOptionService.updateOption(restaurantId, menuId, optionId, dto, owner));

            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.OPTION_NOT_FOUND);
        }
    }

    // ================================================================
    @Nested
    @DisplayName("deleteOption() - 옵션 삭제")
    class DeleteOption {

        @Test
        @DisplayName("성공: 옵션 삭제 처리")
        void success_deleteOption() {
            given(foodService.validateRestaurantOwner(restaurantId, owner)).willReturn(mock(Restaurant.class));
            given(foodService.validateFoodInRestaurant(menuId, restaurantId)).willReturn(food);
            given(foodOptionRepository.findById(optionId)).willReturn(Optional.of(foodOption));
            when(foodOption.isDeleted()).thenReturn(false);

            ResultResponseDto result = foodOptionService.deleteOption(restaurantId, menuId, optionId, owner);

            assertThat(result.getMessage()).isEqualTo("delete success");
            verify(foodOption).changeStatus(true);
            verify(foodOption).deleted(owner.getNickname());
        }

        @Test
        @DisplayName("실패: 이미 삭제된 옵션 (OPTION_ALREADY_DELETED)")
        void fail_alreadyDeleted() {
            given(foodService.validateRestaurantOwner(restaurantId, owner)).willReturn(mock(Restaurant.class));
            given(foodService.validateFoodInRestaurant(menuId, restaurantId)).willReturn(food);
            given(foodOptionRepository.findById(optionId)).willReturn(Optional.of(foodOption));
            when(foodOption.isDeleted()).thenReturn(true);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> foodOptionService.deleteOption(restaurantId, menuId, optionId, owner));

            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.OPTION_ALREADY_DELETED);
        }
    }
}

