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

import java.util.*;


@Service @Log
@AllArgsConstructor
public class ChatService {

    private static Queue<User> userQueue;
    private SimpMessagingTemplate simpMessagingTemplate;

    @Data @NoArgsConstructor
    private static class User{
        private String username;
        private SimpMessagingTemplate simpMessagingTemplate;
    }

    private static class Task extends TimerTask {
        @Override
        public void run() {
            //log.info(String.valueOf(userQueue.size()));

            if(userQueue.size() >= 2){
                User user1 = userQueue.poll();
                User user2 = userQueue.poll();

                new ChatRoom(
                        new String[]{user1.getUsername(), user2.getUsername()},
                        user1.getSimpMessagingTemplate()
                );
            }
        }
    }

    @Bean
    CommandLineRunner commandLineRunner (){

        return args -> {
            startQueue();
        };
    }

    private static void startQueue(){

        userQueue = new LinkedList<>();
        log.info("Timer starting");
        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new Task(), 0, 500);
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
        ChatRoom chatRoom = ChatRoom.getRooms().get(UUID.fromString(message.getRoomUUID()));

        chatRoom.userMessage(chatRoom, message);
    }
}
