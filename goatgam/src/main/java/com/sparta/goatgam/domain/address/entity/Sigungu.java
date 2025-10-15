package com.sparta.goatgam.domain.address.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "p_sigungu",
        indexes = {
                @Index(name = "idx_sigungu_sido", columnList = "sido_code")
        })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Sigungu {
    @Id
    @Column(name = "sigungu_code", length = 5, nullable = false, updatable = false)
    private String sigunguCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sido_code", nullable = false)
    private Sido sido;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "abolished", nullable = false)
    private boolean abolished = false;
}