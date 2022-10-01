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
    EMPTY_STORENAME(false,2020,"상점 이름을 입력해주세요."),
    TOO_LONG_STORENAME(false,2021,"상점 이름의 길이가 10자를 초과하였습니다."),
    TOO_LONG_INTRODUCE(false,2022,"상점 소개의 길이가 1000자를 초과하였습니다."),
    TOO_LONG_POLICY(false,2023,"교환/환불/반품 정책의 길이가 1000자를 초과하였습니다."),
    TOO_LONG_PRECAUTIONS(false,2024,"구매전 유의 사항의 길이가 1000자를 초과하였습니다."),

    // 2100 : 채팅 관련
    TOO_LONG_TEXT(false,2100,"텍스트의 길이가 천자를 초과하였습니다."),
    EMPTY_TEXT(false, 2101, "텍스트를 입력해주세요."),
    EMPTY_IMAGE(false, 2102, "이미지를 입력해주세요."),
    EMPTY_EMOTICON(false, 2103, "이모티콘을 입력해주세요."),

    // 2200 : 피드 관련
    INVALID_FEED_ORDER(false,2200,"올바르지 않은 order 입력입니다."),
    INVALID_ONLYSALE(false,2201,"onlySale에 true 혹은 false를 입력해주세요"),
    INVALID_PRICE_RANGE(false,2201,"가격 범위가 올바르지 않습니다. max와 min을 확인해주세요."),


    // 2300 : 상품 관련
    // 2300 : [GET] /bungae/product/:productId : 상품 상세 정보 조회 API(조회수, 찜하기 수, 채팅 수, 상품 관련 내용)
    // 2310 : [POST] /bungae/product/ : 상품 등록하기 API
    EMPTY_NAME(false,2310,"name을 입력해주세요."),
    TOO_LONG_TITLE(false,2311,"name의 길이가 100자를 초과하였습니다."),
    EMPTY_CONTENT(false,2312 ,"content를 입력해주세요."),
    TOO_LONG_CONTENT(false,2313 ,"content의 길이가 1000자를 초과하였습니다."),
    EMPTY_IMAGEURL(false,2314 ,"imageUrl을 한 개 이상 입력해주세요."),
    TOO_MANY_IMAGEURL(false,2315 ,"imageUrl의 수가 10개를 초과하였습니다."),
    TOO_LONG_IMAGEURL00 (false,2316 ,"imageUrl의 0 번째 항목이 500자를 초과하였습니다."),
    TOO_LONG_IMAGEURL01(false,2317 ,"imageUrl의 1 번째 항목이 500자를 초과하였습니다."),
    TOO_LONG_IMAGEURL02(false,2318,"imageUrl의 2 번째 항목이 500자를 초과하였습니다."),
    TOO_LONG_IMAGEURL03(false,2319,"imageUrl의 3 번째 항목이 500자를 초과하였습니다."),
    TOO_LONG_IMAGEURL04(false,2320,"imageUrl의 4 번째 항목이 500자를 초과하였습니다."),
    TOO_LONG_IMAGEURL05(false,2321,"imageUrl의 5 번째 항목이 500자를 초과하였습니다."),
    TOO_LONG_IMAGEURL06(false,2322,"imageUrl의 6 번째 항목이 500자를 초과하였습니다."),
    TOO_LONG_IMAGEURL07(false,2323,"imageUrl의 7 번째 항목이 500자를 초과하였습니다."),
    TOO_LONG_IMAGEURL08(false,2324,"imageUrl의 8 번째 항목이 500자를 초과하였습니다."),
    TOO_LONG_IMAGEURL09(false,2325,"imageUrl의 9 번째 항목이 500자를 초과하였습니다."),
    EMPTY_CATEGORYD1ID(false,2326,"categoryDepth1Id을 입력해주세요."),
    EMPTY_CATEGORYD2ID(false,2327,"categoryDepth2Id을 입력해주세요."),
    EMPTY_CATEGORYD3ID(false,2328,"categoryDepth3Id을 입력해주세요."),
    TOO_MANY_TAGS(false,2329,"tag의 수가 5개를 초과하였습니다."),
    TOO_LONG_TAGS(false,2330,"tag의 글자수가 15자를 초과하였습니다."),
    EMPTY_PRICE(false,2331,"price를 입력해주세요"),
    EMPTY_DELIVERYFREE(false,2332,"deliveryFee를 입력해주세요"),
    EMPTY_QUANTITY(false,2333,"quantity를 입력해주세요"),
    EMPTY_CONDITION(false,2334,"condition를 입력해주세요"),
    EMPTY_CHANGE(false,2334,"change를 입력해주세요"),
    TOO_LONG_LOCATION(false,2334,"location의 길이가 50자를 초과하였습니다."),
    EMPTY_IMAGEURL_LIST(false,2335,"imageUrls 리스트를 입력해주세요."),
    INVALID_CONDITION(false,2336,"condition은 '새상품' 혹은 '중고상품' 으로 기록해주세요."),
    BASKET_EXIST(false,2337,"이미 찜하기에 존재하는 상품입니다."),
    BASKET_DELETE(false,2338,"이미 찜하기 해제가 완료되었습니다."),
    FOLLOW_EXIST(false,2339,"이미 팔로우한 계정입니다."),
    FOLLOW_DELETE(false,2340,"이미 언팔로우한 계정입니다."),
    STATUS_UNAVAILABE(false,2341,"sale, reserved, closed 형태로만 가능합니다."),



    // 2400 : 번개톡 관련
    ACCOUNT_REGEX_ERR(false,2401,"올바르지 않은 계좌번호입니다."),
    PHONE_REGEX_ERR(false,2402,"올바르지 않은 전화번호입니다."),


    /**
     * 3000 : Response 오류
     */
    // Common
    RESPONSE_ERROR(false, 3000, "값을 불러오는데 실패하였습니다."),
    INVALID_STORE_ID (false, 3001, "존재하지 않는 상점 id 입니다."),

    // [POST] /users
    DUPLICATED_EMAIL(false, 3013, "중복된 이메일입니다."),
    FAILED_TO_LOGIN(false,3014,"없는 번호이거나 비밀번호가 틀렸습니다."),
    // 3200 : 피드 관련
    BRAND_NOT_EXIST(false, 3200, "존재하지 않는 브랜드명입니다."),

    // 3300 : 상품 관련
    // 3300 : [GET] /bungae/product/:productId : 상품 상세 정보 조회 API(조회수, 찜하기 수, 채팅 수, 상품 관련 내용)
    INVALID_PRODUCT_ID(false, 3301, "존재하지 않는 상품입니다."),
    USER_NOT_PERMITTED(false, 3302, "해당 사용자가 접근할 수 없는 상품입니다."),
    // 3310 : [POST] /bungae/product/ : 상품 등록하기 API
    // 3330 : [GET] /bungae/product/category : 카테고리 항목 조회
    NOT_MATCH_CATEGORY_12_ID(false, 3330, "연관되지 않은 depth1Id와 depth2Id입니다."),
    NOT_EXIST_CATEGORY_ID(false, 3331, "더이상 데이터가 존재하지 않는 카테고리 id 입니다."),
    INVALID_CATEGORYD1ID(false, 3332, "잘못된 categoryDepth1Id 입니다."),
    INVALID_CATEGORYD2ID(false, 3333, "잘못된 categoryDepth2Id 입니다."),
    NOT_MATCH_CATEGORY_23_ID(false, 3334, "연관되지 않은 depth2Id와 depth3Id입니다."),

    // 3400 : 번개톡 관련
    INVALID_ROOM_ID(false, 3400, "이용자가 접속 불가능한 대화방입니다."),
    SEND_NOT_PERMITTED(false, 3401, "이용자가 전송할 수 없는 상품입니다."),
    VIEW_NOT_PERMITTED(false, 3402, "이용자가 조회할 수 없는 상품입니다."),
    MODIFY_NOT_PERMITTED(false, 3403, "이용자가 수정할 수 없는 메시지입니다."),
    INVALID_MESSAGE_ID(false, 3404, "존재하지 않는 메시지 아이디입니다."),
    ITS_MY_PRODUCT(false, 3405, "내 상품과는 채팅이 불가능합니다."),


    /**
     * 4000 : Database, Server 오류
     */
    DATABASE_ERROR(false, 4000, "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, 4001, "서버와의 연결에 실패하였습니다."),
    VALIDATION_ERROR(false, 4002, "Validation에 실패하였습니다."),

    //[PATCH] /users/{userIdx}
    MODIFY_FAIL_STOREINFO(false,4014,"상품 정보 수정 실패"),

    PASSWORD_ENCRYPTION_ERROR(false, 4011, "번호 암호화에 실패하였습니다."),
    PASSWORD_DECRYPTION_ERROR(false, 4012, "번호 복호화에 실패하였습니다."),

    // 4300 : 상품 관련
    // 4310 : [POST] /bungae/product/ : 상품 등록하기 API
    NEW_PRODUCT_ERROR(false, 4310, "상품 정보 생성에 실패하였습니다"),
    NEW_TAGS_ERROR(false, 4311, "상품 태그 정보 생성에 실패하였습니다"),
    NEW_IMAGES_ERROR(false, 4312, "상품 이미지 정보 생성에 실패하였습니다"),
    NEW_LOCATION_ERROR(false, 4313, "상품 location 정보 생성에 실패하였습니다.");


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
