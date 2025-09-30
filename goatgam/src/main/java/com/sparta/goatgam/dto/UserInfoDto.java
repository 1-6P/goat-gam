package com.sparta.goatgam.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

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
