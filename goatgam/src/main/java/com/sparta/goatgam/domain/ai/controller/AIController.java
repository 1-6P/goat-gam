package com.sparta.goatgam.domain.ai.controller;

import com.sparta.goatgam.domain.ai.dto.AIRequestDto;
import com.sparta.goatgam.domain.ai.dto.AiResponseDto;
import com.sparta.goatgam.domain.ai.service.AIService;
import com.sparta.goatgam.domain.owner.dto.ResultResponseDto;
import com.sparta.goatgam.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/ai")
public class AIController {
    private final AIService aiService;

    @PostMapping("/{menuId}")
    public ResultResponseDto createAiRequest(@PathVariable("menuId") UUID menuId,
                                             @RequestBody AIRequestDto aiRequestDto,
                                             @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return aiService.createAiRequest(menuId, userDetails.getUser(), aiRequestDto);
    }

    @PreAuthorize("hasAnyAuthority('Master','Manager')")
    @GetMapping
    public Page<AiResponseDto> getAllAiRequest(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "desc") String sort
    ) {
        Sort.Direction direction = sort.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        return aiService.getAiRequest(page, size, direction);
    }

    @GetMapping("/{aiId}")
    public AiResponseDto getAiRequest(@PathVariable UUID aiId) {
        return aiService.getAiRequestById(aiId);
    }
}
