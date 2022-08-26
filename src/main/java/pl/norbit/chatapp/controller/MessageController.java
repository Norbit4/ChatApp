package pl.norbit.chatapp.controller;

import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import pl.norbit.chatapp.model.Message;
import pl.norbit.chatapp.model.ResponseMessage;
import pl.norbit.chatapp.service.ChatService;

@Controller
@AllArgsConstructor
@CrossOrigin
public class MessageController {
    private SimpMessagingTemplate simpMessagingTemplate;
    private ChatService chatService;


    @MessageMapping("/message")
    @SendTo("/topic/messages")
    public ResponseMessage getMessage(@Payload Message message){
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setMessage(message.getMessage());
        responseMessage.setUsername(message.getUsername());

        return responseMessage;

    }

    @MessageMapping("/private-message")
    public Message recMessage(@Payload Message message){

        simpMessagingTemplate.convertAndSendToUser(message.getReceiver(),"/private",message);
        return message;
    }

    @MessageMapping("/join")
    public Message joinMessage(@Payload Message message){

        chatService.join(message);

        return message;
    }
    @MessageMapping("/leave")
    public Message leaveMessage(@Payload Message message){
        chatService.leave(message);

        return message;
    }

    @MessageMapping("/chat-message")
    public Message message(@Payload Message message){
        chatService.message(message);

        return message;
    }
}