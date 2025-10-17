package com.sparta.goatgam.domain.address.service;

import com.sparta.goatgam.domain.address.dto.AddressChangeDto;
import com.sparta.goatgam.domain.address.dto.AddressCreateRequestDto;
import com.sparta.goatgam.domain.address.dto.AddressDeleteResponseDto;
import com.sparta.goatgam.domain.address.dto.AddressResponseDto;
import com.sparta.goatgam.domain.address.entity.Address;
import com.sparta.goatgam.domain.address.entity.Beopjeongdong;
import com.sparta.goatgam.domain.address.entity.Sido;
import com.sparta.goatgam.domain.address.entity.Sigungu;
import com.sparta.goatgam.domain.address.repository.AddressRepository;
import com.sparta.goatgam.domain.address.repository.BeopjeongdongRepository;
import com.sparta.goatgam.domain.user.entity.User;
import com.sparta.goatgam.global.dto.MessageAndIdResponseDto;
import com.sparta.goatgam.global.exception.BusinessException;
import com.sparta.goatgam.global.exception.ExceptionCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final BeopjeongdongRepository beopjeongdongRepository;

    // 유저 주소 추가
    @Transactional
    public AddressResponseDto addAddress(Long userId, @Valid AddressCreateRequestDto requestDto) {
        String code10 = normalizeCode10(requestDto.getBeopjeongDong());
        if (code10 == null) throw new IllegalArgumentException("법정동형식이 올바르지 않습니다.");
        String base8 = code10.substring(0, 8);

        Beopjeongdong beopjeongdong = beopjeongdongRepository.findById(base8)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 법정동 코드: " + base8));

        Sigungu sigungu = beopjeongdong.getSigungu();
        Sido sido = beopjeongdong.getSigungu().getSido();

        Address newAddress = Address.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .dong(beopjeongdong).sigungu(sigungu).sido(sido)
                .roadAddress(requestDto.getRoadAddress()).detail(requestDto.getDetail())
                .isDefault(requestDto.isDefaultAddress()).status(true).build();

        if (requestDto.isDefaultAddress()) {
            addressRepository.findByUserIdAndIsDefaultTrue(userId)
                    .ifPresent(prev -> prev.setDefault(false));
            newAddress.setDefault(true);
        }

        Address saved = addressRepository.save(newAddress);
        return toResponse(saved);
    }

    // 유저 주소 조회
    public List<AddressResponseDto> getUserAddress(Long userId) {
        List<Address> a = addressRepository.findByUserId(userId);
        List<AddressResponseDto> response = new ArrayList<>();
        for (Address b : a) {
            if (!b.isStatus()) continue;
            response.add(toResponse(b));
        }
        if (response.isEmpty()) {
            throw new BusinessException(ExceptionCode.ADDRESS_USER_NOT_FOUND);
        }
        return response;
    }

    // 유저 주소 삭제
    public AddressDeleteResponseDto delete(Long userId, UUID addressId) {
        Address a = addressRepository.findByUserIdAndId(userId, addressId)
                .orElseThrow(() -> new IllegalArgumentException("유저에게 해당 주소가 존재하지 않습니다."));
        if (!a.isStatus()) {
            return new AddressDeleteResponseDto(
                    a.getUserId(),
                    a.getId(),
                    "이미 삭제된 주소입니다.");
        }
        a.setStatus(false);
        a.deleted(userId.toString());
        return new AddressDeleteResponseDto(
                a.getUserId(),
                a.getId(),
                "주소가 성공적으로 삭제되었습니다.");
    }


    // 모든 주소 조회
    public List<AddressResponseDto> getAllAddress() {
        List<Address> a = addressRepository.findAll();

        return a.stream().map(this::toResponse).toList();
    }

    private String normalizeCode10(String beopjeongDong) {
        if (beopjeongDong == null) return null;
        String digits = beopjeongDong.replaceAll("\\D", "");
        if (digits.length() == 10) return digits;
        if (digits.length() == 8) return digits + "00";
        if (digits.length() == 9) return digits + "0";
        if (digits.length() > 10) return digits.substring(0, 10);
        return null;
    }

    private AddressResponseDto toResponse(Address a) {
        return AddressResponseDto.from(a);
    }

    @Transactional
    public MessageAndIdResponseDto updateDefaultAddress(UUID addressId, User user) {
        Address address = addressRepository.findById(addressId).orElseThrow(() ->
                new BusinessException(ExceptionCode.ADDRESS_NOT_FOUND));

        if (!address.getUserId().equals(user.getUserId()))
            throw new BusinessException(ExceptionCode.FORBIDDEN_UPDATE_ADDRESS);

        Optional<Address> defaultAddress = addressRepository.findByUserIdAndIsDefaultTrue(user.getUserId());

        defaultAddress.ifPresent(value -> value.setDefault(false));
        address.setDefault(true);

        return new MessageAndIdResponseDto("기본 주소가 변경되었습니다.", addressId);
    }

    @Transactional
    public MessageAndIdResponseDto changeAddress(UUID addressId, AddressChangeDto requestDto, User user) {
        Address address = addressRepository.findById(addressId).orElseThrow(() -> new BusinessException(ExceptionCode.ADDRESS_NOT_FOUND));
        if (address.getUserId().equals(user.getUserId()))
            throw new BusinessException(ExceptionCode.FORBIDDEN_UPDATE_ADDRESS);

        String code10 = normalizeCode10(requestDto.getBeopjeongDong());

        if (code10 == null) throw new BusinessException(ExceptionCode.ADDRESS_INPUT_ERROR);
        String base8 = code10.substring(0, 8);

        Beopjeongdong beopjeongdong = beopjeongdongRepository.findById(base8)
                .orElseThrow(() -> new BusinessException(ExceptionCode.ADDRESS_NOT_EXIST));

        Sigungu sigungu = beopjeongdong.getSigungu();
        Sido sido = beopjeongdong.getSigungu().getSido();

        Address newAddress = Address.builder()
                .id(UUID.randomUUID())
                .userId(user.getUserId())
                .dong(beopjeongdong).sigungu(sigungu).sido(sido)
                .roadAddress(requestDto.getRoadAddress()).detail(requestDto.getDetail())
                .isDefault(address.isDefault()).status(true).build();

        address.update(newAddress);

        return new MessageAndIdResponseDto("update address success", addressId);
    }
}