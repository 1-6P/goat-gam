package com.sparta.goatgam.domain.user.controller;

import com.sparta.goatgam.domain.user.dto.*;
import com.sparta.goatgam.global.jwt.jwt.JwtUtil;
import com.sparta.goatgam.domain.user.repository.UserRepository;
import com.sparta.goatgam.global.security.security.UserDetailsImpl;
import com.sparta.goatgam.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<?> login(@RequestBody LoginRequestDto requestDto) {
        log.info("로그인 시도: email={}", requestDto.getEmail());

        try {
            var auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestDto.getEmail(), requestDto.getPassword())
            );
            var principal = (UserDetailsImpl) auth.getPrincipal();
            var token = jwtUtil.createToken(principal.getEmail(), principal.getRole());
            log.info("로그인 성공: email={}, role={}", principal.getEmail(), principal.getRole());
            return ResponseEntity.ok(Map.of("tokenType", "Bearer", "token", token));
        } catch (AuthenticationException e) {
            log.warn("로그인 실패: {}", e.getMessage());
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }
    }

    @PutMapping("/user/{userId}")
    public ResponseEntity<?> userInfoUpdate (@Valid @RequestBody UserInfoUpdateDto requestDto, @PathVariable Long userId) {
        userService.updateUser(userId, requestDto);
        return ResponseEntity.ok(new UserUpdateResponseDto("OK", userId));
    }

    // 유저 본인 계정 삭제 (소프트 삭제)
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/user/me")
    public ResponseEntity<?> deleteMyAccount (@AuthenticationPrincipal UserDetailsImpl principal) {
        userService.softDelete(principal.getUser().getUserId());
        return ResponseEntity.noContent().build();
    }

    // 관리자 특정 유저 비활성화
    @PreAuthorize("hasAnyAuthority('Master','Manager')")
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<?> deleteUser (@PathVariable Long userId) {
        userService.softDelete(userId);
        return ResponseEntity.noContent().build();
    }
    // 관리자 특정 유저 복구
    @PreAuthorize("hasAnyAuthority('Master','Manager')")
    @DeleteMapping("/user/{userId}/restore")
    public ResponseEntity<?> restoreUser (@PathVariable Long userId) {
        userService.restore(userId);
        return ResponseEntity.noContent().build();
    }


    // 관리자 모든 유저 조회
    @PreAuthorize("hasAnyAuthority('Master','Manager')")
    @GetMapping("/user")
    public ResponseEntity<List<UserInfoDto>> getAllUsers (){
        List<UserInfoDto> users = userService.getAllUsers();

        return ResponseEntity.ok(users);
    }
}
