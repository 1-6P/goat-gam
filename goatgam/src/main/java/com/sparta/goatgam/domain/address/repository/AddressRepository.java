package com.sparta.goatgam.domain.address.repository;

import com.sparta.goatgam.domain.address.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID> {
    List<Address> findByUserId(long userId);
    Optional<Address> findByUserIdAndIsDefaultTrue(long userId);
    Optional<Address> findByUserIdAndId(long userId, UUID id);
}
