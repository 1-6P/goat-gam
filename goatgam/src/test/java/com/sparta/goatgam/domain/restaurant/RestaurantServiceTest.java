package com.sparta.goatgam.domain.restaurant;

import com.sparta.goatgam.domain.owner.repository.FoodOptionRepository;
import com.sparta.goatgam.domain.owner.repository.FoodRepository;
import com.sparta.goatgam.domain.restaurant.dto.RestaurantDetailDto;
import com.sparta.goatgam.domain.restaurant.dto.RestaurantInfoDto;
import com.sparta.goatgam.domain.restaurant.dto.RestaurantRequestDto;
import com.sparta.goatgam.domain.restaurant.entity.Restaurant;
import com.sparta.goatgam.domain.restaurant.entity.RestaurantType;
import com.sparta.goatgam.domain.restaurant.repository.RestaurantRepository;
import com.sparta.goatgam.domain.restaurant.repository.RestaurantTypeRepository;
import com.sparta.goatgam.domain.restaurant.service.RestaurantService;
import com.sparta.goatgam.domain.user.entity.User;
import com.sparta.goatgam.domain.user.entity.UserRoleEnum;
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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RestaurantServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RestaurantTypeRepository restaurantTypeRepository;
    @Mock
    private FoodRepository foodRepository;
    @Mock
    private FoodOptionRepository foodOptionRepository;

    @InjectMocks
    private RestaurantService restaurantService;

    // ---------- createRestaurant ----------
    @Nested
    class CreateRestaurant {

        @Test
        @DisplayName("성공: Owner/Manager/Master 권한 + 본인 요청")
        void success_owner() {
            // given
            User owner = mockUser(10L, UserRoleEnum.Owner);
            UUID typeId = UUID.randomUUID();
            RestaurantType type = mock(RestaurantType.class);

            RestaurantRequestDto req = mockRequest(10L, typeId, "공리짬뽕", "서울 종로구", 11001112);

            given(userRepository.findById(10L)).willReturn(Optional.of(owner));
            given(restaurantTypeRepository.findById(typeId)).willReturn(Optional.of(type));
            // save는 저장된 엔티티를 그대로 반환하는 것처럼 스텁
            given(restaurantRepository.save(any(Restaurant.class))).willAnswer(inv -> inv.getArgument(0));

            // when
            RestaurantInfoDto dto = restaurantService.createRestaurant(req, owner);

            // then
            assertThat(dto).isNotNull();
            assertThat(dto.getRestaurantName()).isEqualTo("공리짬뽕");
            verify(restaurantRepository, times(1)).save(any(Restaurant.class));
        }

        @Test
        @DisplayName("실패: USER_NOT_FOUND")
        void fail_userNotFound() {
            // given
            UUID typeId = UUID.randomUUID();
            RestaurantRequestDto req = mockRequest(10L, typeId, "공리짬뽕", "서울", 11001112);

            given(userRepository.findById(10L)).willReturn(Optional.empty());

            // when & then
            BusinessException ex = assertThrows(BusinessException.class,
                    () -> restaurantService.createRestaurant(req, mockUser(10L, UserRoleEnum.Owner)));
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.USER_NOT_FOUND);
            verify(restaurantRepository, never()).save(any());
        }

        @Test
        @DisplayName("실패: RESTAURANT_TYPE_NOT_FOUND")
        void fail_typeNotFound() {
            // given
            User owner = mockUser(10L, UserRoleEnum.Owner);
            UUID typeId = UUID.randomUUID();
            RestaurantRequestDto req = mockRequest(10L, typeId, "공리짬뽕", "서울", 11001112);

            given(userRepository.findById(10L)).willReturn(Optional.of(owner));
            given(restaurantTypeRepository.findById(typeId)).willReturn(Optional.empty());

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> restaurantService.createRestaurant(req, owner));
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.RESTAURANT_TYPE_NOT_FOUND);
            verify(restaurantRepository, never()).save(any());
        }

        @Test
        @DisplayName("실패: 본인 아님(FORBIDDEN_UPDATE_RESTAURANT) - checkUser")
        void fail_notSelf_forbiddenUpdate() {
            // given
            User owner = mockUser(10L, UserRoleEnum.Owner);
            User other = mockUser(99L, UserRoleEnum.Customer);
            UUID typeId = UUID.randomUUID();
            RestaurantType type = mock(RestaurantType.class);
            RestaurantRequestDto req = mockRequest(10L, typeId, "공리짬뽕", "서울", 11001112);

            given(userRepository.findById(10L)).willReturn(Optional.of(owner));
            given(restaurantTypeRepository.findById(typeId)).willReturn(Optional.of(type));

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> restaurantService.createRestaurant(req, other));
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.FORBIDDEN_UPDATE_RESTAURANT);
            verify(restaurantRepository, never()).save(any());
        }

        @Test
        @DisplayName("실패: 권한 불가(FORBIDDEN_CREATE_RESTAURANT)")
        void fail_roleForbidden() {
            // given
            User customer = mockUser(10L, UserRoleEnum.Customer); // 허용: Owner/Manager/Master
            UUID typeId = UUID.randomUUID();
            RestaurantType type = mock(RestaurantType.class);
            RestaurantRequestDto req = mockRequest(10L, typeId, "공리짬뽕", "서울", 11001112);

            given(userRepository.findById(10L)).willReturn(Optional.of(customer));
            given(restaurantTypeRepository.findById(typeId)).willReturn(Optional.of(type));

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> restaurantService.createRestaurant(req, customer));
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.FORBIDDEN_CREATE_RESTAURANT);
            verify(restaurantRepository, never()).save(any());
        }
    }

    // ---------- getRestaurant ----------
    @Nested
    class GetRestaurant {

        @Test
        @DisplayName("성공: 단건 상세 조회")
        void success() {
            UUID id = UUID.randomUUID();

            // Restaurant도 mock으로 만들어서 from()이 호출할 법한 게터 스텁
            Restaurant r = mock(Restaurant.class);
            when(r.getRestaurantId()).thenReturn(id);
            when(r.getRestaurantName()).thenReturn("공리짬뽕");
            when(r.getRestaurantAddress()).thenReturn("서울");
            RestaurantType t = mock(RestaurantType.class);
            t.setRestaurantTypeCode(20);
            when(r.getRestaurantTypeId()).thenReturn(t);
            when(r.isStatus()).thenReturn(true);

            given(restaurantRepository.findById(id)).willReturn(Optional.of(r));

            RestaurantDetailDto dto = restaurantService.getRestaurant(id);

            assertThat(dto).isNotNull();
            verify(restaurantRepository).findById(id);
        }

        @Test
        @DisplayName("실패: RESTAURANT_NOT_FOUND")
        void notFound() {
            UUID id = UUID.randomUUID();
            given(restaurantRepository.findById(id)).willReturn(Optional.empty());

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> restaurantService.getRestaurant(id));
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.RESTAURANT_NOT_FOUND);
        }
    }

    // ====== 테스트 헬퍼 (세터/생성자 없어도 동작하게 mock 기반) ======
    private User mockUser(Long id, UserRoleEnum role) {
        User u = mock(User.class);
        when(u.getUserId()).thenReturn(id);
        when(u.getRole()).thenReturn(role);
        return u;
    }

    private RestaurantRequestDto mockRequest(Long userId, UUID typeId, String name, String addr, int region) {
        RestaurantRequestDto dto = mock(RestaurantRequestDto.class);
        when(dto.getUserId()).thenReturn(userId);
        when(dto.getRestaurantTypeId()).thenReturn(typeId);
        when(dto.getRestaurantName()).thenReturn(name);
        when(dto.getRestaurantAddress()).thenReturn(addr);
        when(dto.getRegionCode()).thenReturn(region);
        when(dto.getRestaurantNumber()).thenReturn("02-1234-5678");
        return dto;
    }
}
