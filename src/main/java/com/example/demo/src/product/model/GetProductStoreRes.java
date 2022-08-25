package com.example.demo.src.product.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetProductStoreRes {

    private int storeId;
    private String profileImgUrl;
    private String storeName;
    private int star;
    private String contactTime;
    private String introduce;
    private String policy;
    private String precautions;

}
