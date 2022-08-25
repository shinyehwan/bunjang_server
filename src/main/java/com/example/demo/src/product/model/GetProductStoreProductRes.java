package com.example.demo.src.product.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetProductStoreProductRes {
    private int storeId;
    private int productId;
    private String imageUrl01;
    private int price;
    private String title;
}
