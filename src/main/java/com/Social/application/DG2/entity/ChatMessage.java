package com.Social.application.DG2.entity;

import com.Social.application.DG2.entity.Enum.MessageType;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage {
    private MessageType type;

    private String content;

    private String sender;
}
