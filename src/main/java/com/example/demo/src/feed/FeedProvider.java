package com.example.demo.src.feed;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.feed.model.GetFeedRes;
import com.example.demo.src.product.model.GetCategoryDepth01Res;
import com.example.demo.src.product.model.GetCategoryDepth02Res;
import com.example.demo.src.product.model.GetCategoryDepth03Res;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

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
            List<GetFeedRes> result = feedDao.FeedByKeywordOrderByDate(k, p);

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
     * 상품 검색 (Detail Ver.)
     */
    public List<GetFeedRes> getFeedRes (int uid,
                                        String q,
                                        String order,
                                        String brand,
                                        Integer c1,
                                        Integer c2,
                                        Integer c3,
                                        String onlySale,
                                        Integer min,
                                        Integer max,
                                        int p) throws BaseException {
        try {
            String orderQuery;
            String whereQuery = "";

            if (order == null || order.equals("recent"))
                orderQuery = " Order By createdAt DESC ";
            else if (order.equals("cheep"))
                orderQuery = " Order By price ";
            else if (order.equals("expensive"))
                orderQuery = " Order By price DESC ";
            else
                throw new BaseException(INVALID_FEED_ORDER); // |2xxxx| 올바르지 않은 order 입력입니다.

            // 키워드 검색
            if (q != null) {
                String[] keywords = q.split(" ");
                for (int i = 0; i < keywords.length; i++) {
                    if (i==0)
                        whereQuery += " AND ( title LIKE '%" + keywords[i]
                                + "%' OR content LIKE '%"  + keywords[i] + "%' ";
                    else
                        whereQuery += " OR title LIKE '%" + keywords[i]
                                + "%' OR content LIKE '%"  + keywords[i] + "%' ";
                }
                whereQuery += ") ";
            }

            // 카테고리 검색
            if (c1 == null)
                ;
            else if (c2 == null) {
                // c1 체크
                if (getCategoryInfoDepth01(c1).getDepth1Id() == 0)
                    throw new BaseException(INVALID_CATEGORYD1ID); // |3XXX|잘못된 categoryDepth1Id 입니다.
                // validation 통과
                whereQuery += " AND categoryDepth1Id=" + c1;
            } else if (c3 == null) {
                // c1 체크
                if (getCategoryInfoDepth01(c1).getDepth1Id() == 0)
                    throw new BaseException(INVALID_CATEGORYD1ID); // |3XXX|잘못된 categoryDepth1Id 입니다.
                // c2 체크
                if (getCategoryInfoDepth02(c2).getDepth2Id() == 0)
                    throw new BaseException(EMPTY_CATEGORYD2ID); // |2XXX|categoryDepth2Id을 입력해주세요.
                // c1,c2 체크
                if (!isMatchCategory1and2(c1,c2))
                    throw new BaseException(NOT_MATCH_CATEGORY_12_ID); // NOT_MATCH_CATEGORY_ID|3330|연관되지 않은 depth1Id와 depth2Id입니다.
                // validation 통과
                whereQuery += " AND categoryDepth2Id=" + c2;
            } else {
                // c1 체크
                if (getCategoryInfoDepth01(c1).getDepth1Id() == 0)
                    throw new BaseException(INVALID_CATEGORYD1ID); // |3XXX|잘못된 categoryDepth1Id 입니다.
                // c2 체크
                if (getCategoryInfoDepth02(c2).getDepth2Id() == 0)
                    throw new BaseException(EMPTY_CATEGORYD2ID); // |2XXX|categoryDepth2Id을 입력해주세요.
                // c1,c2 체크
                if (!isMatchCategory1and2(c1,c2))
                    throw new BaseException(NOT_MATCH_CATEGORY_12_ID); // NOT_MATCH_CATEGORY_ID|3330|연관되지 않은 depth1Id와 depth2Id입니다.
                // c2,c3 체크
                if (isMatchCategory2and3(c2,c3))
                    throw new BaseException(NOT_MATCH_CATEGORY_23_ID); // NOT_MATCH_CATEGORY_ID|3330|연관되지 않은 depth2Id와 depth3Id입니다.
                // validation 통과
                whereQuery += " AND categoryDepth3Id=" + c3;
            }

            // 판매중인 상품만 검색
            if (onlySale != null ) {
                if (onlySale.equals("true"))
                    whereQuery += " AND dealStatus='sale'";
                else if (onlySale.equals("false"))
                    ;
                else
                    throw new BaseException(INVALID_ONLYSALE); // |2xxx|onlySale에 true 혹은 false를 입력해주세요
            }

            if (min != null && max != null){
                if (min>max)
                    throw new BaseException(INVALID_PRICE_RANGE); // |2xxxx| max보다 큰 min 값입니다.
                else
                    whereQuery += " AND price BETWEEN "+min+" AND "+max;
            } else if (min != null){
                whereQuery += " AND price >= "+min;
            } else if (max != null){
                whereQuery += " AND price <= "+max;
            }

            List<GetFeedRes> result = feedDao.getFeed(whereQuery, orderQuery, p);

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



    // Validation ===================================================================
    /**
     * 카테고리01 정보 확인
     */
    public GetCategoryDepth01Res getCategoryInfoDepth01 (int depth1Id) {
        return feedDao.getCategoryInfoDepth01(depth1Id);
    }
    /**
     * 카테고리02 정보 확인
     */
    public GetCategoryDepth02Res getCategoryInfoDepth02 (int depth1Id) {
        return feedDao.getCategoryInfoDepth02(depth1Id);
    }

    /**
     * 카테고리03 정보 확인
     */
    public GetCategoryDepth03Res getCategoryInfoDepth03 (int depth3Id) {
        return feedDao.getCategoryInfoDepth03(depth3Id);
    }

    /**
     * (validation) 카테고리 아이디 d1 d2 가 일치하는지 확인
     */
    public boolean isMatchCategory1and2(int depth1Id, int depth2Id) {
        try {
            feedDao.getMatchCategory1and2(depth1Id, depth2Id);
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
            feedDao.getMatchCategory2and3(depth2Id, depth3Id);
            return true;
        } catch (Exception e){
            return false;
        }
    }
}
