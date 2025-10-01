package com.sparta.goatgam.domain.restaurant.service;

import com.sparta.goatgam.domain.restaurant.dto.RestaurantInfoDto;
import com.sparta.goatgam.domain.restaurant.entity.Restaurant;
import com.sparta.goatgam.domain.restaurant.repository.RestaurantRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Service
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    public RestaurantService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

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




}
