package com.sparta.goatgam.domain.address.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddressChangeDto {
    private String BeopjeongDong;
    private String roadAddress;
    private String detail;
}
