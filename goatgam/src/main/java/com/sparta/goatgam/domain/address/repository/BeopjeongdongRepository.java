// BeopjeongdongRepository.java
package com.sparta.goatgam.domain.address.repository;

import com.sparta.goatgam.domain.address.dto.BeopjeongdongSearchDto;
import com.sparta.goatgam.domain.address.entity.Beopjeongdong;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

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
          AND (:qLower IS NULL OR LOWER(d.name) LIKE CONCAT('%', :qLower, '%'))
        ORDER BY
          CASE
            WHEN LOWER(d.name) = :qLower THEN 0
            WHEN LOWER(d.name) LIKE CONCAT(:qLower, '%') THEN 1
            ELSE 2
          END,
          d.name ASC
    """)
    List<BeopjeongdongSearchDto> search(
            @Param("qLower") String qLower,
            @Param("sidoCode") String sidoCode,
            @Param("sigunguCode") String sigunguCode,
            Pageable pageable
    );
}
