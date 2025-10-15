package com.sparta.goatgam.domain.follow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class FollowInfoDto {
    private UUID followId;
    private Boolean followStatus;
    private UUID restaurantId;
}
