package com.example.demo.src.product;

import com.example.demo.config.BaseException;
import com.example.demo.src.product.model.*;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

//Provider : Read의 비즈니스 로직 처리
@Service    // [Business Layer에서 Service를 명시하기 위해서 사용] 비즈니스 로직이나 respository layer 호출하는 함수에 사용된다.
            // [Business Layer]는 컨트롤러와 데이터 베이스를 연결
/**
 * Provider란?
 * Controller에 의해 호출되어 실제 비즈니스 로직과 트랜잭션을 처리: Read의 비즈니스 로직 처리
 * 요청한 작업을 처리하는 관정을 하나의 작업으로 묶음
 * dao를 호출하여 DB CRUD를 처리 후 Controller로 반환
 */
public class ProductProvider {


    // *********************** 동작에 있어 필요한 요소들을 불러옵니다. *************************
    private final ProductDao productDao;
    private final JwtService jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired //readme 참고
    public ProductProvider(ProductDao productDao, JwtService jwtService) {
        this.productDao = productDao;
        this.jwtService = jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!
    }
    // ******************************************************************************
    private Utils utils;
    @Autowired
    public void setUtils(Utils utils) {
        this.utils = utils;
    }
    // ******************************************************************************

    /**
     * 상품 상세정보 조회 - 상품정보
     */
    public GetProductRes getProductDetailInfo(int productId) throws BaseException {
        ProductDetailInfoModel productInfoModel;
        try {
            productInfoModel = productDao.getProductDetailInfo(productId);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new BaseException(INVALID_PRODUCT_ID); // 3xxx|존재하지 않는 상품입니다.
        }

        GetProductRes result = new GetProductRes();
        result.setProductId(productInfoModel.getProductId());
        result.setStoreId(productInfoModel.getStoreId());
        result.setName(productInfoModel.getName());
        result.setDealStatus(productInfoModel.getDealStatus());
        result.setPrice(productInfoModel.getPrice());
        if (productInfoModel.getLocation() != null)
            result.setLocation(productInfoModel.getLocation());
        result.setUploaded(productInfoModel.getUploaded());
        result.setUploadedEasyText(productInfoModel.getUploadedEasyText());
        result.setCondition(productInfoModel.getCondition());
        result.setQuantity(productInfoModel.getQuantity());
        if (productInfoModel.getDeliveryFee().equals("true"))
            result.setDeliveryFee(true);
        else
            result.setDeliveryFee(false);
        if (productInfoModel.getChange().equals("true"))
            result.setChange(true);
        else
            result.setChange(false);
        result.setContent(productInfoModel.getContent());
        result.setCategoryDepth1Id(productInfoModel.getCategoryDepth1Id());
        result.setCategoryDepth2Id(productInfoModel.getCategoryDepth2Id());
        result.setCategoryDepth3Id(productInfoModel.getCategoryDepth3Id());

        try {
            // categoryText
            String categoryText = this.getCategoryInfoDepth01(productInfoModel.getCategoryDepth1Id()).getName();
            if (productInfoModel.getCategoryDepth2Id() != null && productInfoModel.getCategoryDepth2Id() != 0) {
                categoryText += " > ";
                categoryText += this.getCategoryInfoDepth02(productInfoModel.getCategoryDepth2Id()).getName();
            }
            if (productInfoModel.getCategoryDepth3Id() != null && productInfoModel.getCategoryDepth3Id() != 0) {
                categoryText += " > ";
                categoryText += this.getCategoryInfoDepth03(productInfoModel.getCategoryDepth3Id()).getName();
            }
            result.setCategoryText(categoryText);

            // views
            result.setViews(utils.getViewCount(productId));
            // dibs
            result.setDibs(utils.getBasketCountByProductId(productId));
            // talks // TODO : talks 수 세기!!
            result.setTalks(0);
            // imagUrls
            List<String> imageUrls =  productDao.getImageUrls(productId);
            for(int i=0; i<imageUrls.size(); i++){
                if (imageUrls.get(i) == null)
                    imageUrls.remove(i--);
            }
            result.setImageUrls(imageUrls);
            // tags
            result.setTags(productDao.getTags(productId));

            return result;
        } catch (BaseException e) {
            throw e;
        }
    }


    /**
     * 카테고리 항목 조회
     */
    public List<GetCategoryDepth01Res> getCategoryDepth01 () throws BaseException {
        try {
            // 카테고리 리스트 조회
            List<GetCategoryDepth01Res> getCategoryDepth01ResList = productDao.getCategoryDepth01();

            for (GetCategoryDepth01Res category : getCategoryDepth01ResList ){
                // 세부항목이 있는지 조회
                if( productDao.getCategoryDepth02(category.getDepth1Id())
                        .size() != 0)
                    category.setHasMoreDepth(true); // 있으면 hasMoreDepth = true;
            }
            return getCategoryDepth01ResList;
        } catch (Exception e){
            logger.error(e.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }
    /**
     * 카테고리 항목 조회 - 세부 카테고리 1
     */
    public List<GetCategoryDepth02Res> getCategoryDepth02 (int depth1Id) throws BaseException {
        try {
            List<GetCategoryDepth02Res> getCategoryDepth02ResList = productDao.getCategoryDepth02(depth1Id);

            // (Validation) 데이터가 없다면 에러 반환
            if (getCategoryDepth02ResList.size() == 0)
                throw new BaseException(NOT_EXIST_CATEGORY_ID);

            for (GetCategoryDepth02Res category : getCategoryDepth02ResList) {
                // 세부항목이 있는지 조회
                if( productDao.getCategoryDepth03(category.getDepth2Id())
                        .size() != 0)
                    category.setHasMoreDepth(true);
            }
            return getCategoryDepth02ResList;
        } catch (BaseException e){
            throw e;
        } catch (Exception e){
            logger.error(e.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }
    /**
     * 카테고리 항목 조회 - 세부 카테고리 2
     */
    public List<GetCategoryDepth03Res> getCategoryDepth03 (int depth1Id, int depth2Id) throws BaseException {
        try {
            // (Validation) 카테고리 아이디 d1 d2 가 일치하는지 확인
            if (!isMatchCategory1and2(depth1Id, depth2Id))
                throw new BaseException(NOT_MATCH_CATEGORY_12_ID);

            List<GetCategoryDepth03Res>  getCategoryDepth03ResList =  productDao.getCategoryDepth03(depth2Id);

            // (Validation) 데이터가 없다면 에러 반환
            if (getCategoryDepth03ResList.size() == 0)
                throw new BaseException(NOT_EXIST_CATEGORY_ID);

            return getCategoryDepth03ResList;
        } catch (BaseException e){
            throw e;
        } catch (Exception e){
            logger.error(e.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 카테고리01 정보 확인
     */
    public GetCategoryDepth01Res getCategoryInfoDepth01 (int depth1Id) {
            return productDao.getCategoryInfoDepth01(depth1Id);
    }

    /**
     * 카테고리02 정보 확인
     */
    public GetCategoryDepth02Res getCategoryInfoDepth02 (int depth1Id) {
            return productDao.getCategoryInfoDepth02(depth1Id);
    }


    /**
     * 카테고리03 정보 확인
     */
    public GetCategoryDepth03Res getCategoryInfoDepth03 (int depth3Id) {
            return productDao.getCategoryInfoDepth03(depth3Id);
    }

    /**
     * (validation) 카테고리 아이디 d1 d2 가 일치하는지 확인
     */
    public boolean isMatchCategory1and2(int depth1Id, int depth2Id) {
        try {
            productDao.getMatchCategory1and2(depth1Id, depth2Id);
            return true;
        } catch (Exception e){
            return false;
        }
    }

    /**
     * (validation) 카테고리 아이디 d1 d2 가 일치하는지 확인
     */
    public boolean isMatchCategory2and3(int depth2Id, int depth3Id) {
        try {
            productDao.getMatchCategory2and3(depth2Id, depth3Id);
            return true;
        } catch (Exception e){
            return false;
        }
    }
}
