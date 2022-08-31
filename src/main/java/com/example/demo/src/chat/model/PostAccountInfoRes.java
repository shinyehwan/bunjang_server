package com.example.demo.src.chat.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostAccountInfoRes {
    private String type="Object_account";
    private String storeName;
    private int productId;
    private String productName;
    private int price;
    private boolean deliveryFee;
    private int accountInfoId;

    public PostAccountInfoRes(String storeName, int productId, String productName, int price, boolean deliveryFee, int accountInfoId) {
        this.storeName = storeName;
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.deliveryFee = deliveryFee;
        this.accountInfoId = accountInfoId;
    }
}
