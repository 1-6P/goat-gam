package com.sparta.goatgam.domain.address.controller;

import com.sparta.goatgam.domain.address.dto.AddressCreateRequestDto;
import com.sparta.goatgam.domain.address.dto.AddressResponseDto;
import com.sparta.goatgam.domain.address.service.AddressService;
import com.sparta.goatgam.global.dto.MessageAndIdResponseDto;
import com.sparta.goatgam.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(
            summary = "주소 추가",
            description = "유저는 본인 주소를 추가할 수 있다."
    )
    @PostMapping("")
    public ResponseEntity<AddressResponseDto> createAddress(@AuthenticationPrincipal UserDetailsImpl principal, @RequestBody AddressCreateRequestDto requestDto) {
        return ResponseEntity.ok(addressService.addAddress(principal.getUser(), requestDto));
    }

    // 유저 주소 조회
    @Operation(
            summary = "유저 본인 주소 조회",
            description = "유저는 본인 주소 리스트를 조회할 수 있습니다."
    )
    @GetMapping("/my")
    public ResponseEntity<List<AddressResponseDto>> getAddress(@AuthenticationPrincipal UserDetailsImpl principal) {
        return ResponseEntity.ok(addressService.getUserAddress(principal.getUser().getUserId()));
    }

    // 유저 주소 삭제
    @Operation(
            summary = "주소 삭제",
            description = "주소 아이디로 주소를 삭제할 수 있다."
    )
    @DeleteMapping("/{addressId}")
    public ResponseEntity<?> deleteUserAddress(@AuthenticationPrincipal UserDetailsImpl principal, @PathVariable UUID addressId) {
        return ResponseEntity.ok(addressService.delete(principal.getUser().getUserId(), addressId));
    }

    // 전체 주소 조회
    @Operation(
            summary = "전체 주소 조회",
            description = "Master, Manager가 전체 주소를 조회할 수 있다."
    )
    @PreAuthorize("hasAnyAuthority('Master','Manager')")
    @GetMapping("")
    public ResponseEntity<List<AddressResponseDto>> getAllAddresses() {
        return ResponseEntity.ok(addressService.getAllAddress());
    }

    @Operation(summary = "default 주소 설정", description = "기본 배송지를 변경할 수 있습니다. 설정된 주소로 주문이 수행됩니다.")
    @PatchMapping("/{addressId}")
    public ResponseEntity<MessageAndIdResponseDto> updateDefaultAddress(
            @PathVariable UUID addressId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return ResponseEntity.ok(addressService.updateDefaultAddress(addressId, userDetails.getUser()));
    }
}
