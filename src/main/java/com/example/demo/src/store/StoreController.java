package com.example.demo.src.store;

import com.example.demo.utils.Verifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.store.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.*;

@RestController // Rest API 또는 WebAPI를 개발하기 위한 어노테이션. @Controller + @ResponseBody 를 합친것.
                // @Controller      [Presentation Layer에서 Contoller를 명시하기 위해 사용]
                //  [Presentation Layer?] 클라이언트와 최초로 만나는 곳으로 데이터 입출력이 발생하는 곳
                //  Web MVC 코드에 사용되는 어노테이션. @RequestMapping 어노테이션을 해당 어노테이션 밑에서만 사용할 수 있다.
                // @ResponseBody    모든 method의 return object를 적절한 형태로 변환 후, HTTP Response Body에 담아 반환.
@RequestMapping("/bungae/stores")
/**
 * Controller란?
 * 사용자의 Request를 전달받아 요청의 처리를 담당하는 Service, Prodiver 를 호출
 */
public class StoreController {

    final Logger logger = LoggerFactory.getLogger(this.getClass()); // Log를 남기기: 일단은 모르고 넘어가셔도 무방합니다.

    @Autowired
    private final StoreProvider storeProvider;
    @Autowired
    private final StoreService storeService;
    @Autowired
    private final JwtService jwtService;


    public StoreController(StoreProvider storeProvider, StoreService storeService, JwtService jwtService) {
        this.storeProvider = storeProvider;
        this.storeService = storeService;
        this.jwtService = jwtService;
    }

    private Verifier verifier;
    @Autowired
    public void setVerifier(Verifier verifier){
        this.verifier = verifier;
    }

    /**
     * 회원가입
     * [POST] bungae/stores/new
     */
    // Body
    @ResponseBody
    @PostMapping("/new")    // POST 방식의 요청을 매핑하기 위한 어노테이션
    public BaseResponse<PostStoreRes> createUser(@RequestBody PostStoreReq postStoreReq) {
        if (postStoreReq.getName().isEmpty()) {
            return new BaseResponse<>(POST_USERS_EMPTY_NAME);
        }
        if(postStoreReq.getPhone().isEmpty()){
            return new BaseResponse<>(POST_USERS_EMPTY_PHONE);
        }
        if (!isRegexPhone(postStoreReq.getPhone())) {
            return new BaseResponse<>(POST_USERS_INVALID_PHONE);
        }
        if(postStoreReq.getBirth().isEmpty()){
            return new BaseResponse<>(POST_USERS_EMPTY_BIRTH);
        }
        if(!isRegexBirth(postStoreReq.getBirth())){
            return new BaseResponse<>(POST_USERS_INVALID_BIRTH);
        }

        try {
            PostStoreRes postStoreRes = storeService.createUser(postStoreReq);
            return new BaseResponse<>(postStoreRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 로그인 API
     * [POST] bungae/stores/login
     */
    @ResponseBody
    @PostMapping("/login")
    public BaseResponse<PostLoginRes> logIn(@RequestBody PostLoginReq postLoginReq) {

        if(postLoginReq.getPhone().isEmpty()){
            return new BaseResponse<>(POST_USERS_EMPTY_PHONE);
        }
        if (!isRegexPhone(postLoginReq.getPhone())) {
            return new BaseResponse<>(POST_USERS_INVALID_PHONE);
        }

        try {
            PostLoginRes postLoginRes = storeProvider.logIn(postLoginReq);
            return new BaseResponse<>(postLoginRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 마이페이지 화면 조회 API(판매중)
     * [GET] /bungae/stores/sale
     */
    @ResponseBody
    @GetMapping("/{storeId}/sale")
    public BaseResponse<List<GetStoreSaleRes>> getSale(@PathVariable int storeId) {
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(storeId != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<GetStoreSaleRes> getStoreSaleRes = storeProvider.getStoreSale(storeId);
            return new BaseResponse<>(getStoreSaleRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 마이페이지 화면 조회 API(예약중)
     * [GET] /bungae/stores/reserved
     */
    @ResponseBody
    @GetMapping("/{storeId}/reserved")
    public BaseResponse<List<GetStoreReservedRes>> getReserved(@PathVariable int storeId) {
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(storeId != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<GetStoreReservedRes> getStoreReservedRes = storeProvider.getStoreReserved(storeId);
            return new BaseResponse<>(getStoreReservedRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * 마이페이지 화면 조회 API(판매완료)
     * [GET] /bungae/stores/closed
     */
    @ResponseBody
    @GetMapping("/{storeId}/closed")
    public BaseResponse<List<GetStoreClosedRes>> getClosed(@PathVariable int storeId) {
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(storeId != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<GetStoreClosedRes> getStoreClosedRes = storeProvider.getStoreClosed(storeId);
            return new BaseResponse<>(getStoreClosedRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * 마이페이지 화면 상점 클릭시 상점 설명 조회 API
     * [GET] /bungae/stores/detail
     */
    @ResponseBody
    @GetMapping("/{storeId}/detail")
    public BaseResponse<GetStoreDetailRes> getDetail(@PathVariable int storeId) {
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(storeId != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            GetStoreDetailRes getStoreDetailRes = storeProvider.getStoreDetail(storeId);
            return new BaseResponse<>(getStoreDetailRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 상점 상세 정보변경 API
     * [PATCH] /stores/:storeId/detail
     */
    @ResponseBody
    @PatchMapping("/{storeId}/detail")
    public BaseResponse<String> modifyStoreDetail(@PathVariable int storeId, @RequestBody PatchStoreDetailReq patchStoreDetailReq) {
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //storeId와 접근한 유저가 같은지 확인
            if(storeId != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
//            PatchStoreDetailReq patchStoreDetailReq = new PatchStoreDetailReq(storeId, );
            storeService.modifyStore(storeId, patchStoreDetailReq);

            String result = "상점 정보가 수정되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 찜한 목록 조회 API
     * [GET] /bungae/stores/:storeId/basket
     */
    @ResponseBody
    @GetMapping("/{storeId}/basket")
    public BaseResponse<List<GetStoreBasketRes>> getBasket(@PathVariable int storeId) {
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(storeId != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<GetStoreBasketRes> getStoreBasketRes = storeProvider.getStoreBasket(storeId);
            return new BaseResponse<>(getStoreBasketRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * 리뷰 목록 조회 API
     * [GET] /bungae/stores/:storeId/review
     */
    @ResponseBody
    @GetMapping("/{storeId}/review")
    public BaseResponse<List<GetStoreReviewRes>> getReview(@PathVariable int storeId) {
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(storeId != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<GetStoreReviewRes> getStoreReviewRes = storeProvider.getStoreReview(storeId);
            return new BaseResponse<>(getStoreReviewRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 팔로워 목록 조회 API
     * [GET] /bungae/stores/:storeId/following
     */
    @ResponseBody
    @GetMapping("/{storeId}/following")
    public BaseResponse<List<GetStoreFollowingRes>> getFollowing(@PathVariable int storeId) {
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(storeId != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<GetStoreFollowingRes> getStoreFollowingRes = storeProvider.getStoreFollowing(storeId);
            return new BaseResponse<>(getStoreFollowingRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * 팔로워 목록 조회 API
     * [GET] /bungae/stores/:storeId/follower
     */
    @ResponseBody
    @GetMapping("/{storeId}/follower")
    public BaseResponse<List<GetStoreFollowerRes>> getFollower(@PathVariable int storeId) {
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(storeId != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<GetStoreFollowerRes> getStoreFollowerRes = storeProvider.getStoreFollower(storeId);
            return new BaseResponse<>(getStoreFollowerRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * 마이페이지 목록 조회 API
     * [GET] /bungae/stores/:storeId/mypage
     */
    @ResponseBody
    @GetMapping("/{storeId}/mypage")
    public BaseResponse<GetStoreCountRes> getCount(@PathVariable int storeId) {
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(storeId != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            GetStoreCountRes getStoreCountRes = storeProvider.getStoreCount(storeId);
            return new BaseResponse<>(getStoreCountRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }



//    /**
//
//
//
//
//    /**
//     * 회원 1명 조회 API
//     * [GET] /users/:userIdx
//     */
//    // Path-variable
//    @ResponseBody
//    @GetMapping("/{userIdx}") // (GET) 127.0.0.1:9000/app/users/:userIdx
//    public BaseResponse<GetUserRes> getUser(@PathVariable("userIdx") int userIdx) {
//        // @PathVariable RESTful(URL)에서 명시된 파라미터({})를 받는 어노테이션, 이 경우 userId값을 받아옴.
//        //  null값 or 공백값이 들어가는 경우는 적용하지 말 것
//        //  .(dot)이 포함된 경우, .을 포함한 그 뒤가 잘려서 들어감
//        // Get Users
//        try {
//            GetUserRes getUserRes = userProvider.getUser(userIdx);
//            return new BaseResponse<>(getUserRes);
//        } catch (BaseException exception) {
//            return new BaseResponse<>((exception.getStatus()));
//        }
//
//    }
//
//
}
