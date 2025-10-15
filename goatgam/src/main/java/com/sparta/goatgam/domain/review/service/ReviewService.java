package com.sparta.goatgam.domain.review.service;

import com.sparta.goatgam.domain.order.entity.Order;
import com.sparta.goatgam.domain.order.entity.StatusEnum;
import com.sparta.goatgam.domain.order.repository.OrderRepository;
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
import com.sparta.goatgam.global.exception.BusinessException;
import com.sparta.goatgam.global.exception.ExceptionCode;
import com.sparta.goatgam.global.util.PageableUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public void createReview(Long userId,UUID restaurantId , UUID orderId ,ReviewRequestDto requestDto) {
        Restaurant restaurant =restaurantRepository.findById(restaurantId)
                        .orElseThrow(() -> new BusinessException(ExceptionCode.RESTAURANT_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.USER_NOT_FOUND));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.ORDER_NOT_FOUND));

        if (!order.getUser().getUserId().equals(userId)){
            throw new BusinessException(ExceptionCode.FORBIDDEN_CREATE_REVIEW_);
        }

        if (!order.getStatus().equals(StatusEnum.Completed)){
            throw new BusinessException(ExceptionCode.FORBIDDEN_ORDER_REVIEW);
        }

        if (reviewRepository.existsByOrderAndStatus(order, true)){
            throw new BusinessException(ExceptionCode.REVIEW_ORDER_NOT_ALLOWED);
        }

        if (requestDto.getRate() < 0 || requestDto.getRate() > 6){
            throw new BusinessException(ExceptionCode.REVIEW_RATE_ERROR);
        }


        Review review = Review.builder()
                .rate(requestDto.getRate())
                .content(requestDto.getContent())
                .reviewImage(requestDto.getReview_image())
                .status(true)
                .user(user)
                .restaurant(restaurant)
                .order(order)
                .build();

        reviewRepository.save(review);
    }

    @Transactional
    public ReviewUpdateResponseDto updateReview(Long userId, UUID reviewId, UpdateReviewRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.USER_NOT_FOUND));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.REVIEW_NOT_FOUND));

        if (!review.getUser().getUserId().equals(userId)){
            throw new BusinessException(ExceptionCode.FORBIDDEN_ORDER_REVIEW);
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
                .orElseThrow(() -> new BusinessException(ExceptionCode.USER_NOT_FOUND));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.REVIEW_NOT_FOUND));

        review.deleteReview(user.getNickname());
        return new ReviewUpdateResponseDto(reviewId, "리뷰가 삭제되었습니다.");
    }

    @Transactional(readOnly = true)
    public PagedModel<ReviewInfoListDto> reviewAll(UUID restaurantId, int page, int size, Sort.Direction direction) {

        Pageable pageable = PageableUtils.makePageable(page,size, PageableUtils.order(direction, "createdAt"));

        Page<Review> reviewPage = reviewRepository.findByRestaurant_RestaurantIdAndStatusTrue(restaurantId, pageable);

        return new PagedModel<>( reviewPage.map(f -> {
            ReviewInfoListDto dto = new ReviewInfoListDto();
            dto.setReviewId(f.getReviewId());
            dto.setRestaurantId(f.getRestaurant().getRestaurantId());
            dto.setNickname(f.getUser().getNickname());
            dto.setContent(f.getContent());
            dto.setReviewImage(f.getReviewImage());
            dto.setRate(f.getRate());
            dto.setCreatedAt(f.getCreatedAt());
            return dto;
        }));
    }
}