package com.example.demo.src.feed;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.chat.ChatProvider;
import com.example.demo.src.chat.ChatService;
import com.example.demo.src.feed.model.GetFeedRes;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.Verifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.INVALID_STORE_ID;

@RestController // Rest API 또는 WebAPI를 개발하기 위한 어노테이션. @Controller + @ResponseBody 를 합친것.
                // @Controller      [Presentation Layer에서 Contoller를 명시하기 위해 사용]
                //  [Presentation Layer?] 클라이언트와 최초로 만나는 곳으로 데이터 입출력이 발생하는 곳
                //  Web MVC 코드에 사용되는 어노테이션. @RequestMapping 어노테이션을 해당 어노테이션 밑에서만 사용할 수 있다.
                // @ResponseBody    모든 method의 return object를 적절한 형태로 변환 후, HTTP Response Body에 담아 반환.
@RequestMapping("/bungae/feed")
/**
 * Controller란?
 * 사용자의 Request를 전달받아 요청의 처리를 담당하는 Service, Prodiver 를 호출
 */
public class FeedController {

    final Logger logger = LoggerFactory.getLogger(this.getClass()); // Log를 남기기: 일단은 모르고 넘어가셔도 무방합니다.

    @Autowired
    private final FeedProvider feedProvider;
    @Autowired
    private final FeedService feedService;
    @Autowired
    private final JwtService jwtService;


    public FeedController(FeedProvider feedProvider, FeedService feedService, JwtService jwtService) {
        this.feedProvider = feedProvider;
        this.feedService = feedService;
        this.jwtService = jwtService;
    }
    // ******************************************************************************

    // 검증코드 클래스 추가
    private Verifier verifier;

    @Autowired
    public void setVerifier(Verifier verifier) {
        this.verifier = verifier;
    }
    // ******************************************************************************

    /**
     * 홈화면 피드
     * [GET] /bungae/feed/
     */
    public BaseResponse<List<GetFeedRes>> recommendFeedByUser(@RequestParam(required = false) Integer p) {
        try {
            int uid = jwtService.getUserIdx();
            // uid 검증
            if (!verifier.isPresentStoreId(uid))
                throw new BaseException(INVALID_STORE_ID); // /3001/존재하지 않는 상점 id 입니다.

            if (p == null) p=1;

            // 나의 조회, 좋아요, 구매, 팔로우 와 관련된 모든 상품 조회 -> pid 모아오기
            // 중복제거
            // 정보 가져오기

            return new BaseResponse<>();
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 상품 검색
     * [GET] /bungae/feed/keyword ? q=&order=&brand=&c1=&c2=&c3=&onlySale=&min=&max=&p=
     */
    @ResponseBody
    @GetMapping("/keyword")
    public BaseResponse<List<GetFeedRes>> FeedByKeyword(
            @RequestParam(required = true) String q,
            @RequestParam(required = false) String order,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) Integer c1,
            @RequestParam(required = false) Integer c2,
            @RequestParam(required = false) Integer c3,
            @RequestParam(required = false) String onlySale,
            @RequestParam(required = false) Integer min,
            @RequestParam(required = false) Integer max,
            @RequestParam(required = false) Integer p){
        try {
            int uid = jwtService.getUserIdx();
            // uid 검증
            if (!verifier.isPresentStoreId(uid))
                throw new BaseException(INVALID_STORE_ID); // /3001/존재하지 않는 상점 id 입니다.

            if (p == null) p=1;

            return new BaseResponse<>(feedProvider.getFeedRes(uid,q,order,brand,c1,c2,c3,onlySale,min,max,p));
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * 카테고리 검색
     * [GET] /bungae/feed/category/:depth1Id/:depth2Id/:depth3Id? q=&order=&brand=&c1=&c2=&c3=&onlySale=&min=&max=&p=
     */
    @ResponseBody
    @GetMapping("/category/{depth1Id}")
    public BaseResponse<List<GetFeedRes>> FeedByCategory(
            @PathVariable("depth1Id") Integer depth1Id,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String order,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String onlySale,
            @RequestParam(required = false) Integer min,
            @RequestParam(required = false) Integer max,
            @RequestParam(required = false) Integer p){
        try {
            int uid = jwtService.getUserIdx();
            // uid 검증
            if (!verifier.isPresentStoreId(uid))
                throw new BaseException(INVALID_STORE_ID); // /3001/존재하지 않는 상점 id 입니다.

            if (p == null) p=1;

            return new BaseResponse<>(feedProvider.getFeedRes(uid,q,order,brand,depth1Id,null,null,onlySale,min,max,p));
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * 카테고리 검색 - depth2
     * [GET] /bungae/feed/category/:depth1Id/:depth2Id? q=&order=&brand=&c1=&c2=&c3=&onlySale=&min=&max=&p=
     */
    @ResponseBody
    @GetMapping("/category/{depth1Id}/{depth2Id}")
    public BaseResponse<List<GetFeedRes>> FeedByCategory(
            @PathVariable("depth1Id") Integer depth1Id,
            @PathVariable("depth2Id") Integer depth2Id,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String order,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String onlySale,
            @RequestParam(required = false) Integer min,
            @RequestParam(required = false) Integer max,
            @RequestParam(required = false) Integer p){
        try {
            int uid = jwtService.getUserIdx();
            // uid 검증
            if (!verifier.isPresentStoreId(uid))
                throw new BaseException(INVALID_STORE_ID); // /3001/존재하지 않는 상점 id 입니다.

            if (p == null) p=1;

            return new BaseResponse<>(feedProvider.getFeedRes(uid,q,order,brand,depth1Id,depth2Id,null,onlySale,min,max,p));
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * 카테고리 검색 - depth3
     * [GET] /bungae/feed/category/:depth1Id/:depth2Id/:depth3Id? q=&order=&brand=&c1=&c2=&c3=&onlySale=&min=&max=&p=
     */
    @ResponseBody
    @GetMapping("/category/{depth1Id}/{depth2Id}/{depth3Id}")
    public BaseResponse<List<GetFeedRes>> FeedByCategory(
            @PathVariable("depth1Id") Integer depth1Id,
            @PathVariable("depth2Id") Integer depth2Id,
            @PathVariable("depth3Id") Integer depth3Id,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String order,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String onlySale,
            @RequestParam(required = false) Integer min,
            @RequestParam(required = false) Integer max,
            @RequestParam(required = false) Integer p){
        try {
            int uid = jwtService.getUserIdx();
            // uid 검증
            if (!verifier.isPresentStoreId(uid))
                throw new BaseException(INVALID_STORE_ID); // /3001/존재하지 않는 상점 id 입니다.

            if (p == null) p=1;

            return new BaseResponse<>(feedProvider.getFeedRes(uid,q,order,brand,depth1Id,depth2Id,depth3Id,onlySale,min,max,p));
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

}
