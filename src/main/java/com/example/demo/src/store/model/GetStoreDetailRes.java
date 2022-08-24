package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetStoreDetailRes {
    private String storeName;
    private String profileImgUrl;
    private int star;

    private String contactTime;
    private String introduce;
    private String policy;
    private String precautions;


}
