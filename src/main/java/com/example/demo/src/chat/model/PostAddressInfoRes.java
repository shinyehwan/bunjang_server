package com.example.demo.src.chat.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostAddressInfoRes {
    private String type="Object_address";
    private String storeName;
    private int productId;
    private String productName;
    private int price;
    private boolean deliveryFee;
    private int addressInfoId;

    public PostAddressInfoRes(String storeName, int productId, String productName, int price, boolean deliveryFee, int addressInfoId) {
        this.storeName = storeName;
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.deliveryFee = deliveryFee;
        this.addressInfoId = addressInfoId;
    }
}
