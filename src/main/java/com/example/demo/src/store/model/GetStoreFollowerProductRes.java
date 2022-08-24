package com.example.demo.src.store.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetStoreFollowerProductRes {

    private int followerId;
    private String storeName;
    private int productId;
    private String imageUrl01;
    private int price;


}
