package de.ait.chat.controller;

import de.ait.chat.entity.ChatMessage;
import de.ait.chat.entity.User;
import de.ait.chat.service.ChatMessageService;
import de.ait.chat.service.ChatService;
import de.ait.chat.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatMessageService chatMessageService;
    private final ChatService chatService;
    private final UserService userService;




    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage, Principal principal) {
        // Assuming you have a method to find the user by username or some identifier
        User user = userService.findByUsername(principal.getName()); // Use the username or a similar identifier
        chatMessage.setUser(user); // Set the user in the chat message

        chatMessageService.saveMessage(chatMessage); // Save the message to the database
        return chatMessage; // Broadcast the message to all connected clients
    }


    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(
            @Payload ChatMessage chatMessage,
            SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }
}