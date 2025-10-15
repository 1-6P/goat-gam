package com.sparta.goatgam.domain.ai.controller;

import com.sparta.goatgam.domain.ai.dto.AIRequestDto;
import com.sparta.goatgam.domain.ai.dto.AiResponseDto;
import com.sparta.goatgam.domain.ai.service.AIService;
import com.sparta.goatgam.domain.owner.dto.ResultResponseDto;
import com.sparta.goatgam.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/ai")
@Tag(name = "AI API", description = "AI 관련 기능 API입니다.")
public class AIController {
    private final AIService aiService;

    @Operation(summary = "AI 음식 설명 생성", description = "AI를 통해 음식의 설명을 사용자의 요청에 맞게 생성")
    @PostMapping("/{menuId}")
    public ResultResponseDto createAiRequest(@PathVariable("menuId") UUID menuId,
                                             @RequestBody AIRequestDto aiRequestDto,
                                             @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return aiService.createAiRequest(menuId, userDetails.getUser(), aiRequestDto);
    }

    @Operation(summary = "관리자용 AI요청 전체 조회", description = "모든 AI 요청을 조회 (관리자만 가능)")
    @PreAuthorize("hasAnyAuthority('Master','Manager')")
    @GetMapping
    public ResponseEntity<PagedModel<AiResponseDto>> getAllAiRequest(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "DESC") Sort.Direction sort
    ) {

        return ResponseEntity.ok(aiService.getAiRequest(page, size, sort));
    }

    @Operation(summary = "AI 요청 단건 조회", description = "하나의 AI요청에 대한 정보를 조회")
    @GetMapping("/{aiId}")
    public AiResponseDto getAiRequest(@PathVariable UUID aiId) {
        return aiService.getAiRequestById(aiId);
    }
}
