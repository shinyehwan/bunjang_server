package com.example.demo.src.chat.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PatchAddressInfoReq {
    private String name;
    private String phoneNum;
    private String address;
    private String addressDetail;
}
