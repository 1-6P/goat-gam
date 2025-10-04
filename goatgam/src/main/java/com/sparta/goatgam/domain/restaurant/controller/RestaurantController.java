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

    //카테고리별/ 키워드 를 이용해 레스토랑 목록 조회 (리스트로 받야아 할 거 같아요)
    //api/v1/restauran?restaruant_type_code=&keyword=


    //(진현 의견 : 아래 두개의 api는 식당 전체 조회 / 특정 조회처럼 비슷할거같긴해요)
    //특정 식당의 메뉴 조회 (전체정보)
    //api/v1/restaurant/{restaurant_id}/menu

    // 특정 식당의 특정 메뉴 조회하기
    // api/v1/restaurant/{restaurantId}/menu?keyword=

    //특정 식당 메뉴 상세보기
    // ~~반점 -> 짬뽕 클릭 ->  짬뽕 상세정보 조회
    // -> 메뉴 상세정보 조회하기
    //api/v1/restaurant/{restaurant_id}/menu/{menu_id}

    //특정 메뉴의 모든 옵션을 조회하기 ->
    //해당 메뉴의 옵션 조회하기
    // api/v1/restaurant/{restaurantId}/menu/{menuId}/option

}
