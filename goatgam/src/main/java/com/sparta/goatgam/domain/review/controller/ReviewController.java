package com.sparta.goatgam.domain.review.controller;

import com.sparta.goatgam.domain.review.dto.ReviewInfoListDto;
import com.sparta.goatgam.domain.review.dto.ReviewRequestDto;
import com.sparta.goatgam.domain.review.dto.ReviewUpdateResponseDto;
import com.sparta.goatgam.domain.review.dto.UpdateReviewRequestDto;
import com.sparta.goatgam.domain.review.service.ReviewService;
import com.sparta.goatgam.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/review")
    public ResponseEntity<String> createReview(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody ReviewRequestDto requestDto
            ){
         reviewService.createReview(userDetails.getUser().getUserId(),requestDto);
         return ResponseEntity.ok("리뷰가 성공적으로 등록 되었습니다.");
    }

    @PutMapping("/review/{reviewId}")
    public ResponseEntity<ReviewUpdateResponseDto> updateReview(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID reviewId,
            @RequestBody UpdateReviewRequestDto requestDto
    ){
        ReviewUpdateResponseDto responseDto = reviewService.updateReview(userDetails.getUser().getUserId(),reviewId, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/review/{reviewId}")
    public ResponseEntity<ReviewUpdateResponseDto> deleteReview(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID reviewId
    ){
        ReviewUpdateResponseDto responseDto = reviewService.deleteReview(userDetails.getUser().getUserId(),reviewId);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/review/{restaurantId}")
    public ResponseEntity<List<ReviewInfoListDto>> reviewAll(
            @PathVariable UUID restaurantId
    ){
        List<ReviewInfoListDto> reviewInfoList = reviewService.reviewAll(restaurantId);
        return ResponseEntity.ok(reviewInfoList);
    }
}