package com.example.demo.src.product.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetProductStoreReviewRes {
    private int purchaserId;
    private String profileImgUrl;
    private String storeName;
    private int star;
    private String content;
    private int productId;
    private String title;
    private String createdAt;

}
