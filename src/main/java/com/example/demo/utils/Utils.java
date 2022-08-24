package com.example.demo.utils;

import com.example.demo.config.BaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class Utils {
    final Logger logger = LoggerFactory.getLogger(this.getClass()); // Log 처리부분: Log를 기록하기 위해 필요한 함수입니다.

    // *********************** 동작에 있어 필요한 요소들을 불러옵니다. *************************
    private final UtilsDao utilsDao;

//    private final JwtService jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!


    @Autowired //readme 참고
    public Utils(UtilsDao utilsDao) {
        this.utilsDao = utilsDao;
    }
    // ******************************************************************************

    /**
     * 제품 조회수 조회
     */
    public int getViewCount (int productId) throws BaseException {
        try {
            return utilsDao.getViewCount(productId);
        } catch (Exception e){
            logger.error(e.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 제품 찜수 조회
     */
    public int getBasketCountByProductId (int productId) throws BaseException {
        try {
            return utilsDao.getBasketCountByProductId(productId);
        } catch (Exception e){
            logger.error(e.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 제품 리뷰수 조회
     */
    public int getReviewCountByProductId (int productId) throws BaseException {
        try {
            return utilsDao.getReviewCountByProductId(productId);
        } catch (Exception e){
            logger.error(e.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 상점 리뷰수 조회
     */
    public int getReviewCountByStoreId (int storeId) throws BaseException {
        try {
            return utilsDao.getReviewCountByStoreId(storeId);
        } catch (Exception e){
            logger.error(e.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 상점 리뷰총점 조회
     */
    public double getTotalReviewAvgByStoreId (int storeId) throws BaseException {
        try {
            return utilsDao.getTotalReviewAvgByStoreId(storeId);
        } catch (Exception e){
            logger.error(e.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 상점 팔로잉 수 조회
     */
    public int getFollowingByStoreId (int storeId) throws BaseException {
        try {
            return utilsDao.getFollowingByStoreId(storeId);
        } catch (Exception e){
            logger.error(e.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 상점 팔로우 수 조회
     */
    public int getFollowByStoreId (int storeId) throws BaseException {
        try {
            return utilsDao.getFollowByStoreId(storeId);
        } catch (Exception e){
            logger.error(e.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
