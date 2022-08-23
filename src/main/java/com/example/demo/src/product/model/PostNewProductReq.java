package com.example.demo.src.product.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostNewProductReq {
    private String name;
    private String imageUrl01;
    private String imageUrl02;
    private String imageUrl03;
    private String imageUrl04;
    private String imageUrl05;
    private String imageUrl06;
    private String imageUrl07;
    private String imageUrl08;
    private String imageUrl09;
    private String imageUrl10;
    private int categoryDepth1Id;
    private int categoryDepth2Id;
    private int categoryDepth3Id;
    // 태그 리스트
    private int price;
    private String deliveryFree;
    private String quantity;
    private String condition;
    private String change;
    private String location;
}
