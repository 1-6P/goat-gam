package com.sparta.goatgam.domain.restaurant.controller;

import com.sparta.goatgam.domain.restaurant.dto.RestaurantDetailDto;
import com.sparta.goatgam.domain.restaurant.dto.RestaurantInfoDto;
import com.sparta.goatgam.domain.restaurant.dto.RestaurantRequestDto;
import com.sparta.goatgam.domain.restaurant.service.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/restaurant")
@Tag(name = "식당 API", description = "식당 관련 기능 API입니다.")
public class RestaurantController {

    private final RestaurantService restaurantService;

    //전체 조회

    @Operation(summary = "전체 식당 조회", description = "등록된 모든 식당을 조회합니다")
    @GetMapping("/")
    public List<RestaurantInfoDto> getRestaurants() {
        return restaurantService.getAllRestaurants();
    }

    //등록
    @Operation(summary = "식당 등록", description = "새로운 식당 등록하기")
    @PostMapping
    public RestaurantInfoDto createRestaurant(@RequestBody RestaurantRequestDto restaurantRequestDto) {
        return restaurantService.createRestaurant(restaurantRequestDto);
    }

    //단건 조회
    @GetMapping("/{restaurantId}")
    public ResponseEntity<RestaurantDetailDto> getDetail(@PathVariable UUID restaurantId) {
        return ResponseEntity.ok(restaurantService.getRestaurant(restaurantId));
    }
}
