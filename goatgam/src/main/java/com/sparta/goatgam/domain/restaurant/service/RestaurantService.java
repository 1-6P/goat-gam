package com.sparta.goatgam.domain.restaurant.service;

import com.sparta.goatgam.domain.restaurant.dto.RestaurantDetailDto;
import com.sparta.goatgam.domain.restaurant.dto.RestaurantInfoDto;
import com.sparta.goatgam.domain.restaurant.dto.RestaurantRequestDto;
import com.sparta.goatgam.domain.restaurant.entity.Restaurant;
import com.sparta.goatgam.domain.restaurant.entity.RestaurantType;
import com.sparta.goatgam.domain.restaurant.repository.RestaurantRepository;
import com.sparta.goatgam.domain.restaurant.repository.RestaurantTypeRepository;
import com.sparta.goatgam.domain.user.entity.User;
import com.sparta.goatgam.domain.user.entity.UserRoleEnum;
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
    @Transactional
    public RestaurantInfoDto createRestaurant(RestaurantRequestDto restaurantRequestDto) {
        User user = userRepository.findById(restaurantRequestDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found")); //userID 체크

        RestaurantType type = restaurantTypeRepository.findById(restaurantRequestDto.getRestaurantTypeId())
                .orElseThrow(() -> new IllegalArgumentException("RestaurantType not found")); //타입값 체크

        // Restaurant entity를 생성한다 (편의 생성자 이용)
        Restaurant res = new Restaurant(user, type, restaurantRequestDto);

        //사용자 ROLE 체크함.
        if(user.getRole() != UserRoleEnum.Owner) {
            throw new IllegalArgumentException("해당 유저는 사장님으로 등록되어 있지 않습니다. 확인 후 재시도해주세요");
        }

        //생성된 엔티티를 DB에 저장해 Insert query 작동시킴
        Restaurant saved = restaurantRepository.save(res);

        //저장된 엔티티를 클라이언트에게 DTO를 이용해 변환한 후 반환해준다.
        return RestaurantInfoDto.convertDto(saved); //Response
    }



    //전체 조회
    @Transactional(readOnly = true)
    public List<RestaurantInfoDto> getAllRestaurants() {
        List<Restaurant> restaurants = restaurantRepository.findAll();


        //엔티티 -> DTO 변환
        return restaurants.stream()
                .map(RestaurantInfoDto::convertDto) //dto 변환한 객체 넣기
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
