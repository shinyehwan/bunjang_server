package com.example.demo.src.chat;


import com.example.demo.src.chat.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository //  [Persistence Layer에서 DAO를 명시하기 위해 사용]
@Slf4j
/**
 * DAO란?
 * 데이터베이스 관련 작업을 전담하는 클래스
 * 데이터베이스에 연결하여, 입력 , 수정, 삭제, 조회 등의 작업을 수행
 */
public class ChatDao {

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
     * 채팅방 id 리스트 조회
     */
    public List<Integer> getRoomListById(int uid) {
        String Query = "SELECT chatRoomId,storeId FROM ChatRoomStoreMap WHERE storeId= ? ";

        return this.jdbcTemplate.query(Query,
                (rs, rowNum) -> rs.getInt("chatRoomId"),
                uid);
    }


    /**
     * 채팅방 최근 메시지 정보 조회
     */
    public MessageRawInfoModel getRecentRoomInfo(int roomId) {
        try {
            String Query = "SELECT Chat.id, Chat.sendStoreId, description,mediaType, mediaDescriptionUrl, createdAt AS 'uploaded'\n" +
                    "From Chat\n" +
                    "JOIN (SELECT chatRoomId, MAX(createdAt) AS 'created'\n" +
                    "           FROM Chat\n" +
                    "           WHERE status='active' AND chatRoomId = ?\n" +
                    "           GROUP BY chatRoomId) recentDate\n" +
                    "ON Chat.createdAt=recentDate.created AND Chat.chatRoomId=recentDate.chatRoomId";
            return this.jdbcTemplate.queryForObject(Query,
                    (rs, rn) -> new MessageRawInfoModel(
                            rs.getInt("id"),
                            rs.getInt("sendStoreId"),
                            rs.getString("description"),
                            rs.getString("mediaType"),
                            rs.getString("mediaDescriptionUrl"),
                            rs.getString("uploaded")
                    ),
                    roomId);
        } catch (Exception e) {
            return new MessageRawInfoModel(0,0, "", "", "", "");
        }
    }

    /**
     * 채팅방 상대방 정보 조회
     */
    public GetChatRoomsRes getChatProfileInfo(int roomId, int uid) {
        try {
            String Query = "SELECT id, storeName, profileImgUrl FROM Store\n" +
                    "JOIN (SELECT storeId FROM ChatRoomStoreMap\n" +
                    "WHERE status='active' AND chatRoomId=? AND storeId != ?) Id\n" +
                    "ON Store.id = Id.storeId";

            return this.jdbcTemplate.queryForObject(Query,
                    (rs,rn)-> new GetChatRoomsRes(
                            roomId,
                            rs.getInt("id"),
                            rs.getString("storeName"),
                            rs.getString("profileImgUrl"),
                            "",""
                    ),roomId,uid);
        } catch (Exception e) {
            return new GetChatRoomsRes( roomId,0, "", "", "","");
        }
    }

    /**
     * 최근 채팅 메시지 조회
     */
    public List<MessageRawInfoModel> getChatHistory (int roomId, int p){
            String Query = "SELECT id, Chat.sendStoreId, description, mediaType,  mediaDescriptionUrl, createdAt From Chat\n" +
                    "WHERE status='active' AND chatRoomId=?\n" +
                    "ORDER BY createdAt DESC\n" +
                    "LIMIT ?,? ";

            return this.jdbcTemplate.query(Query,
                    (rs, rn) -> new MessageRawInfoModel(
                            rs.getInt("id"),
                            rs.getInt("sendStoreId"),
                            rs.getString("description"),
                            rs.getString("mediaType"),
                            rs.getString("mediaDescriptionUrl"),
                            rs.getString("createdAt")
                    ),
                    roomId, 20*(p-1), 20);
    }


    /**
     * 상품 정보 가져오기
     */
    public PostProductInfoRes getProductInfoMessage(int productId) {
        String Query = "SELECT id,title,imageUrl01,price,deliveryFee FROM Product\n" +
                "WHERE  status='active' AND id=?";

        return this.jdbcTemplate.queryForObject(Query,
                (rs,rn)-> new PostProductInfoRes(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("imageUrl01"),
                        rs.getInt("price"),
                        rs.getString("deliveryFee").equals("true")
                ), productId);
    }
    /**
     * 계좌 메시지 정보 가져오기
     */
    public PostAccountInfoRes getAccountInfoMessage(int productId) {
        String Query = "SELECT storeName, Product.id, Product.title, Product.price, Product.deliveryFee FROM Product\n" +
                "LEFT JOIN Store S on Product.storeId = S.id\n" +
                "WHERE  Product.status='active' AND Product.id=?";

        return this.jdbcTemplate.queryForObject(Query,
                (rs,rn)-> new PostAccountInfoRes(
                        rs.getString("storeName"),
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getInt("price"),
                        rs.getString("deliveryFee").equals("true"),
                        0
                ), productId);
    }
    /**
     * 계좌 메시지 세부정보 생성하기
     */
    public int newAccountInfoMessage(int uid, PostAccountInfoReq p) {
        String Query = "INSERT INTO Account (storeId,productId,owner,bankName,accountNum) VALUES (?,?,'"+
                p.getOwner() +"','"+
                p.getBankName() +"','"+
                p.getAccountNum() +"' ) ";
        this.jdbcTemplate.update(Query, uid, p.getProductId());

        return this.jdbcTemplate.queryForObject("SELECT last_insert_id()",
                Integer.class);
    }
    /**
     * 사용자 정보 가져오기
     */
    public String getStoreNameById(int uid){
        return this.jdbcTemplate.queryForObject(
                "SELECT storeName FROM Store\n" +
                        "WHERE  status='active' AND id=?",
                (rs,rn) -> rs.getString("storeName"),
                uid
        );
    }
    /**
     * 주소 메시지 정보 가져오기
     */
    public PostAddressInfoRes getAddressInfoMessage(int productId) {
        String Query = "SELECT Product.id, Product.title, Product.price, Product.deliveryFee FROM Product\n" +
                "WHERE  Product.status='active' AND Product.id=?";

        return this.jdbcTemplate.queryForObject(Query,
                (rs,rn)-> new PostAddressInfoRes(
                        "",
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getInt("price"),
                        rs.getString("deliveryFee").equals("true"),
                        0
                ), productId);
    }
    /**
     * 주소 메시지 세부정보 생성하기
     */
    public int newAddressInfoMessage(int uid, PostAddressInfoReq p) {
        String Query = "INSERT INTO Address (storeId,productId,name,phoneNum,address,addressDetail) VALUES (?,?,'"+
                p.getName() +"','"+
                p.getPhoneNum() +"','"+
                p.getAddress() +"','"+
                p.getAddressDetail() +"')";
        this.jdbcTemplate.update(Query, uid, p.getProductId());

        return this.jdbcTemplate.queryForObject("SELECT last_insert_id()",
                Integer.class);
    }
    /**
     * 주소 메시지 정보 가져오기
     */
    public PostDealInfoRes getDealInfoMessage(int productId) {
        String Query = "SELECT Product.id, Product.title, Product.price, Product.deliveryFee FROM Product\n" +
                "WHERE  Product.status='active' AND Product.id=?";

        return this.jdbcTemplate.queryForObject(Query,
                (rs,rn)-> new PostDealInfoRes(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getInt("price"),
                        0
                ), productId);
    }
    /**
     * 주소 메시지 세부정보 생성하기
     */
    public int newDealInfoMessage(int uid, PostDealInfoReq p) {
        String Query = "INSERT INTO DirectDeal (storeId,productId,date,location,phone) VALUES (?,?,'"+
                p.getDate() +"','"+
                p.getLocation() +"','"+
                p.getPhoneNum() +"')";
        this.jdbcTemplate.update(Query, uid, p.getProductId());

        return this.jdbcTemplate.queryForObject("SELECT last_insert_id()",
                Integer.class);
    }
    /**
     * 메시지 Object 등록하기
     */
    public void sendObjectMessage (int sendStoreId, int chatRoomId, String mediaType, String mediaDescriptionUrl) throws Exception {
        String Query = "INSERT INTO Chat (sendStoreId,chatRoomId,mediaType, mediaDescriptionUrl) VALUES (?,?,?,?)";
        if (this.jdbcTemplate.update(Query, sendStoreId,chatRoomId,mediaType,mediaDescriptionUrl)
             > 0)
            ;
        else
            throw new Exception();
    }

    /**
     * 계좌 세부정보 불러오기
     */
    public GetAccountInfoRes getAccountDetail(int messageId) {
        String Query = "SELECT status, productId, owner, bankName, accountNum FROM Account\n" +
                "WHERE id=?";
        return this.jdbcTemplate.queryForObject(Query,
                (rs,rn)-> new GetAccountInfoRes(
                        rs.getString("status").equals("active"),
                        rs.getInt("productId"),
                        "","",false, 0,
                        rs.getString("owner"),
                        rs.getString("bankName"),
                        rs.getString("accountNum")
                ),
                messageId);
    }
    /**
     * 주소 세부정보 불러오기
     */
    public GetAddressInfoRes getAddressDetail(int messageId) {
        String Query = "SELECT status, productId, address, addressDetail, name, phoneNum FROM Address\n" +
                "WHERE id=?";
        return this.jdbcTemplate.queryForObject(Query,
                (rs,rn)-> new GetAddressInfoRes(
                        rs.getString("status").equals("active"),
                        rs.getInt("productId"),
                        "","",false, 0,
                        rs.getString("address"),
                        rs.getString("addressDetail"),
                        rs.getString("name"),
                        rs.getString("phoneNum")
                ),
                messageId);
    }
    /**
     * 직거래 세부정보 불러오기
     */
    public GetDealInfoRes getDealDetail(int messageId) {
        String Query = "SELECT status, productId, date, location, phone FROM DirectDeal\n" +
                "WHERE id=?";
        return this.jdbcTemplate.queryForObject(Query,
                (rs,rn)-> new GetDealInfoRes(
                        rs.getString("status").equals("active"),
                        rs.getInt("productId"),
                        "","",0,
                        rs.getString("date"),
                        rs.getString("location"),
                        rs.getString("phone")
                ),
                messageId);
    }
    /**
     * 계좌정보 삭제
     */
    public void delAccountInfo (int messageId) {
        String Query = "UPDATE Account SET status='deleted' WHERE  status='active' AND id=?";
        this.jdbcTemplate.update(Query, messageId);
    }
    /**
     * 배송정보 삭제
     */
    public void delAddressInfo (int messageId) {
        String Query = "UPDATE Address SET status='deleted' WHERE  status='active' AND id=?";
        this.jdbcTemplate.update(Query, messageId);
    }
    /**
     * 직거래정보 삭제
     */
    public void delDealtInfo (int messageId) {
        String Query = "UPDATE DirectDeal SET status='deleted' WHERE  status='active' AND id=?";
        this.jdbcTemplate.update(Query, messageId);
    }




    /**
     * 판매자 id 가져오기
     */
    public int getStoreIdByProduct(int productId) {
        String Query = "SELECT * FROM Product\n" +
                "WHERE  status='active' AND id=?";

        return this.jdbcTemplate.queryForObject(Query,
                (rs,rn)-> rs.getInt("storeId"),
                productId);
    }
    /**
     * 새 채팅방 생성
     */
    public int openNewChatRoom(int productId) {
        this.jdbcTemplate.update("INSERT INTO ChatRoom (productId) VALUES (?)", productId);

        String lastInserIdQuery = "select last_insert_id()"; // 가장 마지막에 삽입된(생성된) id값은 가져온다.
        return this.jdbcTemplate.queryForObject(lastInserIdQuery, int.class);
    }
    /**
     * 채팅방 - 이용자 연결
     */
    public void connectChatRoom(int roomId, int uid, int storeId){
        String Query = "INSERT INTO ChatRoomStoreMap (chatRoomId, storeId) VALUES (?,?)";
        this.jdbcTemplate.update(Query,roomId,uid);
        this.jdbcTemplate.update(Query,roomId,storeId);
    }
    /**
     * 이미 있는 채팅방인지 확인
     */
    public int checkExistRoom(int uid, int storeId){
        try {
            String Query = "SELECT * FROM ChatRoomStoreMap\n" +
                    "WHERE  status='active' AND storeId= ? AND chatRoomId IN (\n" +
                    "SELECT chatRoomId FROM ChatRoomStoreMap\n" +
                    "WHERE  status='active' AND storeId= ? )";

            return this.jdbcTemplate.queryForObject(Query,
                    (rs,rn)-> rs.getInt("chatRoomId"),
                    uid, storeId
            );
        } catch (IncorrectResultSizeDataAccessException error) {
            return 0;
        }
    }

    /**
     * (Validation) 내 상품인지 확인
     */
    public void isMyProduct (int uid, int productId) {
        String Query = "SELECT * FROM Product\n" +
                "WHERE status='active'\n" +
                "AND storeId = ?\n" +
                "AND id= ?";
        this.jdbcTemplate.queryForObject(Query,
                (rs,rn)-> rs.getInt("id"),
                uid, productId
                );
    }
    /**
     * (Validation) 대화상대의 상품인지 확인
     */
    public void isYourProduct (int uid, int roomId, int productId) {
        String Query = "SELECT * FROM Product\n" +
                "WHERE status='active' AND\n" +
                "      storeId IN (SELECT storeId FROM ChatRoomStoreMap WHERE status='active' AND chatRoomId = ?)\n" +
                "AND storeId <> ?\n" +
                "AND id= ?";
        this.jdbcTemplate.queryForObject(Query,
                (rs,rn)-> rs.getInt("id"),
                roomId,uid,productId
                );
    }
    /**
     * (Validation) 나 또는 대화상대의 상품인지 확인
     */
    public void isOurProduct (int roomId, int productId) {
        String Query = "SELECT * FROM Product\n" +
                "WHERE status='active' AND\n" +
                "      storeId IN (SELECT storeId FROM ChatRoomStoreMap WHERE status='active' AND chatRoomId = ?)\n" +
                "AND id= ?";
        this.jdbcTemplate.queryForObject(Query,
                (rs,rn)-> rs.getInt("id"),
                roomId, productId
                );
    }
    /**
     * (validation) 내가 수정 가능한 데이터인지 확인 - uid, mid 일치하는지 조회
     */
    public int isModifiableAccountData(int uid, int messageId){
        try {
            String Query = "SELECT productId FROM Account\n" +
                    "WHERE status='active' AND storeId=? AND id= ?";

            return this.jdbcTemplate.queryForObject(Query,
                    (rs,rn)-> rs.getInt("productId"),
                    uid, messageId
            );
        } catch (IncorrectResultSizeDataAccessException error) {
            return 0;
        }
    }
    /**
     * (validation) 내가 수정 가능한 데이터인지 확인 - uid, mid 일치하는지 조회
     */
    public int isModifiableAddressData(int uid, int messageId){
        try {
            String Query = "SELECT productId FROM Address\n" +
                    "WHERE status='active' AND storeId=? AND id= ?";

            return this.jdbcTemplate.queryForObject(Query,
                    (rs,rn)-> rs.getInt("productId"),
                    uid, messageId
            );
        } catch (IncorrectResultSizeDataAccessException error) {
            return 0;
        }
    }
    /**
     * (validation) 내가 수정 가능한 데이터인지 확인 - uid, mid 일치하는지 조회
     */
    public int isModifiableDealData(int uid, int messageId){
        try {
            String Query = "SELECT productId FROM DirectDeal\n" +
                    "WHERE status='active' AND storeId=? AND id= ?";

            return this.jdbcTemplate.queryForObject(Query,
                    (rs,rn)-> rs.getInt("productId"),
                    uid, messageId
            );
        } catch (IncorrectResultSizeDataAccessException error) {
            return 0;
        }
    }
    /**
     * (Validation) 사용자가 접근 가능한 채팅방인지 검증
     */
    public void isAccessableRoom (int uid, int roomId){
        String Query = "SELECT * FROM ChatRoomStoreMap\n" +
                "WHERE status='active' AND storeId=? AND chatRoomId=?";
        this.jdbcTemplate.queryForObject(Query,
                (rs, rowNum) -> rs.getInt("id"),
                uid,roomId);
    }

    /**
     * 채팅방 정보 조회
     */
    public GetChatRoomInfoRes getChatRoomInfo(int uid, int roomId){
        String Query = "select distinct Store.id as storeId, Store.storeName, Chat.chatRoomId, Product.id as productId, Product.imageUrl01, Product.title, Product.price\n" +
                "from Chat,\n" +
                "     ChatRoom,\n" +
                "     ChatRoomStoreMap,\n" +
                "     Product,\n" +
                "     Store\n" +
                "where Chat.chatRoomId = ChatRoom.id\n" +
                "  and ChatRoom.productId = Product.id\n" +
                "  and ChatRoomStoreMap.chatRoomId = ChatRoom.id\n" +
                "  and ChatRoomStoreMap.storeId = Store.id\n" +
                "  and ChatRoomStoreMap.storeId not in(?)\n" +
                "  and ChatRoomStoreMap.chatRoomId = ?;";
        int param1 = uid;
        int param2 = roomId;
        return this.jdbcTemplate.queryForObject(Query,
                (rs, rn) -> new GetChatRoomInfoRes(
                        rs.getInt("storeId"),
                        rs.getString("storeName"),
                        rs.getInt("chatRoomId"),
                        rs.getInt("productId"),
                        rs.getString("imageUrl01"),
                        rs.getString("title"),
                        rs.getInt("price")
                ),
              param1, param2);
    }
    /**
     * 채팅방 정보 조회
     */
    public List<GetChatRoomMessageRes> getChatRoomMessage(int roomId){
        String Query = "select Chat.chatRoomId, Chat.sendStoreId, Chat.description, Chat.mediaType, Chat.mediaDescriptionUrl,\n" +
                "       CASE\n" +
                "            WHEN TIMESTAMPDIFF (MINUTE,Chat.createdAt, CURRENT_TIMESTAMP) < 60\n" +
                "            THEN CONCAT(TIMESTAMPDIFF (MINUTE,Chat.createdAt, CURRENT_TIMESTAMP), '분 전')\n" +
                "            WHEN TIMESTAMPDIFF(HOUR,Chat.createdAt, CURRENT_TIMESTAMP) < 24\n" +
                "            THEN CONCAT(TIMESTAMPDIFF(HOUR,Chat.createdAt, CURRENT_TIMESTAMP), '시간 전')\n" +
                "            WHEN TIMESTAMPDIFF(DAY,Chat.createdAt, CURRENT_TIMESTAMP)< 30\n" +
                "            THEN CONCAT(TIMESTAMPDIFF(DAY,Chat.createdAt, CURRENT_TIMESTAMP), '일 전')\n" +
                "            WHEN TIMESTAMPDIFF(MONTH,Chat.createdAt, CURRENT_TIMESTAMP)< 12\n" +
                "            THEN CONCAT(TIMESTAMPDIFF(MONTH,Chat.createdAt, CURRENT_TIMESTAMP), '개월 전')\n" +
                "            ELSE CONCAT(TIMESTAMPDIFF(YEAR,Chat.createdAt, CURRENT_TIMESTAMP ), '년 전')\n" +
                "        END AS 'createdAt'\n" +
                "from Chat\n" +
                "where Chat.chatRoomId = ?";
        int param2 = roomId;
        return this.jdbcTemplate.query(Query,
                (rs, rn) -> new GetChatRoomMessageRes(
                        rs.getInt("chatRoomId"),
                        rs.getInt("sendStoreId"),
                        rs.getString("description"),
                        rs.getString("mediaType"),
                        rs.getString("mediaDescriptionUrl"),
                        rs.getString("createdAt")
                ),
              param2);
    }

    // 텍스트 메시지 전송
    public PostChatMessageRes postChatMessage(int uid, int roomId, PostChatMessageReq postChatMessageReq) {
        String query = "insert into Chat (sendStoreId, chatRoomId, description) VALUES (?, ?, ?)"; // 실행될 동적 쿼리문
        Object[] params = new Object[]{uid, roomId, postChatMessageReq.getMessage()}; // 동적 쿼리의 ?부분에 주입될 값
        this.jdbcTemplate.update(query, params);
        String lastInserIdQuery = "select description as message, DATE_FORMAT(Chat.createdAt, '%p %h:%i') as createdAt\n" +
                "from Chat\n" +
                "where Chat.id = (select last_insert_id())"; // 가장 마지막에 삽입된(생성된) id값은 가져온다.
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,
                (rs, rn) -> new PostChatMessageRes(
                        rs.getString("message"),
                        rs.getString("createdAt")
                )
        );
    }

    // 이미지 파일 전송
    public PostImageRes postImageUrl(int uid, int roomId, PostImageReq postImageReq) {
        String query = "insert into Chat(sendStoreId, chatRoomId, mediaType, mediaDescriptionUrl) values(?, ?, ?, ?)"; // 실행될 동적 쿼리문
        Object[] params = new Object[]{uid, roomId, postImageReq.getType(), postImageReq.getImageUrl()}; // 동적 쿼리의 ?부분에 주입될 값
        this.jdbcTemplate.update(query, params);
        String lastInserIdQuery = "select mediaDescriptionUrl, DATE_FORMAT(Chat.createdAt, '%p %h:%i') as createdAt\n" +
                "from Chat\n" +
                "where Chat.id = (select last_insert_id())"; // 가장 마지막에 삽입된(생성된) id값은 가져온다.
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,
                (rs, rn) -> new PostImageRes(
                        rs.getString("mediaDescriptionUrl"),
                        rs.getString("createdAt")
                ));
    }

    // 이모티콘 조회
    public List<GetEmoticonListRes> getEmoticonList(int roomId) {
        String query = "select * from Emoticon";
        int param = roomId;
        return this.jdbcTemplate.query(query,
                (rs, rn) -> new GetEmoticonListRes(
                        rs.getInt("id"),
                        rs.getString("emoticon")
                ));
    }

    // 이모티콘 전송
    public PostEmoticonRes postEmoticonUrl(int uid, int roomId, PostEmoticonReq postEmoticonReq) {
        String query1 = "select Emoticon.emoticon as emoticonUrl, Emoticon.createdAt\n" +
                "from Emoticon\n" +
                "where Emoticon.id = ?";// 실행될 동적 쿼리문
        int param1 = postEmoticonReq.getEmoticonId();
        PostEmoticonRes postEmoticonUrl = this.jdbcTemplate.queryForObject(query1,
                (rs, rn) -> new PostEmoticonRes(
                        rs.getString("emoticonUrl"),
                        rs.getString("createdAt")),
                param1);


        String query2 = "insert into Chat(sendStoreId, chatRoomId, emoticonId, mediaType, mediaDescriptionUrl) values(?, ?, ?, ?, ?)";
        Object[] param2 = new Object[]{uid, roomId, postEmoticonReq.getEmoticonId(), postEmoticonReq.getType(), postEmoticonUrl.getEmoticonUrl()}; // 동적 쿼리의 ?부분에 주입될 값
        this.jdbcTemplate.update(query2, param2);

        String query3 = "select Chat.mediaDescriptionUrl, DATE_FORMAT(Chat.createdAt, '%p %h:%i') as createdAt\n" +
                "from Chat\n" +
                "where Chat.id = (last_insert_id())";
        return this.jdbcTemplate.queryForObject(query3,
                (rs, rn) -> new PostEmoticonRes(
                        rs.getString("mediaDescriptionUrl"),
                        rs.getString("createdAt")
                       )
                );

//
//
//        String query3 = "update Chat set mediaDescriptionUrl = ? where Chat.id = (select last_insert_id())";
//        Object[] param3 = new Object[]{selectPostEmoticonUrl.getEmoticonUrl()};
//        this.jdbcTemplate.update(query3, param3);


    }




//    // 회원가입
//    public int createUser(PostUserReq postUserReq) {
//        String createUserQuery = "insert into Store (name, birth, gender, phone, password) VALUES (?,?,?,?,?)"; // 실행될 동적 쿼리문
//        Object[] createUserParams = new Object[]{postUserReq.getName(), postUserReq.getBirth(), postUserReq.getGender(), postUserReq.getPhone(), postUserReq.getPassword()}; // 동적 쿼리의 ?부분에 주입될 값
//        this.jdbcTemplate.update(createUserQuery, createUserParams);
//
//        String lastInserIdQuery = "select last_insert_id()"; // 가장 마지막에 삽입된(생성된) id값은 가져온다.
//        return this.jdbcTemplate.queryForObject(lastInserIdQuery, int.class); // 해당 쿼리문의 결과 마지막으로 삽인된 유저의 userIdx번호를 반환한다.
//    }
//
//    // 핸드폰 확인
//    public int checkPhone(String phone) {
//        String checkPhoneQuery = "select exists(select phone from Store where phone = ?)";
//        String checkPhoneParams = phone;
//        return this.jdbcTemplate.queryForObject(checkPhoneQuery,
//                int.class,
//                checkPhoneParams);
//    }
//
//    // 이름 확인
//    public int checkName(String name) {
//        String checkPhoneQuery = "select exists(select name from Store where name = ?)";
//        String checkPhoneParams = name;
//        return this.jdbcTemplate.queryForObject(checkPhoneQuery,
//                int.class,
//                checkPhoneParams);
//    }
//
//    // 회원정보 변경
//    public int modifyUserName(PatchUserReq patchUserReq) {
//        String modifyUserNameQuery = "update User set nickname = ? where userIdx = ? "; // 해당 userIdx를 만족하는 User를 해당 nickname으로 변경한다.
//        Object[] modifyUserNameParams = new Object[]{patchUserReq.getNickname(), patchUserReq.getUserIdx()}; // 주입될 값들(nickname, userIdx) 순
//        return this.jdbcTemplate.update(modifyUserNameQuery, modifyUserNameParams); // 대응시켜 매핑시켜 쿼리 요청(생성했으면 1, 실패했으면 0)
//    }
//
//
//    // 로그인
//    public User getPwd(PostLoginReq postLoginReq) {
//        String getPwdQuery = "select id, name, birth, gender, phone, password from Store where phone = ?";
//        String getPwdParams = postLoginReq.getPhone();
//
//        return this.jdbcTemplate.queryForObject(getPwdQuery,
//                (rs, rowNum) -> new User(
//                        rs.getInt("id"),
//                        rs.getString("name"),
//                        rs.getString("birth"),
//                        rs.getString("gender"),
//                        rs.getString("phone"),
//                        rs.getString("password")
//                ), // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
//                getPwdParams
//        ); // 한 개의 회원정보를 얻기 위한 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
//    }
//
//    // User 테이블에 존재하는 전체 유저들의 정보 조회
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
//
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
//
//    // 해당 userIdx를 갖는 유저조회
//    public GetUserRes getUser(int userIdx) {
//        String getUserQuery = "select * from User where userIdx = ?"; // 해당 userIdx를 만족하는 유저를 조회하는 쿼리문
//        int getUserParams = userIdx;
//        return this.jdbcTemplate.queryForObject(getUserQuery,
//                (rs, rowNum) -> new GetUserRes(
//                        rs.getInt("userIdx"),
//                        rs.getString("nickname"),
//                        rs.getString("Email"),
//                        rs.getString("password")), // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
//                getUserParams); // 한 개의 회원정보를 얻기 위한 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
//    }
}
