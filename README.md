# Architecture
![KakaoTalk_Photo_2022-10-17-18-04-52](https://user-images.githubusercontent.com/77479647/196305286-c607bba2-3503-474e-91d3-fbfe2e61879e.png)

# Structure
<img width="282" alt="스크린샷 2022-10-18 오후 6 36 49" src="https://user-images.githubusercontent.com/77479647/196395191-4a3b7d00-ff29-406d-8575-fe945bc86887.png">



# API Specification
- [Bunjang - API specification](https://docs.google.com/spreadsheets/d/1xFWkTea2nxwVpAfKPkQA3OhuB2xRzCOQD3YocloCrgI/edit?usp=sharing)
- [Board](https://www.notion.so/softsquared/B-f746c78fa5c44506bb4144550e51d3ba)

# ERD(AqueryTool)
- [Bunjang - ERD](https://aquerytool.com/aquerymain/index/?rurl=1788d167-85ad-44bf-b0e5-8869208ff1dd)
> Password : eylmq0

# 번개장터 개발일지
템플릿의 저작권은 (주)소프트스퀘어드에 있습니다. 상업적 용도의 사용을 금합니다.


## 2022-08-20

- 기획서 작성 (90%)
- dev/prod 서브도메인 적용(진행중)
- ERD 설계 (30%)
- 스프링 템플릿 세팅, 깃허브 커밋

## 2022-08-21

- dev/prod 서브도메인 적용(케빈 완료 / 레이 완료)
- ERD 설계 작업 (100%) -> 알람테이블 추가
- User 로그인 회원가입 api 작성
- 명세서 작성(진행중)

## 2022-08-22

- 피드백 진행 완료
- 무중단 서비스 서버 적용완료
- 이미지관련 처리 해결 완료
- API구현 집중
  - 유저 생성 API (회원가입) - 서버 적용 완료
  - 유저 로그인 API - 서버 적용 완료
  - 채팅방 목록 조회 - 서버 적용 완료
- 더미데이터 생성
  - Store 11
  - ChatRoom 3 / ChatRoomStoreMap 6
  - Chat 95
  - Category 3 / CategoryDepth2 41 / CategoryDepth3 101
  - ...

## 2022-08-23
- API 구현 집중(상점 관련 부분 90% 완료) 
  - 상점 설명 조회 및 수정
  - 리뷰 목록 조회
  - 찜한 목록 조회
  - 팔로잉 팔로워 목록 조회
  - 마이페이지 조회

## 2022-08-24
- 상품 등록 API 개발
- 제품 상세정보 API - 상품정보 개발
  - 상품 세부정보
- 팔로워 상점 리스트 조회 API
- 팔로잉 상점 상품 조회 API
- 마이페이지 조회 (100%)

## 2022-08-25
- 검색, 추천상품 피드 API 개발 (50%)
  - 키워드 검색 완료
  - 카테고리 검색 완료
  - 홈화면 추천상품 피드 완료
- 제품 상세정보 API - 판매자 정보, 관련상품정보 개발
  - 별점 평균
  - 상품, 후기, 팔로잉, 팔로워
  - 판매자 최근상품
  - 판매자 리뷰
  - 관련상품리스트

## 2022-08-26
- 마이페이지 명세서 작성(50%)
  - 명세서 반영하면서 수정이 필요한 부분 하나하나씩 수정중
  - 상점 설명 조회 API -> validation 추가
  - 찜한 목록 조회 API -> 수정 예정
  
## 2022-08-27
- 상품 관련 API 개발 (90%)
  - 판매자 팔로우, 취소 구현 완료
  - 해당 상품 찜하기, 취소 구현 완료
  - 상품 정보 수정, 삭제 구현 완료 
- 상점 관련 API 수정 내용
  - 상점 설명 조회 API -> validation 추가 완료
  - 찜한 목록 조회 API -> 수정 완료
- 홈 화면 관련 API 구현 중

## 2022-08-28
- 상품 관련 API 개발 (100%)
- 채팅관련 외부 API 구현 예정 (케빈 작업중)
- 거래 목록 조회 구현을 어떻게 할지 고민중
  - 일단 채팅을 구현 한 다음에 구매, 판매, 트랜잭션 테이블에 행 추가로 진행할 예정
- 홈 화면(50%) -> 추천상품 구현 완료, 홈 화면 작업중

## 2022-08-29
- 채팅 관련 API 구현 중
  - ~~채팅관련 외부 API 구현 예정 (케빈 작업중)~~ 외부 API가 아닌 웹소켓 구현하려 했으나 원래 방식으로 진행
- 홈 화면 개발(90%) 완료
- 브랜드 리스트, 브랜드 검색 개발 완료
- 소셜 로그인 부분 개발 예정

## 2022-08-30
- 채팅 관련 API 구현 중 (70%)
  - ~~채팅관련 외부 API 구현 예정 (케빈 작업중)~~ 외부 API가 아닌 웹소켓 구현하려 했으나 원래 방식으로 진행
- 명세서 작성
- 소셜 로그인 부분 개발 예정
- 홈 화면 개발(100%) 완료
- 번개톡 계좌,배송,상품,직거래 정보 전송 CRUD 중 C 완료


## 2022-08-31
- 채팅 관련 API 구현 중 (90%)
  - ~~채팅관련 외부 API 구현 예정 (케빈 작업중)~~ 외부 API가 아닌 웹소켓 구현하려 했으나 원래 방식으로 진행
  - 이미지, 메세지, 이모티콘 전송 구현 완료
- 소셜 로그인 부분 개발 예정
- 번개톡 계좌,배송,상품,직거래 정보 전송 CRUD 중 R,U 완료
- 명세서 작성중


## 2022-09-01
- 채팅 관련 API 구현 중 (95%)
  - ~~채팅관련 외부 API 구현 예정 (케빈 작업중)~~ 외부 API가 아닌 웹소켓 구현하려 했으나 원래 방식으로 진행
- 명세서 작성 완료(100%)
- 번개톡, 피드 도메인 validation 점검
- 소셜 로그인 부분 로컬 구현 완료

## 2022-09-02
- 채팅 관련 API
- 명세서 작성 완료(100%)
- 소셜 로그인 부분 로컬 구현 완료
- 최종 영상 제출 
