package com.sparta.goatgam.domain.address.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "p_beopjeong_code_raw")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BeopjeongCodeRaw {
    @Id
    @Column(name = "code_10", length = 10, nullable = false, updatable = false)
    private String code10;

    @Column(name = "sido_name", nullable = false, length = 50)
    private String sidoName;

    @Column(name = "sigungu_name", nullable = false, length = 50)
    private String sigunguName;

    @Column(name = "dong_name", nullable = false, length = 50)
    private String dongName;

    @Column(name = "abolished", nullable = false)
    private boolean abolished;

    @Column(name = "loaded_at", columnDefinition = "timestamptz", nullable = false)
    private OffsetDateTime loadedAt;
}