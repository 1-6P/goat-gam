package com.sparta.goatgam.service;

import com.sparta.goatgam.dto.SignupRequestDto;
import com.sparta.goatgam.entity.User;
import com.sparta.goatgam.entity.UserRoleEnum;
import com.sparta.goatgam.repository.UserRepository;
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

    public void signup(SignupRequestDto signupRequestDto) {

        String username = signupRequestDto.getUsername();
        String nickname = signupRequestDto.getNickname();
        String email = signupRequestDto.getEmail();
        String password = signupRequestDto.getPassword();
        UserRoleEnum  role = signupRequestDto.getRole();
        String phoneNumber = signupRequestDto.getPhoneNumber();
        String address = signupRequestDto.getAddress();

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
        User user = new User(username, nickname, password, email, role, phoneNumber, address);
        userRepository.save(user);
    }
}

//
//    }
//}