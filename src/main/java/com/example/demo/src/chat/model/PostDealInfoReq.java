package com.example.demo.src.chat.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostDealInfoReq {
    private Integer productId;
    private String date;
    private String location;
    private String phoneNum;
}
