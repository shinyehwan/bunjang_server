package com.example.demo.src.feed.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FeedElementInfo {
    private int productId;
    private String name;
    private String imageUrl; // 메인 사진
    private int price;
    private String location; // 지역정보
    private String uploaded; // 업로드날짜 raw 텍스트
    private String uploadedEasyText; // 업로드날짜 가공한버전
    private int dibs;
    private String dealStatus;
    private boolean userDibed; // 사용자가 좋아요 눌렀는지?
}
