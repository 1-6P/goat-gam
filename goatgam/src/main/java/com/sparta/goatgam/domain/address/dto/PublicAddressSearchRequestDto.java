package com.sparta.goatgam.domain.address.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PublicAddressSearchRequestDto {
    private String dongNameKeyword;
    private String sidoCode;
    private String sigunguCode;
    private int page;
    private int size;
}
