package com.sparta.goatgam.domain.follow.service;

import com.sparta.goatgam.domain.follow.entity.Follow;
import com.sparta.goatgam.domain.follow.repository.FollowRepository;
import com.sparta.goatgam.domain.user.entity.User;
import com.sparta.goatgam.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
//    private final RestaurantRepository restaurantRepository;

    /**
     * 유저가 식당을 팔로우하는 API
     * **/
    public UUID addFollow(Long userId, UUID restaurantId) {

        User user = userRepository.findById(userId).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 사용자입니다."));

//        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() ->
//                new IllegalArgumentException("존재하지 않는 식당입니다."));

        Follow follow = Follow.builder()
                .user(user)
//                .restaurant(restaurant)
                .followStatus(true)
                .build();
        return follow.getFollowId();
    }


}
