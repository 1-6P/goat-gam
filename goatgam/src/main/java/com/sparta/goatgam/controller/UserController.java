package com.sparta.goatgam.controller;

import com.sparta.goatgam.dto.SignupRequestDto;
import com.sparta.goatgam.dto.UserInfoDto;
import com.sparta.goatgam.entity.UserRoleEnum;
import com.sparta.goatgam.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public String signup(@Valid SignupRequestDto requestDto, BindingResult bindingResult) {
        // Validation 예외처리
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        if(fieldErrors.size() > 0) {
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                log.error(fieldError.getField() + " 필드 : " + fieldError.getDefaultMessage());
            }
            return "redirect:/api/v1/signup";
        }

        userService.signup(requestDto);

        return "redirect:/api/v1/login-page";
    }
    @GetMapping("/users")
    @ResponseBody
    public UserInfoDto getAllUsers (@AuthenticationPrincipal UserDetailsImpl userDetails){
        String username = userDetails.getUser().getUsername();
        UserRoleEnum role = userDetails.getUser().getRole();
        boolean isAdmin = (role == UserRoleEnum.Master || role == UserRoleEnum.Manager);

        return new UserInfoDto(username, isAdmin);
    }
}

//
//    // 회원 관련 정보 받기
//    @GetMapping("/user-info")
//    @ResponseBody
//    public UserInfoDto getUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
//        String username = userDetails.getUser().getUsername();
//        UserRoleEnum role = userDetails.getUser().getRole();
//        boolean isAdmin = (role == UserRoleEnum.ADMIN);
//
//        return new UserInfoDto(username, isAdmin);
//    }
//}