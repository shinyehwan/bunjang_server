package com.example.demo.src.chat.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetChatRoomsRes {
    private int roomId;
    private int callerId;
    private String name;
    private String thumbnailImgUrl;
    private String lastMessage;
    private String lastMessageTime;
}
