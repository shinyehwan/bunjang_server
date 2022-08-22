# 번개장터 B 개발일지

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

## 해시태그 저장 관련 코드 
-> 필요하신 도메인의 DAO에 넣고 쓰시면 됩니다!
tag들을 `List<String>`형태로 입력해주세요
```java
    /**
     * 해시태그 입력
     */
    public void addHashTags(int productId, List<String> hashtags) {
        int tagId;
        for (String h : hashtags) {
            try {
                // 이미 저장된 태그인지 확인
                tagId = this.jdbcTemplate.queryForObject(
                        "SELECT * FROM Tag WHERE tag = " + h ,
                        (rs, rowNum) -> rs.getInt("id")
                );
            } catch (IncorrectResultSizeDataAccessException error) {
                // 저장되지 않은 태그이므로
                // 새로운 태그 생성
                this.jdbcTemplate.update(
                        "INSERT INTO Tag (tag) VALUES (" + h +")"
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
```

## 검증 관련 코드

- 자주 쓰일만한 validation 코드들을 utils/Verifier 에 넣어놓으려고 합니다
- 현재 "상점 아이디 검증 코드" , 예환님께서 만들어주신 전화번호,이름 검색 코드 넣어놓았으니, 필요시에 추가,수정해주세요!

### 사용방법
```java
// Controller
// ...
    // 아래 코드를 Contoller/Service/Provider 위에 붙여넣기
    // 검증코드 클래스 추가
    private Verifier verifier;
    @Autowired
    public void setVerifier(Verifier verifier){
    this.verifier = verifier;
    }
    
    // ...
    // (예시) 존재하는 상점 아이디인지 검증
    if (!verifier.isPresentStoreId(uid))
        throw new BaseException(INVALID_STORE_ID);

    
```