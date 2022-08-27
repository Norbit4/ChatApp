package pl.norbit.chatapp.model;

import lombok.Data;
import lombok.Getter;
import lombok.extern.java.Log;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import pl.norbit.chatapp.service.ChatService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

@Data
@Log
public class ChatRoom {
    @Getter
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

    public void userMessage(ChatRoom chatRoom, Message message){

        for (String user : chatRoom.users) {

            message.setMessageType(MessageType.CHAT_MESSAGE);
            message.setReceiver(user);

            sendMessageToClient(message);
        }
    }
    private void roomReady(){

        for (String user : users) {
            Message message = new Message();
            message.setMessageType(MessageType.READY);
            message.setRoomUUID(roomId.toString());
            message.setReceiver(user);

            sendMessageToClient(message);
        }
    }

    private void kickUsers(){

        for (String user : users) {
            Message message = new Message();
            message.setMessageType(MessageType.DISCONNECT);
            message.setReceiver(user);

            sendMessageToClient(message);
        }
    }

    private void sendMessageToClient(Message message) {

        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setMessage(message.getMessage());
        responseMessage.setMessageType(message.getMessageType().toString());
        responseMessage.setUsername(message.getUsername());
        responseMessage.setRoomUUID(message.getRoomUUID());

        simpMessagingTemplate.convertAndSendToUser(message.getReceiver(),"/private", responseMessage);
    }
}
