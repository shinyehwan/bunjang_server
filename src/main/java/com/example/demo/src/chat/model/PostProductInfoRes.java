package com.example.demo.src.chat.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostProductInfoRes {
    private String type="Object_product";
    private int productId;
    private String name;
    private String imageUrl;
    private int price;
    private boolean deliveryFee;

    public PostProductInfoRes(int productId, String name, String imageUrl, int price, boolean deliveryFee) {
        this.productId = productId;
        this.name = name;
        this.imageUrl = imageUrl;
        this.price = price;
        this.deliveryFee = deliveryFee;
    }
}
