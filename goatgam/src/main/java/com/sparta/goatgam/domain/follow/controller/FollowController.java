package com.sparta.goatgam.domain.follow.controller;

import com.sparta.goatgam.domain.follow.dto.FollowInfoDto;
import com.sparta.goatgam.domain.follow.service.FollowService;
import com.sparta.goatgam.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class FollowController {

    private final FollowService followService;

    @PostMapping("/follow")
    public ResponseEntity<UUID> addFollow(
            @RequestParam UUID restaurantId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        UUID followId = followService.addFollow(userDetails.getUser().getUserId(), restaurantId);
        return ResponseEntity.ok().body(followId);
    }

    @GetMapping ("/follow")
    public ResponseEntity<List<FollowInfoDto>> followAll(@AuthenticationPrincipal UserDetailsImpl userDetails){
        List<FollowInfoDto> followList = followService.selectAll(userDetails.getUser().getUserId());
        return ResponseEntity.ok(followList);
    }


}
