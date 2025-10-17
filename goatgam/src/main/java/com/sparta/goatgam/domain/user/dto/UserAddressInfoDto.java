package com.sparta.goatgam.domain.user.dto;

import com.sparta.goatgam.domain.address.entity.Address;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class UserAddressInfoDto {
    private UUID addressId;
    private String sidoCode;
    private String sigunguCode;
    private String dongCode;
    private String roadAddress;
    private String detail;
    private boolean defaultAddress;

    public UserAddressInfoDto(Address a) {
        this.addressId = a.getId();
        this.sidoCode = a.getSido().getSidoCode();
        this.sigunguCode = a.getSigungu().getSigunguCode();
        this.dongCode = a.getDong().getDongCode();
        this.roadAddress = a.getRoadAddress();
        this.detail = a.getDetail();
        this.defaultAddress = a.isDefault();
    }
}
