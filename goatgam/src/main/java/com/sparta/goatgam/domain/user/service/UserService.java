package com.sparta.goatgam.domain.user.service;

import com.sparta.goatgam.domain.user.dto.SignupRequestDto;
import com.sparta.goatgam.domain.user.entity.User;
import com.sparta.goatgam.domain.user.entity.UserRoleEnum;
import com.sparta.goatgam.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // ADMIN_TOKEN
    private final String ADMIN_TOKEN = "AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC";

    public void signup(SignupRequestDto RequestDto) {

        String username = RequestDto.getUsername();
        String nickname = RequestDto.getNickname();
        String email = RequestDto.getEmail();
        String encodedPassword = passwordEncoder.encode(RequestDto.getPassword());
        UserRoleEnum  role = RequestDto.getRole();
        String phoneNumber = RequestDto.getPhoneNumber();
        String address = RequestDto.getAddress();

        // 별명 중복 확인
        Optional<User> checkNickname = userRepository.findByNickname(nickname);
        if (checkNickname.isPresent()) {
            throw new IllegalArgumentException("중복된 nickname이 존재합니다.");
        }

        // email 중복확인
        Optional<User> checkEmail = userRepository.findByEmail(email);
        if (checkEmail.isPresent()) {
            throw new IllegalArgumentException("중복된 Email 입니다.");
        }

        // 사용자 등록
        User user = new User(username, nickname, encodedPassword, email, role, phoneNumber, address);
        userRepository.save(user);
    }
}