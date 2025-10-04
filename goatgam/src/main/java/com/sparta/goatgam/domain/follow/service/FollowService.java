package com.sparta.goatgam.domain.follow.service;

import com.sparta.goatgam.domain.follow.dto.FollowInfoDto;
import com.sparta.goatgam.domain.follow.dto.FollowResponseDto;
import com.sparta.goatgam.domain.follow.entity.Follow;
import com.sparta.goatgam.domain.follow.repository.FollowRepository;
import com.sparta.goatgam.domain.restaurant.entity.Restaurant;
import com.sparta.goatgam.domain.restaurant.repository.RestaurantRepository;
import com.sparta.goatgam.domain.user.entity.User;
import com.sparta.goatgam.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;

    /**
     * 유저가 식당을 팔로우하는 API
     * **/
    @Transactional
    public UUID addFollow(Long userId, UUID restaurantId) {

        User user = userRepository.findById(userId).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 식당입니다."));

        Follow existFollow = followRepository.findByUser_UserIdAndRestaurant_RestaurantId(userId,restaurantId);

        if (existFollow != null){
            if (Boolean.TRUE.equals(existFollow.getFollowStatus())){
                throw new IllegalArgumentException("이미 팔로우 중입니다.");
            }
            existFollow.reFollow(user.getNickname());
            return existFollow.getFollowId();
        }


        Follow follow = Follow.builder()
                .user(user)
                .restaurant(restaurant)
                .followStatus(true)
                .build();

        followRepository.save(follow);
        return follow.getFollowId();
    }


    /**
     * 유저가 팔로우한 식당 목록을 가져오는 API
     * **/
    @Transactional(readOnly = true)
    public List<FollowInfoDto> selectAll(Long userId) {
         List<Follow> followList  = followRepository.findAllByUser_UserId(userId)
                .stream()
                .filter(f -> Boolean.TRUE.equals(f.getFollowStatus()))
                .collect(Collectors.toList());

        List<FollowInfoDto> followInfoDtoList = followList.stream()
                .map(f -> new FollowInfoDto(
                        f.getFollowId(),
                        f.getFollowStatus(),
                        f.getRestaurant().getRestaurantId()
                ))
                .collect(Collectors.toList());;
        return followInfoDtoList;
    }

    /**
     * 유저가 팔로우한 식당을 언팔로우 하는 API
     **/
    @Transactional
    public FollowResponseDto unFollow(Long userId, UUID followId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Follow follow = followRepository.findById(followId).orElseThrow(() ->
                new IllegalArgumentException("현재 팔로우 상태가 아닙니다."));

        if (follow.getFollowStatus()){
            follow.unFollow(user.getNickname());
            return new FollowResponseDto(true, "팔로우가 해제되었습니다.");
        }
        return new FollowResponseDto(false, "이미 언팔로우 상태입니다.");
    }
}
