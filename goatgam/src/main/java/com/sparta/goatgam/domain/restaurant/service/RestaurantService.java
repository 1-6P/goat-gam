package com.sparta.goatgam.domain.restaurant.service;

import com.sparta.goatgam.domain.owner.dto.FoodListDto;
import com.sparta.goatgam.domain.owner.entity.Food;
import com.sparta.goatgam.domain.owner.entity.FoodOption;
import com.sparta.goatgam.domain.owner.entity.FoodStatus;
import com.sparta.goatgam.domain.owner.repository.FoodOptionRepository;
import com.sparta.goatgam.domain.owner.repository.FoodRepository;
import com.sparta.goatgam.domain.restaurant.dto.*;
import com.sparta.goatgam.domain.restaurant.entity.Restaurant;
import com.sparta.goatgam.domain.restaurant.entity.RestaurantType;
import com.sparta.goatgam.domain.restaurant.repository.RestaurantRepository;
import com.sparta.goatgam.domain.restaurant.repository.RestaurantTypeRepository;
import com.sparta.goatgam.domain.user.entity.User;
import com.sparta.goatgam.domain.user.entity.UserRoleEnum;
import com.sparta.goatgam.domain.user.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final RestaurantTypeRepository restaurantTypeRepository;
    private final FoodRepository foodRepository;
    private final FoodOptionRepository foodOptionRepository;

    public RestaurantService(
            RestaurantRepository restaurantRepository,
            UserRepository userRepository,
            RestaurantTypeRepository restaurantTypeRepository, FoodRepository foodRepository, FoodOptionRepository foodOptionRepository)
    {
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
        this.restaurantTypeRepository = restaurantTypeRepository;
        this.foodRepository = foodRepository;
        this.foodOptionRepository = foodOptionRepository;
    }

    //등록
    @Transactional
    public RestaurantInfoDto createRestaurant(RestaurantRequestDto restaurantRequestDto, User userInfo) {
        User user = userRepository.findById(restaurantRequestDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found")); //userID 체크

        RestaurantType type = restaurantTypeRepository.findById(restaurantRequestDto.getRestaurantTypeId())
                .orElseThrow(() -> new IllegalArgumentException("RestaurantType not found")); //타입값 체크

        //1010 update, 권한 생성 후, 유저Id 체크
        checkUser(new Restaurant(user, type, restaurantRequestDto), userInfo);

        //사용자 ROLE 체크함. 권한 체크
        if(user.getRole() != UserRoleEnum.Owner && user.getRole() != UserRoleEnum.Manager && user.getRole() != UserRoleEnum.Master) {
            throw new IllegalArgumentException("해당 유저는 사장님으로 등록되어 있지 않습니다. 확인 후 재시도해주세요");
        }

        // Restaurant entity를 생성한다 (편의 생성자 이용)
        Restaurant res = new Restaurant(user, type, restaurantRequestDto);

        //생성된 엔티티를 DB에 저장해 Insert query 작동시킴
        Restaurant saved = restaurantRepository.save(res);
        //저장된 엔티티를 클라이언트에게 DTO를 이용해 변환한 후 반환해준다.
        return RestaurantInfoDto.convertDto(saved); //Response
    }

    //전체 조회(관리자용), 값이 false 인것 까지 볼 수 있음
    @Transactional(readOnly = true)
    public List<RestaurantInfoDto> getAllRestaurants(String typeCodeStr, String keyword) {
        Integer typeCode = parseIntSafely(typeCodeStr); // 잘못된 값/빈문자 → null
        String kw = normalize(keyword);

        return restaurantRepository.findAll().stream()
                // 카테고리 필터
                .filter(r -> typeCode == null || hasTypeCode(r, typeCode))
                // 키워드 필터 (이름/주소)
                .filter(r -> kw == null
                        || containsIgnoreCase(r.getRestaurantName(), kw)
                        || containsIgnoreCase(r.getRestaurantAddress(), kw))
                .map(RestaurantInfoDto::convertDto)
                .toList();
    }

    //단건 상세 조회
    @Transactional(readOnly = true)
    public RestaurantDetailDto getRestaurant(UUID restaurantId) {
        Restaurant r = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("식당을 찾을 수 없습니다: " + restaurantId));
        return RestaurantDetailDto.from(r);
    }

    //레스토랑 정보 수정
    @Transactional
    public RestaurantInfoDto updateRestaurant(UUID restaurantId, RestaurantUpdateDto restaurantUpdateDto, User userInfo) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("식당을 찾을 수 없습니다."));
        //체크로직 적용
        checkUser(restaurant, userInfo);
        restaurant.setRestaurantName(restaurantUpdateDto.getRestaurantName());
        restaurant.setRestaurantAddress(restaurantUpdateDto.getRestaurantAddress());
        restaurant.setRestaurantNumber(restaurantUpdateDto.getRestaurantNumber());
        restaurant.setIsPublic(restaurantUpdateDto.getIsPublic());
        if(restaurantUpdateDto.getRegionCode() != 0) {
            restaurant.setRegionCode(restaurantUpdateDto.getRegionCode());
        }
        return RestaurantInfoDto.convertDto(restaurant);
    }
    //레스토랑 정보 삭제
    @Transactional
    public RestaurantInfoDto deleteRestaurant(UUID restaurantId,User userInfo) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("식당을 찾을 수 없습니다."));
        checkUser(restaurant, userInfo);
        restaurant.setStatus(false);
        return RestaurantInfoDto.convertDto(restaurant);
    }
    //삭제된 레스토랑 롤백
    //식당 status 체크하는 로직을 따로 도입해야 할까요?
    @Transactional
    public RestaurantInfoDto RollbackDeletedRestaurant(UUID restaurantId,User userInfo) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("식당을 찾을 수 없습니다."));
        checkUser(restaurant, userInfo);
        restaurant.setStatus(true);
        return RestaurantInfoDto.convertDto(restaurant);
    }
    //  카테고리/키워드 기반 목록 조회 (for users)
    @Transactional(readOnly = true)
    public List<RestaurantInfoDto> findRestaurants(String typeCodeStr, String keyword) {
        Integer typeCode = parseIntSafely(typeCodeStr); // 잘못된 값/빈문자 → null
        String kw = normalize(keyword);

        return restaurantRepository.findAll().stream()
                //값이 true인것만 출력됨
                .filter(Restaurant::isStatus)
                // 카테고리 필터
                .filter(r -> typeCode == null || hasTypeCode(r, typeCode))
                // 키워드 필터 (이름/주소)
                .filter(r -> kw == null
                        || containsIgnoreCase(r.getRestaurantName(), kw)
                        || containsIgnoreCase(r.getRestaurantAddress(), kw))
                .map(RestaurantInfoDto::convertDto)
                .toList();
    }

    /* ---------- helpers ---------- 이실직고합니다. 그녀석의 힘을 빌렸어요.. GPT....*/

    private Integer parseIntSafely(String s) {
        if (s == null || s.isBlank()) return null;
        try { return Integer.valueOf(s.trim()); }
        catch (NumberFormatException e) { return null; }
    }

    private String normalize(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t.toLowerCase();
    }

    private boolean containsIgnoreCase(String src, String kwLower) {
        return src != null && src.toLowerCase().contains(kwLower);
    }

    private boolean hasTypeCode(Restaurant r, Integer code) {
        var t = r.getRestaurantTypeId();
        return t != null
                && t.getRestaurantTypeCode() != null
                && t.getRestaurantTypeCode().equals(code);
    }


    // 특정 식당의 메뉴 조회 (기본: Hidden/Deleted 제외, includeHidden=true면 전부)
    @Transactional(readOnly = true)
    public List<FoodListDto> getRestaurantMenu(UUID restaurantId, boolean includeHidden) {
        // 식당 존재 여부만 확인 (없으면 404 성격의 예외)
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new IllegalArgumentException("식당을 찾을 수 없습니다: " + restaurantId);
        }

        return foodRepository.findAll().stream()
                .filter(f -> f.getRestaurant() != null
                        && restaurantId.equals(f.getRestaurant().getRestaurantId()))
                .filter(f -> includeHidden
                        || (f.getFoodStatus() != FoodStatus.Hidden
                        && f.getFoodStatus() != FoodStatus.Deleted))
                .map(FoodListDto::from)
                .toList();
    }

    //특정 식당 메뉴 상세보기
    @Transactional(readOnly = true)
    public RestaurantFoodDetailDto getFoodDetail(UUID restaurantId, UUID foodId) {
        Food foodDetails = foodRepository.findByIdAndRestaurant_RestaurantId(foodId,restaurantId)
                .orElseThrow( () -> new IllegalArgumentException("메뉴가 등록되어있지 않거나 정보 입력이 잘못되었습니다. " +
                        " restaurantId:" + restaurantId + " foodId:" + foodId));

        //판매중인 상품이 아니면 조회가 불가능하다.
        if(foodDetails.getFoodStatus() != FoodStatus.Ok) {
            throw new IllegalArgumentException("해당 상품은 현재 판매하지 않는 상품입니다.");
        }
        return RestaurantFoodDetailDto.convertDto(foodDetails);
    }


    //특정 메뉴 옵션 전체보기
    @Transactional(readOnly = true)
    public List<RestaurantFoodOptionDetailDto> getFoodDetails(UUID restaurantId, UUID foodId) {
        Food foodDetails = foodRepository.findByIdAndRestaurant_RestaurantId(foodId,restaurantId)
                .orElseThrow( () -> new IllegalArgumentException("메뉴가 등록되어있지 않거나 정보 입력이 잘못되었습니다. " +
                        " restaurantId:" + restaurantId + " foodId:" + foodId));
        //판매중인 상품이 아니면 조회가 불가능하다.
        if(foodDetails.getFoodStatus() != FoodStatus.Ok) {
            throw new IllegalArgumentException("해당 상품은 현재 판매하지 않는 상품이라 옵션조회가 불가능합니다.");
        }
        List<FoodOption> foodOption = foodOptionRepository.findByFood_IdAndDeletedFalse(foodId);
        return RestaurantFoodOptionDetailDto.convertList(restaurantId,foodId,foodOption);
    }

    //본인 체크하기
    private void checkUser(Restaurant restaurant, User userInfo) {
        //요청하는 user
        User requester = restaurant.getUser();
        //Duplicated Fragment 경고로 인해 방식 수정
        //본인 체크 로직
        if (!userInfo.getUserId().equals(requester.getUserId()) && (restaurant.getUser().getRole() == UserRoleEnum.Owner || restaurant.getUser().getRole() == UserRoleEnum.Customer)) {
            throw new IllegalArgumentException("인증된 사용자 정보와 요청의 userId가 일치하지 않습니다.");
        }
    }
}


