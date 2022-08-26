package com.example.demo.src.store;


import com.example.demo.src.store.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository //  [Persistence Layer에서 DAO를 명시하기 위해 사용]

/**
 * DAO란?
 * 데이터베이스 관련 작업을 전담하는 클래스
 * 데이터베이스에 연결하여, 입력 , 수정, 삭제, 조회 등의 작업을 수행
 */
public class StoreDao {

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

    // 회원가입
    public int createUser(PostStoreReq postStoreReq) {
        String createUserQuery = "insert into Store (name, birth, gender, phone, password) VALUES (?,?,?,?,?)"; // 실행될 동적 쿼리문
        Object[] createUserParams = new Object[]{postStoreReq.getName(), postStoreReq.getBirth(), postStoreReq.getGender(), postStoreReq.getPhone(), postStoreReq.getPassword()}; // 동적 쿼리의 ?부분에 주입될 값
        this.jdbcTemplate.update(createUserQuery, createUserParams);

        String lastInserIdQuery = "select last_insert_id()"; // 가장 마지막에 삽입된(생성된) id값은 가져온다.
        return this.jdbcTemplate.queryForObject(lastInserIdQuery, int.class); // 해당 쿼리문의 결과 마지막으로 삽인된 유저의 userIdx번호를 반환한다.
    }

    // 핸드폰 확인
    public int checkPhone(String phone) {
        String checkPhoneQuery = "select exists(select phone from Store where phone = ?)";
        String checkPhoneParams = phone;
        return this.jdbcTemplate.queryForObject(checkPhoneQuery,
                int.class,
                checkPhoneParams);
    }

    // 로그인
    public Store getPwd(PostLoginReq postLoginReq) {
        String getPwdQuery = "select id, name, birth, gender, phone, password from Store where phone = ?";
        String getPwdParams = postLoginReq.getPhone();

        return this.jdbcTemplate.queryForObject(getPwdQuery,
                (rs, rowNum) -> new Store(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("birth"),
                        rs.getString("gender"),
                        rs.getString("phone"),
                        rs.getString("password")
                ), // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
                getPwdParams
        ); // 한 개의 회원정보를 얻기 위한 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
    }

    // 상품 갯수 조회
    public int getStoreProductCount (int storeId) {
        String getUserQuery = "select COUNT(Product.storeId) as productCount\n" +
                "    from Store, Product\n" +
                "    where Store.id = Product.storeId\n" +
                "        and Store.id = ?";
        int getUserParams = storeId;
        return this.jdbcTemplate.queryForObject(getUserQuery,
                (rs, rowNum) -> (
                        rs.getInt("productCount")
                ), // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
                getUserParams); // 한 개의 회원정보를 얻기 위한 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
    }
    // 찜 갯수 조회
    public int getStoreBasketCount (int storeId) {
        String getUserQuery = "select COUNT(Basket.storeId) as basketCount\n" +
                "from Basket, Store, Product\n" +
                "where Basket.storeId = Store.id\n" +
                "  and Basket.productId = Product.id\n" +
                "  and Store.id = ?";
        int getUserParams = storeId;
        return this.jdbcTemplate.queryForObject(getUserQuery,
                (rs, rowNum) -> (
                        rs.getInt("basketCount")
                ), // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
                getUserParams); // 한 개의 회원정보를 얻기 위한 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
    }
    // 리뷰 갯수 조회
    public int getStoreReviewCount (int storeId) {
        String getUserQuery = "select COUNT(Review.sellerStoreId) as reviewCount\n" +
                "from Review, Store\n" +
                "where Review.sellerStoreId = Store.id\n" +
                "  and Store.id = ?";

        int getUserParams = storeId;
        return this.jdbcTemplate.queryForObject(getUserQuery,
                (rs, rowNum) -> (
                        rs.getInt("reviewCount")
                ), // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
                getUserParams); // 한 개의 회원정보를 얻기 위한 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
    }

    // 나를 팔로우 하는(팔로워) 갯수 조회
    public int getStoreFollowerCount (int storeId) {
        String getUserQuery = "select COUNT(Follow.followingStoreId) as followerCount\n" +
                "from Follow, Store\n" +
                "where Follow.followerStoreId = Store.id\n" +
                "  and Store.id = ?";

        int getUserParams = storeId;
        return this.jdbcTemplate.queryForObject(getUserQuery,
                (rs, rowNum) -> (
                        rs.getInt("followerCount")
                ), // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
                getUserParams); // 한 개의 회원정보를 얻기 위한 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
    }

    // 내가 팔로우 하는(팔로잉) 갯수 조회
    public int getStoreFollowingCount (int storeId) {
        String getUserQuery = "select COUNT(Follow.followerStoreId) as followingCount\n" +
                "from Follow, Store\n" +
                "where Follow.followingStoreId = Store.id\n" +
                "  and Store.id = ?";

        int getUserParams = storeId;
        return this.jdbcTemplate.queryForObject(getUserQuery,
                (rs, rowNum) -> (
                        rs.getInt("followingCount")
                ), // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
                getUserParams); // 한 개의 회원정보를 얻기 위한 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
    }


    // 상점에 따른 판매중인 상품 조회
    public List<GetStoreSaleRes> getStoreSale (int storeId) {
        String getUserQuery = "select Product.dealStatus, Product.imageUrl01, Product.title, Product.price from Product where dealStatus = \"sale\" and Product.storeId = ?";
        int getUserParams = storeId;
        return this.jdbcTemplate.query(getUserQuery,
                (rs, rowNum) -> new GetStoreSaleRes(
                        rs.getString("dealStatus"),
                        rs.getString("imageUrl01"),
                        rs.getString("title"),
                        rs.getInt("price")
                        ), // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
                getUserParams); // 한 개의 회원정보를 얻기 위한 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
    }
    // 상점에 따른 예약중인 상품 조회
    public List<GetStoreReservedRes> getStoreReserved (int storeId) {
        String getUserQuery = "select Product.dealStatus, Product.imageUrl01, Product.title, Product.price from Product where dealStatus = \"reserved\" and Product.storeId = ?";
        int getUserParams = storeId;
        return this.jdbcTemplate.query(getUserQuery,
                (rs, rowNum) -> new GetStoreReservedRes(
                        rs.getString("dealStatus"),
                        rs.getString("imageUrl01"),
                        rs.getString("title"),
                        rs.getInt("price")
                        ), // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
                getUserParams); // 한 개의 회원정보를 얻기 위한 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
    }
    // 상점에 따른 예약중인 상품 조회
    public List<GetStoreClosedRes> getStoreClosed (int storeId) {
        String getUserQuery = "select Product.dealStatus, Product.imageUrl01, Product.title, Product.price from Product where dealStatus = \"closed\" and Product.storeId = ?";
        int getUserParams = storeId;
        return this.jdbcTemplate.query(getUserQuery,
                (rs, rowNum) -> new GetStoreClosedRes(
                        rs.getString("dealStatus"),
                        rs.getString("imageUrl01"),
                        rs.getString("title"),
                        rs.getInt("price")
                        ), // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
                getUserParams); // 한 개의 회원정보를 얻기 위한 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
    }
    // 상점 상세 정보 조회
    public GetStoreDetailRes getStoreDetail (int storeId) {
        String getUserQuery = "select Store.storeName, Store.profileImgUrl, round(avg(Review.star)) as star, Store.contactTime, Store.introduce, Store.policy, Store.precautions\n" +
                "from Store, Review\n" +
                "where Review.sellerStoreId = Store.id\n" +
                "    and Store.id = ?";
        int getUserParams = storeId;
        return this.jdbcTemplate.queryForObject(getUserQuery,
                (rs, rowNum) -> new GetStoreDetailRes(
                        rs.getString("storeName"),
                        rs.getString("profileImgUrl"),
                        rs.getInt("star"),
                        rs.getString("contactTime"),
                        rs.getString("introduce"),
                        rs.getString("policy"),
                        rs.getString("precautions")
                        ), // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
                getUserParams); // 한 개의 회원정보를 얻기 위한 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
    }

    // 상점 정보 변경
    public int modifyStore(int storeId, PatchStoreDetailReq patchStoreDetailReq) {
        String modifyUserNameQuery = "update Store set storeName = ?, profileImgUrl = ?, contactTime = ?, introduce = ?, policy = ?, precautions = ?\n" +
                "             where id = ?"; // 해당 userIdx를 만족하는 User를 해당 nickname으로 변경한다.
        Object[] modifyUserNameParams = new Object[]{patchStoreDetailReq.getStoreName(), patchStoreDetailReq.getProfileImgUrl(), patchStoreDetailReq.getContactTime(), patchStoreDetailReq.getIntroduce(), patchStoreDetailReq.getPolicy(), patchStoreDetailReq.getPrecautions(), storeId};
        return this.jdbcTemplate.update(modifyUserNameQuery, modifyUserNameParams); // 대응시켜 매핑시켜 쿼리 요청(생성했으면 1, 실패했으면 0)
    }

    // 상점 찜한 목록 조회
    public List<GetStoreBasketRes> getStoreBasket (int storeId) {
        String getUserQuery = "select Product.imageUrl01, Product.title, Store.profileImgUrl, Store.storeName,\n" +
                "       CASE\n" +
                "                            WHEN TIMESTAMPDIFF (MINUTE,Product.updatedAt, CURRENT_TIMESTAMP) < 60\n" +
                "                            THEN CONCAT(TIMESTAMPDIFF (MINUTE,Product.updatedAt, CURRENT_TIMESTAMP), '분 전')\n" +
                "                            WHEN TIMESTAMPDIFF(HOUR,Product.updatedAt, CURRENT_TIMESTAMP) < 24\n" +
                "                            THEN CONCAT(TIMESTAMPDIFF(HOUR,Product.updatedAt, CURRENT_TIMESTAMP), '시간 전')\n" +
                "                            WHEN TIMESTAMPDIFF(DAY,Product.updatedAt, CURRENT_TIMESTAMP)< 30\n" +
                "                            THEN CONCAT(TIMESTAMPDIFF(DAY,Product.updatedAt, CURRENT_TIMESTAMP), '일 전')\n" +
                "                            WHEN TIMESTAMPDIFF(MONTH,Product.updatedAt, CURRENT_TIMESTAMP)< 12\n" +
                "                            THEN CONCAT(TIMESTAMPDIFF(MONTH,Product.updatedAt, CURRENT_TIMESTAMP), '개월 전')\n" +
                "                            ELSE CONCAT(TIMESTAMPDIFF(YEAR,Product.updatedAt, CURRENT_TIMESTAMP ), '년 전')\n" +
                "                        END AS 'updatedAt'\n" +
                "from Product, Store, Basket\n" +
                "where Product.storeId = Store.id\n" +
                "and Basket.productId = Product.id\n" +
                "and Basket.basket = \"true\"\n" +
                "and Basket.storeId = ?";
        int getUserParams = storeId;
        return this.jdbcTemplate.query(getUserQuery,
                (rs, rowNum) -> new GetStoreBasketRes(
                        rs.getString("imageUrl01"),
                        rs.getString("title"),
                        rs.getString("profileImgUrl"),
                        rs.getString("storeName"),
                        rs.getString("updatedAt")
                ), // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
                getUserParams); // 한 개의 회원정보를 얻기 위한 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
    }

//    // 상점 찜한 목록 조회(해당 제품 프로필 이미지, 상점 이름, updatedAt)
//    public List<String> getStoreBasket2 (int storeId) {
//        String getUserQuery = "select Product.imageUrl01, Product.title\n" +
//                "from Store, Basket, Product\n" +
//                "where Basket.storeId = Store.id and Basket.productId = Product.id and basket = \"true\" and\n" +
//                "      Store.id = ?";
//        int getUserParams = storeId;
//        return this.jdbcTemplate.query(getUserQuery,
//                (rs, rowNum) -> (
//                        rs.getString("profileImgUrl")
//                ), // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
//                getUserParams); // 한 개의 회원정보를 얻기 위한 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
//    }

    // 상점 리뷰 목록 조회
    public List<GetStoreReviewRes> getStoreReview (int storeId) {
        String getUserQuery = "select Store.profileImgUrl, Store.storeName, Review.star, Review.content\n" +
                "from Review, Store\n" +
                "where Review.purchaserStoreId = Store.id and Review.sellerStoreId = ?";
        int getUserParams = storeId;
        return this.jdbcTemplate.query(getUserQuery,
                (rs, rowNum) -> new GetStoreReviewRes(
                        rs.getString("profileImgUrl"),
                        rs.getString("storeName"),
                        rs.getInt("star"),
                        rs.getString("content")
                ), // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
                getUserParams); // 한 개의 회원정보를 얻기 위한 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
    }

    // 팔로잉 목록 조회
    public List<GetStoreFollowingRes> getStoreFollowing (int storeId) {
        String getUserQuery = "select Store.profileImgUrl, Store.storeName, COUNT(Product.storeId) as productNumber\n" +
                "from Follow, Store, Product\n" +
                "where Follow.followerStoreId = Store.id\n" +
                "  and Store.id = Product.storeId\n" +
                "  and Follow.followingStoreId = ?\n" +
                "group by Store.storeName";
        int getUserParams = storeId;
        return this.jdbcTemplate.query(getUserQuery,
                (rs, rowNum) -> new GetStoreFollowingRes(
                        rs.getString("profileImgUrl"),
                        rs.getString("storeName"),
                        rs.getInt("productNumber")
                ), // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
                getUserParams); // 한 개의 회원정보를 얻기 위한 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
    }
    // 팔로워 목록 조회
    public List<GetStoreFollowerRes> getStoreFollower (int storeId) {
        String getUserQuery = "select Store.profileImgUrl, Store.storeName, COUNT(Product.storeId) as productNumber\n" +
                "from Follow, Store, Product\n" +
                "where Follow.followingStoreId = Store.id\n" +
                "  and Store.id = Product.storeId\n" +
                "  and Follow.followerStoreId = ?\n" +
                "group by Store.storeName";
        int getUserParams = storeId;
        return this.jdbcTemplate.query(getUserQuery,
                (rs, rowNum) -> new GetStoreFollowerRes(
                        rs.getString("profileImgUrl"),
                        rs.getString("storeName"),
                        rs.getInt("productNumber")
                ), // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
                getUserParams); // 한 개의 회원정보를 얻기 위한 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
    }

    // 팔로잉 상품 조회
    public List<GetStoreFollowingProductRes> getStoreFollowingProduct (int storeId) {
        String getUserQuery = "select Follow.followerStoreId as followingId, Store.storeName, Product.id as productId, Product.imageUrl01, Product.price\n" +
                "    from Follow, Store, Product\n" +
                "where Follow.followerStoreId = Store.id\n" +
                "    and Store.id = Product.storeId\n" +
                "    and Follow.followingStoreId = ?";
        int getUserParams = storeId;
        return this.jdbcTemplate.query(getUserQuery,
                (rs, rowNum) -> new GetStoreFollowingProductRes(
                        rs.getInt("followingId"),
                        rs.getString("storeName"),
                        rs.getInt("productId"),
                        rs.getString("imageUrl01"),
                        rs.getInt("price")

                ), // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
                getUserParams); // 한 개의 회원정보를 얻기 위한 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
    }
    // 팔로워 상품 조회
    public List<GetStoreFollowerProductRes> getStoreFollowerProduct (int storeId) {
        String getUserQuery = "select Follow.followingStoreId as followerId, Store.storeName, Product.id as productId, Product.imageUrl01, Product.price\n" +
                "    from Follow, Store, Product\n" +
                "where Follow.followingStoreId = Store.id\n" +
                "    and Store.id = Product.storeId\n" +
                "    and Follow.followerStoreId = ?\n";
        int getUserParams = storeId;
        return this.jdbcTemplate.query(getUserQuery,
                (rs, rowNum) -> new GetStoreFollowerProductRes(
                        rs.getInt("followerId"),
                        rs.getString("storeName"),
                        rs.getInt("productId"),
                        rs.getString("imageUrl01"),
                        rs.getInt("price")
                ), // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
                getUserParams); // 한 개의 회원정보를 얻기 위한 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
    }





//    // 이름 확인
//    public int checkName(String name) {
//        String checkPhoneQuery = "select exists(select name from Store where name = ?)";
//        String checkPhoneParams = name;
//        return this.jdbcTemplate.queryForObject(checkPhoneQuery,
//                int.class,
//                checkPhoneParams);
//    }






    // User 테이블에 존재하는 전체 유저들의 정보 조회
//    public List<GetUserRes> getUsers() {
//        String getUsersQuery = "select * from User"; //User 테이블에 존재하는 모든 회원들의 정보를 조회하는 쿼리
//        return this.jdbcTemplate.query(getUsersQuery,
//                (rs, rowNum) -> new GetUserRes(
//                        rs.getInt("userIdx"),
//                        rs.getString("nickname"),
//                        rs.getString("Email"),
//                        rs.getString("password")) // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
//        ); // 복수개의 회원정보들을 얻기 위해 jdbcTemplate 함수(Query, 객체 매핑 정보)의 결과 반환(동적쿼리가 아니므로 Parmas부분이 없음)
//    }

//    // 해당 nickname을 갖는 유저들의 정보 조회
//    public List<GetUserRes> getUsersByNickname(String nickname) {
//        String getUsersByNicknameQuery = "select * from User where nickname =?"; // 해당 이메일을 만족하는 유저를 조회하는 쿼리문
//        String getUsersByNicknameParams = nickname;
//        return this.jdbcTemplate.query(getUsersByNicknameQuery,
//                (rs, rowNum) -> new GetUserRes(
//                        rs.getInt("userIdx"),
//                        rs.getString("nickname"),
//                        rs.getString("Email"),
//                        rs.getString("password")), // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
//                getUsersByNicknameParams); // 해당 닉네임을 갖는 모든 User 정보를 얻기 위해 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
//    }




}
