package com.example.demo.src.chat.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetDealInfoRes {
    private boolean status;
    private Integer productId;
    private String productName;
    private String productImageUrl;
    private Integer price;
    private String date;
    private String location;
    private String phoneNum;
}
