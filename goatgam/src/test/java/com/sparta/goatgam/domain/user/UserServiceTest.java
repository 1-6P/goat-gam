package com.sparta.goatgam.domain.user;

import com.sparta.goatgam.domain.user.dto.SignupRequestDto;
import com.sparta.goatgam.domain.user.dto.UserInfoDto;
import com.sparta.goatgam.domain.user.dto.UserInfoUpdateDto;
import com.sparta.goatgam.domain.user.entity.User;
import com.sparta.goatgam.domain.user.entity.UserRoleEnum;
import com.sparta.goatgam.domain.user.repository.UserRepository;
import com.sparta.goatgam.domain.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Nested
    @DisplayName("signup")
    class SignupTest {

        @Test
        @DisplayName("성공: 회원가입 정상 처리")
        void success_signup() {
            // given
            SignupRequestDto dto = mock(SignupRequestDto.class);
            given(dto.getUsername()).willReturn("홍길동");
            given(dto.getNickname()).willReturn("gildong");
            given(dto.getEmail()).willReturn("test@test.com");
            given(dto.getPassword()).willReturn("1234");
            given(dto.getRole()).willReturn(UserRoleEnum.Customer);
            given(dto.getPhoneNumber()).willReturn("010-1234-5678");
            given(dto.getAddress()).willReturn("서울시");

            given(userRepository.findByNickname("gildong")).willReturn(Optional.empty());
            given(userRepository.findByEmail("test@test.com")).willReturn(Optional.empty());
            given(passwordEncoder.encode("1234")).willReturn("encoded1234");

            // when
            userService.signup(dto);

            // then
            then(userRepository).should(times(1)).save(any(User.class));
        }

        @Test
        @DisplayName("실패: 중복된 이메일 존재 시 예외 발생")
        void fail_duplicateEmail() {
            // given
            SignupRequestDto dto = mock(SignupRequestDto.class);
            given(dto.getNickname()).willReturn("kyu");
            given(dto.getEmail()).willReturn("test@test.com");
            given(userRepository.findByNickname("kyu")).willReturn(Optional.empty());
            given(userRepository.findByEmail("test@test.com")).willReturn(Optional.of(mock(User.class)));

            // when & then
            assertThatThrownBy(() -> userService.signup(dto))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("중복된 Email");
        }
    }

    @Nested
    @DisplayName("updateUser")
    class UpdateUserTest {

        @Test
        @DisplayName("성공: 유저 정보 수정 성공")
        void success_updateUser() {
            // given
            Long userId = 1L;
            User user = mock(User.class);
            UserInfoUpdateDto dto = mock(UserInfoUpdateDto.class);

            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(dto.getNickname()).willReturn("newNick");
            given(dto.getUsername()).willReturn("newName");
            given(dto.getPassword()).willReturn("newPw");
            given(dto.getUserRole()).willReturn(UserRoleEnum.Master);
            given(dto.getPhoneNumber()).willReturn("010-9999-9999");
            given(dto.getAddress()).willReturn("서울시 강남구");

            // when
            userService.updateUser(userId, dto);

            // then
            then(user).should(times(1)).setNickname("newNick");
            then(user).should(times(1)).setUsername("newName");
            then(userRepository).should(times(1)).save(user);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 유저")
        void fail_userNotFound() {
            // given
            given(userRepository.findById(1L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.updateUser(1L, mock(UserInfoUpdateDto.class)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("해당 유저가 존재하지 않습니다");
        }
    }

    @Nested
    @DisplayName("softDelete")
    class SoftDeleteTest {

        @Test
        @DisplayName("성공: 유저 soft delete 성공")
        void success_softDelete() {
            // given
            Long userId = 1L;
            User user = mock(User.class);
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(user.getStatus()).willReturn(true);
            given(user.getUserId()).willReturn(userId);
            LocalDateTime now = LocalDateTime.now();
            given(user.getDeletedAt()).willReturn(now);

            // when
            UserService.SoftDeleteResult result = userService.softDelete(userId);

            // then
            assertThat(result.userId()).isEqualTo(1L);
            assertThat(result.status()).isFalse();
            then(user).should(times(1)).setStatus(false);
            then(user).should(times(1)).deleted(userId.toString());
        }

        @Test
        @DisplayName("실패: 이미 비활성화된 유저인 경우 상태 변경 없이 반환")
        void fail_alreadyDeleted() {
            // given
            Long userId = 1L;
            User user = mock(User.class);
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(user.getStatus()).willReturn(false);
            given(user.getDeletedAt()).willReturn(LocalDateTime.now());
            given(user.getUserId()).willReturn(userId);

            // when
            UserService.SoftDeleteResult result = userService.softDelete(userId);

            // then
            assertThat(result.status()).isFalse();
            then(user).should(never()).deleted(any());
        }
    }

    @Nested
    @DisplayName("restore")
    class RestoreTest {

        @Test
        @DisplayName("성공: 비활성화된 유저 복구 성공")
        void success_restore() {
            // given
            Long userId = 1L;
            User user = mock(User.class);
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(user.getStatus()).willReturn(false);

            // when
            userService.restore(userId);

            // then
            then(user).should(times(1)).setStatus(true);
            then(user).should(times(1)).restore();
        }

        @Test
        @DisplayName("실패: 이미 활성화된 유저")
        void fail_alreadyActive() {
            // given
            Long userId = 1L;
            User user = mock(User.class);
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(user.getStatus()).willReturn(true);

            // when
            userService.restore(userId);

            // then
            then(user).should(never()).restore();
        }
    }

    @Nested
    @DisplayName("getAllUsers")
    class GetAllUsersTest {

        @Test
        @DisplayName("성공: 모든 유저 정보 조회 성공")
        void success_getAllUsers() {
            // given
            User user1 = mock(User.class);
            User user2 = mock(User.class);

            given(user1.getUserId()).willReturn(1L);
            given(user1.getUsername()).willReturn("a");
            given(user1.getNickname()).willReturn("nick1");
            given(user1.getEmail()).willReturn("a@test.com");
            given(user1.getRole()).willReturn(UserRoleEnum.Customer);
            given(user1.getPhoneNumber()).willReturn("010-1234-5678");
            given(user1.getAddress()).willReturn("서울");
            given(user1.getStatus()).willReturn(true);

            given(user2.getUserId()).willReturn(2L);
            given(user2.getUsername()).willReturn("b");
            given(user2.getNickname()).willReturn("nick2");
            given(user2.getEmail()).willReturn("b@test.com");
            given(user2.getRole()).willReturn(UserRoleEnum.Master);
            given(user2.getPhoneNumber()).willReturn("010-9999-9999");
            given(user2.getAddress()).willReturn("부산");
            given(user2.getStatus()).willReturn(false);

            given(userRepository.findAll()).willReturn(List.of(user1, user2));

            // when
            List<UserInfoDto> result = userService.getAllUsers();

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getNickname()).isEqualTo("nick1");
            assertThat(result.get(1).getUserRole()).isEqualTo(UserRoleEnum.Master);
        }
    }
}
