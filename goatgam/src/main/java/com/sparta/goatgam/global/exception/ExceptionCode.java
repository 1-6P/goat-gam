package com.sparta.goatgam.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ExceptionCode {

    //공통(E)
    INTERNAL_SERVER_ERROR("E01", "서버 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_INPUT("E02", "잘못된 요청입니다.", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED("E03", "인증이 필요합니다.", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("E04", "접근이 거부되었습니다.", HttpStatus.FORBIDDEN),
    METHOD_NOT_ALLOWED("E05", "지원하지 않는 API 요청입니다.", HttpStatus.METHOD_NOT_ALLOWED),
    DATA_INTEGRITY_VIOLATION("E06", "데이터 무결성 제약 조건을 위반했습니다.", HttpStatus.CONFLICT),
    UNSUPPORTED_MEDIA_TYPE("E07", "지원하지 않는 요청 형식입니다.", HttpStatus.UNSUPPORTED_MEDIA_TYPE),

    //사용자 도메인(U)
    USER_NOT_FOUND("U01", "사용자 정보를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    DUPLICATE_EMAIL("U02", "이미 가입된 이메일입니다.", HttpStatus.CONFLICT),
    DUPLICATE_D("U03", "이미 사용 중인 닉네임입니다.", HttpStatus.CONFLICT),
    INVALID_EMAIL("U04", "이메일이 일치하지 않습니다..", HttpStatus.UNAUTHORIZED),
    INVALID_PASSWORD("U05", "비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),
    INVALID_USER("U05", "인증된 사용자 정보와 요청의 userId가 다릅니다", HttpStatus.BAD_REQUEST),

    //TOKEN(AUTH)
    INVALID_TOKEN("AUTH01", "유효하지 않은 토큰입니다.,HttpStatus.UNAUTHORIZED", HttpStatus.UNAUTHORIZED),

    //식당(R)
    RESTAURANT_NOT_FOUND("R01", "식당을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    FORBIDDEN_CREATE_RESTAURANT_("R02", "식당 생성 권한이 없습니다..", HttpStatus.FORBIDDEN),
    FORBIDDEN_UPDATE_RESTAURANT("R03", "식당 정보를 수정할 권한이 없습니다.", HttpStatus.FORBIDDEN),
    FORBIDDEN_DELETE_RESTAURANT("R04", "식당 정보를 삭제할 권한이 없습니다.", HttpStatus.FORBIDDEN),
    FORBIDDEN_ROLLBACK_RESTAURANT("R05", "식당 삭제를 롤백할 권한이 없습니다.", HttpStatus.FORBIDDEN),

    //주소 (A)
    ADDRESS_USER_NOT_FOUND("A01", "해당 유저의 주소가 없습니다.", HttpStatus.NOT_FOUND),
    ADDRESS_USER_NOT_HAVE("A02", "유저에게 해당 주소가 없습니다.", HttpStatus.NOT_FOUND),
    ADDRESS_INPUT_ERROR("A03", "올바른 법정동코드 형식이 아닙니다.", HttpStatus.BAD_REQUEST),
    ADDRESS_NOT_EXIST("A04", "존재하지 않는 법정동 코드입니다.", HttpStatus.NOT_FOUND),

    //음식(F)
    FOOD_NOT_FOUND("F01", "메뉴를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    FOOD_DUPLICATED("F02", "중복된 메뉴 이름입니다.", HttpStatus.CONFLICT),
    FORBIDDEN_CREATE_MENU("F03", "메뉴 생성 권한이 없습니다.", HttpStatus.FORBIDDEN),
    FORBIDDEN_UPDATE_MENU("F04", "메뉴 수정 권한이 없습니다.", HttpStatus.FORBIDDEN),
    FORBIDDEN_DELETE_MENU("F05", "메뉴 삭제 권한이 없습니다.", HttpStatus.FORBIDDEN),
    FOOD_INPUT_ERROR("F06", "식당과 음식이 올바르게 연결되지 않았습니다.", HttpStatus.BAD_REQUEST),
    FOOD_INPUT_ERROR_OPEN("F07", "운영중인 식당의 음식만 담을 수 있습니다.", HttpStatus.BAD_REQUEST),
    FOOD_NOT_SELL("F08", "판매중인 음식이 아닙니다", HttpStatus.BAD_REQUEST),
    FOOD_DELETED_USER("F09", "삭제된 사용자는 음식을 담을 수 없음", HttpStatus.GONE),
    FOOD_ALREADY_DELETED("F10", "삭제된 음식입니다.", HttpStatus.GONE),
    FOOD_RESTAURANT_ALREADY_DELETED("F11", "삭제된 식당엔 음식을 추가할 수 없습니다.", HttpStatus.GONE),

    //음식 옵션, 알파벳 O로 시작
    OPTION_NOT_FOUND("O01", "해당 음식의 옵션을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    OPTION_DUPLICATED("O02", "해당 음식의 옵션이 중복됩니다.", HttpStatus.CONFLICT),
    OPTION_INPUT_ERROR("O03", "음식과 옵션이 올바르게 연결되지 않았습니다.", HttpStatus.BAD_REQUEST),
    OPTION_ALREADY_DELETED("O04", "삭제된 옵션입니다", HttpStatus.GONE),

    //장바구니 (C)
    CART_NOT_FOUND("C01", "생성된 장바구니가 없습니다.", HttpStatus.NOT_FOUND),
    CART_MISSING_FOOD("C02", "장바구니에 해당 음식이 없습니다.", HttpStatus.NOT_FOUND),
    CART_DELETED_OPTION("C03", "장바구니에 삭제된 옵션은 담을 수 없습니다.", HttpStatus.GONE),
    CART_DELETED_FOOD("C04", "이미 삭제된 음식입니다.", HttpStatus.GONE),
    CART_FOOD_QUANTITY_LOWER_THEN_2("C05", "장바구니에 담긴 음식의 수량이 2보다 작습니다.", HttpStatus.BAD_REQUEST),
    FORBIDDEN_UPDATE_CART("C06", "장바구니 접근 권한이 없습니다.", HttpStatus.FORBIDDEN),

    //AI (AI)
    AI_LOG_NOT_FOUND("AI01", "AI 로그를 찾을 수 없습니다", HttpStatus.NOT_FOUND),

    //팔로우 (UF)
    FOLLOW_ALREADY("UF01", "이미 팔로우 되어있는 상태입니다.", HttpStatus.BAD_REQUEST),
    FOLLOW_NO("UF01", "팔로우 상태가 아닙니다.", HttpStatus.BAD_REQUEST),

    //주문내역 (ORDER)
    ORDER_NOT_FOUND("ORDER01", "존재하지 않는 주문내역입니다.", HttpStatus.NOT_FOUND),
    FORBIDDEN_ORDER("ORDER02", "주문에 대한 권한이 없습니다. ", HttpStatus.FORBIDDEN),

    //리뷰 (RV)
    REVIEW_NOT_FOUND("RV01", "해당 리뷰를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    FORBIDDEN_CREATE_REVIEW_("RV02", "본인 주문이 아닌경우 리뷰를 작성할 수 없습니다.", HttpStatus.FORBIDDEN),
    FORBIDDEN_ORDER_REVIEW("RV03", "배송 완료된 주문만 리뷰를 작성할 수 있습니다.", HttpStatus.FORBIDDEN),
    REVIEW_ORDER_NOT_ALLOWED("RV04", "해당 주문에 대한 리뷰가 이미 작성되었습니다.", HttpStatus.BAD_REQUEST),
    REVIEW_RATE_ERROR("RV05", "평점은 1점 이상 5점 이하만 가능합니다.", HttpStatus.BAD_REQUEST),


    //결제처리 (PG)
    PG_NOT_FOUND("PG01", "결제 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    PG_TIMEOUT("PG02", "결제 처리 시간이 지났습니다.", HttpStatus.CONFLICT),
    ;


    private final String code;
    private final String message;
    private final HttpStatus status;
}
