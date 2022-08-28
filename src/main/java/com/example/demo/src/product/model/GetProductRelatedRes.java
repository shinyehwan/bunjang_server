package com.example.demo.src.product.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetProductRelatedRes {
    private int productId;
    private String imageUrl01;
    private String title;
    private int price;
}
