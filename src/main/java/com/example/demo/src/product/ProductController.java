package com.example.demo.src.product;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.product.model.*;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.Utils;
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
     * 상품 등록
     * [POST] /bungae/product/
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

//    /**
//     * 상품 세부정보 조회
//     * [POST] /bungae/product/
//     */
//    @ResponseBody
//    @GetMapping("/registration")
//    /**

//     * 상품 정보 수정
//     * [POST] /bungae/product/
//     */
//    @ResponseBody
//    @PatchMapping("/registration")

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
