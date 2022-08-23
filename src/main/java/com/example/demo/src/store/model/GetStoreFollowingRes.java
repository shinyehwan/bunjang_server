package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor

public class GetStoreFollowingRes {
    private String profileImgUrl;
    private String storeName;
    private int productNumber;

}
