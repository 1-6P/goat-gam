package com.sparta.goatgam.dto;

import com.sparta.goatgam.entity.UserRoleEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequestDto {

    @NotBlank
    private String username;

    @NotBlank
    private String nickname;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private UserRoleEnum role;

    @NotBlank
    private String phoneNumber;

    @NotBlank
    private String address;
}
