package com.example.demo.config;

import lombok.Getter;

/**
 * 에러 코드 관리
 */
@Getter
public enum BaseResponseStatus {
    /**
     * 1000 : 요청 성공
     */
    SUCCESS(true, 1000, "요청에 성공하였습니다."),


    /**
     * 2000 : Request 오류
     */
    // Common
    REQUEST_ERROR(false, 2000, "입력값을 확인해주세요."),
    EMPTY_JWT(false, 2001, "JWT를 입력해주세요."),
    INVALID_JWT(false, 2002, "유효하지 않은 JWT입니다."),
    INVALID_USER_JWT(false,2003,"권한이 없는 유저의 접근입니다."),

    // users
    USERS_EMPTY_USER_ID(false, 2010, "유저 아이디 값을 확인해주세요."),

    // [POST] /users
    POST_USERS_EMPTY_NAME(false, 2014, "이름을 입력해주세요."),
    POST_USERS_EMPTY_PHONE(false, 2015, "핸드폰 번호를 입력해주세요."),
    POST_USERS_INVALID_PHONE(false, 2016, "핸드폰 번호 형식을 확인해주세요."),
    POST_USERS_EXISTS_USER(false,2017,"이미 등록된 전화번호 입니다."),
    POST_USERS_EMPTY_BIRTH(false,2018,"생년월일을 입력해 주세요"),
    POST_USERS_INVALID_BIRTH(false,2019,"생년월일 형식을 확인해 주세요."),




    /**
     * 3000 : Response 오류
     */
    // Common
    RESPONSE_ERROR(false, 3000, "값을 불러오는데 실패하였습니다."),
    INVALID_STORE_ID (false, 3001, "존재하지 않는 상점 id 입니다."),

    // [POST] /users
    DUPLICATED_EMAIL(false, 3013, "중복된 이메일입니다."),
    FAILED_TO_LOGIN(false,3014,"없는 번호이거나 비밀번호가 틀렸습니다."),

    // 3300 : 상품 관련

    // 3310 : [GET] /bungae/product/:productId : 상품 상세 정보 조회 API(조회수, 찜하기 수, 채팅 수, 상품 관련 내용)
    // 3320 : [POST] /bungae/product/ : 상품 등록하기 API

    // 3330 : [GET] /bungae/product/category : 카테고리 항목 조회
    NOT_MATCH_CATEGORY_ID(false, 3330, "연관되지 않은 depth1Id와 depth2Id입니다."),
    NOT_EXIST_CATEGORY_ID(false, 3331, "더이상 데이터가 존재하지 않는 카테고리 id 입니다."),

    // 3400 : 번개톡 관련
    INVALID_ROOM_ID(false, 3400, "이용자가 접속 불가능한 대화방입니다."),


    /**
     * 4000 : Database, Server 오류
     */
    DATABASE_ERROR(false, 4000, "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, 4001, "서버와의 연결에 실패하였습니다."),

    //[PATCH] /users/{userIdx}
    MODIFY_FAIL_STOREINFO(false,4014,"상품 정보 수정 실패"),

    PASSWORD_ENCRYPTION_ERROR(false, 4011, "번호 암호화에 실패하였습니다."),
    PASSWORD_DECRYPTION_ERROR(false, 4012, "번호 복호화에 실패하였습니다.");


    // 5000 : 필요시 만들어서 쓰세요
    // 6000 : 필요시 만들어서 쓰세요


    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) { //BaseResponseStatus 에서 각 해당하는 코드를 생성자로 맵핑
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
