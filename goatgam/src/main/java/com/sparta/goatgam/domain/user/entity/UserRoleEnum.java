package com.sparta.goatgam.domain.user.entity;

public enum UserRoleEnum {
    Customer(Authority.User), // 소비자
    Owner(Authority.User), // 사장님
    Manager(Authority.Manager), // 관리자
    Master(Authority.Master); // 최고관리자

    private final String authority;

    UserRoleEnum(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return this.authority;
    }

    public static class Authority {
        public static final String User = "User";
        public static final String Manager = "Manager";
        public static final String Master = "Master";
    }
}
