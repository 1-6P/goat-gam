package com.sparta.goatgam.domain.address.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "p_beopjeongdong",
        indexes = {
                @Index(name = "idx_dong_sigungu", columnList = "sigungu_code"),
                @Index(name = "idx_dong_name", columnList = "name")
        })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Beopjeongdong {
    @Id
    @Column(name = "dong_code", length = 8, nullable = false, updatable = false)
    private String dongCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sigungu_code", nullable = false)
    private Sigungu sigungu;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "abolished", nullable = false)
    private boolean abolished = false;
}
