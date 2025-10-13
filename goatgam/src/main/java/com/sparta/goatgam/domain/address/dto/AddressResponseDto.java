package com.sparta.goatgam.domain.address.dto;

import com.sparta.goatgam.domain.address.entity.Address;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class AddressResponseDto {
    private UUID addressId;
    private Long userId;
    private String sidoCode;
    private String sigunguCode;
    private String dongCode;
    private String roadAddress;
    private String detail;
    private boolean defaultAddress;

    // 엔티티에서 그대로 뽑아오는 기본 팩토리
    public static AddressResponseDto from(Address a) {
        return new AddressResponseDto(
                a.getId(),
                a.getUserId(),
                a.getSido().getSidoCode(),
                a.getSigungu().getSigunguCode(),
                a.getDong().getDongCode(),
                a.getRoadAddress(),
                a.getDetail(),
                a.isDefault()
        );
    }

    // 기본주소 여부를 호출부에서 “덮어쓰기” 하고 싶을 때 쓰는 오버로드
    public static AddressResponseDto of(Address a, boolean defaultAddress) {
        return new AddressResponseDto(
                a.getId(),
                a.getUserId(),
                a.getSido().getSidoCode(),
                a.getSigungu().getSigunguCode(),
                a.getDong().getDongCode(),
                a.getRoadAddress(),
                a.getDetail(),
                defaultAddress
        );
    }
}
