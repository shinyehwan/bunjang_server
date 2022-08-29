package com.example.demo.src.chat.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetChatRoomInfoRes {

    private int storeId;
    private String storeName;
    private int chatRoomId;
    private int productId;
    private String ImageUrl01;
    private String title;
    private int price;

}
