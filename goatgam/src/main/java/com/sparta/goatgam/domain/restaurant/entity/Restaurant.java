package com.sparta.goatgam.domain.restaurant.entity;


import com.sparta.goatgam.domain.user.entity.User;
import com.sparta.goatgam.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "p_restaurant")

public class Restaurant extends BaseEntity {
    @Id
    @Column(name = "restaurant_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID restaurantId;

    //FK : USER의 ID 필요 -> 타입 작성 필요
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    //식당명
    @Column(name = "restaurant_name", nullable = false)
    private String restaurantName;

    //식당 타입 아이디
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "restaurant_type_id", nullable = false)
    private RestaurantType restaurantTypeId;

    //사장의 유저 이름
    @Column(name = "user_name", nullable = false)
    private String userName;

    //지역코드
    @Column(name = "region_code", nullable = false)
    private int regionCode;

    //활성화 상태 0 : 운영중: 1: 휴무 2: 폐업 등.. 현재는 휴무까지만 적용
    @Column(name ="is_public", nullable = false)
    private int isPublic;

    //식당 주소
    @Column (name = "restaurant_address" , nullable = false)
    private String restaurantAddress;


    @Column( name = "restaurant_number")
    private String restaurantNumber;

    //상태 (삭제x or 삭제)
    @Column (name = "status" , nullable = false)
    private boolean status;


}
