package com.sparta.goatgam.domain.address.controller;

import com.sparta.goatgam.domain.address.dto.BeopjeongdongSearchDto;
import com.sparta.goatgam.domain.address.dto.PublicAddressSearchRequestDto;
import com.sparta.goatgam.domain.address.entity.Sido;
import com.sparta.goatgam.domain.address.entity.Sigungu;
import com.sparta.goatgam.domain.address.service.PublicAddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

import static com.sparta.goatgam.global.util.PageableUtils.makePageable;
import static com.sparta.goatgam.global.util.PageableUtils.order;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/public")
@Tag(name = "주소 정보 API", description = "주소 정보 관련 기능 API입니다.")
public class PublicAddressController {

    private final PublicAddressService publicAddressService;

    // 시도 목록 조회
    @GetMapping("/sido")
    @Operation(summary = "시/도 조회", description = "전국 시/도의 코드 및 이름을 조회")
    public ResponseEntity<List<Sido>> listSido() {
        return ResponseEntity.ok(publicAddressService.listSido());
    }

    // 시군구 목록 조회
    @GetMapping("/sigungu")
    @Operation(summary = "시/군/구 조회", description = "해당 시/도의 시/군/구를 조회")
    public ResponseEntity<List<Sigungu>> listSigungu(@RequestParam String sidoCode) {
        return ResponseEntity.ok(publicAddressService.listSigungu(sidoCode));
    }

    // 법정동 검색
    @GetMapping("/beopjeongdong/search")
    @Operation(summary = "법정동 검색", description = "해당 주소의 법정동 코드를 검색 가능")
    public ResponseEntity<PagedModel<BeopjeongdongSearchDto>> search(
            PublicAddressSearchRequestDto requestDto
    ) {
        String qNorm = normalizeKo(requestDto.getDongNameKeyword());

        Pageable pageable = makePageable(
                requestDto.getPage(),
                requestDto.getSize(),
                order(Sort.Direction.ASC, "name")  // 기본 정렬 기준(원하는 필드로 교체 가능)
        );

        return ResponseEntity.ok(publicAddressService.search(
                qNorm,
                requestDto.getSidoCode(),
                requestDto.getSigunguCode(),
                pageable
        ));
    }

    static String normalizeKo(String s) {
        if (s == null) return null;
        return s.toLowerCase(Locale.KOREAN)
                .replaceAll("\\s+", "");
    }

}
