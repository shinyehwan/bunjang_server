package com.example.demo.src.chat;


import com.example.demo.config.BaseException;
import com.example.demo.src.chat.model.*;
import com.example.demo.config.BaseException;
import com.example.demo.src.chat.model.PostChatMessageReq;
import com.example.demo.src.chat.model.PostChatMessageRes;
import com.example.demo.utils.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.*;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

/**
 * Service란?
 * Controller에 의해 호출되어 실제 비즈니스 로직과 트랜잭션을 처리: Create, Update, Delete 의 로직 처리
 * 요청한 작업을 처리하는 관정을 하나의 작업으로 묶음
 * dao를 호출하여 DB CRUD를 처리 후 Controller로 반환
 */
@Service    // [Business Layer에서 Service를 명시하기 위해서 사용] 비즈니스 로직이나 respository layer 호출하는 함수에 사용된다.
            // [Business Layer]는 컨트롤러와 데이터 베이스를 연결
public class ChatService {
    final Logger logger = LoggerFactory.getLogger(this.getClass()); // Log 처리부분: Log를 기록하기 위해 필요한 함수입니다.

    // *********************** 동작에 있어 필요한 요소들을 불러옵니다. *************************
    private final ChatDao chatDao;
    private final ChatProvider chatProvider;
    private final JwtService jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!


    @Autowired //readme 참고
    public ChatService(ChatDao chatDao, ChatProvider chatProvider, JwtService jwtService) {
        this.chatDao = chatDao;
        this.chatProvider = chatProvider;
        this.jwtService = jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!

    }
    public PostChatMessageRes postChatMessage(int uid, int roomId, PostChatMessageReq postChatMessageReq) throws BaseException {

//        if (chatProvider.checkPhone(postUserReq.getPhone()) == 1) {
//            throw new BaseException(POST_USERS_EXISTS_USER);
//        }
        try {
            int chatId = chatDao.postChatMessage(uid, roomId, postChatMessageReq);
            return new PostChatMessageRes(chatId);
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }

    }


    /**
     * 상품정보 전송
     */
    public PostProductInfoRes sendProcutInfoMessage(int uid, int roomId, int productId) throws BaseException {
        try {
            // 보낼 수 있는 상품인지 확인 : 내 상품 혹은 상대방의 상품인지?
            if (!isOurProduct(roomId, productId))
                throw new BaseException(SEND_NOT_PERMITTED); // 3401|이용자가 전송할 수 없는 상품입니다.
            // todo: 대표상품 바꾸기!!
            // todo: 존재하는 상품인지 확인

            // 상품 정보 가져오기
            PostProductInfoRes result;
            try {
                result = chatDao.getProductInfoMessage(productId);
            } catch (Exception exception) {
                throw new BaseException(INVALID_PRODUCT_ID); // 3301|존재하지 않는 상품입니다.
            }

            // 상품 정보 json으로 바꿔서 DB에 저장하기
            ObjectMapper mapper = new ObjectMapper();
            String productInfoJson = mapper.writeValueAsString(result);
            chatDao.sendObjectMessage(uid, roomId, "Object_product", productInfoJson);

            // 정보 반환하기 -> getProductInfoMessage(int productId);
            return result;

        } catch (BaseException exception) {
            throw exception;
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 계좌정보 전송
     */
    public PostAccountInfoRes sendAccountInfoMessage(int uid, int roomId, PostAccountInfoReq pInfo) throws BaseException {
        try {
            // 보낼 수 있는 상품인지 확인 : 내 상품 혹은 상대방의 상품인지?
            if (!isMyProduct(uid, pInfo.getProductId()))
                throw new BaseException(SEND_NOT_PERMITTED); // 3401|이용자가 전송할 수 없는 상품입니다.
            // todo: 존재하는 상품인지 확인
            // todo: 입력값 리젝스


            // 상품 정보 가져오기
            PostAccountInfoRes result;
            try {
                result = chatDao.getAccountInfoMessage(pInfo.getProductId());
            } catch (Exception exception) {
                throw new BaseException(INVALID_PRODUCT_ID); // 3301|존재하지 않는 상품입니다.
            }

            // 계좌 데이터 생성하기 -> id 반환
            int lastInsertId = chatDao.newAccountInfoMessage(uid, pInfo);
            result.setAccountInfoId(lastInsertId);

            // 계좌 정보 json으로 바꿔서 DB에 저장하기
            ObjectMapper mapper = new ObjectMapper();
            String productInfoJson = mapper.writeValueAsString(result);
            chatDao.sendObjectMessage(uid, roomId, "Object_account", productInfoJson);

            // 정보 반환하기 -> getProductInfoMessage(int productId);
            return result;
        } catch (BaseException exception) {
            throw exception;
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 주소정보 전송
     */
    public PostAddressInfoRes sendAddressInfoMessage(int uid, int roomId, PostAddressInfoReq pInfo) throws BaseException {
        try {
            // 보낼 수 있는 상품인지 확인 : 내 상품 혹은 상대방의 상품인지?
            if (!isYourProduct(uid, roomId, pInfo.getProductId()))
                throw new BaseException(SEND_NOT_PERMITTED); // 3401|이용자가 전송할 수 없는 상품입니다.
            // todo: 존재하는 상품인지 확인
            // todo: 입력값 리젝스


            // 상품 정보 가져오기
            PostAddressInfoRes result;
            try {
                result = chatDao.getAddressInfoMessage(pInfo.getProductId());
            } catch (Exception exception) {
                throw new BaseException(INVALID_PRODUCT_ID); // 3301|존재하지 않는 상품입니다.
            }

            // 사용자 이름 가져오기
            result.setStoreName(chatDao.getStoreNameById(uid));

            // 계좌 데이터 생성하기 -> id 반환
            int lastInsertId = chatDao.newAddressInfoMessage(uid, pInfo);
            result.setAddressInfoId(lastInsertId);

            // 계좌 정보 json으로 바꿔서 DB에 저장하기
            ObjectMapper mapper = new ObjectMapper();
            String productInfoJson = mapper.writeValueAsString(result);
            chatDao.sendObjectMessage(uid, roomId, "Object_address", productInfoJson);

            // 정보 반환하기 -> getProductInfoMessage(int productId);
            return result;
        } catch (BaseException exception) {
            throw exception;
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 직거래정보 전송
     */
    public PostDealInfoRes sendDealInfoMessage(int uid, int roomId, PostDealInfoReq pInfo) throws BaseException {
        try {
            // 보낼 수 있는 상품인지 확인 : 내 상품 혹은 상대방의 상품인지?
            if (!isOurProduct(roomId, pInfo.getProductId()))
                throw new BaseException(SEND_NOT_PERMITTED); // 3401|이용자가 전송할 수 없는 상품입니다.
            // todo: 존재하는 상품인지 확인
            // todo: 입력값 리젝스


            // 상품 정보 가져오기
            PostDealInfoRes result;
            try {
                result = chatDao.getDealInfoMessage(pInfo.getProductId());
            } catch (Exception exception) {
                throw new BaseException(INVALID_PRODUCT_ID); // 3301|존재하지 않는 상품입니다.
            }

            // 계좌 데이터 생성하기 -> id 반환
            int lastInsertId = chatDao.newDealInfoMessage(uid, pInfo);
            result.setDealInfoId(lastInsertId);

            // 계좌 정보 json으로 바꿔서 DB에 저장하기
            ObjectMapper mapper = new ObjectMapper();
            String productInfoJson = mapper.writeValueAsString(result);
            chatDao.sendObjectMessage(uid, roomId, "Object_address", productInfoJson);

            // 정보 반환하기 -> getProductInfoMessage(int productId);
            return result;
        } catch (BaseException exception) {
            throw exception;
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 계좌정보 조회하기
     */
    public GetAccountInfoRes viewAccountInfoMessage(int uid, int roomId, int messageId) throws BaseException {
        try {
            // todo: validation 내가 조회할 수 있는 정보인지? -> (uid, storeId, roomId) -> 같은 방에 있는지 확인!

            // 계좌정보 불러오기
            GetAccountInfoRes result;
            try {
                result = chatDao.getAccountDetail(messageId);
            } catch (IncorrectResultSizeDataAccessException error) {
                logger.error(error.getMessage());
                throw new BaseException(INVALID_MESSAGE_ID); //3403|존재하지 않는 메시지 아이디입니다.
            }

            // 상품 정보 가져오기
            PostProductInfoRes productInfo;
            try {
                productInfo = chatDao.getProductInfoMessage(result.getProductId());
            } catch (Exception exception) {
                throw new BaseException(INVALID_PRODUCT_ID); // 3301|존재하지 않는 상품입니다.
            }

            result.setProductName(productInfo.getName());
            result.setProductImageUrl(productInfo.getImageUrl());
            result.setPrice(productInfo.getPrice());
            result.setDeliveryFee(productInfo.isDeliveryFee());

            // status 확인 -> 취소된 거래일 경우 정보 보내지 x
            if (!result.isStatus()) {
                result.setOwner("");
                result.setBankName("");
                result.setAccountNum("");
                return result;
            } else
                return result;
        } catch (BaseException exception) {
            throw exception;
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 주소정보 조회하기
     */
    public GetAddressInfoRes viewAddressInfoMessage(int uid, int roomId, int messageId) throws BaseException {
        try {
            // todo: validation 내가 조회할 수 있는 정보인지? -> (uid, storeId, roomId) -> 같은 방에 있는지 확인!

            // 주소정보 불러오기
            GetAddressInfoRes result;
            try {
                result = chatDao.getAddressDetail(messageId);
            } catch (IncorrectResultSizeDataAccessException error) {
                logger.error(error.getMessage());
                throw new BaseException(INVALID_MESSAGE_ID); //3403|존재하지 않는 메시지 아이디입니다.
            }

            // 상품 정보 가져오기
            PostProductInfoRes productInfo;
            try {
                productInfo = chatDao.getProductInfoMessage(result.getProductId());
            } catch (Exception exception) {
                throw new BaseException(INVALID_PRODUCT_ID); // 3301|존재하지 않는 상품입니다.
            }

            result.setProductName(productInfo.getName());
            result.setProductImageUrl(productInfo.getImageUrl());
            result.setPrice(productInfo.getPrice());
            result.setDeliveryFee(productInfo.isDeliveryFee());

            // status 확인 -> 취소된 거래일 경우 정보 보내지 x
            if (!result.isStatus()) {
                result.setAddress("");
                result.setAddressDetail("");
                result.setName("");
                result.setPhoneNum("");
                return result;
            } else
                return result;
        } catch (BaseException exception) {
            throw exception;
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 직거래정보 조회하기
     */
    public GetDealInfoRes viewDealInfoMessage(int uid, int roomId, int messageId) throws BaseException {
        try {
            // todo: validation 내가 조회할 수 있는 정보인지? -> (uid, storeId, roomId) -> 같은 방에 있는지 확인!

            // 계좌정보 불러오기
            GetDealInfoRes result;
            try {
                result = chatDao.getDealDetail(messageId);
            } catch (IncorrectResultSizeDataAccessException error) {
                logger.error(error.getMessage());
                throw new BaseException(INVALID_MESSAGE_ID); //3403|존재하지 않는 메시지 아이디입니다.
            }

            // 상품 정보 가져오기
            PostProductInfoRes productInfo;
            try {
                productInfo = chatDao.getProductInfoMessage(result.getProductId());
            } catch (Exception exception) {
                throw new BaseException(INVALID_PRODUCT_ID); // 3301|존재하지 않는 상품입니다.
            }

            result.setProductName(productInfo.getName());
            result.setProductImageUrl(productInfo.getImageUrl());
            result.setPrice(productInfo.getPrice());

            // status 확인 -> 취소된 거래일 경우 정보 보내지 x
            if (!result.isStatus()) {
                result.setDate("");
                result.setLocation("");
                result.setPhoneNum("");
                return result;
            } else
                return result;


        } catch (BaseException exception) {
            throw exception;
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 계좌정보 거래 취소하기
     */
    public PatchCancelRes delAccountInfoMessage(int uid, int roomId, int messageId) throws BaseException {
        try {
            // (validation) 내가 취소 가능한 데이터인지 확인 - uid, mid 일치하는지 조회
            int productId = chatDao.isModifiableAccountData(uid,messageId);
            if (productId == 0 )
                throw new BaseException(MODIFY_NOT_PERMITTED); // 3403|이용자가 수정할 수 없는 메시지입니다.

            // 해당 데이터 찾아서 active -> deleted 바꿈
            chatDao.delAccountInfo(messageId);

            // 상품 정보 가져오기
            PostProductInfoRes productInfo;
            try {
                productInfo = chatDao.getProductInfoMessage(productId);
            } catch (Exception exception) {
                productInfo = new PostProductInfoRes(productId, "삭제된 상품입니다.", "", 0, false);
            }

            // cancel 데이터 만들기
            PatchCancelRes result = new PatchCancelRes("Object_account",productId, productInfo.getName(), messageId);
            // 계좌 정보 json으로 바꿔서 DB에 저장하기
            ObjectMapper mapper = new ObjectMapper();
            String productInfoJson = mapper.writeValueAsString(result);
            chatDao.sendObjectMessage(uid, roomId, "Object_cancel", productInfoJson);

            // 정보 반환하기 -> getProductInfoMessage(int productId);
            return result;
        } catch (BaseException exception) {
            throw exception;
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }
    /**
     * 배송정보 거래 취소하기
     */
    public PatchCancelRes delAddressInfoMessage(int uid, int roomId, int messageId) throws BaseException {
        try {
            // (validation) 내가 취소 가능한 데이터인지 확인 - uid, mid 일치하는지 조회
            int productId = chatDao.isModifiableAddressData(uid,messageId);
            if (productId == 0 )
                throw new BaseException(MODIFY_NOT_PERMITTED); // 3403|이용자가 수정할 수 없는 메시지입니다.

            // 해당 데이터 찾아서 active -> deleted 바꿈
            chatDao.delAddressInfo(messageId);

            // 상품 정보 가져오기
            PostProductInfoRes productInfo;
            try {
                productInfo = chatDao.getProductInfoMessage(productId);
            } catch (Exception exception) {
                productInfo = new PostProductInfoRes(productId, "삭제된 상품입니다.", "", 0, false);
            }

            // cancel 데이터 만들기
            PatchCancelRes result = new PatchCancelRes("Object_address",productId, productInfo.getName(), messageId);
            // 계좌 정보 json으로 바꿔서 DB에 저장하기
            ObjectMapper mapper = new ObjectMapper();
            String productInfoJson = mapper.writeValueAsString(result);
            chatDao.sendObjectMessage(uid, roomId, "Object_cancel", productInfoJson);

            // 정보 반환하기 -> getProductInfoMessage(int productId);
            return result;
        } catch (BaseException exception) {
            throw exception;
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }
    /**
     * 계좌정보 거래 취소하기
     */
    public PatchCancelRes delDealInfoMessage(int uid, int roomId, int messageId) throws BaseException {
        try {
            // (validation) 내가 취소 가능한 데이터인지 확인 - uid, mid 일치하는지 조회
            int productId = chatDao.isModifiableDealData(uid,messageId);
            if (productId == 0 )
                throw new BaseException(MODIFY_NOT_PERMITTED); // 3403|이용자가 수정할 수 없는 메시지입니다.

            // 해당 데이터 찾아서 active -> deleted 바꿈
            chatDao.delDealtInfo(messageId);

            // 상품 정보 가져오기
            PostProductInfoRes productInfo;
            try {
                productInfo = chatDao.getProductInfoMessage(productId);
            } catch (Exception exception) {
                productInfo = new PostProductInfoRes(productId, "삭제된 상품입니다.", "", 0, false);
            }

            // cancel 데이터 만들기
            PatchCancelRes result = new PatchCancelRes("Object_deal",productId, productInfo.getName(), messageId);
            // 계좌 정보 json으로 바꿔서 DB에 저장하기
            ObjectMapper mapper = new ObjectMapper();
            String productInfoJson = mapper.writeValueAsString(result);
            chatDao.sendObjectMessage(uid, roomId, "Object_cancel", productInfoJson);

            // 정보 반환하기 -> getProductInfoMessage(int productId);
            return result;
        } catch (BaseException exception) {
            throw exception;
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new BaseException(DATABASE_ERROR);
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
//
//    // 회원정보 수정(Patch)
//    public void modifyUserName(PatchUserReq patchUserReq) throws BaseException {
//        try {
//            int result = chatDao.modifyUserName(patchUserReq); // 해당 과정이 무사히 수행되면 True(1), 그렇지 않으면 False(0)입니다.
//            if (result == 0) { // result값이 0이면 과정이 실패한 것이므로 에러 메서지를 보냅니다.
//                throw new BaseException(MODIFY_FAIL_USERNAME);
//            }
//        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }
}
