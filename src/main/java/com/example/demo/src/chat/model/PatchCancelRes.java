package com.example.demo.src.chat.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PatchCancelRes {
    private String type;
    private int productId;
    private String productName;
    private int messageId;
}
