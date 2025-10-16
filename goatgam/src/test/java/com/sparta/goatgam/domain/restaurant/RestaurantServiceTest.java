package com.sparta.goatgam.domain.restaurant;

import com.sparta.goatgam.domain.owner.dto.FoodListDto;
import com.sparta.goatgam.domain.owner.entity.Food;
import com.sparta.goatgam.domain.owner.entity.FoodStatus;
import com.sparta.goatgam.domain.owner.repository.FoodOptionRepository;
import com.sparta.goatgam.domain.owner.repository.FoodRepository;
import com.sparta.goatgam.domain.restaurant.dto.RestaurantDetailDto;
import com.sparta.goatgam.domain.restaurant.dto.RestaurantInfoDto;
import com.sparta.goatgam.domain.restaurant.dto.RestaurantRequestDto;
import com.sparta.goatgam.domain.restaurant.dto.RestaurantUpdateDto;
import com.sparta.goatgam.domain.restaurant.entity.Restaurant;
import com.sparta.goatgam.domain.restaurant.entity.RestaurantEnum;
import com.sparta.goatgam.domain.restaurant.entity.RestaurantType;
import com.sparta.goatgam.domain.restaurant.repository.MenuRepository;
import com.sparta.goatgam.domain.restaurant.repository.RestaurantRepository;
import com.sparta.goatgam.domain.restaurant.repository.RestaurantTypeRepository;
import com.sparta.goatgam.domain.restaurant.service.MenuSearchService;
import com.sparta.goatgam.domain.restaurant.service.RestaurantService;
import com.sparta.goatgam.domain.user.entity.User;
import com.sparta.goatgam.domain.user.entity.UserRoleEnum;
import com.sparta.goatgam.domain.user.repository.UserRepository;
import com.sparta.goatgam.global.exception.BusinessException;
import com.sparta.goatgam.global.exception.ExceptionCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
//mock에서 setter 부르지 마세요
class RestaurantServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RestaurantTypeRepository restaurantTypeRepository;
    @Mock
    private FoodRepository foodRepository;
    @Mock
    private FoodOptionRepository foodOptionRepository;

    @InjectMocks
    private MenuSearchService menuSearchService;
    @InjectMocks
    private RestaurantService restaurantService;

    // ---------- createRestaurant ----------
    @Nested
    class CreateRestaurant {

        @Test
        @DisplayName("성공: Owner/Manager/Master 권한 + 본인 요청")
        void success_owner() {
            // given
            User owner = mockUser(10L, UserRoleEnum.Owner);
            UUID typeId = UUID.randomUUID();
            RestaurantType type = mock(RestaurantType.class);

            RestaurantRequestDto req = mockRequest(10L, typeId, "공리짬뽕", "서울 종로구", 11001112);

            given(userRepository.findById(10L)).willReturn(Optional.of(owner));
            given(restaurantTypeRepository.findById(typeId)).willReturn(Optional.of(type));
            // save는 저장된 엔티티를 그대로 반환하는 것처럼 스텁
            given(restaurantRepository.save(any(Restaurant.class))).willAnswer(inv -> inv.getArgument(0));

            // when
            RestaurantInfoDto dto = restaurantService.createRestaurant(req, owner);

            // then
            assertThat(dto).isNotNull();
            assertThat(dto.getRestaurantName()).isEqualTo("공리짬뽕");
            verify(restaurantRepository, times(1)).save(any(Restaurant.class));
        }


        @Test
        @DisplayName("실패: RESTAURANT_TYPE_NOT_FOUND")
        void fail_typeNotFound() {
            // given
            User owner = mockUser(10L, UserRoleEnum.Owner);
            UUID typeId = UUID.randomUUID();
            RestaurantRequestDto req = mockRequest(10L, typeId, "공리짬뽕", "서울", 11001112);

            given(userRepository.findById(10L)).willReturn(Optional.of(owner));
            given(restaurantTypeRepository.findById(typeId)).willReturn(Optional.empty());

            BusinessException ex = assertThrows(BusinessException.class,
                                                () -> restaurantService.createRestaurant(req, owner));
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.RESTAURANT_TYPE_NOT_FOUND);
            verify(restaurantRepository, never()).save(any());
        }

        @Test
        @DisplayName("실패: 본인 아님(FORBIDDEN_UPDATE_RESTAURANT) - checkUser")
        void fail_notSelf_forbiddenUpdate() {
            // given
            User owner = mockUser(10L, UserRoleEnum.Owner);
            User other = mockUser(99L, UserRoleEnum.Customer);
            UUID typeId = UUID.randomUUID();
            RestaurantType type = mock(RestaurantType.class);
            RestaurantRequestDto req = mockRequest(10L, typeId, "공리짬뽕", "서울", 11001112);

            given(userRepository.findById(10L)).willReturn(Optional.of(owner));
            given(restaurantTypeRepository.findById(typeId)).willReturn(Optional.of(type));

            BusinessException ex = assertThrows(BusinessException.class,
                                                () -> restaurantService.createRestaurant(req, other));
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.FORBIDDEN_UPDATE_RESTAURANT);
            verify(restaurantRepository, never()).save(any());
        }

        @Test
        @DisplayName("실패: 권한 불가(FORBIDDEN_CREATE_RESTAURANT)")
        void fail_roleForbidden() {
            // given
            User customer = mockUser(10L, UserRoleEnum.Customer); // 허용: Owner/Manager/Master
            UUID typeId = UUID.randomUUID();
            RestaurantType type = mock(RestaurantType.class);
            RestaurantRequestDto req = mockRequest(10L, typeId, "공리짬뽕", "서울", 11001112);

            given(userRepository.findById(10L)).willReturn(Optional.of(customer));
            given(restaurantTypeRepository.findById(typeId)).willReturn(Optional.of(type));

            BusinessException ex = assertThrows(BusinessException.class,
                                                () -> restaurantService.createRestaurant(req, customer));
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.FORBIDDEN_CREATE_RESTAURANT);
            verify(restaurantRepository, never()).save(any());
        }
    }

    // ---------- getRestaurant ----------
    @Nested
    class GetRestaurant {

        @Test
        @DisplayName("성공: 단건 상세 조회")
        void success() {
            UUID id = UUID.randomUUID();
            User owner = new User();
            owner.setUserId(100L);


            RestaurantType t = mock(RestaurantType.class);
            when(t.getRestaurantTypeCode()).thenReturn(20);

            // Restaurant도 mock으로 만들어서 from()이 호출할 법한 게터 스텁
            Restaurant r = mock(Restaurant.class);
            when(r.getRestaurantId()).thenReturn(id);
            when(r.getRestaurantName()).thenReturn("공리짬뽕");
            when(r.getRestaurantAddress()).thenReturn("서울");
            when(r.getRestaurantTypeId()).thenReturn(t);
            when(r.getUser()).thenReturn(owner);
            when(r.isStatus()).thenReturn(true);

            given(restaurantRepository.findById(id)).willReturn(Optional.of(r));

            RestaurantDetailDto dto = restaurantService.getRestaurant(id);

            assertThat(dto).isNotNull();
            verify(restaurantRepository).findById(id);
        }

        @Test
        @DisplayName("실패: RESTAURANT_NOT_FOUND")
        void notFound() {
            UUID id = UUID.randomUUID();
            given(restaurantRepository.findById(id)).willReturn(Optional.empty());

            BusinessException ex = assertThrows(BusinessException.class,
                                                () -> restaurantService.getRestaurant(id));
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.RESTAURANT_NOT_FOUND);
        }
    }

    //---------------------updateRestaurant--

    @Nested
    class updateRestaurant {
        @Test
        @DisplayName("성공 : 본인, 가게 정보 수정 완료")
        void success() {
            UUID id = UUID.randomUUID();
            User owner = mockUser(100L, UserRoleEnum.Owner);

            Restaurant r = mock(Restaurant.class);
            when(r.getUser()).thenReturn(owner);
            when(r.isStatus()).thenReturn(true);
            when(r.getRestaurantId()).thenReturn(id);

            //convertDto에서 사용
            when(r.getRestaurantName()).thenReturn("다미분식");
            when(r.getRestaurantAddress()).thenReturn("서울시 광화문로 20길");
            when(r.getRestaurantNumber()).thenReturn("02-1234-4440");
            when(r.getRegionCode()).thenReturn(11001112);
            when(r.getIsPublic()).thenReturn(RestaurantEnum.Open);

            //타입
            RestaurantType type = mock(RestaurantType.class);
            when(type.getRestaurantTypeName()).thenReturn("분식");
            when(type.getRestaurantTypeCode()).thenReturn(20);
            when(r.getRestaurantTypeId()).thenReturn(type);

            given(restaurantRepository.findById(id)).willReturn(Optional.of(r));

            RestaurantUpdateDto dto = new RestaurantUpdateDto(
                "다미분식",
                "서울시 광화문로 20길",
                "02-1234-4440",
                11001112,
                RestaurantEnum.Open
            );

            RestaurantInfoDto result = restaurantService.updateRestaurant(id, dto, owner);

            assertThat(result).isNotNull();
            assertThat(result.getRestaurantName()).isEqualTo("다미분식");
            assertThat(result.getRestaurantAddress()).isEqualTo("서울시 광화문로 20길");
            assertThat(result.getRestaurantNumber()).isEqualTo("02-1234-4440");
            assertThat(result.getRegionCode()).isEqualTo(11001112);
            assertThat(result.getIsPublic()).isEqualTo(RestaurantEnum.Open);
            // (타입명도 쓰면)
            // assertThat(result.getRestaurantTypeName()).isEqualTo("분식");
        }

        @Test
        @DisplayName("업데이트 실패: 본인이 아닙니다 -> FORBIDEN_UPDATE_RESTAURANT" )
        void fail_update_restaurant() {
            UUID id = UUID.randomUUID();
            User owner = mockUser(100L,UserRoleEnum.Owner);
            User notOwner = mockUser(10L, UserRoleEnum.Owner);
            Restaurant r = mock(Restaurant.class);

            //onwer 반환해야 본인아 아닌걸 확인할 수 있음
            when(r.getUser()).thenReturn(owner);
            when(r.getRestaurantId()).thenReturn(id);
            when(r.isStatus()).thenReturn(true);


            given(restaurantRepository.findById(id)).willReturn(Optional.of(r));

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> restaurantService.updateRestaurant(id,new RestaurantUpdateDto(),notOwner));

            assertThat(exception.getExceptionCode()).isEqualTo(ExceptionCode.FORBIDDEN_UPDATE_RESTAURANT);

        }

    }

    //------deleteRestaurant / rollback ---

    @Nested
    class deleteAndRollback {

        @Test
        @DisplayName("삭제 성공, 식당의 status를 false로 바꿉니다.")

            //mock에선 set이 안먹기때문에, 기존 상태를 재현해줘야함 (getter /setter)
            //spy 사용
        void success() {
            UUID id = UUID.randomUUID();
            User owner = mockUser(100L, UserRoleEnum.Owner);
            Restaurant r = mock(Restaurant.class);
            //  checkUser
            when(r.getUser()).thenReturn(owner);
            when(r.getRestaurantId()).thenReturn(id);
            //   convertDto
            when(r.getRestaurantName()).thenReturn("가게");
            when(r.getRestaurantAddress()).thenReturn("서울");
            when(r.getRestaurantNumber()).thenReturn("02-0000-0000");
            when(r.getRegionCode()).thenReturn(11001112);
            when(r.getIsPublic()).thenReturn(RestaurantEnum.Open);
            // 삭제 이후 convertDto가 부를 값: status=false
            when(r.isStatus()).thenReturn(false);

            // 타입도 쓰면 스텁 필요
            RestaurantType type = mock(RestaurantType.class);
            when(type.getRestaurantTypeName()).thenReturn("중식");
            when(type.getRestaurantTypeCode()).thenReturn(20);
            when(r.getRestaurantTypeId()).thenReturn(type);

            given(restaurantRepository.findById(id)).willReturn(Optional.of(r));

            RestaurantInfoDto dto = restaurantService.deleteRestaurant(id, owner);

            //then
            assertThat(dto.isStatus()).isFalse();

        }

        @Test
        @DisplayName("삭제 실패 : 권한 문제 -> FORBIDDEN_UPDATE_RESTAURANT" )
        void fail_delete_restaurant() {
            UUID id = UUID.randomUUID();
            User owner = mockUser(100L,UserRoleEnum.Owner);
            User notOwner = mockUser(10L, UserRoleEnum.Owner);

            Restaurant r = mock(Restaurant.class);
            when(r.getUser()).thenReturn(owner);
            when(r.getRestaurantId()).thenReturn(id);

            given(restaurantRepository.findById(id)).willReturn(Optional.of(r));

            //When
            BusinessException ex = assertThrows(BusinessException.class,
                                                () -> restaurantService.deleteRestaurant(id, notOwner));
            //then
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.FORBIDDEN_UPDATE_RESTAURANT);

        }

        @Test
        @DisplayName("롤백 성공")
        void success_rollback() {
            UUID id = UUID.randomUUID();
            User manager = mockUser(7L, UserRoleEnum.Manager);

            Restaurant r = mock(Restaurant.class);
            when(r.getRestaurantId()).thenReturn(id);
            // 롤백 이후 convertDto가 읽을 값들
            when(r.getRestaurantName()).thenReturn("가게");
            when(r.getRestaurantAddress()).thenReturn("서울");
            when(r.getRestaurantNumber()).thenReturn("02-0000-0000");
            when(r.getRegionCode()).thenReturn(11001112);
            when(r.getIsPublic()).thenReturn(RestaurantEnum.Open);
            when(r.isStatus()).thenReturn(true); // 복구 후 상태

            RestaurantType type = mock(RestaurantType.class);
            when(type.getRestaurantTypeName()).thenReturn("중식");
            when(type.getRestaurantTypeCode()).thenReturn(20);
            when(r.getRestaurantTypeId()).thenReturn(type);

            given(restaurantRepository.findById(id)).willReturn(Optional.of(r));

            RestaurantInfoDto dto = restaurantService.RollbackDeletedRestaurant(id, manager);

            assertThat(dto.isStatus()).isTrue();
        }

        @Test
        @DisplayName("롤백 실패, FORBIDDEN_ROLLBACK_RESTAURANT")
        void fail_rollback() {
            UUID id = UUID.randomUUID();
            User owner = mockUser(100L,UserRoleEnum.Owner); // Master & Manager 아님
            Restaurant r =  mock(Restaurant.class);
            r.setStatus(false);

            given(restaurantRepository.findById(id)).willReturn(Optional.of(r));

            BusinessException exception = assertThrows(BusinessException.class,
                                                       () -> restaurantService.RollbackDeletedRestaurant(id,owner));

            assertThat(exception.getExceptionCode()).isEqualTo(ExceptionCode.FORBIDDEN_ROLLBACK_RESTAURANT);
            //
        }
    }

    //---------------------------------------------------------------------
    @Mock
    private MenuRepository menuRepository;
    private final UUID restaurantId = UUID.randomUUID();

    // ---------- searchMenus ----------
    @Nested
    @DisplayName("searchMenus() - 메뉴 검색 테스트")
    class SearchMenus {

        @Test
        @DisplayName("성공: 식당 존재 + 정상 키워드 + 메뉴 존재")
        void success() {
            // given
            given(restaurantRepository.existsById(restaurantId)).willReturn(true);

            Food food1 = mockFood("짬뽕", "얼큰한 짬뽕", FoodStatus.Ok);
            Food food2 = mockFood("짜장면", "달콤한 짜장", FoodStatus.Ok);

            given(menuRepository.findByRestaurant_RestaurantIdAndFoodNameContainingIgnoreCase(restaurantId, "짬"))
                .willReturn(List.of(food1));
            given(menuRepository.findByRestaurant_RestaurantIdAndFoodExplainContainingIgnoreCase(restaurantId, "짬"))
                .willReturn(List.of(food2));

            // when
            List<FoodListDto> result = menuSearchService.searchMenus(restaurantId, "짬");

            // then
            assertThat(result).hasSize(2);
            verify(menuRepository, times(1))
                .findByRestaurant_RestaurantIdAndFoodNameContainingIgnoreCase(any(), any());
            verify(menuRepository, times(1))
                .findByRestaurant_RestaurantIdAndFoodExplainContainingIgnoreCase(any(), any());
        }

        @Test
        @DisplayName("실패: 존재하지 않는 식당 → RESTAURANT_NOT_FOUND")
        void fail_restaurantNotFound() {
            // given
            given(restaurantRepository.existsById(restaurantId)).willReturn(false);

            // when & then
            BusinessException ex = assertThrows(BusinessException.class,
                                                () -> menuSearchService.searchMenus(restaurantId, "짬뽕"));
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.RESTAURANT_NOT_FOUND);

            verify(menuRepository, never())
                .findByRestaurant_RestaurantIdAndFoodNameContainingIgnoreCase(any(), any());
        }

        @Test
        @DisplayName("실패: 키워드 없음 → INVALID_INPUT")
        void fail_keywordBlank() {
            // given
            given(restaurantRepository.existsById(restaurantId)).willReturn(true);

            // when & then
            BusinessException ex = assertThrows(BusinessException.class,
                                                () -> menuSearchService.searchMenus(restaurantId, " "));
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.INVALID_INPUT);

            verify(menuRepository, never())
                .findByRestaurant_RestaurantIdAndFoodNameContainingIgnoreCase(any(), any());
        }

        @Test
        @DisplayName("실패: 메뉴 결과 없음 → FOOD_NOT_FOUND")
        void fail_noMenuFound() {
            // given
            given(restaurantRepository.existsById(restaurantId)).willReturn(true);
            given(menuRepository.findByRestaurant_RestaurantIdAndFoodNameContainingIgnoreCase(restaurantId, "탕"))
                .willReturn(List.of());
            given(menuRepository.findByRestaurant_RestaurantIdAndFoodExplainContainingIgnoreCase(restaurantId, "탕"))
                .willReturn(List.of());

            // when & then
            BusinessException ex = assertThrows(BusinessException.class,
                                                () -> menuSearchService.searchMenus(restaurantId, "탕"));
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.FOOD_NOT_FOUND);

            verify(menuRepository, times(1))
                .findByRestaurant_RestaurantIdAndFoodNameContainingIgnoreCase(any(), any());
        }

        @Test
        @DisplayName("성공: Hidden/Deleted 메뉴는 제외됨")
        void success_excludeHiddenDeleted() {
            // given
            UUID restaurantId = UUID.randomUUID();
            String keyword = "짬뽕";

            given(restaurantRepository.existsById(restaurantId)).willReturn(true);

            Food hidden = mockFood("비밀메뉴", "숨김", FoodStatus.Hidden);
            Food deleted = mockFood("삭제됨", "지워짐", FoodStatus.Deleted);
            Food ok = mockFood("짬뽕", "얼큰", FoodStatus.Ok);

            //  동일 mockRestaurant 사용 (unfinished stubbing 방지)
            Restaurant mockR = mockRestaurant(restaurantId);
            when(hidden.getRestaurant()).thenReturn(mockR);
            when(deleted.getRestaurant()).thenReturn(mockR);
            when(ok.getRestaurant()).thenReturn(mockR);

            given(menuRepository.findByRestaurant_RestaurantIdAndFoodNameContainingIgnoreCase(restaurantId, keyword))
                .willReturn(List.of(hidden, deleted, ok));
            given(menuRepository.findByRestaurant_RestaurantIdAndFoodExplainContainingIgnoreCase(restaurantId, keyword))
                .willReturn(List.of());

            // when
            List<FoodListDto> result = menuSearchService.searchMenus(restaurantId, keyword);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getName()).isEqualTo("짬뽕");
        }

        // ====== 테스트 헬퍼 ======
        private Food mockFood(String name, String explain, FoodStatus status) {
            Food f = mock(Food.class);
            //  Restaurant 연결 (convertDto 시 NPE 방지)
            Restaurant restaurant = mock(Restaurant.class);
            when(restaurant.getRestaurantId()).thenReturn(UUID.randomUUID());
            when(restaurant.getRestaurantName()).thenReturn("테스트식당");
            when(f.getRestaurant()).thenReturn(restaurant);

            when(f.getFoodName()).thenReturn(name);
            when(f.getFoodExplain()).thenReturn(explain);
            when(f.getFoodStatus()).thenReturn(status);
            return f;
        }

        // ---------- findRestaurants ----------
        @Nested
        @DisplayName("findRestaurants() - 카테고리/키워드 기반 목록 조회")
        class FindRestaurants {

            @Test
            @DisplayName("성공: typeCode, keyword 둘 다 null → 전체 중 status=true만 반환")
            void success_all() {
                RestaurantType type = mock(RestaurantType.class);
                when(type.getRestaurantTypeCode()).thenReturn(10);

                Restaurant active = mock(Restaurant.class);
                when(active.isStatus()).thenReturn(true);
                when(active.getRestaurantTypeId()).thenReturn(type);
                when(active.getRestaurantName()).thenReturn("짬뽕타운");
                when(active.getRestaurantAddress()).thenReturn("서울시 강남구");

                Restaurant inactive = mock(Restaurant.class);
                when(inactive.isStatus()).thenReturn(false);

                given(restaurantRepository.findAll()).willReturn(List.of(active, inactive));

                List<RestaurantInfoDto> result = restaurantService.findRestaurants(null, null);

                assertThat(result).hasSize(1);
                assertThat(result.get(0).getRestaurantName()).isEqualTo("짬뽕타운");
            }

            @Test
            @DisplayName("성공: typeCode와 keyword 둘 다 필터 적용")
            void success_filtered() {
                RestaurantType type10 = mock(RestaurantType.class);
                when(type10.getRestaurantTypeCode()).thenReturn(10);
                RestaurantType type20 = mock(RestaurantType.class);
                when(type20.getRestaurantTypeCode()).thenReturn(20);

                Restaurant a = mock(Restaurant.class);
                when(a.isStatus()).thenReturn(true);
                when(a.getRestaurantTypeId()).thenReturn(type10);
                when(a.getRestaurantName()).thenReturn("공리짬뽕");
                when(a.getRestaurantAddress()).thenReturn("서울");

                Restaurant b = mock(Restaurant.class);
                when(b.isStatus()).thenReturn(true);
                when(b.getRestaurantTypeId()).thenReturn(type20);
                when(b.getRestaurantName()).thenReturn("고양이분식");
                when(b.getRestaurantAddress()).thenReturn("부산");

                given(restaurantRepository.findAll()).willReturn(List.of(a, b));

                List<RestaurantInfoDto> result = restaurantService.findRestaurants("10", "짬");

                assertThat(result).hasSize(1);
                assertThat(result.get(0).getRestaurantName()).isEqualTo("공리짬뽕");
            }
        }

        // ---------- getRestaurantMenu ----------
        @Nested
        @DisplayName("getRestaurantMenu() - 특정 식당 메뉴 조회")
        class GetRestaurantMenu {

            @Test
            @DisplayName("성공: Hidden/Deleted 제외")
            void success_excludeHiddenDeleted() {
                UUID id = UUID.randomUUID();
                given(restaurantRepository.existsById(id)).willReturn(true);

                Food hidden = mockFood("숨김", "비밀", FoodStatus.Hidden);
                Food deleted = mockFood("삭제", "지워짐", FoodStatus.Deleted);
                Food ok = mockFood("짬뽕", "얼큰", FoodStatus.Ok);

                Restaurant mockR = mockRestaurant(id);
                when(hidden.getRestaurant()).thenReturn(mockR);
                when(deleted.getRestaurant()).thenReturn(mockR);
                when(ok.getRestaurant()).thenReturn(mockR);

                given(foodRepository.findAll()).willReturn(List.of(hidden, deleted, ok));

                List<FoodListDto> result = restaurantService.getRestaurantMenu(id, false);

                assertThat(result).hasSize(1);
                assertThat(result.get(0).getName()).isEqualTo("짬뽕");
            }

            @Test
            @DisplayName("실패: 식당 존재하지 않음")
            void fail_notFound() {
                UUID id = UUID.randomUUID();
                given(restaurantRepository.existsById(id)).willReturn(false);

                BusinessException ex = assertThrows(BusinessException.class,
                                                    () -> restaurantService.getRestaurantMenu(id, false));

                assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.RESTAURANT_NOT_FOUND);
            }
        }

        // ---------- getFoodDetail ----------
        @Nested
        @DisplayName("getFoodDetail() - 특정 메뉴 상세 보기")
        class GetFoodDetail {

            @Test
            @DisplayName("성공: 판매중인 메뉴 조회")
            void success_ok() {
                UUID restaurantId = UUID.randomUUID();
                UUID foodId = UUID.randomUUID();

                Food food = mockFood("짬뽕", "얼큰", FoodStatus.Ok);
                //  Restaurant 연결
                Restaurant restaurantMock = mockRestaurant(restaurantId);
                when(food.getRestaurant()).thenReturn(restaurantMock);

                given(foodRepository.findByIdAndRestaurant_RestaurantId(foodId, restaurantId))
                    .willReturn(Optional.of(food));

                restaurantService.getFoodDetail(restaurantId, foodId);

                verify(foodRepository).findByIdAndRestaurant_RestaurantId(foodId, restaurantId);
            }

            @Test
            @DisplayName("실패: FOOD_NOT_FOUND")
            void fail_foodNotFound() {
                UUID restaurantId = UUID.randomUUID();
                UUID foodId = UUID.randomUUID();

                given(foodRepository.findByIdAndRestaurant_RestaurantId(foodId, restaurantId))
                    .willReturn(Optional.empty());

                BusinessException ex = assertThrows(BusinessException.class,
                                                    () -> restaurantService.getFoodDetail(restaurantId, foodId));

                assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.FOOD_NOT_FOUND);
            }

            @Test
            @DisplayName("실패: FOOD_NOT_SELL (판매중 아님)")
            void fail_notSell() {
                UUID restaurantId = UUID.randomUUID();
                UUID foodId = UUID.randomUUID();

                Food food = mockFood("짬뽕", "얼큰", FoodStatus.Hidden);
                Restaurant restaurantMock = mockRestaurant(restaurantId);
                when(food.getRestaurant()).thenReturn(restaurantMock);
                given(foodRepository.findByIdAndRestaurant_RestaurantId(foodId, restaurantId))
                    .willReturn(Optional.of(food));

                BusinessException ex = assertThrows(BusinessException.class,
                                                    () -> restaurantService.getFoodDetail(restaurantId, foodId));

                assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.FOOD_NOT_SELL);
            }
        }

        // ---------- getFoodDetails ----------
        @Nested
        @DisplayName("getFoodDetails() - 특정 메뉴 옵션 전체 보기")
        class GetFoodDetails {

            @Test
            @DisplayName("성공: 판매중 + 옵션 존재")
            void success() {
                UUID restaurantId = UUID.randomUUID();
                UUID foodId = UUID.randomUUID();

                Food food = mockFood("짬뽕", "얼큰", FoodStatus.Ok);
                Restaurant restaurantMock = mockRestaurant(restaurantId);
                when(food.getRestaurant()).thenReturn(restaurantMock);
                given(foodRepository.findByIdAndRestaurant_RestaurantId(foodId, restaurantId))
                    .willReturn(Optional.of(food));

                var option = mock(com.sparta.goatgam.domain.owner.entity.FoodOption.class);
                given(foodOptionRepository.findByFood_IdAndDeletedFalse(foodId))
                    .willReturn(List.of(option));

                restaurantService.getFoodDetails(restaurantId, foodId);

                verify(foodOptionRepository).findByFood_IdAndDeletedFalse(foodId);
            }

            @Test
            @DisplayName("실패: 옵션 없음 → OPTION_NOT_FOUND")
            void fail_noOption() {
                UUID restaurantId = UUID.randomUUID();
                UUID foodId = UUID.randomUUID();

                Food food = mockFood("짬뽕", "얼큰", FoodStatus.Ok);
                Restaurant restaurantMock = mockRestaurant(restaurantId);
                when(food.getRestaurant()).thenReturn(restaurantMock);
                given(foodRepository.findByIdAndRestaurant_RestaurantId(foodId, restaurantId))
                    .willReturn(Optional.of(food));
                given(foodOptionRepository.findByFood_IdAndDeletedFalse(foodId))
                    .willReturn(List.of());

                BusinessException ex = assertThrows(BusinessException.class,
                                                    () -> restaurantService.getFoodDetails(restaurantId, foodId));

                assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.OPTION_NOT_FOUND);
            }
        }

        // ====== 헬퍼 ======
        private Restaurant mockRestaurant(UUID id) {
            Restaurant r = mock(Restaurant.class);
            when(r.getRestaurantId()).thenReturn(id);
            when(r.getRestaurantName()).thenReturn("가짜식당");

            //  user mock 추가 (Dto 변환, checkUser 방지)
            User u = mock(User.class);
            when(u.getUserId()).thenReturn(1L);
            when(r.getUser()).thenReturn(u);
            when(r.isStatus()).thenReturn(true);

            return r;
        }
    }


    // ====== 테스트 헬퍼 (세터/생성자 없어도 동작하게 mock 기반) ======
    private User mockUser(Long id, UserRoleEnum role) {
        User u = mock(User.class);
        when(u.getUserId()).thenReturn(id);
        when(u.getRole()).thenReturn(role);
        return u;
    }

    private RestaurantRequestDto mockRequest(Long userId, UUID typeId, String name, String addr, int region) {
        RestaurantRequestDto dto = mock(RestaurantRequestDto.class);
        when(dto.getUserId()).thenReturn(userId);
        when(dto.getRestaurantTypeId()).thenReturn(typeId);
        when(dto.getRestaurantName()).thenReturn(name);
        when(dto.getRestaurantAddress()).thenReturn(addr);
        when(dto.getRegionCode()).thenReturn(region);
        when(dto.getRestaurantNumber()).thenReturn("02-1234-5678");
        return dto;
    }
}
