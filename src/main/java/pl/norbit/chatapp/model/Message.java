package pl.norbit.chatapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private String message;
    private String username;
    private String receiver;
    private String color;
    private MessageType messageType;
    private String roomUUID;
}
