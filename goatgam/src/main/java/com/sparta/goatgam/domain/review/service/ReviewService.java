package com.sparta.goatgam.domain.review.service;

import com.sparta.goatgam.domain.restaurant.entity.Restaurant;
import com.sparta.goatgam.domain.restaurant.repository.RestaurantRepository;
import com.sparta.goatgam.domain.review.dto.ReviewInfoListDto;
import com.sparta.goatgam.domain.review.dto.ReviewRequestDto;
import com.sparta.goatgam.domain.review.dto.ReviewUpdateResponseDto;
import com.sparta.goatgam.domain.review.dto.UpdateReviewRequestDto;
import com.sparta.goatgam.domain.review.entity.Review;
import com.sparta.goatgam.domain.review.repository.ReviewRepository;
import com.sparta.goatgam.domain.user.entity.User;
import com.sparta.goatgam.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;

    @Transactional
    public void createReview(Long userId, ReviewRequestDto requestDto) {
        Restaurant restaurant =restaurantRepository.findById(requestDto.getRestaurantId())
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 식당입니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을수 없습니다."));

        Review review = Review.builder()
                .rate(requestDto.getRate())
                .content(requestDto.getContent())
                .reviewImage(requestDto.getReview_image())
                .status(true)
                .user(user)
                .restaurant(restaurant)
                .build();

        reviewRepository.save(review);
    }

    @Transactional
    public ReviewUpdateResponseDto updateReview(Long userId, UUID reviewId, UpdateReviewRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 후기입니다."));

        if (!review.getUser().getUserId().equals(userId)){
            throw new IllegalArgumentException("본인이 작성한 리뷰만 수정할 수 있습니다.");
        }

        review.updateReview(
                requestDto.getRate(),
                requestDto.getContent(),
                requestDto.getReview_image()
        );
        return new ReviewUpdateResponseDto(reviewId ,"리뷰가 성공적으로 수정 되었습니다.");
    }

    @Transactional
    public ReviewUpdateResponseDto deleteReview(Long userId, UUID reviewId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 후기입니다."));

        review.deleteReview(user.getNickname());
        return new ReviewUpdateResponseDto(reviewId, "리뷰가 삭제되었습니다.");
    }

    @Transactional(readOnly = true)
    public List<ReviewInfoListDto> reviewAll(UUID restaurantId) {

        List<Review> reviewList = reviewRepository.findAllByRestaurant_RestaurantId(restaurantId)
                .stream()
                .filter(f -> Boolean.TRUE.equals(f.getStatus()))
                .collect(Collectors.toList());

        List<ReviewInfoListDto> reviewInfoListDto = reviewList.stream()
                .map(f -> {
                    ReviewInfoListDto dto = new ReviewInfoListDto();
                    dto.setReviewId(f.getReviewId());
                    dto.setRestaurantId(f.getRestaurant().getRestaurantId());
                    dto.setNickname(f.getUser().getNickname());
                    dto.setContent(f.getContent());
                    dto.setReviewImage(f.getReviewImage());
                    dto.setRate(f.getRate());
                    dto.setCreatedAt(f.getCreatedAt());
                    return dto;
                })
                .collect(Collectors.toList());

        return reviewInfoListDto;
    }
}