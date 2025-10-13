package com.sparta.goatgam.domain.review.controller;

import com.sparta.goatgam.domain.review.dto.ReviewInfoListDto;
import com.sparta.goatgam.domain.review.dto.ReviewRequestDto;
import com.sparta.goatgam.domain.review.dto.ReviewUpdateResponseDto;
import com.sparta.goatgam.domain.review.dto.UpdateReviewRequestDto;
import com.sparta.goatgam.domain.review.service.ReviewService;
import com.sparta.goatgam.global.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/review/{restaurantId}/{orderId}")
    public ResponseEntity<String> createReview(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID restaurantId,
            @PathVariable UUID orderId,
            @Valid @RequestBody ReviewRequestDto requestDto
            ){
         reviewService.createReview(userDetails.getUser().getUserId(), restaurantId, orderId, requestDto);
         return ResponseEntity.ok("리뷰가 성공적으로 등록 되었습니다.");
    }

    @PutMapping("/review/{reviewId}")
    public ResponseEntity<ReviewUpdateResponseDto> updateReview(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID reviewId,
            @Valid @RequestBody UpdateReviewRequestDto requestDto
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
    public ResponseEntity<PagedModel<ReviewInfoListDto>> reviewAll(
            @PathVariable UUID restaurantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "desc") String sort
    ){
        Sort.Direction direction = sort.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        PagedModel<ReviewInfoListDto> reviewInfoList = reviewService.reviewAll(restaurantId,page,size,direction);
        return ResponseEntity.ok(reviewInfoList);
    }
}