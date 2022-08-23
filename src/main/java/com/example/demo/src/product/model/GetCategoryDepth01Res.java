package com.example.demo.src.product.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetCategoryDepth01Res {
    private int depth1Id;
    private String  name;
    private boolean hasMoreDepth;
}
