package com.sparta.goatgam.domain.follow.service;
import com.sparta.goatgam.domain.follow.dto.FollowInfoDto;
import com.sparta.goatgam.domain.follow.dto.FollowResponseDto;
import com.sparta.goatgam.domain.follow.entity.Follow;
import com.sparta.goatgam.domain.follow.repository.FollowRepository;
import com.sparta.goatgam.domain.restaurant.entity.Restaurant;
import com.sparta.goatgam.domain.restaurant.repository.RestaurantRepository;
import com.sparta.goatgam.domain.user.entity.User;
import com.sparta.goatgam.domain.user.repository.UserRepository;
import com.sparta.goatgam.global.exception.BusinessException;
import com.sparta.goatgam.global.exception.ExceptionCode;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class FollowServiceTest {

    @Mock
    private FollowRepository followRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private FollowService followService;

    // ---------- addFollow ----------
    @Nested
    class AddFollow {

        @Test
        @DisplayName("성공: 정상적으로 팔로우 추가 (Follow 생성자 mock 처리)")
        void success_addFollow_withConstructorMock() {
            // given
            Long userId = 1L;
            UUID restaurantId = UUID.randomUUID();
            UUID fakeFollowId = UUID.randomUUID();

            User user = mock(User.class);
            Restaurant restaurant = mock(Restaurant.class);

            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(restaurantRepository.findById(restaurantId)).willReturn(Optional.of(restaurant));
            given(followRepository.findByUser_UserIdAndRestaurant_RestaurantId(userId, restaurantId)).willReturn(null);

            try (MockedConstruction<Follow> mocked = mockConstruction(Follow.class, (mock, context) -> {
                when(mock.getFollowId()).thenReturn(fakeFollowId);
            })) {
                // when
                UUID result = followService.addFollow(userId, restaurantId);

                // then
                assertThat(result).isNotNull();
                assertThat(result).isEqualTo(fakeFollowId);
                then(followRepository).should(times(1)).save(any(Follow.class));
            }
        }

        @Test
        @DisplayName("실패: 이미 팔로우한 상태(ALREADY_FOLLOWED)")
        void fail_alreadyFollowed() {
            // given
            Long userId = 1L;
            UUID restaurantId = UUID.randomUUID();
            User user = mock(User.class);
            Restaurant restaurant = mock(Restaurant.class);
            Follow existingFollow = mock(Follow.class);

            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(restaurantRepository.findById(restaurantId)).willReturn(Optional.of(restaurant));
            given(followRepository.findByUser_UserIdAndRestaurant_RestaurantId(userId, restaurantId))
                    .willReturn(existingFollow);
            // followStatus를 true로 세팅
            given(existingFollow.getFollowStatus()).willReturn(true);

            // when & then
            BusinessException ex = assertThrows(BusinessException.class,
                    () -> followService.addFollow(userId, restaurantId));

            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.FOLLOW_ALREADY);
            then(followRepository).should(never()).save(any());
        }
    }

    @Nested
    @DisplayName("SelectAll")
    class SelectAll {

        @Test
        @DisplayName("성공: 유저가 팔로우한 식당 목록 조회 성공")
        void success_selectAll() {
            // given
            Long userId = 1L;

            Restaurant restaurant = mock(Restaurant.class);
            given(restaurant.getRestaurantId()).willReturn(UUID.randomUUID());

            Follow follow1 = mock(Follow.class);
            given(follow1.getFollowStatus()).willReturn(true);
            given(follow1.getFollowId()).willReturn(UUID.randomUUID());
            given(follow1.getRestaurant()).willReturn(restaurant);

            Follow follow2 = mock(Follow.class);
            given(follow2.getFollowStatus()).willReturn(true);
            given(follow2.getFollowId()).willReturn(UUID.randomUUID());
            given(follow2.getRestaurant()).willReturn(restaurant);

            given(followRepository.findAllByUser_UserId(userId)).willReturn(List.of(follow1, follow2));

            // when
            List<FollowInfoDto> result = followService.selectAll(userId);

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getFollowStatus()).isTrue();
            verify(followRepository, times(1)).findAllByUser_UserId(userId);
        }

        @Test
        @DisplayName("실패: 팔로우한 식당이 없는 경우 빈 리스트 반환")
        void fail_noFollowList() {
            // given
            Long userId = 1L;
            given(followRepository.findAllByUser_UserId(userId)).willReturn(List.of());

            // when
            List<FollowInfoDto> result = followService.selectAll(userId);

            // then
            assertThat(result).isEmpty();
            verify(followRepository, times(1)).findAllByUser_UserId(userId);
        }
    }

    @Nested
    @DisplayName("UnFollow")
    class UnFollow {

        @Test
        @DisplayName("성공: 팔로우 상태인 식당을 언팔로우 성공")
        void success_unFollow() {
            // given
            Long userId = 1L;
            UUID followId = UUID.randomUUID();

            User user = mock(User.class);
            given(userRepository.findById(userId)).willReturn(Optional.of(user));

            Follow follow = mock(Follow.class);
            given(follow.getFollowStatus()).willReturn(true);
            given(followRepository.findById(followId)).willReturn(Optional.of(follow));

            // when
            FollowResponseDto result = followService.unFollow(userId, followId);

            // then
            assertThat(result.isSuccess()).isTrue();
            assertThat(result.getMessage()).isEqualTo("팔로우가 해제되었습니다.");
            verify(follow).unFollow(any());
        }

        @Test
        @DisplayName("실패: 이미 언팔로우 상태인 경우")
        void fail_alreadyUnfollowed() {
            // given
            Long userId = 1L;
            UUID followId = UUID.randomUUID();

            User user = mock(User.class);
            given(userRepository.findById(userId)).willReturn(Optional.of(user));

            Follow follow = mock(Follow.class);
            given(follow.getFollowStatus()).willReturn(false);
            given(followRepository.findById(followId)).willReturn(Optional.of(follow));

            // when
            FollowResponseDto result = followService.unFollow(userId, followId);

            // then
            assertThat(result.isSuccess()).isFalse();
            assertThat(result.getMessage()).isEqualTo("이미 언팔로우 상태입니다.");
            verify(follow, never()).unFollow(any());
        }

        @Test
        @DisplayName("실패: 존재하지 않는 유저로 요청한 경우 (USER_NOT_FOUND)")
        void fail_userNotFound() {
            // given
            Long userId = 99L;
            UUID followId = UUID.randomUUID();

            given(userRepository.findById(userId)).willReturn(Optional.empty());

            // when & then
            BusinessException ex = assertThrows(BusinessException.class,
                    () -> followService.unFollow(userId, followId));

            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.USER_NOT_FOUND);
            verify(userRepository, times(1)).findById(userId);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 팔로우 ID로 요청한 경우 (FOLLOW_NO)")
        void fail_followNotFound() {
            // given
            Long userId = 1L;
            UUID followId = UUID.randomUUID();

            User user = mock(User.class);
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(followRepository.findById(followId)).willReturn(Optional.empty());

            // when & then
            BusinessException ex = assertThrows(BusinessException.class,
                    () -> followService.unFollow(userId, followId));

            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.FOLLOW_NO);
        }
    }
}