package com.sparta.goatgam.domain.user.dto;

import com.sparta.goatgam.domain.user.entity.UserRoleEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoUpdateDto {

    @NotBlank
    private String username;

    @NotBlank
    private String nickname;

    @NotBlank
    private String password;

    @NotNull(message = "role is required")
    private UserRoleEnum userRole;

    @NotBlank
    private String phoneNumber;

    @NotBlank
    private String address;
}
