package com.sparta.goatgam.domain.ai;

import com.sparta.goatgam.domain.ai.dto.AIRequestDto;
import com.sparta.goatgam.domain.ai.dto.AiResponseDto;
import com.sparta.goatgam.domain.ai.entity.AI;
import com.sparta.goatgam.domain.ai.repository.AIRepository;
import com.sparta.goatgam.domain.ai.service.AIService;
import com.sparta.goatgam.domain.ai.service.GeminiService;
import com.sparta.goatgam.domain.owner.dto.ResultResponseDto;
import com.sparta.goatgam.domain.owner.entity.Food;
import com.sparta.goatgam.domain.owner.entity.FoodStatus;
import com.sparta.goatgam.domain.owner.repository.FoodRepository;
import com.sparta.goatgam.domain.restaurant.entity.Restaurant;
import com.sparta.goatgam.domain.user.entity.User;
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
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PagedModel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AIServiceTest {

    @Mock private GeminiService geminiService;
    @Mock private FoodRepository foodRepository;
    @Mock private AIRepository aiRepository;
    @InjectMocks private AIService aiService;

    private final UUID foodId = UUID.randomUUID();
    private final UUID aiId = UUID.randomUUID();
    private final User owner = mock(User.class);
    private final Restaurant restaurant = mock(Restaurant.class);
    private final Food food = mock(Food.class);
    private final AI aiEntity = mock(AI.class);

    @BeforeEach
    void setUp() {
        when(owner.getUserId()).thenReturn(10L);
        when(restaurant.getUser()).thenReturn(owner);
        when(food.getId()).thenReturn(foodId);
        when(food.getRestaurant()).thenReturn(restaurant);
        when(food.getFoodStatus()).thenReturn(FoodStatus.Ok);
    }

    // ================================================================
    @Nested
    @DisplayName("createAiRequest() - AI 생성 요청")
    class CreateAiRequest {

        @Test
        @DisplayName("성공: AI 설명 생성 및 저장 성공")
        void success_createAiRequest() {
            AIRequestDto dto = new AIRequestDto("짬뽕 설명 작성해줘");
            given(foodRepository.findById(foodId)).willReturn(Optional.of(food));
            given(geminiService.generateMenuDescription(food, dto.getPrompt()))
                    .willReturn("매운 해물 짬뽕입니다");
            given(aiRepository.save(any(AI.class))).willAnswer(inv -> inv.getArgument(0));

            ResultResponseDto result = aiService.createAiRequest(foodId, owner, dto);

            assertThat(result.getMessage()).isEqualTo("매운 해물 짬뽕입니다");
            assertThat(result.getId()).isEqualTo(foodId);

            verify(foodRepository).findById(foodId);
            verify(aiRepository).save(any(AI.class));
            verify(geminiService).generateMenuDescription(food, "짬뽕 설명 작성해줘");
        }

        @Test
        @DisplayName("실패: FOOD_NOT_FOUND")
        void fail_foodNotFound() {
            AIRequestDto dto = new AIRequestDto("짜장면 설명 생성");
            given(foodRepository.findById(foodId)).willReturn(Optional.empty());

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> aiService.createAiRequest(foodId, owner, dto));

            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.FOOD_NOT_FOUND);
            verify(aiRepository, never()).save(any());
        }

        @Test
        @DisplayName("실패: 권한 없는 사용자 (FORBIDDEN_CREATE_MENU)")
        void fail_forbiddenUser() {
            User other = mock(User.class);
            when(other.getUserId()).thenReturn(99L);

            AIRequestDto dto = new AIRequestDto("탕수육 설명 생성");
            given(foodRepository.findById(foodId)).willReturn(Optional.of(food));

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> aiService.createAiRequest(foodId, other, dto));

            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.FORBIDDEN_CREATE_MENU);
        }

        @Test
        @DisplayName("실패: 삭제된 메뉴 (FOOD_ALREADY_DELETED)")
        void fail_deletedFood() {
            AIRequestDto dto = new AIRequestDto("짬뽕 설명 생성");
            when(food.getFoodStatus()).thenReturn(FoodStatus.Deleted);
            given(foodRepository.findById(foodId)).willReturn(Optional.of(food));

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> aiService.createAiRequest(foodId, owner, dto));

            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.FOOD_ALREADY_DELETED);
        }
    }

    // ================================================================
    @Nested
    @DisplayName("getAiRequest() - 전체 AI 로그 조회")
    class GetAiRequest {

        @Test
        @DisplayName("성공: AI 로그 페이지 조회")
        void success_getAiRequest() {
            AI ai = mock(AI.class);
            Page<AI> aiPage = new PageImpl<>(List.of(ai));
            given(aiRepository.findAll(any(Pageable.class))).willReturn(aiPage);

            PagedModel<?> result = aiService.getAiRequest(0, 5, Direction.DESC);

            assertThat(result).isNotNull();
            verify(aiRepository).findAll(any(Pageable.class));
        }
    }

    // ================================================================
    @Nested
    @DisplayName("getAiRequestById() - 단일 AI 로그 조회")
    class GetAiRequestById {

        @Test
        @DisplayName("성공: 단일 로그 조회")
        void success_getById() {
            given(aiRepository.findById(aiId)).willReturn(Optional.of(aiEntity));

            AiResponseDto result = aiService.getAiRequestById(aiId);

            assertThat(result).isNotNull();
            verify(aiRepository).findById(aiId);
        }

        @Test
        @DisplayName("실패: AI_LOG_NOT_FOUND")
        void fail_notFound() {
            given(aiRepository.findById(aiId)).willReturn(Optional.empty());

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> aiService.getAiRequestById(aiId));

            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.AI_LOG_NOT_FOUND);
        }
    }
}
