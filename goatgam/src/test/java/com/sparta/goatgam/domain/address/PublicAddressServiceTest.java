package com.sparta.goatgam.domain.address;

import com.sparta.goatgam.domain.address.dto.BeopjeongdongSearchDto;
import com.sparta.goatgam.domain.address.entity.Sido;
import com.sparta.goatgam.domain.address.entity.Sigungu;
import com.sparta.goatgam.domain.address.repository.BeopjeongdongRepository;
import com.sparta.goatgam.domain.address.repository.SidoRepository;
import com.sparta.goatgam.domain.address.repository.SigunguRepository;
import com.sparta.goatgam.domain.address.service.PublicAddressService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * PublicAddressService 단위 테스트
 * - Mockito + JUnit5
 * - 이전 RestaurantServiceTest/AddressServiceTest와 톤 맞춤
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class PublicAddressServiceTest {

    @Mock
    private SidoRepository sidoRepository;
    @Mock
    private SigunguRepository sigunguRepository;
    @Mock
    private BeopjeongdongRepository beopjeongdongRepository;

    @InjectMocks
    private PublicAddressService publicAddressService;

    // ---------------- listSido ----------------
    @Nested
    class ListSido {

        @Test
        @DisplayName("성공: 폐지되지 않은 시도 전체(오름차순)")
        void success_list_all_active_sido() {
            // given
            Sido s1 = mock(Sido.class);
            Sido s2 = mock(Sido.class);
            given(sidoRepository.findAllByAbolishedFalseOrderByNameAsc())
                    .willReturn(List.of(s1, s2));

            // when
            List<Sido> res = publicAddressService.listSido();

            // then
            assertThat(res).hasSize(2);
            verify(sidoRepository, times(1)).findAllByAbolishedFalseOrderByNameAsc();
        }

        @Test
        @DisplayName("성공: 결과가 비어도 빈 리스트 반환")
        void success_empty_result() {
            // given
            given(sidoRepository.findAllByAbolishedFalseOrderByNameAsc())
                    .willReturn(List.of());

            // when
            List<Sido> res = publicAddressService.listSido();

            // then
            assertThat(res).isEmpty();
        }
    }

    // ---------------- listSigungu ----------------
    @Nested
    class ListSigungu {

        @Test
        @DisplayName("성공: sidoCode가 null/blank면 빈 리스트 및 repo 호출 안함")
        void success_blank_code_returns_empty_without_repo_call() {
            // when
            List<Sigungu> r1 = publicAddressService.listSigungu(null);
            List<Sigungu> r2 = publicAddressService.listSigungu("");
            List<Sigungu> r3 = publicAddressService.listSigungu("   ");

            // then
            assertThat(r1).isEmpty();
            assertThat(r2).isEmpty();
            assertThat(r3).isEmpty();
            verify(sigunguRepository, never())
                    .findBySido_SidoCodeAndAbolishedFalseOrderByNameAsc(anyString());
        }

        @Test
        @DisplayName("성공: 유효한 sidoCode면 트리밍되어 조회 호출")
        void success_valid_code_calls_repo_with_trimmed() {
            // given
            Sigungu g1 = mock(Sigungu.class);
            given(sigunguRepository.findBySido_SidoCodeAndAbolishedFalseOrderByNameAsc("11"))
                    .willReturn(List.of(g1));

            // when
            List<Sigungu> res = publicAddressService.listSigungu(" 11 ");

            // then
            assertThat(res).hasSize(1);
            verify(sigunguRepository, times(1))
                    .findBySido_SidoCodeAndAbolishedFalseOrderByNameAsc("11");
        }
    }

    // ---------------- search ----------------
    @Nested
    class SearchBeopjeongdong {

        @Captor
        ArgumentCaptor<String> qLowerCap;
        @Captor
        ArgumentCaptor<String> qLikeCap;
        @Captor
        ArgumentCaptor<String> qPrefixCap;
        @Captor
        ArgumentCaptor<String> sidoCap;
        @Captor
        ArgumentCaptor<String> sigunguCap;
        @Captor
        ArgumentCaptor<Pageable> pageableCap;

        @Test
        @DisplayName("성공: qNorm, sidoCode, sigunguCode가 주어지면 lower/like/prefix 및 trim 적용")
        void success_patterns_and_trim_applied() {
            // given
            Pageable pageable = mock(Pageable.class);
            BeopjeongdongSearchDto d1 = mock(BeopjeongdongSearchDto.class);
            BeopjeongdongSearchDto d2 = mock(BeopjeongdongSearchDto.class);

            Page<BeopjeongdongSearchDto> page = new PageImpl<>(List.of(d1, d2));
            given(beopjeongdongRepository.search(any(), any(), any(), any(), any(), any()))
                    .willReturn(page);

            // when
            PagedModel<BeopjeongdongSearchDto> model =
                    publicAddressService.search("  SeoUl  ", " 11 ", " 1101 ", pageable);

            // then
            assertThat(model).isNotNull();
            assertThat(model.getContent()).hasSize(2);

            verify(beopjeongdongRepository, times(1))
                    .search(qLowerCap.capture(), qLikeCap.capture(), qPrefixCap.capture(),
                            sidoCap.capture(), sigunguCap.capture(), pageableCap.capture());

            assertThat(qLowerCap.getValue()).isEqualTo("seoul");
            assertThat(qLikeCap.getValue()).isEqualTo("%seoul%");
            assertThat(qPrefixCap.getValue()).isEqualTo("seoul%");
            assertThat(sidoCap.getValue()).isEqualTo("11");
            assertThat(sigunguCap.getValue()).isEqualTo("1101");
            assertThat(pageableCap.getValue()).isSameAs(pageable);
        }

        @Test
        @DisplayName("성공: qNorm/sidoCode/sigunguCode가 공백이면 null 로 전달")
        void success_null_when_blank_inputs() {
            // given
            Pageable pageable = mock(Pageable.class);
            BeopjeongdongSearchDto d = mock(BeopjeongdongSearchDto.class);
            Page<BeopjeongdongSearchDto> page = new PageImpl<>(List.of(d));
            given(beopjeongdongRepository.search(any(), any(), any(), any(), any(), any()))
                    .willReturn(page);

            // when
            PagedModel<BeopjeongdongSearchDto> model =
                    publicAddressService.search("   ", "   ", null, pageable);

            // then
            assertThat(model).isNotNull();
            assertThat(model.getContent()).hasSize(1);

            verify(beopjeongdongRepository).search(
                    isNull(),  // qLower
                    isNull(),  // qLike
                    isNull(),  // qPrefix
                    isNull(),  // sido
                    isNull(),  // sigungu
                    eq(pageable)
            );
        }
    }
}
