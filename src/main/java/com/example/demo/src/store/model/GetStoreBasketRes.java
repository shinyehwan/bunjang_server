package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetStoreBasketRes {
    private String imageUrl01;
    private String title;
    private String profileImgUrl;
    private String storeName;
    private String dealStatus;
    private String updatedAt;

}
