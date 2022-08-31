package com.example.demo.src.chat.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetAccountInfoRes {
    private boolean status;
    private Integer productId;
    private String productName;
    private String productImageUrl;
    private boolean deliveryFee;
    private int price;
    private String owner;
    private String bankName;
    private String accountNum;
}
