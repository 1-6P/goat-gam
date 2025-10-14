package com.sparta.goatgam.domain.address.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BeopjeongdongSearchDto {
    private String beopjeongdongCode;
    private String beopjeongdongName;
    private String sigunguCode;
    private String sigunguName;
    private String sidoCode;
    private String sidoName;
}
