package com.sparta.goatgam.domain.address.repository;

import com.sparta.goatgam.domain.address.entity.Sido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SidoRepository extends JpaRepository<Sido, String> {
    List<Sido> findAllByAbolishedFalseOrderByNameAsc();
}

