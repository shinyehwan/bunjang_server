package com.example.demo.src.chat.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MessageRawInfoModel {
    private int chatId;
    private int sendStoreId;
    private String description;
    private String mediaType;
    private String mediaDescriptionUrl;
    private String uploaded;
}
