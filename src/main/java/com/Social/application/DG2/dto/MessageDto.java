package com.Social.application.DG2.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageDto {
    private Long id;
    private String senderId;
    private String conversationId;
    private String content;
    private LocalDateTime sentAt;
    private String topic;
}
