package com.example.demo.utils;

import com.example.demo.config.BaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class Verifier {
    final Logger logger = LoggerFactory.getLogger(this.getClass()); // Log 처리부분: Log를 기록하기 위해 필요한 함수입니다.

    // *********************** 동작에 있어 필요한 요소들을 불러옵니다. *************************
    private final VerifierDao verifierDao;

//    private final JwtService jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!


    @Autowired //readme 참고
    public Verifier(VerifierDao verifierDao) {
        this.verifierDao = verifierDao;
    }
    // ******************************************************************************


    // 해당 이메일이 이미 User Table에 존재하는지 확인
    public int checkPhone(String phone) throws BaseException {
        try {
            return verifierDao.checkPhone(phone);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
//    public int checkName(String name) throws BaseException {
//        try {
//            return verifierDao.checkName(name);
//        } catch (Exception exception) {
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }

    /**
     * 상점,유저 Store.id 검증
     */
    public boolean isPresentStoreId(int uid) {
        try {
            verifierDao.isPresentStoreId(uid);
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    /**
     * 존재하는 상품인지 검증
     */
    public boolean isPresentProductId (int productId) {
        try {
            verifierDao.isPresentProductId(productId);
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
    }
    /**
     * 상품이 해당 스토어의 상품인지 검증
     */
    public boolean isUsersProductId(int uid, int productId){
        try{
            verifierDao.isUsersProductId(uid, productId);
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
    }

//    /**
//     * 채널 cid 검증
//     */
//    public boolean verifyChannelId (long cid){
//        try {
//            verifierDao.verifyChannelId(cid);
//            return true;
//        } catch (Exception e) {
//            logger.error(e.getMessage());
//            return false;
//        }
//    }
//    /**
//     * 영상 vid 검증
//     */
//    public boolean verifyVideoId (long vid) {
//        try {
//            verifierDao.verifyVideoId(vid);
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }
//    /**
//     * 영상 viewId 검증
//     */
//    public boolean verifyViewId (long viewId) {
//        try {
//            verifierDao.verifyViewId(viewId);
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }
//    /**
//     * LanguageId 검증
//     */
//    public boolean verifyLanguageId (int languageId) {
//        try {
//            verifierDao.verifyLanguageId(languageId);
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//    /**
//     * 사용자가 접속 가능한 영상인지 확인
//     */
//    public boolean authorizeVideo (long vid, long uid) {
//        try {
//            verifierDao.authorizeVideo(vid,uid);
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }
}
