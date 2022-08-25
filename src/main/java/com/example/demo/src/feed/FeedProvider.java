package com.example.demo.src.feed;

import com.example.demo.config.BaseException;
import com.example.demo.src.chat.ChatDao;
import com.example.demo.src.feed.model.GetFeedRes;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

//Provider : Read의 비즈니스 로직 처리
@Service    // [Business Layer에서 Service를 명시하기 위해서 사용] 비즈니스 로직이나 respository layer 호출하는 함수에 사용된다.
            // [Business Layer]는 컨트롤러와 데이터 베이스를 연결
/**
 * Provider란?
 * Controller에 의해 호출되어 실제 비즈니스 로직과 트랜잭션을 처리: Read의 비즈니스 로직 처리
 * 요청한 작업을 처리하는 관정을 하나의 작업으로 묶음
 * dao를 호출하여 DB CRUD를 처리 후 Controller로 반환
 */
public class FeedProvider {


    // *********************** 동작에 있어 필요한 요소들을 불러옵니다. *************************
    private final FeedDao feedDao;
    private final JwtService jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired //readme 참고
    public FeedProvider(FeedDao feedDao, JwtService jwtService) {
        this.feedDao = feedDao;
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
     * 상품 검색
     */
    public List<GetFeedRes> FeedByKeyword(int uid, String k, int p) throws BaseException {
        try {
            List<GetFeedRes> result = feedDao.FeedByKeyword(k, p);

            for (GetFeedRes elem : result) {

                // dibs
                elem.setDibs(
                        utils.getBasketCountByProductId(
                                elem.getProductId()
                        )
                );

                // 사용자 찜 여부
                elem.setUserDibed(this.isBasketByUid(uid, elem.getProductId()));

            }

            return result;
        } catch (BaseException e){
            throw e;
        }
    }




    /**
     *  사용자 찜 여부 조회
     */
    public boolean isBasketByUid (int uid, int productId){
        try {
            feedDao.isBasketByUid(uid, productId);
            return true;
        } catch (Exception e){
            return false;
        }
    }
}
