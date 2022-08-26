package com.example.demo.src.product.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter@AllArgsConstructor
public class PatchDeleteRes {
    private int deletedProductId;
    private String message;
}
