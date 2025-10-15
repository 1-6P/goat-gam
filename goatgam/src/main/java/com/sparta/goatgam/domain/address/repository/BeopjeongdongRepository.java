// BeopjeongdongRepository.java
package com.sparta.goatgam.domain.address.repository;

import com.sparta.goatgam.domain.address.dto.BeopjeongdongSearchDto;
import com.sparta.goatgam.domain.address.entity.Beopjeongdong;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BeopjeongdongRepository extends JpaRepository<Beopjeongdong, String> {
    // 지피티니야 고마웡...
    @Query("""
                SELECT new com.sparta.goatgam.domain.address.dto.BeopjeongdongSearchDto(
                    d.dongCode,
                    d.name,
                    sg.sigunguCode,
                    sg.name,
                    sd.sidoCode,
                    sd.name
                )
                FROM Beopjeongdong d
                JOIN d.sigungu sg
                JOIN sg.sido sd
                WHERE d.abolished = false
                  AND sg.abolished = false
                  AND sd.abolished = false
                  AND (:sidoCode IS NULL OR sd.sidoCode = :sidoCode)
                  AND (:sigunguCode IS NULL OR sg.sigunguCode = :sigunguCode)
                  AND (:qLike IS NULL OR LOWER(d.name) LIKE :qLike)
                ORDER BY
                  CASE
                    WHEN LOWER(d.name) = :qLower THEN 0
                    WHEN :qPrefix IS NOT NULL AND LOWER(d.name) LIKE :qPrefix THEN 1
                    ELSE 2
                  END,
                  d.name ASC
            """)
    Page<BeopjeongdongSearchDto> search(
            @Param("qLower") String qLower,
            @Param("qLike") String qLike,
            @Param("qPrefix") String qPrefix,
            @Param("sidoCode") String sidoCode,
            @Param("sigunguCode") String sigunguCode,
            Pageable pageable
    );
}
