package com.sparta.goatgam.domain.review.controller;

import com.sparta.goatgam.domain.review.dto.ReviewInfoListDto;
import com.sparta.goatgam.domain.review.dto.ReviewRequestDto;
import com.sparta.goatgam.domain.review.dto.ReviewUpdateResponseDto;
import com.sparta.goatgam.domain.review.dto.UpdateReviewRequestDto;
import com.sparta.goatgam.domain.review.service.ReviewService;
import com.sparta.goatgam.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "후기 API", description = "후기 관련 기능 API입니다.")
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "후기 작성", description = "배송이 완료된 주문일 경우 후기를 작성합니다.")
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

    @Operation(summary = "후기 수정", description = "작성된 후기를 수정합니다.")
    @PutMapping("/review/{reviewId}")
    public ResponseEntity<ReviewUpdateResponseDto> updateReview(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID reviewId,
            @Valid @RequestBody UpdateReviewRequestDto requestDto
    ){
        ReviewUpdateResponseDto responseDto = reviewService.updateReview(userDetails.getUser().getUserId(),reviewId, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "후기 삭제", description = "작성된 후기를 삭제합니다.")
    @DeleteMapping("/review/{reviewId}")
    public ResponseEntity<ReviewUpdateResponseDto> deleteReview(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID reviewId
    ){
        ReviewUpdateResponseDto responseDto = reviewService.deleteReview(userDetails.getUser().getUserId(),reviewId);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "식당에 작성된 전체 후기 조회", description = "식당에 작성된 전체 후기를 조회합니다.")
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