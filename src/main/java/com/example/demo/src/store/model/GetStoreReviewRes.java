package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetStoreReviewRes {

    private String profileImgUrl;
    private String storeName;
    private int star;
    private String content;

}
