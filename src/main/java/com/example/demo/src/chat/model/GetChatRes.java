package com.example.demo.src.chat.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
public class GetChatRes {
    private int chatId;
    private int callerId;
    private String callerProfileImgUrl;
    private String type;
    private Object description;
    private String time;
}
