package com.example.demo.src.store.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // 해당 클래스의 파라미터가 없는 생성자를 생성, 접근제한자를 PROTECTED로 설정.
public class PatchStoreDetailReq {
    private String storeName;
    private String profileImgUrl;
    private String contactTime;
    private String introduce;
    private String policy;
    private String precautions;
}
