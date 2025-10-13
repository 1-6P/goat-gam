package com.sparta.goatgam.domain.cart.service;

import com.sparta.goatgam.domain.cart.dto.CartFoodRequestDto;
import com.sparta.goatgam.domain.cart.dto.CartFoodUpdateRequestDto;
import com.sparta.goatgam.domain.cart.dto.CartResponseDto;
import com.sparta.goatgam.domain.cart.entity.Cart;
import com.sparta.goatgam.domain.cart.entity.CartFood;
import com.sparta.goatgam.domain.cart.entity.CartFoodOption;
import com.sparta.goatgam.domain.cart.repository.CartFoodRepository;
import com.sparta.goatgam.domain.cart.repository.CartRepository;
import com.sparta.goatgam.domain.owner.entity.Food;
import com.sparta.goatgam.domain.owner.entity.FoodOption;
import com.sparta.goatgam.domain.owner.repository.FoodOptionRepository;
import com.sparta.goatgam.domain.owner.repository.FoodRepository;
import com.sparta.goatgam.domain.restaurant.entity.Restaurant;
import com.sparta.goatgam.domain.restaurant.repository.RestaurantRepository;
import com.sparta.goatgam.domain.user.entity.User;
import com.sparta.goatgam.global.dto.MessageAndIdResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {

    private final CartRepository cartRepository;
    private final CartFoodRepository cartFoodRepository;

    private final RestaurantRepository restaurantRepository;

    private final FoodRepository foodRepository;
    private final FoodOptionRepository foodOptionRepository;

    @Transactional
    public MessageAndIdResponseDto addCartFood(CartFoodRequestDto cartFoodRequestDto, User user) {
        Restaurant restaurant = restaurantRepository.findById(cartFoodRequestDto.restaurantId()).orElseThrow(() ->
                new IllegalArgumentException("식당을 찾을 수 없습니다."));
        Food food = foodRepository.findByIdAndRestaurant_RestaurantId(
                cartFoodRequestDto.foodId(),
                cartFoodRequestDto.restaurantId()
        ).orElseThrow(() -> new IllegalArgumentException("음식을 찾을 수 없습니다."));
        // 1. 해당 유저가 소유하고 있는 활성화 카드 정보 가져오기.
        // 2. 없으면 새로 생성
        Cart cart = cartRepository.findByUserAndIsDeletedFalse(user).orElseGet(() -> {
            Cart newCart = Cart.create(user, restaurant);
            cartRepository.save(newCart);
            return newCart;
        });

        // 3. 있으면 requestDto에서 restaurantId 가져와서 cart의 restaurantId와 비교
        // 4. 있는데 같으면 카트 유지
        if (cart.getRestaurant().getRestaurantId().equals(cartFoodRequestDto.restaurantId())) {
            // 7. 카트에 이미 해당 음식 있으면 개수만 더해서 update(옵션까지 동일해야함)
            List<CartFood> cartFoodList = cartFoodRepository.findAllByCartAndFoodAndIsDeletedFalse(cart, food);
            for (CartFood cartFood : cartFoodList) {
                if (food.getId().equals(cartFood.getFood().getId()) &&
                        optionEquals(cartFood, cartFoodRequestDto.options())
                ) {
                    cartFood.setQuantity(cartFood.getQuantity() + cartFoodRequestDto.quantity());

                    // 가격 업데이트
                    for (CartFoodOption cartFoodOption : cartFood.getCartFoodOptions()) {
                        cartFoodOption.setPrice(cartFoodOption.getFoodOption().getSurcharge());
                    }
                    cartFood.setPrice(cartFood.getFood().getFoodPrice());

                    return new MessageAndIdResponseDto("장바구니에 음식을 성공적으로 담았습니다.", cart.getCartId());
                }
            }
        } else {
            // 5. 있는데 다르면 카트 삭제 후 새 카트 생성
            cart.delete(user);
            cart = Cart.create(user, restaurant);
            cartRepository.save(cart);
        }

        // 6. 카트에 음식 담기   -> message와 카트 ID return
        CartFood cartFood = CartFood.create(food, cartFoodRequestDto.quantity());

        // 7. 옵션 있으면 옵션도 담기
        if (cartFoodRequestDto.options() != null)
            for (UUID foodOptionId : cartFoodRequestDto.options()) {
                FoodOption foodOption = foodOptionRepository.findById(foodOptionId).orElseThrow(() ->
                        new IllegalArgumentException("음식 옵션을 찾을 수 없습니다. foodOptionId: " + foodOptionId));

                if (!foodOption.getFood().getId().equals(food.getId())) {
                    throw new IllegalArgumentException("이 음식의 옵션이 아닙니다. " +
                            "foodId: " + food.getId() + "foodOptionId: " + foodOptionId);
                }

                CartFoodOption cartFoodOption = CartFoodOption.create(foodOption);
                cartFood.addCartFoodOption(cartFoodOption);
            }

        cart.addCartFood(cartFood);

        return new MessageAndIdResponseDto("장바구니에 음식을 성공적으로 담았습니다.", cart.getCartId());
    }

    public CartResponseDto getCartInfo(User user) {
        Cart cart = cartRepository.findByUserAndIsDeletedFalse(user).orElseThrow(() ->
                new IllegalArgumentException("생성된 장바구니가 없습니다."));

        // @SQLRestriction 어노테이션으로 인해 삭제된 CartFood와 CartFoodOption은 자동으로 제외됨
        return new CartResponseDto(cart);
    }

    @Transactional
    public MessageAndIdResponseDto updateCartFoodOption(CartFoodUpdateRequestDto cartFoodUpdateRequestDto, User user) {
        CartFood cartFood = cartFoodRepository.findById(cartFoodUpdateRequestDto.cartFoodId()).orElseThrow(() ->
                new IllegalArgumentException("장바구니에 해당 음식이 존재하지 않습니다."));

        ArrayList<UUID> newFoodOptionList;
        if (cartFoodUpdateRequestDto.changeOptionList() == null)
            newFoodOptionList = new ArrayList<>();
        else
            newFoodOptionList = new ArrayList<>(cartFoodUpdateRequestDto.changeOptionList());

        // 옵션 비교해서 삭제된 옵션 지우고
        for (CartFoodOption cartFoodOption : cartFood.getCartFoodOptions()) {
            if (!newFoodOptionList.contains(cartFoodOption.getFoodOption().getId())) {
                cartFoodOption.delete(user);
            } else {
                newFoodOptionList.remove(cartFoodOption.getFoodOption().getId());
            }
        }

        // 변경된 음식과 기존 음식 비교해서 같으면 음식 자체를 지우고 qnatity plus
        for (CartFood cartFoodItem : cartFood.getCart().getCartFoods()) {
            if (optionEquals(cartFoodItem, cartFoodUpdateRequestDto.changeOptionList())) {
                cartFood.delete(user);
                cartFoodItem.setQuantity(cartFoodItem.getQuantity() + cartFood.getQuantity());
                return new MessageAndIdResponseDto("옵션을 성공적으로 변경했습니다.", cartFood.getCart().getCartId());
            }
        }

        // 새 옵션 저장
        for (UUID foodOptionId : newFoodOptionList) {
            FoodOption foodOption = foodOptionRepository.findById(foodOptionId).orElseThrow(() ->
                    new IllegalArgumentException("음식 옵션을 찾을 수 없습니다. foodOptionId: " + foodOptionId));

            if (!foodOption.getFood().getId().equals(cartFood.getFood().getId())) {
                throw new IllegalArgumentException("이 음식의 옵션이 아닙니다. " +
                        "foodId: " + cartFood.getFood().getId() + "foodOptionId: " + foodOptionId);
            }

            CartFoodOption cartFoodOption = CartFoodOption.create(foodOption);
            cartFood.addCartFoodOption(cartFoodOption);
        }

        return new MessageAndIdResponseDto("옵션을 성공적으로 변경했습니다.", cartFood.getCart().getCartId());
    }

    private boolean optionEquals(CartFood cartFoodA, List<UUID> optionIdListB) {
        Set<UUID> optionIdSetA = cartFoodA.getCartFoodOptions().stream().map(o -> o.getFoodOption().getId())
                .collect(Collectors.toSet());
        Set<UUID> optionIdSetB = new HashSet<>(Optional.ofNullable(optionIdListB).orElse(List.of()));

        return optionIdSetA.equals(optionIdSetB);
    }
}
