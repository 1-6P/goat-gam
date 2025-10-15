package com.sparta.goatgam.domain.address.dto;

import lombok.Data;

@Data
public class AddressCreateRequestDto {
    private String BeopjeongDong;
    private String roadAddress;
    private String detail;
    private boolean defaultAddress;
}
