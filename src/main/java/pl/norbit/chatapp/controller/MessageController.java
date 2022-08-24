package pl.norbit.chatapp.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.util.HtmlUtils;
import pl.norbit.chatapp.model.Message;
import pl.norbit.chatapp.model.ResponseMessage;

@Controller
public class MessageController {

    @CrossOrigin
    @MessageMapping("/message")
    @SendTo("/topic/messages")
    public ResponseMessage getMessage(@Payload Message message){

        return new ResponseMessage(HtmlUtils.htmlEscape(message.getMessage()), message.getUsername());
    }
}