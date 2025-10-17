package com.sparta.goatgam.domain.address;

import com.sparta.goatgam.domain.address.dto.AddressCreateRequestDto;
import com.sparta.goatgam.domain.address.dto.AddressDeleteResponseDto;
import com.sparta.goatgam.domain.address.dto.AddressResponseDto;
import com.sparta.goatgam.domain.address.entity.Address;
import com.sparta.goatgam.domain.address.entity.Beopjeongdong;
import com.sparta.goatgam.domain.address.entity.Sido;
import com.sparta.goatgam.domain.address.entity.Sigungu;
import com.sparta.goatgam.domain.address.repository.AddressRepository;
import com.sparta.goatgam.domain.address.repository.BeopjeongdongRepository;
import com.sparta.goatgam.domain.address.service.AddressService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * AddressService 단위 테스트
 * - Mockito + JUnit5
 * - RestaurantServiceTest 와 동일한 톤/패턴 유지
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;
    @Mock
    private BeopjeongdongRepository beopjeongdongRepository;

    @InjectMocks
    private AddressService addressService;

    // ---------------- addAddress ----------------
    @Nested
    class AddAddress {

        @Test
        @DisplayName("성공: 기본주소(true)로 추가 시 기존 기본주소 해제")
        void success_setDefault_and_unsetPrevious() {
            // given
            Long userId = 7L;
            // 8자리 or 10자리 어떤 입력이 와도 normalize -> base8(id) 조회
            AddressCreateRequestDto req = mockCreateReq("11010515", "서울시 어딘가 1", "101호", true);

            // 행정구역 엔티티 체인 mock
            Sido sido = mock(Sido.class);
            Sigungu sigungu = mock(Sigungu.class);
            given(sigungu.getSido()).willReturn(sido);

            Beopjeongdong dong = mock(Beopjeongdong.class);
            given(dong.getSigungu()).willReturn(sigungu);

            given(beopjeongdongRepository.findById("11010515")).willReturn(Optional.of(dong));

            // 이전 기본주소 존재 -> setDefault(false) 호출 기대
            Address prevDefault = mock(Address.class);
            given(addressRepository.findByUserIdAndIsDefaultTrue(userId)).willReturn(Optional.of(prevDefault));

            // save는 그대로 넘겨받은 엔티티 반환
            given(addressRepository.save(any(Address.class))).willAnswer(inv -> inv.getArgument(0));

            // when
            AddressResponseDto res = addressService.addAddress(userId, req);

            // then
            assertThat(res).isNotNull();
            // 기본주소 true로 들어갔는지(이 필드가 있다면) 확인
            // DTO에 접근자 이름은 프로젝트에 따라 다를 수 있으므로 널만 체크하고 상호작용 위주로 검증
            verify(addressRepository, times(1)).findByUserIdAndIsDefaultTrue(userId);
            verify(prevDefault, times(1)).setDefault(false);
            verify(addressRepository, times(1)).save(any(Address.class));
        }

        @Test
        @DisplayName("성공: 기본주소(false)로 추가 시 이전 기본주소 조회/해제 없음")
        void success_nonDefault_noPreviousTouch() {
            // given
            Long userId = 8L;
            AddressCreateRequestDto req = mockCreateReq("1101051500", "서울시 어딘가 2", "202호", false);

            Sido sido = mock(Sido.class);
            Sigungu sigungu = mock(Sigungu.class);
            given(sigungu.getSido()).willReturn(sido);

            Beopjeongdong dong = mock(Beopjeongdong.class);
            given(dong.getSigungu()).willReturn(sigungu);

            given(beopjeongdongRepository.findById("11010515")).willReturn(Optional.of(dong));
            given(addressRepository.save(any(Address.class))).willAnswer(inv -> inv.getArgument(0));

            // when
            AddressResponseDto res = addressService.addAddress(userId, req);

            // then
            assertThat(res).isNotNull();
            verify(addressRepository, never()).findByUserIdAndIsDefaultTrue(anyLong());
            verify(addressRepository, times(1)).save(any(Address.class));
        }

        @Test
        @DisplayName("실패: 법정동 코드 형식 오류(IllegalArgumentException)")
        void fail_invalid_code_format() {
            // given
            Long userId = 1L;
            AddressCreateRequestDto req = mockCreateReq("abc", "서울시", "303호", true);

            // when & then
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> addressService.addAddress(userId, req));
            assertThat(ex.getMessage()).contains("법정동형식이 올바르지 않습니다");
            verify(addressRepository, never()).save(any());
        }

        @Test
        @DisplayName("실패: 존재하지 않는 법정동 코드(IllegalArgumentException)")
        void fail_beopjeongdong_not_found() {
            // given
            Long userId = 2L;
            AddressCreateRequestDto req = mockCreateReq("11010515", "서울시", "404호", true);
            given(beopjeongdongRepository.findById("11010515")).willReturn(Optional.empty());

            // when & then
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> addressService.addAddress(userId, req));
            assertThat(ex.getMessage()).contains("존재하지 않는 법정동 코드");
            verify(addressRepository, never()).save(any());
        }
    }

    // ---------------- getUserAddress ----------------
    @Nested
    class GetUserAddress {

        @Test
        @DisplayName("성공: status=true 인 주소만 필터링하여 반환")
        void success_filter_only_active() {
            // given
            Long userId = 10L;
            Address inactive = mockAddress(UUID.randomUUID(), userId, false, false, "서울시 비활성", null, null, null);
            Address active = mockAddress(UUID.randomUUID(), userId, true, true, "서울시 활성", null, null, null);

            given(addressRepository.findByUserId(userId)).willReturn(List.of(inactive, active));

            // when
            List<AddressResponseDto> list = addressService.getUserAddress(userId);

            // then
            assertThat(list).hasSize(1);
        }

        @Test
        @DisplayName("실패: 활성 주소 없음 -> IllegalArgumentException")
        void fail_empty_active_list() {
            // given
            Long userId = 11L;
            Address inactive = mockAddress(UUID.randomUUID(), userId, false, false, "아무곳", null, null, null);
            given(addressRepository.findByUserId(userId)).willReturn(List.of(inactive));

            // when & then
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> addressService.getUserAddress(userId));
            assertThat(ex.getMessage()).contains("해당유저의 주소가 존재하지 않습니다");
        }
    }

    // ---------------- delete ----------------
    @Nested
    class DeleteAddress {

        @Test
        @DisplayName("성공: 활성 주소 삭제 -> status=false, deleted() 호출, 성공 메시지")
        void success_delete_active() {
            // given
            Long userId = 100L;
            UUID addrId = UUID.randomUUID();
            Address a = mockAddress(addrId, userId, true, false, "서울시", null, null, null);

            given(addressRepository.findByUserIdAndId(userId, addrId)).willReturn(Optional.of(a));

            // when
            AddressDeleteResponseDto res = addressService.delete(userId, addrId);

            // then
            assertThat(res).isNotNull();
            assertThat(res.getId()).isEqualTo(addrId);
            assertThat(res.getUserId()).isEqualTo(userId);
            assertThat(res.getMessage()).contains("성공적으로 삭제");

            verify(a, times(1)).setStatus(false);
            verify(a, times(1)).deleted(String.valueOf(userId));
        }

        @Test
        @DisplayName("성공: 이미 삭제된 주소 -> 상태 변경 없이 '이미 삭제' 메시지")
        void success_already_deleted() {
            // given
            Long userId = 101L;
            UUID addrId = UUID.randomUUID();
            Address a = mockAddress(addrId, userId, false, false, "서울시", null, null, null);

            given(addressRepository.findByUserIdAndId(userId, addrId)).willReturn(Optional.of(a));

            // when
            AddressDeleteResponseDto res = addressService.delete(userId, addrId);

            // then
            assertThat(res).isNotNull();
            assertThat(res.getMessage()).contains("이미 삭제된");

            verify(a, never()).setStatus(anyBoolean());
            verify(a, never()).deleted(anyString());
        }

        @Test
        @DisplayName("실패: 해당 유저의 주소 없음 -> IllegalArgumentException")
        void fail_not_found() {
            // given
            Long userId = 102L;
            UUID addrId = UUID.randomUUID();
            given(addressRepository.findByUserIdAndId(userId, addrId)).willReturn(Optional.empty());

            // when & then
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> addressService.delete(userId, addrId));
            assertThat(ex.getMessage()).contains("유저에게 해당 주소가 존재하지 않습니다");
        }
    }

    // ---------------- getAllAddress ----------------
    @Nested
    class GetAllAddress {

        @Test
        @DisplayName("성공: 전체 조회 -> DTO 매핑 수행")
        void success_all() {
            // given
            Address a1 = mockAddress(UUID.randomUUID(), 1L, true, false, "서울1", null, null, null);
            Address a2 = mockAddress(UUID.randomUUID(), 2L, false, false, "서울2", null, null, null);
            given(addressRepository.findAll()).willReturn(List.of(a1, a2));

            // when
            List<AddressResponseDto> res = addressService.getAllAddress();

            // then
            assertThat(res).hasSize(2);
        }
    }

    // ===================== 헬퍼 =====================

    private AddressCreateRequestDto mockCreateReq(String beopjeong, String road, String detail, boolean isDefault) {
        AddressCreateRequestDto dto = mock(AddressCreateRequestDto.class);
        when(dto.getBeopjeongDong()).thenReturn(beopjeong);
        when(dto.getRoadAddress()).thenReturn(road);
        when(dto.getDetail()).thenReturn(detail);
        when(dto.isDefaultAddress()).thenReturn(isDefault);
        return dto;
    }

    private Address mockAddress(UUID id, Long userId, boolean status, boolean isDefault,
                                String roadAddress, Beopjeongdong d, Sigungu g, Sido s) {
        Address a = mock(Address.class);
        when(a.getId()).thenReturn(id);
        when(a.getUserId()).thenReturn(userId);
        when(a.isStatus()).thenReturn(status);
        when(a.isDefault()).thenReturn(isDefault);
        when(a.getRoadAddress()).thenReturn(roadAddress);
        when(a.getDong()).thenReturn(d);
        when(a.getSigungu()).thenReturn(g);
        when(a.getSido()).thenReturn(s);
        // setStatus / setDefault / deleted 는 void 이므로 기본 no-op, verify 로만 검증
        return a;
    }
}
