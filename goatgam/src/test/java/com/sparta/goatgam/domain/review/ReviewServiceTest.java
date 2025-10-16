package com.sparta.goatgam.domain.review;

import com.sparta.goatgam.domain.order.entity.Order;
import com.sparta.goatgam.domain.order.entity.StatusEnum;
import com.sparta.goatgam.domain.order.repository.OrderRepository;
import com.sparta.goatgam.domain.restaurant.entity.Restaurant;
import com.sparta.goatgam.domain.restaurant.repository.RestaurantRepository;
import com.sparta.goatgam.domain.review.dto.ReviewRequestDto;
import com.sparta.goatgam.domain.review.dto.UpdateReviewRequestDto;
import com.sparta.goatgam.domain.review.entity.Review;
import com.sparta.goatgam.domain.review.repository.ReviewRepository;
import com.sparta.goatgam.domain.review.service.ReviewService;
import com.sparta.goatgam.domain.user.entity.User;
import com.sparta.goatgam.domain.user.repository.UserRepository;
import com.sparta.goatgam.global.exception.BusinessException;
import com.sparta.goatgam.global.exception.ExceptionCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RestaurantRepository restaurantRepository;
    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private ReviewService reviewService;

    @Nested
    @DisplayName("createReview")
    class CreateReview {

        @Test
        @DisplayName("성공: 주문 완료 상태에서 리뷰 정상 생성")
        void success_createReview() {
            // given: 테스트 데이터 준비
            Long userId = 1L;
            UUID restaurantId = UUID.randomUUID();
            UUID orderId = UUID.randomUUID();

            User user = mock(User.class);
            Restaurant restaurant = mock(Restaurant.class);
            Order order = mock(Order.class);
            ReviewRequestDto requestDto = mock(ReviewRequestDto.class);

            // given: Mock 객체의 동작 정의
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(restaurantRepository.findById(restaurantId)).willReturn(Optional.of(restaurant));
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
            given(order.getUser()).willReturn(user);
            given(order.getUser().getUserId()).willReturn(userId);
            given(order.getStatus()).willReturn(StatusEnum.Completed);
            given(reviewRepository.existsByOrderAndStatus(order, true)).willReturn(false);
            given(requestDto.getRate()).willReturn(5);
            given(requestDto.getContent()).willReturn("맛있어요!");
            given(requestDto.getReview_image()).willReturn("image.png");

            // when: 테스트 대상 메서드 실행
            reviewService.createReview(userId, restaurantId, orderId, requestDto);

            // then: 결과 검증 - save 메서드가 1번 호출되었는지 확인
            then(reviewRepository).should(times(1)).save(any(Review.class));
        }

        @Test
        @DisplayName("실패: 주문 상태가 Completed가 아닌 경우(FORBIDDEN_ORDER_REVIEW)")
        void fail_notCompletedOrder() {
            // given: 테스트 데이터 준비 - 주문 상태가 OnDelivery
            Long userId = 1L;
            UUID restaurantId = UUID.randomUUID();
            UUID orderId = UUID.randomUUID();

            User user = mock(User.class);
            Restaurant restaurant = mock(Restaurant.class);
            Order order = mock(Order.class);
            ReviewRequestDto requestDto = mock(ReviewRequestDto.class);

            // given: Mock 객체의 동작 정의 - 주문 상태를 OnDelivery로 설정
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(restaurantRepository.findById(restaurantId)).willReturn(Optional.of(restaurant));
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
            given(order.getUser()).willReturn(user);
            given(order.getUser().getUserId()).willReturn(userId);
            given(order.getStatus()).willReturn(StatusEnum.OnDelivery);

            // when & then: 예외 발생 검증
            assertThatThrownBy(() -> reviewService.createReview(userId, restaurantId, orderId, requestDto))
                    .isInstanceOf(BusinessException.class)
                    .extracting("exceptionCode")
                    .isEqualTo(ExceptionCode.FORBIDDEN_ORDER_REVIEW);
        }
    }

    @Nested
    @DisplayName("reviewAll")
    class ReviewAll {

        @Test
        @DisplayName("성공: 특정 식당의 리뷰 목록 조회 성공")
        void success_reviewAll() {
            // given: 테스트 데이터 준비
            UUID restaurantId = UUID.randomUUID();
            UUID reviewId1 = UUID.randomUUID();
            UUID reviewId2 = UUID.randomUUID();

            // given: Mock 객체들 생성
            Review review1 = mock(Review.class);
            Review review2 = mock(Review.class);
            Restaurant restaurant = mock(Restaurant.class);
            User user = mock(User.class);

            // given: Restaurant Mock 설정
            given(restaurant.getRestaurantId()).willReturn(restaurantId);

            // given: User Mock 설정
            given(user.getNickname()).willReturn("testUser");

            // given: Review1 Mock 설정 - 모든 필드 값 정의
            given(review1.getReviewId()).willReturn(reviewId1);
            given(review1.getRestaurant()).willReturn(restaurant);
            given(review1.getUser()).willReturn(user);
            given(review1.getContent()).willReturn("맛있어요!");
            given(review1.getReviewImage()).willReturn("image1.png");
            given(review1.getRate()).willReturn(5);
            given(review1.getCreatedAt()).willReturn(LocalDateTime.now());

            // given: Review2 Mock 설정 - 모든 필드 값 정의
            given(review2.getReviewId()).willReturn(reviewId2);
            given(review2.getRestaurant()).willReturn(restaurant);
            given(review2.getUser()).willReturn(user);
            given(review2.getContent()).willReturn("좋아요!");
            given(review2.getReviewImage()).willReturn("image2.png");
            given(review2.getRate()).willReturn(4);
            given(review2.getCreatedAt()).willReturn(LocalDateTime.now());

            // given: Repository가 리뷰 목록을 반환하도록 설정
            Page<Review> page = new PageImpl<>(List.of(review1, review2));
            given(reviewRepository.findByRestaurant_RestaurantIdAndStatusTrue(any(), any())).willReturn(page);

            // when: 테스트 대상 메서드 실행
            var result = reviewService.reviewAll(restaurantId, 0, 10, Sort.Direction.DESC);

            // then: 결과 검증 - 리뷰가 2개 조회되었는지 확인
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(2);
        }

        @Test
        @DisplayName("실패: 해당 식당에 리뷰가 없는 경우")
        void fail_noReviews() {
            // given: 테스트 데이터 준비 - 빈 리뷰 목록
            UUID restaurantId = UUID.randomUUID();
            Page<Review> emptyPage = new PageImpl<>(List.of());

            // given: Repository가 빈 페이지를 반환하도록 설정
            given(reviewRepository.findByRestaurant_RestaurantIdAndStatusTrue(any(), any())).willReturn(emptyPage);

            // when: 테스트 대상 메서드 실행
            var result = reviewService.reviewAll(restaurantId, 0, 10, Sort.Direction.DESC);

            // then: 결과 검증 - 빈 목록 확인
            assertThat(result.getContent()).isEmpty();
        }
    }

    @Nested
    @DisplayName("updateReview")
    class UpdateReview {

        @Test
        @DisplayName("성공: 리뷰 수정 성공")
        void success_updateReview() {
            // given: 테스트 데이터 준비
            Long userId = 1L;
            UUID reviewId = UUID.randomUUID();

            User user = mock(User.class);
            Review review = mock(Review.class);
            UpdateReviewRequestDto requestDto = mock(UpdateReviewRequestDto.class);

            // given: UpdateReviewRequestDto의 getter 메서드 반환값 설정
            given(requestDto.getRate()).willReturn(4);
            given(requestDto.getContent()).willReturn("수정된 내용");
            given(requestDto.getReview_image()).willReturn("updated_image.png");

            // given: Mock 객체의 동작 정의
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));
            given(review.getUser()).willReturn(user);
            given(user.getUserId()).willReturn(userId);

            // given: void 메서드 stub 설정
            doNothing().when(review).updateReview(anyInt(), anyString(), anyString());

            // when: 테스트 대상 메서드 실행
            reviewService.updateReview(userId, reviewId, requestDto);

            // then: 결과 검증 - updateReview 메서드가 올바른 인자로 1번 호출되었는지 확인
            then(review).should(times(1)).updateReview(4, "수정된 내용", "updated_image.png");
        }

        @Test
        @DisplayName("실패: 리뷰 작성자가 아닌 경우(FORBIDDEN_ORDER_REVIEW)")
        void fail_notOwner() {
            // given: 테스트 데이터 준비 - 다른 사용자가 리뷰 수정 시도
            Long userId = 1L;
            UUID reviewId = UUID.randomUUID();

            User user = mock(User.class);
            User otherUser = mock(User.class);
            Review review = mock(Review.class);

            // given: Mock 객체의 동작 정의 - 리뷰 작성자와 요청자가 다름
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));
            given(review.getUser()).willReturn(otherUser);
            given(otherUser.getUserId()).willReturn(99L);

            // when & then: 예외 발생 검증 - 권한 없음 예외
            assertThatThrownBy(() -> reviewService.updateReview(userId, reviewId, null))
                    .isInstanceOf(BusinessException.class)
                    .extracting("exceptionCode")
                    .isEqualTo(ExceptionCode.FORBIDDEN_ORDER_REVIEW);
        }
    }

    @Nested
    @DisplayName("deleteReview")
    class DeleteReview {

        @Test
        @DisplayName("성공: 리뷰 삭제 성공")
        void success_deleteReview() {
            // given: 테스트 데이터 준비
            Long userId = 1L;
            UUID reviewId = UUID.randomUUID();

            User user = mock(User.class);
            Review review = mock(Review.class);

            // given: Mock 객체의 동작 정의
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));

            // when: 테스트 대상 메서드 실행
            reviewService.deleteReview(userId, reviewId);

            // then: 결과 검증 - deleteReview 메서드가 1번 호출되었는지 확인
            then(review).should(times(1)).deleteReview(any());
        }

        @Test
        @DisplayName("실패: 존재하지 않는 리뷰(REVIEW_NOT_FOUND)")
        void fail_reviewNotFound() {
            // given: 테스트 데이터 준비 - 존재하지 않는 리뷰 ID
            Long userId = 1L;
            UUID reviewId = UUID.randomUUID();
            User user = mock(User.class);

            // given: Mock 객체의 동작 정의 - 리뷰를 찾지 못함
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(reviewRepository.findById(reviewId)).willReturn(Optional.empty());

            // when & then: 예외 발생 검증 - 리뷰를 찾을 수 없음
            assertThatThrownBy(() -> reviewService.deleteReview(userId, reviewId))
                    .isInstanceOf(BusinessException.class)
                    .extracting("exceptionCode")
                    .isEqualTo(ExceptionCode.REVIEW_NOT_FOUND);
        }
    }
}