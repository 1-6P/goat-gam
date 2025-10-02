package com.sparta.goatgam.domain.user.dto;

import com.sparta.goatgam.domain.user.entity.UserRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserInfoDto {
    Long userId;
    String username;
    String nickname;
    String email;
    UserRoleEnum userRole;
    String phoneNumber;
    String address;
    Boolean status;
}
