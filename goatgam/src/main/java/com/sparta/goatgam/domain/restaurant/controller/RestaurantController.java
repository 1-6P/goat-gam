package com.sparta.goatgam.domain.restaurant.controller;

import com.sparta.goatgam.domain.owner.dto.FoodListDto;
import com.sparta.goatgam.domain.restaurant.dto.*;
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
    @Operation(summary = "특정 식당 조회." , description = "특정 식당의 정보를 상세조회합니다.")
    @GetMapping("/{restaurantId}")
    public ResponseEntity<RestaurantDetailDto> getDetail(@PathVariable UUID restaurantId) {
        return ResponseEntity.ok(restaurantService.getRestaurant(restaurantId));
    }

    //식당 등록정보 수정
    @Operation(summary ="식당 등록정보 수정", description = "식당의 정보를 수정합니다.")
    @PutMapping("/{restaurantId}")
    public ResponseEntity<RestaurantInfoDto> updateRestaurant(
            @PathVariable UUID restaurantId,
            @RequestBody RestaurantUpdateDto dto) {
        return ResponseEntity.ok(restaurantService.updateRestaurant(restaurantId, dto));
    }


    //카테고리별/ 키워드 를 이용해 레스토랑 목록 조회
    // 예) GET /api/v1/restaurant?restaurant_type_code=1&keyword=멘션
    @Operation(summary = "식당 목록 조회", description = "카테고리 코드/키워드로 필터링. 파라미터 없으면 전체 조회")
    @GetMapping
    public List<RestaurantInfoDto> list(
            @RequestParam(value = "restaurant_type_code", required = false) String typeCodeStr,
            @RequestParam(value = "keyword", required = false) String keyword
    ) {
        return restaurantService.findRestaurants(typeCodeStr, keyword);
    }

    //특정 식당의 메뉴 조회 (전체정보)
    //api/v1/restaurant/{restaurant_id}/menu
    @Operation(summary = "특정 식당의 메뉴 조회", description = "특정 식당의 메뉴 전체정보를 조회합니다.")
    @GetMapping("/{restaurantId}/menu")
    public List<FoodListDto> getMenu(
            @PathVariable UUID restaurantId,
            @RequestParam(defaultValue = "false") boolean includeHidden
    ) {
        return restaurantService.getRestaurantMenu(restaurantId, includeHidden);
    }

    //특정 식당 메뉴 상세보기
    @Operation(summary = "특정 식당 메뉴 상세보기 ", description = "특정 식당의 메뉴를 상세조회합니다.")
    @GetMapping("/{restaurantId}/menu/{foodId}")
    public ResponseEntity<RestaurantFoodDetailDto> getFoodDetail(@PathVariable UUID restaurantId, @PathVariable UUID foodId) {
        return ResponseEntity.ok(restaurantService.getFoodDetail(restaurantId,foodId));
    }

    //특정 메뉴의 모든 옵션을 조회하기 ->
    @Operation(summary = "특정 메뉴의 모든 옵션을 조회 ", description = "특정 메뉴의 모든 옵션을 조회합니다.")
    @GetMapping("/{restaurantId}/menu/{foodId}/option")
    public List<RestaurantFoodOptionDetailDto> getFoodOptionDetail (@PathVariable UUID restaurantId, @PathVariable UUID foodId) {
        return restaurantService.getFoodDetails(restaurantId,foodId);
    }
}


