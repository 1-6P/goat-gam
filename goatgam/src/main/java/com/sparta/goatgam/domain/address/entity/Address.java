package com.sparta.goatgam.domain.address.entity;

import com.sparta.goatgam.domain.user.entity.User;
import com.sparta.goatgam.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "p_address")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address extends BaseEntity {

    @Id
    @Column(name = "address_id", columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dong_code", nullable = false)
    private Beopjeongdong dong;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sigungu_code", nullable = false)
    private Sigungu sigungu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sido_code", nullable = false)
    private Sido sido;

    @Column(name = "road_address", nullable = false, columnDefinition = "TEXT")
    private String roadAddress;

    @Column(name = "detail", columnDefinition = "TEXT")
    private String detail;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault;

    @Column(name = "status", nullable = false)
    private boolean status;

    public void update(Address newAddress) {
        this.dong = newAddress.dong;
        this.sigungu = newAddress.sigungu;
        this.sido = newAddress.sido;
        this.roadAddress = newAddress.roadAddress;
        this.detail = newAddress.detail;
    }
}
