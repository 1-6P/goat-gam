package com.sparta.goatgam.domain.ai.dto;

import com.sparta.goatgam.domain.ai.entity.AI;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class AiResponseDto {
    private final UUID id;
    private final String input;
    private final String answer;
    private final boolean status;
    private final long userId;
    private final UUID foodId;
    private final LocalDateTime createdAt;

    public AiResponseDto(AI ai) {
        this.id = ai.getId();
        this.input = ai.getInput();
        this.answer = ai.getAnswer();
        this.status = ai.isStatus();
        this.userId = ai.getUser() != null ? ai.getUser().getUserId() : null;
        this.foodId = ai.getFood() != null ? ai.getFood().getId() : null;
        this.createdAt = ai.getCreatedAt();
    }
}
