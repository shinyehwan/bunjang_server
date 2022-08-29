package com.example.demo.src.feed;


import com.example.demo.src.feed.model.GetFeedRes;
import com.example.demo.src.product.model.GetCategoryDepth01Res;
import com.example.demo.src.product.model.GetCategoryDepth02Res;
import com.example.demo.src.product.model.GetCategoryDepth03Res;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Repository //  [Persistence Layer에서 DAO를 명시하기 위해 사용]

/**
 * DAO란?
 * 데이터베이스 관련 작업을 전담하는 클래스
 * 데이터베이스에 연결하여, 입력 , 수정, 삭제, 조회 등의 작업을 수행
 */
public class FeedDao {

    // *********************** 동작에 있어 필요한 요소들을 불러옵니다. *************************

    private JdbcTemplate jdbcTemplate;

    @Autowired //readme 참고
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    // ******************************************************************************

    /**
     * DAO관련 함수코드의 전반부는 크게 String ~~~Query와 Object[] ~~~~Params, jdbcTemplate함수로 구성되어 있습니다.(보통은 동적 쿼리문이지만, 동적쿼리가 아닐 경우, Params부분은 없어도 됩니다.)
     * Query부분은 DB에 SQL요청을 할 쿼리문을 의미하는데, 대부분의 경우 동적 쿼리(실행할 때 값이 주입되어야 하는 쿼리) 형태입니다.
     * 그래서 Query의 동적 쿼리에 입력되어야 할 값들이 필요한데 그것이 Params부분입니다.
     * Params부분은 클라이언트의 요청에서 제공하는 정보(~~~~Req.java에 있는 정보)로 부터 getXXX를 통해 값을 가져옵니다. ex) getEmail -> email값을 가져옵니다.
     *      Notice! get과 get의 대상은 카멜케이스로 작성됩니다. ex) item -> getItem, password -> getPassword, email -> getEmail, userIdx -> getUserIdx
     * 그 다음 GET, POST, PATCH 메소드에 따라 jabcTemplate의 적절한 함수(queryForObject, query, update)를 실행시킵니다(DB요청이 일어납니다.).
     *      Notice!
     *      POST, PATCH의 경우 jdbcTemplate.update
     *      GET은 대상이 하나일 경우 jdbcTemplate.queryForObject, 대상이 복수일 경우, jdbcTemplate.query 함수를 사용합니다.
     * jdbcTeplate이 실행시킬 때 Query 부분과 Params 부분은 대응(값을 주입)시켜서 DB에 요청합니다.
     * <p>
     * 정리하자면 < 동적 쿼리문 설정(Query) -> 주입될 값 설정(Params) -> jdbcTemplate함수(Query, Params)를 통해 Query, Params를 대응시켜 DB에 요청 > 입니다.
     * <p>
     * <p>
     * DAO관련 함수코드의 후반부는 전반부 코드를 실행시킨 후 어떤 결과값을 반환(return)할 것인지를 결정합니다.
     * 어떠한 값을 반환할 것인지 정의한 후, return문에 전달하면 됩니다.
     * ex) return this.jdbcTemplate.query( ~~~~ ) -> ~~~~쿼리문을 통해 얻은 결과를 반환합니다.
     */

    /**
     * 참고 링크
     * https://jaehoney.tistory.com/34 -> JdbcTemplate 관련 함수에 대한 설명
     * https://velog.io/@seculoper235/RowMapper%EC%97%90-%EB%8C%80%ED%95%B4 -> RowMapper에 대한 설명
     */

    /**
     * 상품 검색 (간단)
     */
    public List<GetFeedRes> FeedByKeywordOrderByDate(String k, int p) {
        String Query = "SELECT id, title, imageUrl01,price,location,createdAt,\n" +
                "-- 업로드날짜 표시 형식\n" +
                "        CASE\n" +
                "            WHEN TIMESTAMPDIFF (MINUTE,createdAt, CURRENT_TIMESTAMP) < 60\n" +
                "            THEN CONCAT(TIMESTAMPDIFF (MINUTE,createdAt, CURRENT_TIMESTAMP), '분 전')\n" +
                "            WHEN TIMESTAMPDIFF(HOUR,createdAt, CURRENT_TIMESTAMP) < 24\n" +
                "            THEN CONCAT(TIMESTAMPDIFF(HOUR,createdAt, CURRENT_TIMESTAMP), '시간 전')\n" +
                "            WHEN TIMESTAMPDIFF(DAY,createdAt, CURRENT_TIMESTAMP)< 30\n" +
                "            THEN CONCAT(TIMESTAMPDIFF(DAY,createdAt, CURRENT_TIMESTAMP), '일 전')\n" +
                "            WHEN TIMESTAMPDIFF(MONTH,createdAt, CURRENT_TIMESTAMP)< 12\n" +
                "            THEN CONCAT(TIMESTAMPDIFF(MONTH,createdAt, CURRENT_TIMESTAMP), '개월 전')\n" +
                "            ELSE CONCAT(TIMESTAMPDIFF(YEAR,createdAt, CURRENT_TIMESTAMP ), '년 전')\n" +
                "        END AS 'uploadedEasyText',\n" +
                "    dealStatus FROM Product\n" +
                "WHERE\n" +
                "    status='active'\n" +
                "    AND (\n" +
                "        title LIKE '%" + k + "%'\n" +
                "        OR content LIKE '%" + k + "%'\n" +
                "    )\n" +
                "ORDER BY createdAt DESC\n" +
                "LIMIT ?,?";
        return this.jdbcTemplate.query(Query,
                (rs,rn) -> new GetFeedRes(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("imageUrl01"),
                        rs.getInt("price"),
                        rs.getString("location"),
                        rs.getString("createdAt"),
                        rs.getString("uploadedEasyText"),
                        0,
                        rs.getString("dealStatus"),
                        false
                ), 20*(p-1), 20);
    }
    /**
     * 상품 검색 (복잡)
     */
    public List<GetFeedRes> getFeed(String whereQuery, String orderQuery, int p) {
        String selectQuery = "SELECT id, title, imageUrl01,price,location,createdAt,\n" +
                "-- 업로드날짜 표시 형식\n" +
                "        CASE\n" +
                "            WHEN TIMESTAMPDIFF (MINUTE,createdAt, CURRENT_TIMESTAMP) < 60\n" +
                "            THEN CONCAT(TIMESTAMPDIFF (MINUTE,createdAt, CURRENT_TIMESTAMP), '분 전')\n" +
                "            WHEN TIMESTAMPDIFF(HOUR,createdAt, CURRENT_TIMESTAMP) < 24\n" +
                "            THEN CONCAT(TIMESTAMPDIFF(HOUR,createdAt, CURRENT_TIMESTAMP), '시간 전')\n" +
                "            WHEN TIMESTAMPDIFF(DAY,createdAt, CURRENT_TIMESTAMP)< 30\n" +
                "            THEN CONCAT(TIMESTAMPDIFF(DAY,createdAt, CURRENT_TIMESTAMP), '일 전')\n" +
                "            WHEN TIMESTAMPDIFF(MONTH,createdAt, CURRENT_TIMESTAMP)< 12\n" +
                "            THEN CONCAT(TIMESTAMPDIFF(MONTH,createdAt, CURRENT_TIMESTAMP), '개월 전')\n" +
                "            ELSE CONCAT(TIMESTAMPDIFF(YEAR,createdAt, CURRENT_TIMESTAMP ), '년 전')\n" +
                "        END AS 'uploadedEasyText',\n" +
                "    dealStatus FROM Product\n" +
                "WHERE\n" +
                "    status='active' \n";

        String limitQuery = "\n LIMIT ?,?";

        String Query = selectQuery + whereQuery+ orderQuery + limitQuery;

        // System.out.println(Query);

        return this.jdbcTemplate.query(Query,
                (rs,rn) -> new GetFeedRes(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("imageUrl01"),
                        rs.getInt("price"),
                        rs.getString("location"),
                        rs.getString("createdAt"),
                        rs.getString("uploadedEasyText"),
                        0,
                        rs.getString("dealStatus"),
                        false
                ), 20*(p-1), 20);
    }
    /**
     * 최신 상품 조회 (조건x)
     */
    public List<GetFeedRes> getRecentFeed(int start, int rowNum) {
        String Query = "SELECT id, title, imageUrl01,price,location,createdAt,\n" +
                "-- 업로드날짜 표시 형식\n" +
                "        CASE\n" +
                "            WHEN TIMESTAMPDIFF (MINUTE,createdAt, CURRENT_TIMESTAMP) < 60\n" +
                "            THEN CONCAT(TIMESTAMPDIFF (MINUTE,createdAt, CURRENT_TIMESTAMP), '분 전')\n" +
                "            WHEN TIMESTAMPDIFF(HOUR,createdAt, CURRENT_TIMESTAMP) < 24\n" +
                "            THEN CONCAT(TIMESTAMPDIFF(HOUR,createdAt, CURRENT_TIMESTAMP), '시간 전')\n" +
                "            WHEN TIMESTAMPDIFF(DAY,createdAt, CURRENT_TIMESTAMP)< 30\n" +
                "            THEN CONCAT(TIMESTAMPDIFF(DAY,createdAt, CURRENT_TIMESTAMP), '일 전')\n" +
                "            WHEN TIMESTAMPDIFF(MONTH,createdAt, CURRENT_TIMESTAMP)< 12\n" +
                "            THEN CONCAT(TIMESTAMPDIFF(MONTH,createdAt, CURRENT_TIMESTAMP), '개월 전')\n" +
                "            ELSE CONCAT(TIMESTAMPDIFF(YEAR,createdAt, CURRENT_TIMESTAMP ), '년 전')\n" +
                "        END AS 'uploadedEasyText',\n" +
                "    dealStatus FROM Product\n" +
                "WHERE\n" +
                "    status='active' \n" +
                "ORDER BY createdAt DESC\n " +
                "LIMIT ?,?";

        // System.out.println(Query);

        return this.jdbcTemplate.query(Query,
                (rs,rn) -> new GetFeedRes(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("imageUrl01"),
                        rs.getInt("price"),
                        rs.getString("location"),
                        rs.getString("createdAt"),
                        rs.getString("uploadedEasyText"),
                        0,
                        rs.getString("dealStatus"),
                        false
                ), start, rowNum);
    }

    // 최근 조회,찜 한 물품들 id 20개 불러오기
    public List<Integer> getPidListByViewAndBasket(int uid, int p){
        String Query = "SELECT P.id,P.storeId,P.title,P.status,V.storeId,V.status,B.storeId,V.status FROM Product P\n" +
                "LEFT JOIN View V on P.id = V.productId\n" +
                "LEFT JOIN Basket B on P.id = B.productId\n" +
                "WHERE P.status='active'\n" +
                "AND ((V.status='active' AND V.storeId=?) OR (B.status='active' AND B.storeId=?))\n" +
                "GROUP BY P.id\n" +
                "LIMIT ?,?";

        return this.jdbcTemplate.query(Query,
                (rs,rn)-> rs.getInt("id"),
                uid,uid, 20*(p-1), 20);
    }


    /**
     * 팔로잉 상품 조회
     */
    public List<Integer> productIdsByFollowingStore (int uid) {
        String getUserQuery = "select Follow.followerStoreId as followingId, Store.storeName, Product.id as productId, Product.imageUrl01, Product.price\n" +
                "    from Follow, Store, Product\n" +
                "where Follow.followerStoreId = Store.id\n" +
                "    and Store.id = Product.storeId\n" +
                "    and Follow.followingStoreId = ?";
        int getUserParams = uid;
        return this.jdbcTemplate.query(getUserQuery,
                (rs, rowNum) -> rs.getInt("productId") ,
                getUserParams);
    }

    public List<Integer> getProductsByTag(String tag){
        String Query = "SELECT Product.id, Product.title, T.tag FROM Product\n" +
                "LEFT JOIN TagProductMap TPM on Product.id = TPM.productId\n" +
                "LEFT JOIN Tag T on TPM.tagId = T.id\n" +
                "WHERE tag LIKE '%"+tag+"%'\n" +
                "GROUP BY Product.id";
        return this.jdbcTemplate.query(Query,
                (rs, rowNum) -> rs.getInt("id"));
    }
//
//    public List<Integer> productIdsByView (int uid) {}
//    public List<Integer> productIdsByBasket (int uid) {}
//    public List<Integer> productIdsByPurchase (int uid) {}


    /**
     * 상품 태그 리스트 조회
     */
    public List<String> getTags(int productId) {
        String Query = "SELECT TPM.productId, Tag.tag  FROM Tag\n" +
                "LEFT JOIN TagProductMap TPM on Tag.id = TPM.tagId\n" +
                "WHERE TPM.status='active' AND TPM.productId = ?";
        return this.jdbcTemplate.query(Query,
                (rs,rn)-> rs.getString("tag"),
                productId);
    }

    /**
     * 브랜드 검색키워드 조회
     */
    public List<String> getKeywordByBrand(String brandName){
        String Query = "SELECT keyword01, keyword02, keyword03  FROM Brand\n" +
                "WHERE status='active' AND brandName = '"+brandName + "' ";
        return this.jdbcTemplate.queryForObject(Query,
                (rs,rn)-> new ArrayList<>(Arrays.asList(
                        rs.getString("keyword01"),
                        rs.getString("keyword02"),
                        rs.getString("keyword03")
                )));
    }

    /**
     *  사용자 찜 여부 조회
     */
    public void isBasketByUid (int uid, int productId){
        String  Query = "SELECT * From Basket\n" +
                "WHERE status='active' AND basket='true' AND storeId=? AND productId=? \n" +
                "GROUP BY storeId,productId";
        this.jdbcTemplate.query(Query,
                (rs,rn)-> rs.getInt("id"),
                uid,productId);
    }

    /**
     * 카테고리01 정보 확인
     */
    public GetCategoryDepth01Res getCategoryInfoDepth01(int depth1Id) {
        try {
            String Query = "SELECT Category.id, Category.name, COUNT(C2.name) AS 'count' FROM Category\n" +
                    "    LEFT JOIN CategoryDepth2 C2 on Category.id = C2.categoryId\n" +
                    "WHERE Category.status='active' AND Category.id=? \n" +
                    "GROUP BY Category.id";

            return this.jdbcTemplate.queryForObject(Query,
                    (rs, rn) -> new GetCategoryDepth01Res(
                            rs.getInt("id"),
                            rs.getString("name"),
                            (rs.getInt("count") > 0)
                    ),
                    depth1Id);
        } catch (IncorrectResultSizeDataAccessException error) {
            return new GetCategoryDepth01Res(0, "", false);
        }
    }
    /**
     * 카테고리02 정보 확인
     */
    public GetCategoryDepth02Res getCategoryInfoDepth02(int depth2Id) {
        try {
            String Query = "SELECT CategoryDepth2.id, CategoryDepth2.name, COUNT(C3.name) AS 'count' FROM CategoryDepth2\n" +
                    "    LEFT JOIN CategoryDepth3 C3 on CategoryDepth2.id = C3.category2Id\n" +
                    "WHERE CategoryDepth2.status='active' AND CategoryDepth2.id= ? \n" +
                    "GROUP BY CategoryDepth2.id";

            return this.jdbcTemplate.queryForObject(Query,
                    (rs, rn) -> new GetCategoryDepth02Res(
                            rs.getInt("id"),
                            rs.getString("name"),
                            (rs.getInt("count") > 0)
                    ),
                    depth2Id);
        } catch (IncorrectResultSizeDataAccessException error) {
            return new GetCategoryDepth02Res(0, "", false);
        }
    }
    /**
     * 카테고리03 정보 확인
     */
    public GetCategoryDepth03Res getCategoryInfoDepth03(int depth3Id) {
        try {
            String Query = "SELECT id,name FROM CategoryDepth3 WHERE status='active' AND id=?";

            return this.jdbcTemplate.queryForObject(Query,
                    (rs, rn) -> new GetCategoryDepth03Res(
                            rs.getInt("id"),
                            rs.getString("name"),
                            false
                    ),
                    depth3Id);
        } catch (IncorrectResultSizeDataAccessException error) {
            return new GetCategoryDepth03Res(0, "", false);
        }
    }
    /**
     * (validation) 카테고리 아이디 d1 d2 가 일치하는지 확인
     */
    public void getMatchCategory1and2(int depth1Id, int depth2Id) {
        String Query = "SELECT id,categoryId, name FROM CategoryDepth2 WHERE status='active' AND categoryId = ? And id=?";
        this.jdbcTemplate.queryForObject(Query,
                (rs, rn) -> rs.getInt("id"),
                depth1Id, depth2Id);
    }
    /**
     * (validation) 카테고리 아이디 d2 d3 가 일치하는지 확인
     */
    public void getMatchCategory2and3(int depth2Id, int depth3Id) {
        String Query = "SELECT id,category2Id, name FROM CategoryDepth3 WHERE status='active' AND category2Id = ? And id=?";
        this.jdbcTemplate.queryForObject(Query,
                (rs, rn) -> rs.getInt("id"),
                depth2Id, depth3Id);
    }
}
