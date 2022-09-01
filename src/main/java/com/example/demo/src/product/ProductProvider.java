package com.example.demo.src.product;

import com.example.demo.config.BaseException;
import com.example.demo.src.product.model.*;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional(rollbackFor = BaseException.class)
    public GetProductRes getProductDetailInfo(int productId) throws BaseException {
        ProductDetailInfoModel productInfoModel;
        try {
            productInfoModel = productDao.getProductDetailInfo(productId);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new BaseException(INVALID_PRODUCT_ID); // INVALID_PRODUCT_ID|3301|존재하지 않는 상품입니다.
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
        if (productInfoModel.getDeliveryFee() != null)
            result.setDeliveryFee(productInfoModel.getDeliveryFee().equals("true"));
        else
            result.setDeliveryFee(false);
        if (productInfoModel.getChange() != null)
            result.setChange(productInfoModel.getChange().equals("true"));


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
        } catch (Exception e){
            logger.error(e.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 상품 상세정보 조회 - 판매자 정보(프로필 정보)
     */
    @Transactional(rollbackFor = BaseException.class)
    public List<GetProductStoreRes> getProductStore(int productId) throws BaseException {
        try {
            List<GetProductStoreRes> getProductStoreRes = productDao.getProductStore(productId);
            return getProductStoreRes;
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 상품 상세정보 조회 - 판매자 정보(상품, 후기, 팔로잉, 팔로워 갯수 정보)
     */
    @Transactional(rollbackFor = BaseException.class)
    public GetProductStoreCountRes getProductStoreCount(int productId) throws BaseException {
        try {
            int productCount = utils.getProductCount(productId);
            int reviewCount = utils.getReviewCountByStoreId(productId);
            int followerCount = utils.getFollowByStoreId(productId);
            int followingCount = utils.getFollowingByStoreId(productId);

            return new GetProductStoreCountRes(productCount, reviewCount, followerCount, followingCount);
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 상품 상세정보 조회 - 판매자 정보(상품 정보)
     */
    @Transactional(rollbackFor = BaseException.class)
    public List<GetProductStoreProductRes> getProductStoreProduct(int productId) throws BaseException {
        try {
            List<GetProductStoreProductRes> getProductStoreProductRes = productDao.getProductStoreProduct(productId);
            return getProductStoreProductRes;
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    /**
     * 상품 상세정보 조회 - 판매자 정보(최근 리뷰 정보)
     */
    @Transactional(rollbackFor = BaseException.class)
    public List<GetProductStoreReviewRes> getProductStoreReview(int productId) throws BaseException {
        try {
            List<GetProductStoreReviewRes> getProductStoreReviewRes = productDao.getProductStoreReview(productId);
            return getProductStoreReviewRes;
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 해당 상품 - 관련상품 정보 조회
     */
    @Transactional(rollbackFor = BaseException.class)
    public List<GetProductRelatedRes> getProductRelated(int productId) throws BaseException {
        try {
            List<GetProductRelatedRes> getProductRelatedRes = productDao.getProductRelated(productId);
            return getProductRelatedRes;
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


    /**
     * 상품 세부정보 조회
     * [GET] /bungae/product/registration
     */
    @Transactional(rollbackFor = BaseException.class)
     public GetProductRegistrationRes getProductRegiInfo (int uid,int productId) throws BaseException {
         ProductDetailInfoModel productInfoModel;
         try {
             productInfoModel = productDao.getProductDetailInfo(productId);
         } catch (Exception e) {
             logger.error(e.getMessage());
             throw new BaseException(INVALID_PRODUCT_ID); // INVALID_PRODUCT_ID|3301|존재하지 않는 상품입니다.
         }
         GetProductRegistrationRes result = new GetProductRegistrationRes(
                 productInfoModel.getProductId(),
                 productInfoModel.getStoreId(),
                 productInfoModel.getName(),
                 productInfoModel.getContent(),
                 null,
                 productInfoModel.getCategoryDepth1Id(),
                 productInfoModel.getCategoryDepth2Id(),
                 productInfoModel.getCategoryDepth3Id(),
                 null,
                 productInfoModel.getPrice(),
                 productInfoModel.getDeliveryFee().equals("true"),
                 productInfoModel.getQuantity(),
                 productInfoModel.getCondition(),
                 productInfoModel.getChange().equals("true"),
                 productInfoModel.getLocation()
         );

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
     }

    /**
     * 카테고리 항목 조회
     */
    @Transactional(rollbackFor = BaseException.class)
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
    @Transactional(rollbackFor = BaseException.class)
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
    @Transactional(rollbackFor = BaseException.class)
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

    /**
     * 해당 상품 팔로우 여부 확인
     *
     */
    @Transactional(rollbackFor = BaseException.class)
    public int checkFollow(int uid, int productId) throws BaseException {
        try {
            return productDao.checkFollow(uid, productId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 해당 상품 팔로우 취소 여부 확인
     */
    @Transactional(rollbackFor = BaseException.class)
    public int checkFollowFalse(int uid, int productId) throws BaseException {
        try {
            return productDao.checkFollowFalse(uid, productId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


    /**
     * 해당 상품이 찜하기 등록 여부 확인
     *
     */
    @Transactional(rollbackFor = BaseException.class)
    public int checkBasket(int uid, int productId) throws BaseException {
        try {
            return productDao.checkBasket(uid, productId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 해당 상품이 찜하기 취소 여부 확인
     */
    @Transactional(rollbackFor = BaseException.class)
    public int checkBasketFalse(int uid, int productId) throws BaseException {
        try {
            return productDao.checkBasketFalse(uid, productId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }




}
