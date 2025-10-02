package com.sparta.goatgam.domain.restaurant.service;

import com.sparta.goatgam.domain.restaurant.dto.RestaurantDetailDto;
import com.sparta.goatgam.domain.restaurant.dto.RestaurantInfoDto;
import com.sparta.goatgam.domain.restaurant.entity.Restaurant;
import com.sparta.goatgam.domain.restaurant.entity.RestaurantType;
import com.sparta.goatgam.domain.restaurant.repository.RestaurantRepository;
import com.sparta.goatgam.domain.restaurant.repository.RestaurantTypeRepository;
import com.sparta.goatgam.domain.user.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@Service
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final RestaurantTypeRepository restaurantTypeRepository;
    public RestaurantService(
            RestaurantRepository restaurantRepository,
            UserRepository userRepository,
            RestaurantTypeRepository restaurantTypeRepository)
    {
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
        this.restaurantTypeRepository = restaurantTypeRepository;
    }

    //등록



    //전체 조회
    public List<RestaurantInfoDto> getAllRestaurants() {
        List<Restaurant> restaurants = restaurantRepository.findAll();

        return restaurants.stream()
                .map(r -> new RestaurantInfoDto(
                        r.getRestaurantId(),
                        r.getRestaurantName(),
                        r.getRegionCode(),
                        r.getIsPublic(),
                        r.getRestaurantNumber(),
                        r.isStatus(),
                        r.getRestaurantAddress()
                        //r.getUserName()  // 스냅샷
                ))
                .toList();
    }

    //단건 상세 조회
    @Transactional(readOnly = true)
    public RestaurantDetailDto getRestaurant(UUID restaurantId) {
        Restaurant r = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("식당을 찾을 수 없습니다: " + restaurantId));
        return RestaurantDetailDto.from(r);
    }



}
