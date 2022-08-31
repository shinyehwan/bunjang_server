package com.example.demo.src.chat.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostDealInfoRes {
    private String type="Object_deal";
    private int productId;
    private String productName;
    private int price;
    private int dealInfoId;

    public PostDealInfoRes(int productId, String productName, int price, int dealInfoId) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.dealInfoId = dealInfoId;
    }
}
