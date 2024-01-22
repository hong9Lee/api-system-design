package com.example.shared.message;

import com.example.shared.model.MessageType;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DefaultMessage {

    private String uuid;
    private MessageType messageType;

}
