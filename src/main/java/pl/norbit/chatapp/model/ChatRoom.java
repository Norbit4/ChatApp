package pl.norbit.chatapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import pl.norbit.chatapp.service.ChatService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

@Data
@Log
public class ChatRoom {
    private static HashMap<UUID, ChatRoom> rooms = new HashMap<>();
    private SimpMessagingTemplate simpMessagingTemplate;

    private ChatService chatService;

    private String[] users;
    private UUID roomId;
    private ArrayList<String> messages;

    public static void removeRoom(UUID roomUUID){
        ChatRoom chatRoom = rooms.get(roomUUID);
        chatRoom.kickUsers();
        rooms.remove(roomUUID);
    }

    public ChatRoom(String[] users, SimpMessagingTemplate simpMessagingTemplate) {

        this.simpMessagingTemplate =  simpMessagingTemplate;
        this.messages = new ArrayList<>();
        this.users = users;
        this.roomId = UUID.randomUUID();

        rooms.put(roomId, this);
        log.info("Room created");

        roomReady();
    }

    public void userMessage(UUID roomUUID, Message message){
        ChatRoom chatRoom = rooms.get(roomUUID);

        for (String user : chatRoom.users) {
            //Message message = new Message();

            message.setMessageType("CHAT_MESSAGE");
            message.setReceiver(user);

            sendMessageToClient(message);
        }
    }
    private void roomReady(){

        for (String user : users) {
            Message message = new Message();
            message.setMessageType("READY");
            message.setReceiver(user);

            sendMessageToClient(message);
        }
    }

    private void kickUsers(){

        for (String user : users) {
            Message message = new Message();
            message.setMessageType("DISCONNECT");
            message.setReceiver(user);

            sendMessageToClient(message);
        }
    }

    private void sendMessageToClient(Message message) {

        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setMessage(message.getMessage());
        responseMessage.setMessageType(message.getMessageType());
        responseMessage.setUsername(message.getUsername());

        simpMessagingTemplate.convertAndSendToUser(message.getReceiver(),"/private", responseMessage);
    }
}
