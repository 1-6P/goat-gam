package com.sparta.goatgam.global.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public final class PageableUtils {
    private PageableUtils() {
    }

    public static Pageable makePageable(int page, int size, Sort.Order... orders) {
        if (size != 10 && size != 30 && size != 50) size = 10;

        Sort sort = Sort.by(orders);

        return PageRequest.of(page, size, sort);
    }

    public static Sort.Order order(Sort.Direction direction, String field) {
        return new Sort.Order(direction, field);
    }
}
