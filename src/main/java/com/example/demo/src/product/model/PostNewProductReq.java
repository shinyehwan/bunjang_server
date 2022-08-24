package com.example.demo.src.product.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PostNewProductReq {
    private String name; // M
    private String content; //M
    private List<String> imageUrls; // M
    private Integer categoryDepth1Id; // M
    private Integer categoryDepth2Id;
    private Integer categoryDepth3Id;
    private List<String> tags;
    private Integer price;
    private Boolean deliveryFee;
    private Integer quantity;
    private String condition;
    private Boolean change;
    private String location;
}
