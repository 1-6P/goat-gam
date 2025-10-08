package com.sparta.goatgam.domain.address.controller;

import com.sparta.goatgam.domain.address.dto.AddressCreateRequestDto;
import com.sparta.goatgam.domain.address.dto.AddressResponseDto;
import com.sparta.goatgam.domain.address.service.AddressService;
import com.sparta.goatgam.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/address")
public class AddressController {

    private final AddressService addressService;

    // 주소 등록
    @PostMapping("/")
    public ResponseEntity<AddressResponseDto> createAddress (@AuthenticationPrincipal UserDetailsImpl principal, @RequestBody AddressCreateRequestDto requestDto){
        Long userId = principal.getUser().getUserId();
        return ResponseEntity.ok(addressService.addAddress(userId, requestDto));
    }

    // 유저 주소 조회
    @GetMapping("/{userId}")
    public ResponseEntity<List<AddressResponseDto>> getAddress(@PathVariable Long userId){
        return ResponseEntity.ok(addressService.getUserAddress(userId));
    }

    // 유저 주소 삭제
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{addressId}")
    public ResponseEntity<?> deleteUserAddress(@AuthenticationPrincipal UserDetailsImpl principal, @PathVariable UUID addressId){
        return ResponseEntity.ok(addressService.delete(principal.getUser().getUserId(), addressId));
    }

    // 전체 주소 조회
    @PreAuthorize("hasAnyAuthority('Master','Manager')")
    @GetMapping("/")
    public ResponseEntity<List<AddressResponseDto>> getAllAddresses(){
        return ResponseEntity.ok(addressService.getAllAddress());
    }
}
