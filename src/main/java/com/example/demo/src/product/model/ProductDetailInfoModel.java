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
public class ProductDetailInfoModel {
    private int productId;
    private int storeId;
    private String name;
    private String dealStatus;
    private List<String> imageUrls;
    private Integer price;
    private String location;
    private String uploaded;
    private String uploadedEasyText;
    private String condition;
    private int quantity;
    private String deliveryFee;
    private String change;
    private String content;
    private List<String> tags;
    private Integer categoryDepth1Id;
    private Integer categoryDepth2Id;
    private Integer categoryDepth3Id;
}
