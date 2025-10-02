package com.sparta.goatgam.domain.restaurant.controller;

import com.sparta.goatgam.domain.restaurant.dto.RestaurantDetailDto;
import com.sparta.goatgam.domain.restaurant.dto.RestaurantInfoDto;
import com.sparta.goatgam.domain.restaurant.service.RestaurantService;
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
public class RestaurantController {

    private final RestaurantService restaurantService;

    //전체 조회
    @GetMapping("/")
    public List<RestaurantInfoDto> getRestaurants() {
        return restaurantService.getAllRestaurants();
    }

    //등록
    @PostMapping("/create")
    public RestaurantInfoDto createRestaurant(@RequestBody RestaurantInfoDto restaurantInfoDto) {}

    //단건 조회
    @GetMapping("/{restaurantId}")
    public ResponseEntity<RestaurantDetailDto> getDetail(@PathVariable UUID restaurantId) {
        return ResponseEntity.ok(restaurantService.getRestaurant(restaurantId));
    }
}
