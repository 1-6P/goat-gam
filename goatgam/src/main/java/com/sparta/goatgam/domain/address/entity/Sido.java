package com.sparta.goatgam.domain.address.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "p_sido")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sido {
    @Id
    @Column(name = "sido_code", length = 2, nullable = false, updatable = false)
    private String sidoCode;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "abolished", nullable = false)
    private boolean abolished = false;
}
