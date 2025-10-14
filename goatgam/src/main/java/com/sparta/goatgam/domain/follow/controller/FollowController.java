package com.sparta.goatgam.domain.follow.controller;

import com.sparta.goatgam.domain.follow.dto.FollowInfoDto;
import com.sparta.goatgam.domain.follow.dto.FollowResponseDto;
import com.sparta.goatgam.domain.follow.service.FollowService;
import com.sparta.goatgam.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "팔로우 API", description = "팔로우 관련 기능 API입니다.")
public class FollowController {

    private final FollowService followService;

    @Operation(summary = "팔로우 생성", description = "식당을 팔로우 합니다.")
    @PostMapping("/follow")
    public ResponseEntity<UUID> addFollow(
            @RequestParam UUID restaurantId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        UUID followId = followService.addFollow(userDetails.getUser().getUserId(), restaurantId);
        return ResponseEntity.ok().body(followId);
    }

    @Operation(summary = "전체 팔로우 목록 조회", description = "팔로우된 식당 목록을 조회합니다.")
    @GetMapping ("/follow")
    public ResponseEntity<List<FollowInfoDto>> followAll(@AuthenticationPrincipal UserDetailsImpl userDetails){
        List<FollowInfoDto> followList = followService.selectAll(userDetails.getUser().getUserId());
        return ResponseEntity.ok(followList);
    }

    @Operation(summary = "팔로우 삭제", description = "언팔로우 합니다.")
    @PatchMapping("/follow/{followId}")
    public ResponseEntity<FollowResponseDto> unFollow(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID followId){
        FollowResponseDto followResponseDto = followService.unFollow(userDetails.getUser().getUserId(),followId);

        return ResponseEntity.ok().body(followResponseDto);
    }
}
