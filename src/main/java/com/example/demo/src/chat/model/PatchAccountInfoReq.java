package com.example.demo.src.chat.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PatchAccountInfoReq {
    private String owner;
    private String bankName;
    private String accountNum;
}
