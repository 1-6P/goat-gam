package com.sparta.goatgam.controller;

import com.sparta.goatgam.dto.SignupRequestDto;
import com.sparta.goatgam.dto.UserInfoDto;
import com.sparta.goatgam.entity.User;
import com.sparta.goatgam.entity.UserRoleEnum;
import com.sparta.goatgam.repository.UserRepository;
import com.sparta.goatgam.security.UserDetailsImpl;
import com.sparta.goatgam.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final UserRepository userRepository;

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

    @PreAuthorize("hasAnyAuthority('Master','Manager')")
    @GetMapping("/users")
    public List<UserInfoDto> getAllUsers (){
        List<User> users = userRepository.findAll();

        return users.stream()
                .map(user -> new UserInfoDto(
                        user.getUserId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getPhoneNumber(),
                        user.getAddress(),
                        true, // 활성화 여부 (필요하면 user.isActive() 같은 값으로 교체)
                        user.getRole() == UserRoleEnum.Master || user.getRole() == UserRoleEnum.Manager
                ))
                .toList();
    }
}
