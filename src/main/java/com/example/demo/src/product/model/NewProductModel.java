package com.example.demo.src.product.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NewProductModel {
    private String name; // M
    private String content; //Ms
    private Integer categoryDepth1Id; // M
    private Integer categoryDepth2Id;
    private Integer categoryDepth3Id;
    private Integer price;
    private String deliveryFee;
    private Integer quantity;
    private String condition;
    private String change;
}
