package pl.norbit.chatapp.controller;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import pl.norbit.chatapp.model.Message;
import pl.norbit.chatapp.service.ChatService;

@Controller
@AllArgsConstructor
@CrossOrigin @Log
public class MessageController {
    private ChatService chatService;

    @MessageMapping("/join")
    public Message joinMessage(@Payload Message message){
        log.info(message.getUsername());

        chatService.join(message);

        return message;
    }
    @MessageMapping("/leave")
    public Message leaveMessage(@Payload Message message){

        chatService.leave(message);

        return message;
    }

    @MessageMapping("/message")
    public Message chatMessage(@Payload Message message){
        log.info(message.getMessage());
        log.info(message.getRoomUUID());
        log.info(message.getUsername());

        chatService.message(message);

        return message;
    }
}