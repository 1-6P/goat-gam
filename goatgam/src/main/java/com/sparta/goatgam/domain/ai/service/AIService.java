package com.sparta.goatgam.domain.ai.service;

import com.sparta.goatgam.domain.ai.dto.AIRequestDto;
import com.sparta.goatgam.domain.ai.dto.AiResponseDto;
import com.sparta.goatgam.domain.ai.entity.AI;
import com.sparta.goatgam.domain.ai.repository.AIRepository;
import com.sparta.goatgam.domain.owner.dto.ResultResponseDto;
import com.sparta.goatgam.domain.owner.entity.Food;
import com.sparta.goatgam.domain.owner.entity.FoodStatus;
import com.sparta.goatgam.domain.owner.repository.FoodRepository;
import com.sparta.goatgam.domain.user.entity.User;
import com.sparta.goatgam.global.exception.BusinessException;
import com.sparta.goatgam.global.exception.ExceptionCode;
import com.sparta.goatgam.global.util.PageableUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AIService {

    private final GeminiService geminiService;
    private final FoodRepository foodRepository;
    private final AIRepository aiRepository;

    @Transactional
    public ResultResponseDto createAiRequest(UUID menuId, User currentUser, AIRequestDto dto) {
        Food food = foodRepository.findById(menuId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.FOOD_NOT_FOUND));

        if (!food.getRestaurant().getUser().getUserId().equals(currentUser.getUserId())) {
            throw new BusinessException(ExceptionCode.FORBIDDEN_CREATE_MENU);
        }

        if(food.getFoodStatus() == FoodStatus.Deleted) {
            throw new BusinessException(ExceptionCode.FOOD_ALREADY_DELETED);
        }

        String answer = geminiService.generateMenuDescription(food, dto.getPrompt());

        AI ai = AI.builder().input(dto.getPrompt()).user(currentUser).answer(answer).status(true).food(food).build();

        aiRepository.save(ai);

        return new ResultResponseDto(ai.getAnswer(), food.getId());
    }

    public PagedModel<AiResponseDto> getAiRequest(int page, int size, Sort.Direction direction) {
        Pageable pageable = PageableUtils.makePageable(page, size,
                PageableUtils.order(direction, "createdAt"));

        Page<AI> logs = aiRepository.findAll(pageable);

        return new PagedModel<>(logs.map(AiResponseDto::new));
    }

    public AiResponseDto getAiRequestById(UUID id) {
        AI ai = aiRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ExceptionCode.AI_LOG_NOT_FOUND));
        return new AiResponseDto(ai);
    }
}
