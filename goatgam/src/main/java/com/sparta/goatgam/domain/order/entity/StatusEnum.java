package com.sparta.goatgam.domain.order.entity;

import lombok.Getter;

@Getter
public enum StatusEnum {
    // 주문 요청, 주문 취소, 주문 수락, 주문 거절, 조리 완료, 배달중, 완료, 환불
    Request, Cancel, Accept, Reject, Prepared, OnDelivery, Completed, Refund
}
