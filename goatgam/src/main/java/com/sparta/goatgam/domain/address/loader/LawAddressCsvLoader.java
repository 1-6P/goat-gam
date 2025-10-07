package com.sparta.goatgam.domain.address.loader;

import com.sparta.goatgam.domain.address.entity.*;
import com.sparta.goatgam.domain.address.repository.BeopjeongCodeRawRepository;
import com.sparta.goatgam.domain.address.repository.BeopjeongdongRepository;
import com.sparta.goatgam.domain.address.repository.SidoRepository;
import com.sparta.goatgam.domain.address.repository.SigunguRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class LawAddressCsvLoader implements CommandLineRunner {

    private final SidoRepository sidoRepo;
    private final SigunguRepository sigunguRepo;
    private final BeopjeongdongRepository dongRepo;
    private final BeopjeongCodeRawRepository rawRepo;
    private final ResourceLoader resourceLoader;

    /** 로더 실행 스위치 (기본 false: 운영에서 실수 방지) */
    @Value("${lawaddr.csv.enabled:false}")
    private boolean enabled;

    /** CSV 위치: classpath:법정동.csv 또는 file:/abs/path/법정동.csv */
    @Value("${lawaddr.csv.path:}")
    private String csvPath;

    /** CSV 문자셋 (기본 UTF-8, 필요시 CP949 등으로 교체 가능) */
    @Value("${lawaddr.csv.charset:UTF-8}")
    private String csvCharset;

    // 값 매핑: "폐지" 등 → true, "존재" 등 → false
    private boolean toAbolished(String s) {
        if (s == null) return false;
        String v = s.trim().toLowerCase();
        if (v.contains("폐")) return true;        // "폐지", "폐"
        if (v.contains("존재")) return false;
        if (v.equals("y") || v.equals("yes") || v.equals("true") || v.equals("1")) return true;
        if (v.equals("n") || v.equals("no") || v.equals("false") || v.equals("0")) return false;
        return false;
    }

    /**
     * 법정동코드 정규화:
     * - 원본이 10자리면 그대로 사용
     * - 8자리면 오른쪽에 "00" 붙여 10자리
     * - 9자리면 오른쪽에 "0" 붙여 10자리
     * - 10자리 초과면 앞 10자리만 사용
     * - 8자리 미만/비정상은 null 반환하고 스킵
     */
    private String normalizeCode10(String raw) {
        if (raw == null) return null;
        String digits = raw.replaceAll("\\D", ""); // 숫자만
        if (digits.length() == 10) return digits;
        if (digits.length() == 8)  return digits + "00"; // ✅ 오른쪽 패딩
        if (digits.length() == 9)  return digits + "0";  // ✅ 오른쪽 패딩
        if (digits.length() > 10)  return digits.substring(0,10);
        // 8자리 미만 -> 스킵
        return null;
    }


    // 고마워 지피티니야
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (!enabled) {
            log.info("[LawAddressCsvLoader] disabled. (lawaddr.csv.enabled=false)");
            return;
        }
        if (csvPath == null || csvPath.isBlank()) {
            log.warn("[LawAddressCsvLoader] csvPath is blank. Set lawaddr.csv.path");
            return;
        }

        Charset charset = StandardCharsets.UTF_8;
        try {
            charset = Charset.forName(csvCharset);
        } catch (Exception e) {
            log.warn("[LawAddressCsvLoader] Unsupported charset '{}', fallback to UTF-8", csvCharset);
        }

        Resource resource = resourceLoader.getResource(
                csvPath.startsWith("classpath:") || csvPath.startsWith("file:")
                        ? csvPath
                        : "file:" + csvPath
        );

        if (!resource.exists()) {
            log.error("[LawAddressCsvLoader] CSV not found at: {}", csvPath);
            return;
        }

        long started = System.currentTimeMillis();
        log.info("[LawAddressCsvLoader] Start loading CSV: {}, charset={}", csvPath, charset);

        try (Reader in = new InputStreamReader(resource.getInputStream(), charset)) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .parse(in);

            // 캐시(성능): 이미 본 sido/sigungu는 메모리에서 재사용
            Map<String, Sido> sidoCache = new HashMap<>();
            Map<String, Sigungu> sigunguCache = new HashMap<>();

            long row = 0;
            long upsertSido = 0, upsertSigungu = 0, upsertDong = 0, rawCount = 0;

            for (CSVRecord r : records) {
                row++;

                // 컬럼명은 CSV 헤더 그대로 사용
                String code10Raw   = r.get("법정동코드");
                String sidoName    = r.get("시도명");
                String sigunguName = r.get("시군구명");
                String dongName    = r.get("법정동명");
                String abolishedStr = r.isMapped("폐지여부") ? r.get("폐지여부") : null;

                String code10 = normalizeCode10(code10Raw);
                if (code10 == null) {
                    log.warn("Skip row={} : invalid code10 raw='{}'", row, code10Raw);
                    continue;
                }

                // 항상 10자리 보장된 상태
                String base8      = code10.substring(0, 8);  // 읍/면/동 레벨
                String sidoCode    = base8.substring(0, 2);  // 시/도
                String sigunguCode = base8.substring(0, 5);  // 시/군/구
                String dongCode    = base8;                  // 읍/면/동

                boolean abolished  = toAbolished(abolishedStr);

                // 1) Sido upsert
                Sido sido = sidoCache.get(sidoCode);
                if (sido == null) {
                    sido = sidoRepo.findById(sidoCode).orElse(
                            Sido.builder().sidoCode(sidoCode).name(sidoName).abolished(false).build()
                    );
                    // 이름 갱신(변경 가능성 반영)
                    if (!sidoName.equals(sido.getName())) {
                        sido.setName(sidoName);
                    }
                    sido = sidoRepo.save(sido);
                    sidoCache.put(sidoCode, sido);
                    upsertSido++;
                } else if (!sido.getName().equals(sidoName)) {
                    sido.setName(sidoName);
                    sidoRepo.save(sido);
                    upsertSido++;
                }

                // 2) Sigungu upsert
                Sigungu sigungu = sigunguCache.get(sigunguCode);
                if (sigungu == null) {
                    sigungu = sigunguRepo.findById(sigunguCode).orElse(
                            Sigungu.builder()
                                    .sigunguCode(sigunguCode)
                                    .sido(sido)
                                    .name(sigunguName)
                                    .abolished(abolished)
                                    .build()
                    );
                    sigungu.setSido(sido);
                    if (!sigunguName.equals(sigungu.getName())) sigungu.setName(sigunguName);
                    if (sigungu.isAbolished() != abolished) sigungu.setAbolished(abolished);
                    sigungu = sigunguRepo.save(sigungu);
                    sigunguCache.put(sigunguCode, sigungu);
                    upsertSigungu++;
                } else {
                    boolean changed = false;
                    if (!sigungu.getName().equals(sigunguName)) { sigungu.setName(sigunguName); changed = true; }
                    if (sigungu.isAbolished() != abolished) { sigungu.setAbolished(abolished); changed = true; }
                    if (changed) { sigunguRepo.save(sigungu); upsertSigungu++; }
                }

                // 3) Dong upsert
                Beopjeongdong dong = dongRepo.findById(dongCode).orElse(
                        Beopjeongdong.builder()
                                .dongCode(dongCode)
                                .sigungu(sigungu)
                                .name(dongName)
                                .abolished(abolished)
                                .build()
                );
                boolean changed = false;
                if (dong.getSigungu() == null || !sigunguCode.equals(dong.getSigungu().getSigunguCode())) {
                    dong.setSigungu(sigungu); changed = true;
                }
                if (!dong.getName().equals(dongName)) { dong.setName(dongName); changed = true; }
                if (dong.isAbolished() != abolished) { dong.setAbolished(abolished); changed = true; }
                if (changed || !dongRepo.existsById(dongCode)) {
                    dongRepo.save(dong);
                    upsertDong++;
                }

                // 4) 원본 로그 저장(선택)
                BeopjeongCodeRaw raw = BeopjeongCodeRaw.builder()
                        .code10(code10)
                        .sidoName(sidoName)
                        .sigunguName(sigunguName)
                        .dongName(dongName)
                        .abolished(abolished)
                        .loadedAt(OffsetDateTime.now())
                        .build();
                rawRepo.save(raw);
                rawCount++;

                if (row % 1000 == 0) {
                    log.info("[LawAddressCsvLoader] processed={} upserts => sido:{} sigungu:{} dong:{} raw:{}",
                            row, upsertSido, upsertSigungu, upsertDong, rawCount);
                }
            }

            long took = System.currentTimeMillis() - started;
            log.info("[LawAddressCsvLoader] done. rows={} upserts => sido:{} sigungu:{} dong:{} raw:{} ({} ms)",
                    row, upsertSido, upsertSigungu, upsertDong, rawCount, took);
        }
    }
}