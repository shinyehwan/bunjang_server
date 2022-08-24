package com.example.demo.src.product.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetProductRes {
    private String name;
    private List<String> imageUrls; // M
    private Integer price;
    private int location;
    // private String uploaded;
//    private int  views;
//    private int dibs;
//    private int talks;

    private String condition;
    private int quantity;
    private boolean deliveryFree;
    private boolean change;
    private String content;
    private List<String> tags;

//    상점명
//    사진
//    별점
//    팔로워수
//    팔로우 여부
//    해당상점 상품
//    거래후기
//    비슷한 상품
}
