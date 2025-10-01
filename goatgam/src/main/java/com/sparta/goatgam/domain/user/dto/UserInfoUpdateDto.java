package com.sparta.goatgam.domain.user.dto;

import com.sparta.goatgam.domain.user.entity.UserRoleEnum;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoUpdateDto {

    String username;
    String nickname;
    String password;
    UserRoleEnum userRole;
    String phoneNumber;
    String address;
}
