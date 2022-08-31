package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetStoreReservedRes {
    private int productId;
    private String dealStatus;
    private String imageUrl01;
    private String title;
    private int price;
}
