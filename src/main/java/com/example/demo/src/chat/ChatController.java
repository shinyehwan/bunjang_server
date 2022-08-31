package com.example.demo.src.chat;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.chat.model.*;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.Verifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@RestController // Rest API 또는 WebAPI를 개발하기 위한 어노테이션. @Controller + @ResponseBody 를 합친것.
                // @Controller      [Presentation Layer에서 Contoller를 명시하기 위해 사용]
                //  [Presentation Layer?] 클라이언트와 최초로 만나는 곳으로 데이터 입출력이 발생하는 곳
                //  Web MVC 코드에 사용되는 어노테이션. @RequestMapping 어노테이션을 해당 어노테이션 밑에서만 사용할 수 있다.
                // @ResponseBody    모든 method의 return object를 적절한 형태로 변환 후, HTTP Response Body에 담아 반환.
@RequestMapping("/bungae/chat")

/**
 * Controller란?
 * 사용자의 Request를 전달받아 요청의 처리를 담당하는 Service, Prodiver 를 호출
 */
public class ChatController {

    final Logger logger = LoggerFactory.getLogger(this.getClass()); // Log를 남기기: 일단은 모르고 넘어가셔도 무방합니다.

    @Autowired
    private final ChatProvider chatProvider;
    @Autowired
    private final ChatService chatService;
    @Autowired
    private final JwtService jwtService;


    public ChatController(ChatProvider chatProvider, ChatService chatService, JwtService jwtService) {
        this.chatProvider = chatProvider;
        this.chatService = chatService;
        this.jwtService = jwtService;
    }

    // ******************************************************************************

    // 검증코드 클래스 추가
    private Verifier verifier;
    @Autowired
    public void setVerifier(Verifier verifier){
        this.verifier = verifier;
    }

    // ******************************************************************************

    /**
     * 채팅방 목록 조회
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetChatRoomsRes>> getChatRoomList() {
        try {
            // jwt 에서 uid 추출
            int uid;
            uid = jwtService.getUserIdx();
            // 존재하는 상점 아이디인지 검증
            if (!verifier.isPresentStoreId(uid))
                throw new BaseException(INVALID_STORE_ID);
//            // vid 검증
//            if (!videoProvider.verifyVideoId(vid))
//                throw new BaseException(INVALID_VIDEO_ID);
//            // 시간 형식 검증
//            if (!checkTime(postViewStartReq.getStartViewPoint()))
//                throw new BaseException(INAVLID_STARTVIEWPOINT_FORMAT);
//
            return new BaseResponse<>(chatProvider.getChatRoomList(uid));
//            throw new BaseException(REQUEST_ERROR);
//            return new BaseResponse<>(___);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * 채팅방 상품 정보 조회
     */
    @ResponseBody
    @GetMapping("/{roomId}")
    public BaseResponse<GetChatRoomInfoRes> getChatRoomInfo(
            @PathVariable("roomId") Integer roomId) {
        try {
            // jwt 에서 uid 추출
            int uid;
            uid = jwtService.getUserIdx();
            // 존재하는 상점 아이디인지 검증
            if (!verifier.isPresentStoreId(uid))
                throw new BaseException(INVALID_STORE_ID);
            // 접속 가능한 채팅방인지 검증
            if (!chatProvider.isAccessableRoom(uid, roomId))
                throw new BaseException(INVALID_ROOM_ID);
            GetChatRoomInfoRes getChatRoomInfoRes = chatProvider.getChatRoomInfo(uid, roomId);
            return new BaseResponse<>(getChatRoomInfoRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * 해당 채팅방 모든 메시지 조회
     */
    @ResponseBody
    @GetMapping("/{roomId}/content")
    public BaseResponse<List<GetChatRoomMessageRes>> getChatRoomMessage(
            @PathVariable("roomId") Integer roomId) {
        try {
            // jwt 에서 uid 추출
            int uid;
            uid = jwtService.getUserIdx();
            // 존재하는 상점 아이디인지 검증
            if (!verifier.isPresentStoreId(uid))
                throw new BaseException(INVALID_STORE_ID);
            // 접속 가능한 채팅방인지 검증
            if (!chatProvider.isAccessableRoom(uid, roomId))
                throw new BaseException(INVALID_ROOM_ID);
            List<GetChatRoomMessageRes> getChatRoomMessageRes = chatProvider.getChatRoomMessage(roomId);
            return new BaseResponse<>(getChatRoomMessageRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 최근 채팅 메시지 조회
     * [GET] /bungae/chat/:roomId/message ? p=
     */
    @ResponseBody
    @GetMapping("/{roomId}/message")
    public BaseResponse<List<GetChatRes>> getChatHistory (
            @PathVariable("roomId") Integer roomId,
            @RequestParam(required = false) Integer p) {
        try {
            // jwt 에서 uid 추출
            int uid;
            uid = jwtService.getUserIdx();
            // 존재하는 상점 아이디인지 검증
            if (!verifier.isPresentStoreId(uid))
                throw new BaseException(INVALID_STORE_ID); // /3001/존재하지 않는 상점 id 입니다.
            // 접속 가능한 채팅방인지 검증
            if (!chatProvider.isAccessableRoom(uid, roomId))
                throw new BaseException(INVALID_ROOM_ID);

            if (p == null)
                p = 1;

            return new BaseResponse<>(chatProvider.getChatHistory(roomId,uid,p));
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }
    /**
     * 텍스트 메시지 전송
     */
    @ResponseBody
    @PostMapping("/{roomId}/message")
    public BaseResponse<PostChatMessageRes> postChatRoomMessage(
            @PathVariable("roomId") Integer roomId,
            @RequestBody PostChatMessageReq postChatMessageReq) {
        try {
            // jwt 에서 uid 추출
            int uid;
            uid = jwtService.getUserIdx();
            // 존재하는 상점 아이디인지 검증
            if (!verifier.isPresentStoreId(uid))
                throw new BaseException(INVALID_STORE_ID);
            // 접속 가능한 채팅방인지 검증
            if (!chatProvider.isAccessableRoom(uid, roomId))
                throw new BaseException(INVALID_ROOM_ID);
            PostChatMessageRes postChatMessageRes = chatService.postChatMessage(uid, roomId, postChatMessageReq);
            return new BaseResponse<>(postChatMessageRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 새로운 번개톡 시작하기
     */
    @ResponseBody
    @PostMapping("/new")
    public BaseResponse<PostRoomRes> openNewChatRoom (@RequestParam Integer productId){
        try {
            // jwt 에서 uid 추출
            int uid;
            uid = jwtService.getUserIdx();
            // 존재하는 상점 아이디인지 검증
            if (!verifier.isPresentStoreId(uid))
                throw new BaseException(INVALID_STORE_ID); // /3001/존재하지 않는 상점 id 입니다.


            return new BaseResponse<>(chatService.openNewChatRoom(uid,productId));
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 이미지 전송
     */
    @ResponseBody
    @PostMapping("/{roomId}/image")
    public BaseResponse<PostImageRes> postImageRes(
            @PathVariable("roomId") Integer roomId,
            @RequestBody PostImageReq postImageReq) {
        try {
            // jwt 에서 uid 추출
            int uid;
            uid = jwtService.getUserIdx();
            // 존재하는 상점 아이디인지 검증
            if (!verifier.isPresentStoreId(uid))
                throw new BaseException(INVALID_STORE_ID);
            // 접속 가능한 채팅방인지 검증
            if (!chatProvider.isAccessableRoom(uid, roomId))
                throw new BaseException(INVALID_ROOM_ID);

            PostImageRes postImageRes = chatService.postImageUrl(uid, roomId, postImageReq);
            return new BaseResponse<>(postImageRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 이모티콘 전송
     */
    @ResponseBody
    @PostMapping("/{roomId}/emoticon")
    public BaseResponse<PostEmoticonRes> postImageRes(
            @PathVariable("roomId") Integer roomId,
            @RequestBody PostEmoticonReq postEmoticonReq) {
        try {
            // jwt 에서 uid 추출
            int uid;
            uid = jwtService.getUserIdx();
            // 존재하는 상점 아이디인지 검증
            if (!verifier.isPresentStoreId(uid))
                throw new BaseException(INVALID_STORE_ID);
            // 접속 가능한 채팅방인지 검증
            if (!chatProvider.isAccessableRoom(uid, roomId))
                throw new BaseException(INVALID_ROOM_ID);

            PostEmoticonRes postEmoticonRes = chatService.postEmoticonUrl(uid, roomId, postEmoticonReq);
            return new BaseResponse<>(postEmoticonRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 이모티콘 리스트 조회
     */
    @ResponseBody
    @GetMapping("/{roomId}/emoticon")
    public BaseResponse<List<GetEmoticonListRes>> getEmoticonList(
            @PathVariable("roomId") Integer roomId) {
        try {
            // jwt 에서 uid 추출
            int uid;
            uid = jwtService.getUserIdx();
            // 존재하는 상점 아이디인지 검증
            if (!verifier.isPresentStoreId(uid))
                throw new BaseException(INVALID_STORE_ID);
            // 접속 가능한 채팅방인지 검증
            if (!chatProvider.isAccessableRoom(uid, roomId))
                throw new BaseException(INVALID_ROOM_ID);

            List<GetEmoticonListRes> getEmoticonListRes = chatProvider.getEmoticonList(roomId);
            return new BaseResponse<>(getEmoticonListRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 물품정보 전송
     * [POST] /bungae/chat/:roomId/product
     */
    @ResponseBody
    @PostMapping("/{roomId}/product")
    public BaseResponse<PostProductInfoRes> sendProcutInfoMessage(
            @PathVariable("roomId") Integer roomId,
            @RequestBody PostProductInfoReq postProductInfoReq){
        try {
            // jwt 에서 uid 추출
            int uid;
            uid = jwtService.getUserIdx();
            // 존재하는 상점 아이디인지 검증
            if (!verifier.isPresentStoreId(uid))
                throw new BaseException(INVALID_STORE_ID); // /3001/존재하지 않는 상점 id 입니다.
            // 접속 가능한 채팅방인지 검증
            if (!chatProvider.isAccessableRoom(uid, roomId))
                throw new BaseException(INVALID_ROOM_ID);


            return new BaseResponse<>(chatService.sendProcutInfoMessage(uid,roomId, postProductInfoReq.getProductId()));
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * 계좌정보 전송
     * [POST] /bungae/chat/:roomId/account
     */
    @ResponseBody
    @PostMapping("/{roomId}/account")
    public BaseResponse<PostAccountInfoRes> sendAccountInfoMessage(
            @PathVariable("roomId") Integer roomId,
            @RequestBody PostAccountInfoReq postAccountInfoReq){
        try {
            // jwt 에서 uid 추출
            int uid;
            uid = jwtService.getUserIdx();
            // 존재하는 상점 아이디인지 검증
            if (!verifier.isPresentStoreId(uid))
                throw new BaseException(INVALID_STORE_ID); // /3001/존재하지 않는 상점 id 입니다.
            // 접속 가능한 채팅방인지 검증
            if (!chatProvider.isAccessableRoom(uid, roomId))
                throw new BaseException(INVALID_ROOM_ID);


            return new BaseResponse<>(chatService.sendAccountInfoMessage(uid, roomId, postAccountInfoReq));
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * 주소정보 전송
     * [POST] /bungae/chat/:roomId/address
     */
    @ResponseBody
    @PostMapping("/{roomId}/address")
    public BaseResponse<PostAddressInfoRes> sendAddressInfoMessage(
            @PathVariable("roomId") Integer roomId,
            @RequestBody PostAddressInfoReq postAddressInfoReq){
        try {
            // jwt 에서 uid 추출
            int uid;
            uid = jwtService.getUserIdx();
            // 존재하는 상점 아이디인지 검증
            if (!verifier.isPresentStoreId(uid))
                throw new BaseException(INVALID_STORE_ID); // /3001/존재하지 않는 상점 id 입니다.
            // 접속 가능한 채팅방인지 검증
            if (!chatProvider.isAccessableRoom(uid, roomId))
                throw new BaseException(INVALID_ROOM_ID);


            return new BaseResponse<>(chatService.sendAddressInfoMessage(uid, roomId, postAddressInfoReq));
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * 직거래정보 전송
     * [POST] /bungae/chat/:roomId/deal
     */
    @ResponseBody
    @PostMapping("/{roomId}/deal")
    public BaseResponse<PostDealInfoRes> sendDealInfoMessage(
            @PathVariable("roomId") Integer roomId,
            @RequestBody PostDealInfoReq postDealInfoReq){
        try {
            // jwt 에서 uid 추출
            int uid;
            uid = jwtService.getUserIdx();
            // 존재하는 상점 아이디인지 검증
            if (!verifier.isPresentStoreId(uid))
                throw new BaseException(INVALID_STORE_ID); // /3001/존재하지 않는 상점 id 입니다.
            // 접속 가능한 채팅방인지 검증
            if (!chatProvider.isAccessableRoom(uid, roomId))
                throw new BaseException(INVALID_ROOM_ID);

            return new BaseResponse<>(chatService.sendDealInfoMessage(uid, roomId, postDealInfoReq));
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 계좌 상세 링크 조회
     * [GET] /bungae/chat/:roomId/account ? id=
     */
    @ResponseBody
    @GetMapping("/{roomId}/account")
    public BaseResponse<GetAccountInfoRes> viewAccountInfoMessage(
            @PathVariable("roomId") Integer roomId,
            @RequestParam Integer id){
        try {
            // jwt 에서 uid 추출
            int uid;
            uid = jwtService.getUserIdx();
            // 존재하는 상점 아이디인지 검증
            if (!verifier.isPresentStoreId(uid))
                throw new BaseException(INVALID_STORE_ID); // /3001/존재하지 않는 상점 id 입니다.
            // 접속 가능한 채팅방인지 검증
            if (!chatProvider.isAccessableRoom(uid, roomId))
                throw new BaseException(INVALID_ROOM_ID);


            return new BaseResponse<>(chatService.viewAccountInfoMessage(uid, roomId, id));
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * 주소 상세 링크 조회
     * [GET] /bungae/chat/:roomId/address ? id=
     */
    @ResponseBody
    @GetMapping("/{roomId}/address")
    public BaseResponse<GetAddressInfoRes> viewAddressInfoMessage(
            @PathVariable("roomId") Integer roomId,
            @RequestParam Integer id){
        try {
            // jwt 에서 uid 추출
            int uid;
            uid = jwtService.getUserIdx();
            // 존재하는 상점 아이디인지 검증
            if (!verifier.isPresentStoreId(uid))
                throw new BaseException(INVALID_STORE_ID); // /3001/존재하지 않는 상점 id 입니다.
            // 접속 가능한 채팅방인지 검증
            if (!chatProvider.isAccessableRoom(uid, roomId))
                throw new BaseException(INVALID_ROOM_ID);


            return new BaseResponse<>(chatService.viewAddressInfoMessage(uid, roomId, id));
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }/**
     * 직거래 상세 링크 조회
     * [GET] /bungae/chat/:roomId/deal ? id=
     */
    @ResponseBody
    @GetMapping("/{roomId}/deal")
    public BaseResponse<GetDealInfoRes> viewDealInfoMessage(
            @PathVariable("roomId") Integer roomId,
            @RequestParam Integer id){
        try {
            // jwt 에서 uid 추출
            int uid;
            uid = jwtService.getUserIdx();
            // 존재하는 상점 아이디인지 검증
            if (!verifier.isPresentStoreId(uid))
                throw new BaseException(INVALID_STORE_ID); // /3001/존재하지 않는 상점 id 입니다.
            // 접속 가능한 채팅방인지 검증
            if (!chatProvider.isAccessableRoom(uid, roomId))
                throw new BaseException(INVALID_ROOM_ID);


            return new BaseResponse<>(chatService.viewDealInfoMessage(uid, roomId, id));
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * 계좌 거래 취소
     * [POST] /bungae/chat/:roomId/account/d ? id=
     */
    @ResponseBody
    @PostMapping("/{roomId}/account/d")
    public BaseResponse<PatchCancelRes> delAccountInfoMessage(
            @PathVariable("roomId") Integer roomId,
            @RequestParam Integer id){
        try {
            // jwt 에서 uid 추출
            int uid;
            uid = jwtService.getUserIdx();
            // 존재하는 상점 아이디인지 검증
            if (!verifier.isPresentStoreId(uid))
                throw new BaseException(INVALID_STORE_ID); // /3001/존재하지 않는 상점 id 입니다.
            // 접속 가능한 채팅방인지 검증
            if (!chatProvider.isAccessableRoom(uid, roomId))
                throw new BaseException(INVALID_ROOM_ID);


            return new BaseResponse<>(chatService.delAccountInfoMessage(uid, roomId, id));
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * 배송 거래 취소
     * [POST] /bungae/chat/:roomId/address/d ? id=
     */
    @ResponseBody
    @PostMapping("/{roomId}/address/d")
    public BaseResponse<PatchCancelRes> delAddressInfoMessage(
            @PathVariable("roomId") Integer roomId,
            @RequestParam Integer id){
        try {
            // jwt 에서 uid 추출
            int uid;
            uid = jwtService.getUserIdx();
            // 존재하는 상점 아이디인지 검증
            if (!verifier.isPresentStoreId(uid))
                throw new BaseException(INVALID_STORE_ID); // /3001/존재하지 않는 상점 id 입니다.
            // 접속 가능한 채팅방인지 검증
            if (!chatProvider.isAccessableRoom(uid, roomId))
                throw new BaseException(INVALID_ROOM_ID);


            return new BaseResponse<>(chatService.delAddressInfoMessage(uid, roomId, id));
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * 직거래 거래 취소
     * [POST] /bungae/chat/:roomId/deal/d ? id=
     */
    @ResponseBody
    @PostMapping("/{roomId}/deal/d")
    public BaseResponse<PatchCancelRes> delDealInfoMessage(
            @PathVariable("roomId") Integer roomId,
            @RequestParam Integer id){
        try {
            // jwt 에서 uid 추출
            int uid;
            uid = jwtService.getUserIdx();
            // 존재하는 상점 아이디인지 검증
            if (!verifier.isPresentStoreId(uid))
                throw new BaseException(INVALID_STORE_ID); // /3001/존재하지 않는 상점 id 입니다.
            // 접속 가능한 채팅방인지 검증
            if (!chatProvider.isAccessableRoom(uid, roomId))
                throw new BaseException(INVALID_ROOM_ID);


            return new BaseResponse<>(chatService.delDealInfoMessage(uid, roomId, id));
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
//    /**
//     * 계좌 거래 취소
//     * [PATCH] /bungae/chat/:roomId/account ? id=
//     */
//    @ResponseBody
//    @PatchMapping("/{roomId}/account")
//    public BaseResponse<PatchCancelRes> delAccountInfoMessage(
//            @PathVariable("roomId") Integer roomId,
//            @RequestParam Integer id){
//        try {
//            // jwt 에서 uid 추출
//            int uid;
//            uid = jwtService.getUserIdx();
//            // 존재하는 상점 아이디인지 검증
//            if (!verifier.isPresentStoreId(uid))
//                throw new BaseException(INVALID_STORE_ID); // /3001/존재하지 않는 상점 id 입니다.
//            // 접속 가능한 채팅방인지 검증
//            if (!chatProvider.isAccessableRoom(uid, roomId))
//                throw new BaseException(INVALID_ROOM_ID);
//
//
//            return new BaseResponse<>(chatService.delAccountInfoMessage(uid, roomId, id));
//        } catch (BaseException exception) {
//            return new BaseResponse<>((exception.getStatus()));
//        }
//    }
//    /**
//     * 배송 거래 취소
//     * [PATCH] /bungae/chat/:roomId/address ? id=
//     */
//    @ResponseBody
//    @PatchMapping("/{roomId}/address")
//    public BaseResponse<PatchCancelRes> delAddressInfoMessage(
//            @PathVariable("roomId") Integer roomId,
//            @RequestParam Integer id){
//        try {
//            // jwt 에서 uid 추출
//            int uid;
//            uid = jwtService.getUserIdx();
//            // 존재하는 상점 아이디인지 검증
//            if (!verifier.isPresentStoreId(uid))
//                throw new BaseException(INVALID_STORE_ID); // /3001/존재하지 않는 상점 id 입니다.
//            // 접속 가능한 채팅방인지 검증
//            if (!chatProvider.isAccessableRoom(uid, roomId))
//                throw new BaseException(INVALID_ROOM_ID);
//
//
//            return new BaseResponse<>(chatService.delAddressInfoMessage(uid, roomId, id));
//        } catch (BaseException exception) {
//            return new BaseResponse<>((exception.getStatus()));
//        }
//    }
//    /**
//     * 계좌 거래 취소
//     * [PATCH] /bungae/chat/:roomId/deal ? id=
//     */
//    @ResponseBody
//    @PatchMapping("/{roomId}/deal")
//    public BaseResponse<PatchCancelRes> delDealInfoMessage(
//            @PathVariable("roomId") Integer roomId,
//            @RequestParam Integer id){
//        try {
//            // jwt 에서 uid 추출
//            int uid;
//            uid = jwtService.getUserIdx();
//            // 존재하는 상점 아이디인지 검증
//            if (!verifier.isPresentStoreId(uid))
//                throw new BaseException(INVALID_STORE_ID); // /3001/존재하지 않는 상점 id 입니다.
//            // 접속 가능한 채팅방인지 검증
//            if (!chatProvider.isAccessableRoom(uid, roomId))
//                throw new BaseException(INVALID_ROOM_ID);
//
//
//            return new BaseResponse<>(chatService.delDealInfoMessage(uid, roomId, id));
//        } catch (BaseException exception) {
//            return new BaseResponse<>((exception.getStatus()));
//        }
//    }
    // 입력 : 보낼 상품 id
    // validation : 보낼 수 있는 상품인지 확인 -- roomId -> storeId 2개 -> productId 중에 있는지
    // 상품 정보 가져오기
    // 상품 정보 json으로 바꿔서 저장하기
    // 정보 반환하기 -> getProductInfoMessage(int productId);


    // 입력 : 보낼 상품 id
    // validation : 보낼 수 있는 상품인지 확인 -- roomId -> storeId 2개 -> productId 중에 있는지
    // 상품 정보 가져오기
    // 상품 정보 json으로 바꿔서 저장하기
    // 정보 반환하기 -> getProductInfoMessage(int productId);

    // 입력 : 계좌정보 , 보낼 상품 id  // 계좌/직거래/택배
    // validation : 보낼 수 있는 상품인지 확인
    // 계좌 데이터 생성하기 -> id 반환
    // 상품정보 가져오기
    // 내보낼 정보 json으로 바꿔서 저장하기

//    /**
//     * 최근 채팅 메시지 조회
//     * [GET] /bungae/chat/:roomId/message ? p=
//     */
//    @ResponseBody
//    @GetMapping("/{roomId}/message")
//    public BaseResponse<List<GetChatRes>> getChatHistory (
//            @PathVariable("roomId") Integer roomId,
//            @RequestParam(required = false) Integer p) {
//        try {
//            // jwt 에서 uid 추출
//            int uid;
//            uid = jwtService.getUserIdx();
//            // 존재하는 상점 아이디인지 검증
//            if (!verifier.isPresentStoreId(uid))
//                throw new BaseException(INVALID_STORE_ID); // /3001/존재하지 않는 상점 id 입니다.
//            // 접속 가능한 채팅방인지 검증
//            if (!chatProvider.isAccessableRoom(uid, roomId))
//                throw new BaseException(INVALID_ROOM_ID);
//
//            if (p == null)
//                p = 1;
//
//            return new BaseResponse<>(chatProvider.getChatHistory(roomId,uid,p));
//        } catch (BaseException exception) {
//            return new BaseResponse<>((exception.getStatus()));
//        }
//
//    }

//
//    /**
//     * 회원가입
//     * [POST] bungae/users/new
//     */
//    // Body
//    @ResponseBody
//    @PostMapping("/new")    // POST 방식의 요청을 매핑하기 위한 어노테이션
//    public BaseResponse<PostUserRes> createUser(@RequestBody PostUserReq postUserReq) {
//        if (postUserReq.getName().isEmpty()) {
//            return new BaseResponse<>(POST_USERS_EMPTY_NAME);
//        }
//        if(postUserReq.getPhone().isEmpty()){
//            return new BaseResponse<>(POST_USERS_EMPTY_PHONE);
//        }
//        if (!isRegexPhone(postUserReq.getPhone())) {
//            return new BaseResponse<>(POST_USERS_INVALID_PHONE);
//        }
//        if(postUserReq.getBirth().isEmpty()){
//            return new BaseResponse<>(POST_USERS_EMPTY_BIRTH);
//        }
//        if(!isRegexBirth(postUserReq.getBirth())){
//            return new BaseResponse<>(POST_USERS_INVALID_BIRTH);
//        }
//
//        try {
//            PostUserRes postUserRes = chatService.createUser(postUserReq);
//            return new BaseResponse<>(postUserRes);
//        } catch (BaseException exception) {
//            return new BaseResponse<>((exception.getStatus()));
//        }
//    }
//
//    /**
//     * 로그인 API
//     * [POST] /users/login
//     */
//    @ResponseBody
//    @PostMapping("/login")
//    public BaseResponse<PostLoginRes> logIn(@RequestBody PostLoginReq postLoginReq) {
//
//        try {
//            PostLoginRes postLoginRes = chatProvider.logIn(postLoginReq);
//            return new BaseResponse<>(postLoginRes);
//        } catch (BaseException exception) {
//            return new BaseResponse<>(exception.getStatus());
//        }
//    }
//
//
//    /**
//     * 모든 회원들의  조회 API
//     * [GET] /users
//     *
//     * 또는
//     *
//     * 해당 닉네임을 같는 유저들의 정보 조회 API
//     * [GET] /users? NickName=
//     */
//    //Query String
//    @ResponseBody   // return되는 자바 객체를 JSON으로 바꿔서 HTTP body에 담는 어노테이션.
//    //  JSON은 HTTP 통신 시, 데이터를 주고받을 때 많이 쓰이는 데이터 포맷.
//    @GetMapping("") // (GET) 127.0.0.1:9000/app/users
//    // GET 방식의 요청을 매핑하기 위한 어노테이션
//    public BaseResponse<List<GetUserRes>> getUsers(@RequestParam(required = false) String nickname) {
//        //  @RequestParam은, 1개의 HTTP Request 파라미터를 받을 수 있는 어노테이션(?뒤의 값). default로 RequestParam은 반드시 값이 존재해야 하도록 설정되어 있지만, (전송 안되면 400 Error 유발)
//        //  지금 예시와 같이 required 설정으로 필수 값에서 제외 시킬 수 있음
//        //  defaultValue를 통해, 기본값(파라미터가 없는 경우, 해당 파라미터의 기본값 설정)을 지정할 수 있음
//        try {
//            if (nickname == null) { // query string인 nickname이 없을 경우, 그냥 전체 유저정보를 불러온다.
//                List<GetUserRes> getUsersRes = userProvider.getUsers();
//                return new BaseResponse<>(getUsersRes);
//            }
//            // query string인 nickname이 있을 경우, 조건을 만족하는 유저정보들을 불러온다.
//            List<GetUserRes> getUsersRes = userProvider.getUsersByNickname(nickname);
//            return new BaseResponse<>(getUsersRes);
//        } catch (BaseException exception) {
//            return new BaseResponse<>((exception.getStatus()));
//        }
//    }
//    /**
//
//
//
//
//    /**
//     * 회원 1명 조회 API
//     * [GET] /users/:userIdx
//     */
//    // Path-variable
//    @ResponseBody
//    @GetMapping("/{userIdx}") // (GET) 127.0.0.1:9000/app/users/:userIdx
//    public BaseResponse<GetUserRes> getUser(@PathVariable("userIdx") int userIdx) {
//        // @PathVariable RESTful(URL)에서 명시된 파라미터({})를 받는 어노테이션, 이 경우 userId값을 받아옴.
//        //  null값 or 공백값이 들어가는 경우는 적용하지 말 것
//        //  .(dot)이 포함된 경우, .을 포함한 그 뒤가 잘려서 들어감
//        // Get Users
//        try {
//            GetUserRes getUserRes = userProvider.getUser(userIdx);
//            return new BaseResponse<>(getUserRes);
//        } catch (BaseException exception) {
//            return new BaseResponse<>((exception.getStatus()));
//        }
//
//    }

//    /**
//     * 유저정보변경 API
//     * [PATCH] /users/:userIdx
//     */
//    @ResponseBody
//    @PatchMapping("/{userIdx}")
//    public BaseResponse<String> modifyUserName(@PathVariable("userIdx") int userIdx, @RequestBody User user) {
//        try {
///**
//  *********** 해당 부분은 7주차 - JWT 수업 후 주석해체 해주세요!  ****************
//            //jwt에서 idx 추출.
//            int userIdxByJwt = jwtService.getUserIdx();
//            //userIdx와 접근한 유저가 같은지 확인
//            if(userIdx != userIdxByJwt){
//                return new BaseResponse<>(INVALID_USER_JWT);
//            }
//            //같다면 유저네임 변경
//  **************************************************************************
// */
//            PatchUserReq patchUserReq = new PatchUserReq(userIdx, user.getNickname());
//            userService.modifyUserName(patchUserReq);
//
//            String result = "회원정보가 수정되었습니다.";
//            return new BaseResponse<>(result);
//        } catch (BaseException exception) {
//            return new BaseResponse<>((exception.getStatus()));
//        }
//    }
}
