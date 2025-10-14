package com.sparta.goatgam.domain.address.service;

import com.sparta.goatgam.domain.address.dto.BeopjeongdongSearchDto;
import com.sparta.goatgam.domain.address.entity.Sido;
import com.sparta.goatgam.domain.address.entity.Sigungu;
import com.sparta.goatgam.domain.address.repository.BeopjeongdongRepository;
import com.sparta.goatgam.domain.address.repository.SidoRepository;
import com.sparta.goatgam.domain.address.repository.SigunguRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PublicAddressService {

    private final SidoRepository sidoRepository;
    private final SigunguRepository sigunguRepository;
    private final BeopjeongdongRepository beopjeongdongRepository;

    public List<Sido> listSido() {
        return sidoRepository.findAllByAbolishedFalseOrderByNameAsc();
    }

    public List<Sigungu> listSigungu(String sidoCode) {
        if (isBlank(sidoCode)) return List.of();
        return sigunguRepository.findBySido_SidoCodeAndAbolishedFalseOrderByNameAsc(sidoCode);
    }

    public ResponseEntity<List<BeopjeongdongSearchDto>> search(
            String qNorm,
            String sidoCode,
            String sigunguCode,
            PageRequest page
    ) {
        String qLower = toLowerOrNull(qNorm);
        String sd     = trimOrNull(sidoCode);
        String sg     = trimOrNull(sigunguCode);

        List<BeopjeongdongSearchDto> result =
                beopjeongdongRepository.search(qLower, sd, sg, page);

        return ResponseEntity.ok(result);
    }

    // ---- utils ----
    private static boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
    private static String trimOrNull(String s) { return isBlank(s) ? null : s.trim(); }
    private static String toLowerOrNull(String s) { return isBlank(s) ? null : s.trim().toLowerCase(); }
}
