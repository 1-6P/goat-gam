package com.sparta.goatgam.domain.user.service;

import com.sparta.goatgam.domain.user.dto.SignupRequestDto;
import com.sparta.goatgam.domain.user.dto.UserInfoDto;
import com.sparta.goatgam.domain.user.dto.UserInfoUpdateDto;
import com.sparta.goatgam.domain.user.entity.User;
import com.sparta.goatgam.domain.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public record SoftDeleteResult(Long userId, boolean status, LocalDateTime deletedAt){}

    // ADMIN_TOKEN
    private final String ADMIN_TOKEN = "AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC";

    public void signup(SignupRequestDto requestDto) {

        // 중복체크
        userRepository.findByNickname(requestDto.getNickname())
                .ifPresent(u -> {throw new IllegalArgumentException("중복된 nickname이 존재합니다.");});
        userRepository.findByEmail(requestDto.getEmail())
                .ifPresent(u -> { throw new IllegalArgumentException("중복된 Email 입니다."); });

        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        User user = new User(
                requestDto.getUsername(),
                requestDto.getNickname(),
                requestDto.getEmail(),
                encodedPassword,
                requestDto.getRole(),
                requestDto.getPhoneNumber(),
                requestDto.getAddress()
        );

        // 사용자 등록
        userRepository.save(user);
    }

    @Transactional
    public void updateUser(Long userId, @Valid UserInfoUpdateDto requestDto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다. id=" + userId));
        user.setNickname(requestDto.getNickname());
        user.setUsername(requestDto.getUsername());
        user.setPassword(requestDto.getPassword());
        user.setRole(requestDto.getUserRole());
        user.setPhoneNumber(requestDto.getPhoneNumber());
        user.setAddress(requestDto.getAddress());

        userRepository.save(user);
    }

    @Transactional
    public SoftDeleteResult softDelete(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다 id= " + userId));

        if (!user.getStatus()){
            return new SoftDeleteResult(user.getUserId(), false, user.getDeletedAt());
        }

        user.setStatus(false);
        user.setDeletedAt(LocalDateTime.now());

        return new  SoftDeleteResult(user.getUserId(), true, user.getDeletedAt());
    }

    @Transactional
    public void restore(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다 id= " + userId));
        if (user.getStatus()){
            return;
        }
        user.setStatus(true);
        user.setDeletedAt(null);
    }

    @Transactional(readOnly = true)
    public List<UserInfoDto> getAllUsers() {
        List<User> users = userRepository.findAll();

        return users.stream()
                .map(user -> new UserInfoDto(
                        user.getUserId(),
                        user.getUsername(),
                        user.getNickname(),
                        user.getEmail(),
                        user.getRole(),
                        user.getPhoneNumber(),
                        user.getAddress(),
                        user.getStatus() // 활성화 여부 (필요하면 user.isActive() 같은 값으로 교체)
                ))
                .toList();
    }
}