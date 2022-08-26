package pl.norbit.chatapp.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import pl.norbit.chatapp.model.ChatRoom;
import pl.norbit.chatapp.model.Message;
import pl.norbit.chatapp.model.ResponseMessage;

import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

import static java.lang.Thread.sleep;

@Service
@Log
@AllArgsConstructor
public class ChatService {

    private static Queue<User> userQueue;
    private SimpMessagingTemplate simpMessagingTemplate;

    @Data
    @NoArgsConstructor
    private static class User{
        private String username;
        private SimpMessagingTemplate simpMessagingTemplate;
    }

    @Bean
    CommandLineRunner commandLineRunner (){

        return args -> {
            startQueue();
        };
    }

    private static void startQueue(){

        userQueue = new LinkedList<>();
        log.info("Starting");

        //new Thread(() -> {

            while (true) {
                log.info(String.valueOf(userQueue.size()));
                if(userQueue.size() >= 2){
                    User user1 = userQueue.poll();
                    User user2 = userQueue.poll();

                    new ChatRoom(new String[]{user1.getUsername(), user2.getUsername()}, user1.getSimpMessagingTemplate());
                }

                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        //});
    }
    public void join(Message message){
        User user = new User();
        user.setSimpMessagingTemplate(simpMessagingTemplate);
        user.setUsername(message.getUsername());

        userQueue.add(user);
    }

    public void leave(Message message){
        ChatRoom.removeRoom(UUID.fromString(message.getRoomUUID()));
    }

    public void message(Message message){
        //ChatRoom.userMessage(UUID.fromString(message.getRoomUUID()), message);
    }

    public void sendMessageToClient(Message message) {

        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setMessage(message.getMessage());
        responseMessage.setMessageType(message.getMessageType());
        responseMessage.setUsername(message.getUsername());

        //simpMessagingTemplate.convertAndSendToUser(message.getReceiver(),"/private", responseMessage);
    }
}
