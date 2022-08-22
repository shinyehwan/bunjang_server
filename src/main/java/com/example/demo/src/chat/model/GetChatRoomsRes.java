package com.example.demo.src.chat.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetChatRoomsRes {
    int roomId;
    int talkerId;
    String name;
    String thumbnailImgUrl;
    String lastMessage;
    String lastMessageTime;
}
