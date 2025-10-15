package com.sparta.goatgam.domain.address.repository;

import com.sparta.goatgam.domain.address.entity.Sigungu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SigunguRepository extends JpaRepository<Sigungu, String> {
    List<Sigungu> findBySido_SidoCodeAndAbolishedFalseOrderByNameAsc(String sidoCode);
}
