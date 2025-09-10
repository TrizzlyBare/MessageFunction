package com.chatmessage.chat.config;

import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.chatmessage.chat.model.Room;
import com.chatmessage.chat.model.User;
import com.chatmessage.chat.service.RoomService;
import com.chatmessage.chat.service.UserService;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserService userService, RoomService roomService) {
        return args -> {
            // Create some users
            User user1 = userService.createUser("john");
            User user2 = userService.createUser("alice");
            User user3 = userService.createUser("bob");
            
            // Create a default user for testing
            User defaultUser = new User();
            defaultUser.setUserId("default-user");
            defaultUser.setUsername("Default Test User");
            userService.saveUser(defaultUser);

            System.out.println("Created users:");
            System.out.println("User 1: " + user1.getUserId() + " - " + user1.getUsername());
            System.out.println("User 2: " + user2.getUserId() + " - " + user2.getUsername());
            System.out.println("User 3: " + user3.getUserId() + " - " + user3.getUsername());
            System.out.println("Default User: " + defaultUser.getUserId() + " - " + defaultUser.getUsername());

            // Create a couple of rooms
            Room room1 = roomService.createRoom("General Chat", 
                Arrays.asList(user1.getUserId(), user2.getUserId(), "default-user"));
            Room room2 = roomService.createRoom("Dev Team", 
                Arrays.asList(user1.getUserId(), user2.getUserId(), user3.getUserId(), "default-user"));
            Room room3 = roomService.createRoom("Test Room", 
                Arrays.asList("default-user"));

            System.out.println("Created rooms:");
            System.out.println("Room 1: " + room1.getRoomId() + " - " + room1.getRoomName());
            System.out.println("Room 2: " + room2.getRoomId() + " - " + room2.getRoomName());
            System.out.println("Room 3: " + room3.getRoomId() + " - " + room3.getRoomName());
        };
    }
}
