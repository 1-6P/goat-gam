package com.sparta.goatgam.global.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public final class PageableUtils {
    private PageableUtils() {
    }

    /**
     * 페이지네이션을 적용할 때 필요한 PageRequest 객체를 Pageable로 캐스팅하여 return 하는 함수입니다.
     * <br/>
     * <br/>
     * 사용 예시:
     * <br/>
     * {@code
     * Pageable pageable = makePageable(page, size,
     *                                  order(Sort.Direction.ASC, "userUserId"),
     *                                  order(Sort.Direction.DESC, "createdAt")
     *                      );
     * }
     *
     * @param page   현재 페이지 번호입니다. 0부터 시작하는 integer값을 넣어주세요.
     * @param size   한 페이지 당 item 개수입니다. 10/30/50 옵션이 가능하고 이외의 값은 10으로 처리됩니다.
     * @param orders 결과값을 정렬할 정렬기준입니다. 가변인자로 여러개의 정렬기준을 적용할 수 있습니다.
     * @return Pageable로 캐스팅한 PageRequest 객체
     */
    public static Pageable makePageable(int page, int size, Sort.Order... orders) {
        if (size != 10 && size != 30 && size != 50) size = 10;

        Sort sort = Sort.by(orders);

        return PageRequest.of(page, size, sort);
    }

    /**
     * makePageable 함수에 parameter로 들어갈 Sort.Order 객체를 생성할 수 있는 함수입니다.
     * <br/>
     * <br/>
     * 사용 예시:
     * <br/>
     * {@code
     * order(Sort.Direction.DESC, "createdAt")
     * }
     *
     * @param direction 정렬 방향입니다. asc, desc 등 방향을 넣어주세요.
     * @param field     정렬 기준으로 삼을 field입니다. Entity 기준 field 이름을 적어주세요. table field 명 아닙니다! Entity에서 User 객체를 외래키로 참조한다면 User 내부의 field에도 접근할 수 있습니다.
     * @see Sort.Order
     * @see Sort.Direction
     */
    public static Sort.Order order(Sort.Direction direction, String field) {
        return new Sort.Order(direction, field);
    }
}
