package com.example.demo.src.product;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.product.model.*;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.Verifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@RestController // Rest API 또는 WebAPI를 개발하기 위한 어노테이션. @Controller + @ResponseBody 를 합친것.
// @Controller      [Presentation Layer에서 Contoller를 명시하기 위해 사용]
//  [Presentation Layer?] 클라이언트와 최초로 만나는 곳으로 데이터 입출력이 발생하는 곳
//  Web MVC 코드에 사용되는 어노테이션. @RequestMapping 어노테이션을 해당 어노테이션 밑에서만 사용할 수 있다.
// @ResponseBody    모든 method의 return object를 적절한 형태로 변환 후, HTTP Response Body에 담아 반환.
@RequestMapping("/bungae/product")
/**
 * Controller란?
 * 사용자의 Request를 전달받아 요청의 처리를 담당하는 Service, Prodiver 를 호출
 */
public class ProductController {

    final Logger logger = LoggerFactory.getLogger(this.getClass()); // Log를 남기기: 일단은 모르고 넘어가셔도 무방합니다.

    @Autowired
    private final ProductProvider productProvider;
    @Autowired
    private final ProductService productService;
    @Autowired
    private final JwtService jwtService;


    public ProductController(ProductProvider productProvider, ProductService productService, JwtService jwtService) {
        this.productProvider = productProvider;
        this.productService = productService;
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
     * 상품 상세정보 조회
     */
    @ResponseBody
    @GetMapping("/{productId}")
    public BaseResponse<GetProductRes> getProductInfo(
            @PathVariable("productId") Integer productId) {

        try {
            // jwt 에서 uid 추출
            int uid;
            uid = jwtService.getUserIdx();
            // 존재하는 상점 아이디인지 검증
            if (!verifier.isPresentStoreId(uid))
                throw new BaseException(INVALID_STORE_ID); // /3001/존재하지 않는 상점 id 입니다.

            //TODO : 조회수 추가하기


            return new BaseResponse<>(productProvider.getProductDetailInfo(productId));
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * 상품 상세정보 조회 - 판매자 정보(프로필)
     */
    @GetMapping("/{productId}/store")
    public BaseResponse<List<GetProductStoreRes>> getProductStore(
            @PathVariable("productId") Integer productId) {

        try {
            // jwt 에서 uid 추출
            int uid;
            uid = jwtService.getUserIdx();
            // 존재하는 상점 아이디인지 검증
            if (!verifier.isPresentStoreId(uid)){
                throw new BaseException(INVALID_STORE_ID); // /3001/존재하지 않는 상점 id 입니다.
            }
            List<GetProductStoreRes> getProductStoreRes = productProvider.getProductStore(productId);
            return new BaseResponse<>(getProductStoreRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * 상품 상세정보 조회 - 판매자 정보(상품, 후기, 팔로잉, 팔로워 갯수)
     */
    @GetMapping("/{productId}/store/count")
    public BaseResponse<GetProductStoreCountRes> getProductStoreCount(
            @PathVariable("productId") Integer productId) {

        try {
            // jwt 에서 uid 추출
            int uid;
            uid = jwtService.getUserIdx();
            // 존재하는 상점 아이디인지 검증
            if (!verifier.isPresentStoreId(uid)){
                throw new BaseException(INVALID_STORE_ID); // /3001/존재하지 않는 상점 id 입니다.
            }
            GetProductStoreCountRes getProductStoreCountRes = productProvider.getProductStoreCount(productId);
            return new BaseResponse<>(getProductStoreCountRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }



    /**
     * 상품 상세정보 조회 - 판매자 정보(상품)
     */
    @ResponseBody
    @GetMapping("/{productId}/store/product")
    public BaseResponse<List<GetProductStoreProductRes>> getProductStoreProduct(
            @PathVariable("productId") Integer productId) {

        try {
            // jwt 에서 uid 추출
            int uid;
            uid = jwtService.getUserIdx();
            // 존재하는 상점 아이디인지 검증
            if (!verifier.isPresentStoreId(uid))
                throw new BaseException(INVALID_STORE_ID); // /3001/존재하지 않는 상점 id 입니다.

            //TODO
            List<GetProductStoreProductRes> getProductStoreProductRes = productProvider.getProductStoreProduct(productId);
            return new BaseResponse<>(getProductStoreProductRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * 상품 상세정보 조회 - 판매자 정보(최근 리뷰 정보)
     */
    @GetMapping("/{productId}/store/review")
    public BaseResponse<List<GetProductStoreReviewRes>> getProductReview(
            @PathVariable("productId") Integer productId) {

        try {
            // jwt 에서 uid 추출
            int uid;
            uid = jwtService.getUserIdx();
            // 존재하는 상점 아이디인지 검증
            if (!verifier.isPresentStoreId(uid)){
                throw new BaseException(INVALID_STORE_ID); // /3001/존재하지 않는 상점 id 입니다.
            }
            List<GetProductStoreReviewRes> getProductStoreReviewRes = productProvider.getProductStoreReview(productId);
            return new BaseResponse<>(getProductStoreReviewRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * 해당상품 - 판매자 정보(판매자 팔로우 하기)
     */
    @PostMapping("/{productId}/store/follow")
    public BaseResponse<PostProductFollowRes> postProductFollow(
            @PathVariable("productId") Integer productId) {
        try {
            // jwt 에서 uid 추출
            int uid;
            uid = jwtService.getUserIdx();
            // 존재하는 상점 아이디인지 검증
            if (!verifier.isPresentStoreId(uid)){
                throw new BaseException(INVALID_STORE_ID); // /3001/존재하지 않는 상점 id 입니다.
            }
            PostProductFollowRes postProductFollowRes = productService.postProductFollow(uid, productId);
            return new BaseResponse<>(postProductFollowRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * 해당상품 - 판매자 정보(판매자 팔로우 취소하기)
     */
    @PatchMapping("/{productId}/store/follow")
    public BaseResponse<PatchProductFollowRes> patchProductFollow(
            @PathVariable("productId") Integer productId) {
        try {
            // jwt 에서 uid 추출
            int uid;
            uid = jwtService.getUserIdx();
            // 존재하는 상점 아이디인지 검증
            if (!verifier.isPresentStoreId(uid)){
                throw new BaseException(INVALID_STORE_ID); // /3001/존재하지 않는 상점 id 입니다.
            }
            PatchProductFollowRes patchProductFollowRes = productService.patchProductFollow(uid, productId);
            return new BaseResponse<>(patchProductFollowRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * 해당 상품 찜하기
     */
    @PostMapping("/{productId}/basket")
    public BaseResponse<PostProductBasketRes> getProductBasket(
            @PathVariable("productId") Integer productId) {

        try {
            // jwt 에서 uid 추출
            int uid;
            uid = jwtService.getUserIdx();
            // 존재하는 상점 아이디인지 검증
            if (!verifier.isPresentStoreId(uid)){
                throw new BaseException(INVALID_STORE_ID); // /3001/존재하지 않는 상점 id 입니다.
            }
            PostProductBasketRes postProductBasketRes = productService.postProductBasket(uid, productId);
            return new BaseResponse<>(postProductBasketRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * 해당 상품 찜하기 취소
     */
    @PatchMapping("/{productId}/basket")
    public BaseResponse<PatchProductBasketRes> patchProductBasket(
            @PathVariable("productId") Integer productId) {
        try {
            // jwt 에서 uid 추출
            int uid;
            uid = jwtService.getUserIdx();
            // 존재하는 상점 아이디인지 검증
            if (!verifier.isPresentStoreId(uid)){
                throw new BaseException(INVALID_STORE_ID); // /3001/존재하지 않는 상점 id 입니다.
            }
            PatchProductBasketRes patchProductBasketRes = productService.patchProductBasket(uid, productId);
            return new BaseResponse<>(patchProductBasketRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }




    /**
     * 상품 등록
     * [POST] /bungae/product//registration
     */
    @ResponseBody
    @PostMapping("/registration")
    public BaseResponse<PostNewProductRes> postNewProduct(
            @RequestBody PostNewProductReq newProduct) {

        try {
            // jwt 에서 uid 추출
            int uid;
            uid = jwtService.getUserIdx();
            // 존재하는 상점 아이디인지 검증
            if (!verifier.isPresentStoreId(uid))
                throw new BaseException(INVALID_STORE_ID); // /3001/존재하지 않는 상점 id 입니다.

            //TODO

            return new BaseResponse<>(productService.postNewProduct(uid, newProduct));
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 상품 세부정보 조회
     * [GET] /bungae/product/registration?productId=
     */
    @ResponseBody
    @GetMapping("/registration")
    public BaseResponse<GetProductRegistrationRes> getProductRegiInfo(
            @RequestParam Integer productId) {
        try {
            // jwt 에서 uid 추출
            int uid;
            uid = jwtService.getUserIdx();
            // 존재하는 상점 아이디인지 검증
            if (!verifier.isPresentStoreId(uid))
                throw new BaseException(INVALID_STORE_ID); // /3001/존재하지 않는 상점 id 입니다.

            // 내 상품인지 확인
            if (!verifier.isPresentProductId(productId))
                throw new BaseException(INVALID_PRODUCT_ID); // INVALID_PRODUCT_ID|3301|존재하지 않는 상품입니다.
            if (!verifier.isUsersProductId(uid, productId))
                throw new BaseException(USER_NOT_PERMITTED); // USER_NOT_PERMITTED|3302|해당 사용자가 접근할 수 없는 상품입니다.

            return new BaseResponse<>(productProvider.getProductRegiInfo(uid, productId));
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 상품 정보 수정
     * [PATCH] /bungae/product/registration
     */
    @ResponseBody
    @PatchMapping("/registration")
    public BaseResponse<PostNewProductRes> patchNewProduct(
            @RequestBody PostNewProductReq newProduct,
            @RequestParam Integer productId) {
        try {
            // jwt 에서 uid 추출
            int uid;
            uid = jwtService.getUserIdx();
            // 존재하는 상점 아이디인지 검증
            if (!verifier.isPresentStoreId(uid))
                throw new BaseException(INVALID_STORE_ID); // /3001/존재하지 않는 상점 id 입니다.

            // 내 상품인지 확인
            if (!verifier.isPresentProductId(productId))
                throw new BaseException(INVALID_PRODUCT_ID); // INVALID_PRODUCT_ID|3301|존재하지 않는 상품입니다.
            if (!verifier.isUsersProductId(uid, productId))
                throw new BaseException(USER_NOT_PERMITTED); // USER_NOT_PERMITTED|3302|해당 사용자가 접근할 수 없는 상품입니다.

            return new BaseResponse<>(productService.patchNewProduct(uid, productId, newProduct));
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 상품 삭제
     * [PATCH] /bungae/product/registration/d
     */
    @ResponseBody
    @PatchMapping("/registration/d")
    public BaseResponse<PatchDeleteRes> patchDeleteProduct(
            @RequestParam Integer productId,
            @RequestParam(required = false) Integer kill) {
        try {
            // jwt 에서 uid 추출
            int uid;
            uid = jwtService.getUserIdx();
            // 존재하는 상점 아이디인지 검증
            if (!verifier.isPresentStoreId(uid))
                throw new BaseException(INVALID_STORE_ID); // /3001/존재하지 않는 상점 id 입니다.

            if (kill == null || kill != 9 ) {
                kill = 0;
                // 내 상품인지 확인
                if (!verifier.isPresentProductId(productId))
                    throw new BaseException(INVALID_PRODUCT_ID); // INVALID_PRODUCT_ID|3301|존재하지 않는 상품입니다.
                if (!verifier.isUsersProductId(uid, productId))
                    throw new BaseException(USER_NOT_PERMITTED); // USER_NOT_PERMITTED|3302|해당 사용자가 접근할 수 없는 상품입니다.
            }

            return new BaseResponse<>(productService.patchDeleteProduct(uid, productId, kill));
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }



    /**
     * 카테고리 항목 조회
     * [GET] /bungae/product/category
     */
    @ResponseBody
    @GetMapping("/category")
    public BaseResponse<List<GetCategoryDepth01Res>> getCategoryDepth01() {
        try {
            return new BaseResponse<>(productProvider.getCategoryDepth01());
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 카테고리 항목 조회 - 세부 카테고리 1
     * [GET] /bungae/product/category/:depth1Id
     */
    //  |3331|더이상 데이터가 존재하지 않는 카테고리 id 입니다.
    @ResponseBody
    @GetMapping("/category/{depth1Id}")
    public BaseResponse<List<GetCategoryDepth02Res>> getCategoryDepth02(
            @PathVariable("depth1Id") Integer depth1Id) {
        try {
            return new BaseResponse<>(productProvider.getCategoryDepth02(depth1Id));
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 카테고리 항목 조회 - 세부 카테고리 2
     * [GET] /bungae/product/category/:depth1Id/:depth2Id
     */
    @ResponseBody
    @GetMapping("/category/{depth1Id}/{depth2Id}")
    public BaseResponse<List<GetCategoryDepth03Res>> getCategoryDepth03(
            @PathVariable("depth1Id") Integer depth1Id,
            @PathVariable("depth2Id") Integer depth2Id) {
        try {
            return new BaseResponse<>(productProvider.getCategoryDepth03(depth1Id, depth2Id));
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


}
