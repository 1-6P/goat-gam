package com.sparta.goatgam.domain.follow.controller;

import com.sparta.goatgam.domain.follow.entity.Follow;
import com.sparta.goatgam.domain.follow.service.FollowService;
import io.swagger.v3.oas.models.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vi")
public class FollowController {

    private final FollowService followService;

    @PostMapping("/follow")
    public ResponseEntity<UUID> addFollow(
            @RequestParam Long userId,
            @RequestParam UUID restaurantId
    ) {
        UUID followId = followService.addFollow(userId, restaurantId);
        return ResponseEntity.ok().body(followId);
    }



}
