package com.example.demo.src.chat;

import com.example.demo.config.BaseException;
import com.example.demo.src.chat.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
 */ public class ChatProvider {


    // *********************** 동작에 있어 필요한 요소들을 불러옵니다. *************************
    private final ChatDao chatDao;
    private final JwtService jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired //readme 참고
    public ChatProvider(ChatDao chatDao, JwtService jwtService) {
        this.chatDao = chatDao;
        this.jwtService = jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!
    }

    /**
     * 채팅방 목록 조회
     */
    public List<GetChatRoomsRes> getChatRoomList(int uid) throws BaseException {
        try {
            // 채팅방 id 리스트 조회
            List<Integer> idList = chatDao.getRoomListById(uid);

            // 각 채팅방 정보 조회
            List<GetChatRoomsRes> roomList = new ArrayList<>();

            for (int roomId : idList) {
                // 각 대화방의 가장 최신 메시지 정보 조회
                MessageRawInfoModel messageRawInfoModel = chatDao.getRecentRoomInfo(roomId);

                // 대화 상대방의 프로필정보 조회
                GetChatRoomsRes getChatRoomsRes = chatDao.getChatProfileInfo(roomId, uid);

                // 메시지 정보 조작
                String message;
                if (messageRawInfoModel.getDescription() != null)
                    message = messageRawInfoModel.getDescription(); // 텍스트 메시지는 내용 그대로 전송
                else {
                    // 텍스트가 아닌 객체는 종류에 따라 전송
                    // TODO
                    message = messageRawInfoModel.getMediaType() + messageRawInfoModel.getMediaDescriptionUrl();
                }

                // 대화 상대방 정보가 없다면 패스
                if (getChatRoomsRes.getCallerId() != 0) {
                    getChatRoomsRes.setLastMessage(message);
                    getChatRoomsRes.setLastMessageTime(messageRawInfoModel.getUploaded());

                    roomList.add(getChatRoomsRes);
                }
            }
            return roomList;
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            logger.error(exception.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }


    /**
     * 최근 채팅 메시지 조회
     */
    public List<GetChatRes> getChatHistory(int roomId, int uid, int p) throws BaseException {
        try {
            List<MessageRawInfoModel> messageRawInfoModels = chatDao.getChatHistory(roomId, p);
            List<GetChatRes> getChatResList = new ArrayList<>();


            // 대화 상대방의 프로필정보 조회
            GetChatRoomsRes callerInfo = chatDao.getChatProfileInfo(roomId, uid);

            for (MessageRawInfoModel m : messageRawInfoModels) {

                // 내용 조작
                String description;
                if (m.getDescription() != null) {
                    m.setMediaType("text");
                    description = m.getDescription();
                } else if (m.getMediaType().equals("image")) {
                    description = m.getMediaDescriptionUrl();
                } else {
                    // TODO
                    description = "아직 개발되지 않은 객체입니다.";
                }

                GetChatRes g = new GetChatRes(
                        m.getChatId(),
                        m.getSendStoreId(),
                        callerInfo.getThumbnailImgUrl(),
                        m.getMediaType(),
                        description,
                        m.getUploaded()
                );
                getChatResList.add(g);
            }

            return getChatResList;
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            logger.error(exception.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }


    /**
     * (Validation) 사용자가 접근 가능한 채팅방인지 검증
     */
    public boolean isAccessableRoom(int uid, int roomId) {
        try {
            chatDao.isAccessableRoom(uid, roomId);
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
    }


    /**
     * (Validation) 내 상품인지 확인
     */
    public boolean isMyProduct(int uid, int productId) {
        try {
            chatDao.isMyProduct(uid, productId);
            return true;
        } catch (IncorrectResultSizeDataAccessException error) {
            return false;
        }
    }

    /**
     * (Validation) 대화상대의 상품인지 확인
     */
    public boolean isYourProduct(int uid, int roomId, int productId) {
        try {
            chatDao.isYourProduct(uid, roomId, productId);
            return true;
        } catch (IncorrectResultSizeDataAccessException error) {
            return false;
        }
    }

    /**
     * (Validation) 나 또는 대화상대의 상품인지 확인
     */
    public boolean isOurProduct(int roomId, int productId) {
        try {
            chatDao.isOurProduct(roomId, productId);
            return true;
        } catch (IncorrectResultSizeDataAccessException error) {
            return false;
        }
    }
    @Transactional(rollbackFor = BaseException.class)
    public GetChatRoomInfoRes getChatRoomInfo(int uid, int roomId) throws BaseException {
        try {
            GetChatRoomInfoRes getChatRoomInfoRes = chatDao.getChatRoomInfo(uid, roomId);
            return getChatRoomInfoRes;
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    @Transactional(rollbackFor = BaseException.class)
    public List<GetChatRoomMessageRes> getChatRoomMessage(int roomId) throws BaseException {
        try {
            List<GetChatRoomMessageRes> getChatRoomMessageRes = chatDao.getChatRoomMessage(roomId);
            return getChatRoomMessageRes;
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 이모티콘 리스트 조회
    @Transactional(rollbackFor = BaseException.class)
    public List<GetEmoticonListRes> getEmoticonList(int roomId) throws BaseException {
        try {
            List<GetEmoticonListRes> getEmoticonListRes = chatDao.getEmoticonList(roomId);
            return getEmoticonListRes;
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }



//
//    // 로그인(password 검사)
//    public PostLoginRes logIn(PostLoginReq postLoginReq) throws BaseException {
//        User user = chatDao.getPwd(postLoginReq);
//        String password;
//        try {
//            password = new AES128(Secret.USER_INFO_PASSWORD_KEY).decrypt(user.getPassword()); // 암호화
//            // 회원가입할 때 비밀번호가 암호화되어 저장되었기 떄문에 로그인을 할때도 암호화된 값끼리 비교를 해야합니다.
//        } catch (Exception ignored) {
//            throw new BaseException(PASSWORD_DECRYPTION_ERROR);
//        }
//
//        if (postLoginReq.getPassword().equals(password)) { //비말번호가 일치한다면 userIdx를 가져온다.
//            int userIdx = chatDao.getPwd(postLoginReq).getId();
//            String jwt = jwtService.createJwt(userIdx);
//            return new PostLoginRes(userIdx, jwt);
//
//        } else { // 비밀번호가 다르다면 에러메세지를 출력한다.
//            throw new BaseException(FAILED_TO_LOGIN);
//        }
//    }
//
//    // 해당 이메일이 이미 User Table에 존재하는지 확인
//    public int checkPhone(String phone) throws BaseException {
//        try {
//            return chatDao.checkPhone(phone);
//        } catch (Exception exception) {
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }
//    public int checkName(String name) throws BaseException {
//        try {
//            return chatDao.checkName(name);
//        } catch (Exception exception) {
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }
//
//
//    // User들의 정보를 조회
//    public List<GetUserRes> getUsers() throws BaseException {
//        try {
//            List<GetUserRes> getUserRes = chatDao.getUsers();
//            return getUserRes;
//        } catch (Exception exception) {
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }
//
//    // 해당 nickname을 갖는 User들의 정보 조회
//    public List<GetUserRes> getUsersByNickname(String nickname) throws BaseException {
//        try {
//            List<GetUserRes> getUsersRes = chatDao.getUsersByNickname(nickname);
//            return getUsersRes;
//        } catch (Exception exception) {
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }
//
//
//    // 해당 userIdx를 갖는 User의 정보 조회
//    public GetUserRes getUser(int userIdx) throws BaseException {
//        try {
//            GetUserRes getUserRes = chatDao.getUser(userIdx);
//            return getUserRes;
//        } catch (Exception exception) {
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }

}
