package com.sparta.goatgam.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserInfoDto {
    Long userId;
    String username;
    String email;
    String phoneNumber;
    String address;
    Boolean status;
    Boolean admin;
}
