package com.sparta.goatgam.domain.review.service;

import com.sparta.goatgam.domain.restaurant.entity.Restaurant;
import com.sparta.goatgam.domain.restaurant.repository.RestaurantRepository;
import com.sparta.goatgam.domain.review.dto.ReviewRequestDto;
import com.sparta.goatgam.domain.review.entity.Review;
import com.sparta.goatgam.domain.review.repository.ReviewRepository;
import com.sparta.goatgam.domain.user.entity.User;
import com.sparta.goatgam.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .review_image(requestDto.getReview_image())
                .status(true)
                .user(user)
                .restaurant(restaurant)
                .build();

        reviewRepository.save(review);
    }
}