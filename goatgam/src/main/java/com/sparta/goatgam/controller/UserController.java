package com.sparta.goatgam.controller;

import com.sparta.goatgam.dto.LoginRequestDto;
import com.sparta.goatgam.dto.SignupRequestDto;
import com.sparta.goatgam.dto.UserInfoDto;
import com.sparta.goatgam.entity.User;
import com.sparta.goatgam.entity.UserRoleEnum;
import com.sparta.goatgam.jwt.JwtUtil;
import com.sparta.goatgam.repository.UserRepository;
import com.sparta.goatgam.security.UserDetailsImpl;
import com.sparta.goatgam.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @PostMapping("/auth/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody SignupRequestDto requestDto,
                                         BindingResult bindingResult) {
        // Validation 예외처리
        if(bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .findFirst()
                    .orElse("Invalid input");
            return ResponseEntity.badRequest().body(errorMsg);
        }

        userService.signup(requestDto);

        return ResponseEntity.ok().body("회원 가입 성공");
    }

    @PostMapping("/auth/login")
    public Map<String, String> login(@RequestBody LoginRequestDto  requestDto) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDto.getEmail(), requestDto.getPassword())
        );

        var principal = (UserDetailsImpl) auth.getPrincipal();
        var token = jwtUtil.createToken(principal.getEmail(), principal.getRole());
        return Map.of("tokenType", "Bearer", "token", token);
    }

    @PreAuthorize("hasAnyAuthority('Master','Manager')")
    @GetMapping("/user")
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
