package com.sparta.goatgam.domain.address.controller;

import com.sparta.goatgam.domain.address.dto.BeopjeongdongSearchDto;
import com.sparta.goatgam.domain.address.entity.Sido;
import com.sparta.goatgam.domain.address.entity.Sigungu;
import com.sparta.goatgam.domain.address.service.PublicAddressService;
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
public class PublicAddressController {

    private final PublicAddressService publicAddressService;

    // 시도 목록 조회
    @GetMapping("/sido")
    public ResponseEntity<List<Sido>> listSido(){
        return ResponseEntity.ok(publicAddressService.listSido());
    }
    // 시군구 목록 조회
    @GetMapping("/sigungu")
    public ResponseEntity<List<Sigungu>> listSigungu(@RequestParam String sidoCode){
        return ResponseEntity.ok(publicAddressService.listSigungu(sidoCode));
    }
    // 법정동 검색
    @GetMapping("/beopjeongdong/search")
    public ResponseEntity<PagedModel<BeopjeongdongSearchDto>> search(
            @RequestParam(required = false) String dongNameKeyword,
            @RequestParam(required = false) String sidoCode,
            @RequestParam(required = false) String sigunguCode,
            @RequestParam int page,
            @RequestParam int size
    ){
        String qNorm = normalizeKo(dongNameKeyword);

        Pageable pageable = makePageable(
                page,
                size,
                order(Sort.Direction.ASC, "name")  // 기본 정렬 기준(원하는 필드로 교체 가능)
        );

        return ResponseEntity.ok(publicAddressService.search(qNorm, sidoCode, sigunguCode, pageable));
    }

    static String normalizeKo(String s) {
        if (s == null) return null;
        return s.toLowerCase(Locale.KOREAN)
                .replaceAll("\\s+", "");
    }

}
