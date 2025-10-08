package com.sparta.goatgam.domain.address.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class AddressResponseDto {
    private UUID addressId;
    private Long userId;
    private String sidoCode;
    private String sigunguCode;
    private String dongCode;
    private String roadAddress;
    private String detail;
    private boolean defaultAddress;
}
