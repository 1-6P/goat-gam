package com.sparta.goatgam.domain.review.service;

import com.sparta.goatgam.domain.restaurant.entity.Restaurant;
import com.sparta.goatgam.domain.restaurant.repository.RestaurantRepository;
import com.sparta.goatgam.domain.review.dto.ReviewRequestDto;
import com.sparta.goatgam.domain.review.dto.UpdateReviewRequestDto;
import com.sparta.goatgam.domain.review.entity.Review;
import com.sparta.goatgam.domain.review.repository.ReviewRepository;
import com.sparta.goatgam.domain.user.entity.User;
import com.sparta.goatgam.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

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
    public void updateReview(Long userId, UUID reviewId, UpdateReviewRequestDto requestDto) {
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
    }
}