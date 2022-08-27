package com.example.demo.src.product;


import com.example.demo.src.product.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Repository //  [Persistence Layer에서 DAO를 명시하기 위해 사용]

/**
 * DAO란?
 * 데이터베이스 관련 작업을 전담하는 클래스
 * 데이터베이스에 연결하여, 입력 , 수정, 삭제, 조회 등의 작업을 수행
 */
public class ProductDao {

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
     * 상품 상세정보 조회 - 상품정보
     */
    public ProductDetailInfoModel getProductDetailInfo(int productId) {
        String Query = "SELECT storeId, id, title, dealStatus, price, location, createdAt,\n" +
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
                "`condition`, quantity, deliveryFee,`change`, content, categoryDepth1Id, categoryDepth2Id, categoryDepth3Id FROM Product\n" +
                "WHERE status='active' AND id=?";

        return this.jdbcTemplate.queryForObject(Query, (rs,rn)->
            new ProductDetailInfoModel(
                    rs.getInt("storeId"),
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("dealStatus"),
                    Collections.emptyList(),
                    rs.getInt("price"),
                    rs.getString("location"),
                    rs.getString("createdAt"),
                    rs.getString("uploadedEasyText"),
                    rs.getString("condition"),
                    rs.getInt("quantity"),
                    rs.getString("deliveryFee"),
                    rs.getString("change"),
                    rs.getString("content"),
                    Collections.emptyList(),
                    rs.getInt("categoryDepth1Id"),
                    rs.getInt("categoryDepth2Id"),
                    rs.getInt("categoryDepth3Id")
            ) ,productId);
    }

    /**
     * 상품 상세정보 조회 - 판매자 정보(프로필)
     */
    public List<GetProductStoreRes> getProductStore(int productId) {
        String Query = "select Store.id as storeId, Store.profileImgUrl, Store.storeName,  round(avg(Review.star)) as star, Store.contactTime, Store.introduce, Store.policy, Store.precautions\n" +
                "from Store, Review\n" +
                "where Review.sellerStoreId = Store.id\n" +
                "    and Store.id = (select Product.storeId\n" +
                "                                  from Product, Store\n" +
                "                                  where Product.storeId = Store.id\n" +
                "                                  and Product.id = ?)";

        return this.jdbcTemplate.query(Query, (rs,rn)->
            new GetProductStoreRes(
                    rs.getInt("storeId"),
                    rs.getString("profileImgUrl"),
                    rs.getString("storeName"),
                    rs.getInt("star"),
                    rs.getString("contactTime"),
                    rs.getString("introduce"),
                    rs.getString("policy"),
                    rs.getString("precautions")
            ) ,productId);
    }

    /**
     * 상품 상세정보 조회 - 판매자 정보(상품)
     */
    public List<GetProductStoreProductRes> getProductStoreProduct(int productId) {
        String Query = "select Store.id as storeId, Product.id as productId, Product.imageUrl01,\n" +
                "                Product.price, Product.title\n" +
                "                from Product, Store\n" +
                "                where Product.storeId = Store.id\n" +
                "                    and Product.dealStatus = \"sale\"\n" +
                "                    and Store.id = (select Product.storeId\n" +
                "                                  from Product, Store\n" +
                "                                  where Product.storeId = Store.id\n" +
                "                                  and Product.id = ?) order by Product.updatedAt DESC";

        return this.jdbcTemplate.query(Query, (rs,rn)->
            new GetProductStoreProductRes(
                    rs.getInt("storeId"),
                    rs.getInt("productId"),
                    rs.getString("imageUrl01"),
                    rs.getInt("price"),
                    rs.getString("title")
            ) ,productId);
    }
    /**
     * 상품 상세정보 조회 - 판매자 정보(리뷰)
     */
    public List<GetProductStoreReviewRes> getProductStoreReview(int productId) {
        String Query = "select distinct Review.purchaserStoreId, A.profileImgUrl, A.storeName, Review.star, Review.content, Review.productId, Product.title,\n" +
                "                CASE\n" +
                "                    WHEN TIMESTAMPDIFF (MINUTE,Review.createdAt, CURRENT_TIMESTAMP) < 60\n" +
                "                    THEN CONCAT(TIMESTAMPDIFF (MINUTE,Review.createdAt, CURRENT_TIMESTAMP), '분 전')\n" +
                "                    WHEN TIMESTAMPDIFF(HOUR,Review.createdAt, CURRENT_TIMESTAMP) < 24\n" +
                "                    THEN CONCAT(TIMESTAMPDIFF(HOUR,Review.createdAt, CURRENT_TIMESTAMP), '시간 전')\n" +
                "                    WHEN TIMESTAMPDIFF(DAY,Review.createdAt, CURRENT_TIMESTAMP)< 30\n" +
                "                    THEN CONCAT(TIMESTAMPDIFF(DAY,Review.createdAt, CURRENT_TIMESTAMP), '일 전')\n" +
                "                    WHEN TIMESTAMPDIFF(MONTH,Review.createdAt, CURRENT_TIMESTAMP)< 12\n" +
                "                    THEN CONCAT(TIMESTAMPDIFF(MONTH,Review.createdAt, CURRENT_TIMESTAMP), '개월 전')\n" +
                "                    ELSE CONCAT(TIMESTAMPDIFF(YEAR,Review.createdAt, CURRENT_TIMESTAMP ), '년 전')\n" +
                "                    END AS 'createdAt'\n" +
                "from Store, Review, Product,(\n" +
                "    select Store.id as purchaserStoreId, Store.profileImgUrl, Store.storeName,\n" +
                "       Review.star, Review.content, Review.productId, Product.title\n" +
                "    from Store, Review, Product\n" +
                "    where Review.purchaserStoreId = Store.id\n" +
                "    and Review.productId = Product.id\n" +
                ") A\n" +
                "where Review.sellerStoreId = Store.id\n" +
                "    and Review.productId = Product.id\n" +
                "    and A.purchaserStoreId = Review.purchaserStoreId\n" +
                "    and Store.id = (select Product.storeId\n" +
                "                                  from Product, Store\n" +
                "                                  where Product.storeId = Store.id\n" +
                "                                  and Product.id = ?)";

        return this.jdbcTemplate.query(Query, (rs,rn)->
            new GetProductStoreReviewRes(
                    rs.getInt("purchaserStoreId"),
                    rs.getString("profileImgUrl"),
                    rs.getString("storeName"),
                    rs.getInt("star"),
                    rs.getString("content"),
                    rs.getInt("productId"),
                    rs.getString("title"),
                    rs.getString("createdAt")
            ) ,productId);
    }


    /**
     * 해당 상품 팔로우하기
     */
    public int postProductFollow(int uid, int productId) {
        String createBasketQuery = "insert into Follow(followingStoreId, followerStoreId) values(?, (select Store.id\n" +
                "    from Store, Product\n" +
                "where Product.storeId = Store.id\n" +
                "and Product.id = ?))"; // 실행될 동적 쿼리문
        Object[] createBasketParams = new Object[]{uid, productId}; // 동적 쿼리의 ?부분에 주입될 값
        this.jdbcTemplate.update(createBasketQuery, createBasketParams);
        String lastInserIdQuery = "select last_insert_id()"; // 가장 마지막에 삽입된(생성된) id값은 가져온다.
        return this.jdbcTemplate.queryForObject(lastInserIdQuery, int.class); // 해당 쿼리문의 결과 마지막으로 삽입된 찜하기의 Id번호를 반환한다.

    }

    /**
     * 해당 상품 팔로우 확인
     */
    public int checkFollow(int uid, int productId) {
        String checkNameQuery = "select exists (select id from Follow where followingStoreId = ? and followerStoreId = (select Store.id\n" +
                "    from Store, Product\n" +
                "where Product.storeId = Store.id\n" +
                "and Product.id = ?) and Follow.status = \"active\")";
        int param1 = uid;
        int param2 = productId;
        return this.jdbcTemplate.queryForObject(checkNameQuery,
                int.class,
                param1, param2); // checkNameQuery, checkNameParams 통해 가져온 값(int형)을 반환한다. -> 쿼리문의 결과(존재하지 않음(False,0),존재함(True, 1))를 int형(0,1)으로 반환됩니다.
    }

    /**
     * 해당 상품 팔로우 취소
     */
    public int patchProductFollow(int uid, int productId) {
        String Query = "update Follow set status = \"delete\"  where followingStoreId = ? and followerStoreId = (select Store.id\n" +
                "    from Store, Product\n" +
                "where Product.storeId = Store.id\n" +
                "and Product.id = ?)"; // 실행될 동적 쿼리문
        Object[] Params = new Object[]{uid, productId}; // 동적 쿼리의 ?부분에 주입될 값
        return this.jdbcTemplate.update(Query, Params);

    }

    /**
     * 해당 상품 찜하기 취소 확인
     */
    public int checkFollowFalse(int uid, int productId) {
        String checkNameQuery = "select exists (select id from Follow where followingStoreId = ? and followerStoreId = (select Store.id\n" +
                "    from Store, Product\n" +
                "where Product.storeId = Store.id\n" +
                "and Product.id = ?) and status = \"delete\")";
        int param1 = uid;
        int param2 = productId;
        return this.jdbcTemplate.queryForObject(checkNameQuery,
                int.class,
                param1, param2); // checkNameQuery, checkNameParams 통해 가져온 값(int형)을 반환한다. -> 쿼리문의 결과(존재하지 않음(False,0),존재함(True, 1))를 int형(0,1)으로 반환됩니다.
    }



    /**
     * 해당 상품 찜하기
     */
    public int postProductBasket(int uid, int productId) {
        String createBasketQuery = "insert into Basket (storeId, productId) value (?, ?)"; // 실행될 동적 쿼리문
        Object[] createBasketParams = new Object[]{uid, productId}; // 동적 쿼리의 ?부분에 주입될 값
        this.jdbcTemplate.update(createBasketQuery, createBasketParams);
        String lastInserIdQuery = "select last_insert_id()"; // 가장 마지막에 삽입된(생성된) id값은 가져온다.
        return this.jdbcTemplate.queryForObject(lastInserIdQuery, int.class); // 해당 쿼리문의 결과 마지막으로 삽입된 찜하기의 Id번호를 반환한다.

    }

    /**
     * 해당 상품 찜하기 확인
     */
    public int checkBasket(int uid, int productId) {
        String checkNameQuery = "select exists (select id from Basket where storeId = ? and productId = ? and basket = \"true\")";
        int param1 = uid;
        int param2 = productId;
        return this.jdbcTemplate.queryForObject(checkNameQuery,
                int.class,
                param1, param2); // checkNameQuery, checkNameParams 통해 가져온 값(int형)을 반환한다. -> 쿼리문의 결과(존재하지 않음(False,0),존재함(True, 1))를 int형(0,1)으로 반환됩니다.
    }

    /**
     * 해당 상품 찜하기 취소
     */
    public int patchProductBasket(int uid, int productId) {
        String createBasketQuery = "update Basket set basket = \"false\"  where storeId = ? and productId = ?"; // 실행될 동적 쿼리문
        Object[] createBasketParams = new Object[]{uid, productId}; // 동적 쿼리의 ?부분에 주입될 값
        return this.jdbcTemplate.update(createBasketQuery, createBasketParams);

    }

    /**
     * 해당 상품 찜하기 취소 확인
     */
    public int checkBasketFalse(int uid, int productId) {
        String checkNameQuery = "select exists (select id from Basket where storeId = ? and productId = ? and basket = \"false\")";
        int param1 = uid;
        int param2 = productId;
        return this.jdbcTemplate.queryForObject(checkNameQuery,
                int.class,
                param1, param2); // checkNameQuery, checkNameParams 통해 가져온 값(int형)을 반환한다. -> 쿼리문의 결과(존재하지 않음(False,0),존재함(True, 1))를 int형(0,1)으로 반환됩니다.
    }

    /**
     * 내 상품 상태변경 하기
     */
    public int postProductStatus(int uid, int productId, PostProductStatusReq postProductStatusReq) {
        String Query = "update Product set dealStatus = ?  where Product.storeId = ? and Product.id= ?"; // 실행될 동적 쿼리문
        Object[] Params = new Object[]{postProductStatusReq.getDealStatus(), uid, productId}; // 동적 쿼리의 ?부분에 주입될 값
        return this.jdbcTemplate.update(Query, Params);
    }

    /**
     * 상품 이미지 리스트 ImageUrls 조회
     */
    public List<String> getImageUrls (int productId){
        String Query = "SELECT imageUrl01, imageUrl02, imageUrl03, imageUrl04,imageUrl05,imageUrl06,imageUrl07,imageUrl08,imageUrl09,imageUrl10 FROM Product\n" +
                "WHERE status='active' AND id=?";
        return this.jdbcTemplate.queryForObject(Query,
                (rs,rn)-> new ArrayList<>(Arrays.asList(
                        rs.getString("imageUrl01"),
                        rs.getString("imageUrl02"),
                        rs.getString("imageUrl03"),
                        rs.getString("imageUrl04"),
                        rs.getString("imageUrl05"),
                        rs.getString("imageUrl06"),
                        rs.getString("imageUrl07"),
                        rs.getString("imageUrl08"),
                        rs.getString("imageUrl09"),
                        rs.getString("imageUrl10")
                )), productId);
    }

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
     * 새로운 상품 추가
     */
    public int insertNewProduct(int uid, NewProductModel dataModel) {
        String Query;
        if (dataModel.getCategoryDepth2Id() == 0) {
            Query = "INSERT INTO Product (storeId, title, content, categoryDepth1Id, price, deliveryFee, quantity,`change`)\n" +
                    "VALUES (?,'" + dataModel.getName() + "','"
                    + dataModel.getContent() + "',?,?,'"
                    + dataModel.getDeliveryFee() + "',?,'"
                    + dataModel.getChange() + "');";
            this.jdbcTemplate.update(Query,
                    uid,
                    dataModel.getCategoryDepth1Id(),
                    dataModel.getPrice(),
                    dataModel.getQuantity());
        }
        else if (dataModel.getCategoryDepth3Id() == 0) {
            Query = "INSERT INTO Product (storeId, title, content, categoryDepth1Id, categoryDepth2Id, price, deliveryFee, quantity,`change`)\n" +
                    "VALUES (?,'" + dataModel.getName() + "','"
                    + dataModel.getContent() + "',?,?,?,'"
                    + dataModel.getDeliveryFee() + "',?,'"
                    + dataModel.getChange() + "');";
            this.jdbcTemplate.update(Query,
                    uid,
                    dataModel.getCategoryDepth1Id(),
                    dataModel.getCategoryDepth2Id(),
                    dataModel.getPrice(),
                    dataModel.getQuantity());
        }
        else {
            Query = "INSERT INTO Product (storeId, title, content, categoryDepth1Id, categoryDepth2Id, categoryDepth3Id, price, deliveryFee, quantity,`change`)\n" +
                    "VALUES (?,'" + dataModel.getName() + "','"
                    + dataModel.getContent() + "',?,?,?,?,'"
                    + dataModel.getDeliveryFee() + "',?,'"
                    + dataModel.getChange() + "');";
            this.jdbcTemplate.update(Query,
                    uid,
                    dataModel.getCategoryDepth1Id(),
                    dataModel.getCategoryDepth2Id(),
                    dataModel.getCategoryDepth3Id(),
                    dataModel.getPrice(),
                    dataModel.getQuantity());
        }

        // 새로 생성된 상품의 id 추출
        return this.jdbcTemplate.queryForObject(
                "SELECT last_insert_id()",
                Integer.class);
    }

    /**
     * 이미지 Url 입력
     */
    public void addImgUrls(int productId, List<String> imageUrls) {
        String Query;
        System.out.println(imageUrls.size());
        for (int i=0; i < imageUrls.size(); i++){
            if (i == 9)
                Query = "UPDATE Product SET imageUrl10 = '"+ imageUrls.get(i) +"' WHERE id=?";
            else
                Query = "UPDATE Product SET imageUrl0" + (i+1) + " = '"+ imageUrls.get(i) +"' WHERE id=?";
        this.jdbcTemplate.update(Query, productId);
        }
    }


    /**
     * 해시태그 입력
     */
    public void addHashTags(int productId, List<String> hashtags) {
        int tagId;
        for (String h : hashtags) {
            try {
                // 이미 저장된 태그인지 확인
                tagId = this.jdbcTemplate.queryForObject(
                        "SELECT * FROM Tag WHERE tag = '" + h +"'",
                        (rs, rowNum) -> rs.getInt("id")
                );
            } catch (IncorrectResultSizeDataAccessException error) {
                // 저장되지 않은 태그이므로
                // 새로운 태그 생성
                this.jdbcTemplate.update(
                        "INSERT INTO Tag (tag) VALUES ('" + h + "')"
                );
                // 새로 생성된 태그의 id 추출
                tagId = this.jdbcTemplate.queryForObject(
                        "SELECT last_insert_id()",
                        Integer.class);
            }

            // 영상의 id와 해시태그의 id 입력
            this.jdbcTemplate.update(
                    "INSERT INTO TagProductMap (productId, tagId) VALUE (?,?)",
                    productId, tagId);
        }
    }

    /**
     * 해시태그 삭제
     */
    public void delHashTags(int productId, List<String> hashtags) {
        int tagId;
        for (String h : hashtags) {
            try {
                String Query = "SELECT TPM.id, tag, TPM.status FROM TagProductMap TPM\n" +
                        "LEFT JOIN Product P on P.id = TPM.productId\n" +
                        "LEFT JOIN Tag T on TPM.tagId = T.id\n" +
                        "WHERE TPM.status='active' AND P.id=? AND T.tag = '" + h + "' ";
                tagId = this.jdbcTemplate.queryForObject(Query, (rs,rn)-> rs.getInt("id"), productId);

                Query = "UPDATE TagProductMap SET status = 'deleted' WHERE id = ?";
                this.jdbcTemplate.update(Query,tagId);
            } catch (IncorrectResultSizeDataAccessException error) {
                continue;
            }
        }
    }

    /**
     * 주소 입력
     */
    public void addLocationInfo(int productId, String location){
        String Query = "UPDATE Product SET location = '"+location+"' WHERE id=?";
        this.jdbcTemplate.update(Query,productId);
    }

    /**
     * 상품 정보 수정
     */
    public void updateProductInfo(int productId, String updateQuery){
        String initQuery = "UPDATE Product SET \n ";
        String finlQuery = " \n status='active' WHERE status='active' AND id=?";
        String Query = initQuery + updateQuery + finlQuery;
        this.jdbcTemplate.update(Query,productId);
    }

    /**
     * 상품 상태 -> deleted
     */
    public void deleteProduct(int productId){
        String Query = "UPDATE Product SET status = 'deleted' WHERE status = 'active' AND id = ?";
        this.jdbcTemplate.update(Query,productId);
    }
    /**
     * 상품 데이터 삭제!!
     */
    public void deleteProduct(int productId, int kill){
        String Query1 = "DELETE TPM FROM Product AS P\n" +
                "LEFT JOIN TagProductMap TPM on P.id = TPM.productId\n" +
                "WHERE P.status='deleted' AND P.id=?";
        String Query2 = "DELETE P FROM Product AS P\n" +
                "LEFT JOIN TagProductMap TPM on P.id = TPM.productId\n" +
                "WHERE P.status='deleted' AND P.id=?";
        this.jdbcTemplate.update(Query1,productId);
        this.jdbcTemplate.update(Query2,productId);
    }

    /**
     * (admin) 삭제 가능여부 조회
     */
    public void isDeletableProductId(int productId){
        String Query = "SELECT * FROM Product AS P\n" +
                "WHERE P.status='deleted' AND P.id=?";
        this.jdbcTemplate.queryForObject(Query,(rs,rn)->rs.getInt("id"),productId);
    }

    /**
     * 카테고리 항목 조회
     */
    public List<GetCategoryDepth01Res> getCategoryDepth01() {
        String Query = "SELECT id, name FROM Category WHERE status='active'";
        return this.jdbcTemplate.query(Query,
                (rs, rn) -> new GetCategoryDepth01Res(
                        rs.getInt("id"),
                        rs.getString("name"),
                        false
                ));
    }

    /**
     * 카테고리 항목 조회 - 세부 카테고리 1
     */
    public List<GetCategoryDepth02Res> getCategoryDepth02(int depth1Id) {
        String Query = "SELECT id,categoryId, name FROM CategoryDepth2 WHERE status='active' AND categoryId = ?";
        return this.jdbcTemplate.query(Query,
                (rs, rn) -> new GetCategoryDepth02Res(
                        rs.getInt("id"),
                        rs.getString("name"),
                        false
                ),
                depth1Id);
    }

    /**
     * 카테고리 항목 조회 - 세부 카테고리 2
     */
    public List<GetCategoryDepth03Res> getCategoryDepth03(int depth2Id) {
        String Query = "SELECT id,category2Id, name FROM CategoryDepth3 WHERE status='active' AND category2Id = ?";
        return this.jdbcTemplate.query(Query,
                (rs, rn) -> new GetCategoryDepth03Res(
                        rs.getInt("id"),
                        rs.getString("name"),
                        false
                ),
                depth2Id);
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
