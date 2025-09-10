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
            User user4 = userService.createUser("emma");
            User user5 = userService.createUser("david");
            User user6 = userService.createUser("sarah");

            // Create a default user for testing
            User defaultUser = new User();
            defaultUser.setUserId("default-user");
            defaultUser.setUsername("Default Test User");
            userService.saveUser(defaultUser);

            System.out.println("Created users:");
            System.out.println("User 1: " + user1.getUserId() + " - " + user1.getUsername());
            System.out.println("User 2: " + user2.getUserId() + " - " + user2.getUsername());
            System.out.println("User 3: " + user3.getUserId() + " - " + user3.getUsername());
            System.out.println("User 4: " + user4.getUserId() + " - " + user4.getUsername());
            System.out.println("User 5: " + user5.getUserId() + " - " + user5.getUsername());
            System.out.println("User 6: " + user6.getUserId() + " - " + user6.getUsername());
            System.out.println("Default User: " + defaultUser.getUserId() + " - " + defaultUser.getUsername());

            // Create rooms with different member combinations
            // Room 1: General Chat - Most users + default user (main discussion room)
            Room room1 = roomService.createRoom("General Chat",
                    Arrays.asList(user1.getUserId(), user2.getUserId(), user3.getUserId(),
                            user4.getUserId(), user5.getUserId(), "default-user"));

            // Room 2: Dev Team - Tech-focused users
            Room room2 = roomService.createRoom("Dev Team",
                    Arrays.asList(user1.getUserId(), user3.getUserId(), user5.getUserId(), "default-user"));

            // Room 3: Marketing Team - Business-focused users
            Room room3 = roomService.createRoom("Marketing Team",
                    Arrays.asList(user2.getUserId(), user4.getUserId(), user6.getUserId()));

            // Room 4: Project Alpha - Cross-functional team
            Room room4 = roomService.createRoom("Project Alpha",
                    Arrays.asList(user1.getUserId(), user2.getUserId(), user6.getUserId(), "default-user"));

            // Room 5: Random Chat - Smaller casual group
            Room room5 = roomService.createRoom("Random Chat",
                    Arrays.asList(user3.getUserId(), user4.getUserId(), user5.getUserId()));

            // Room 6: Test Room - Only default user (for testing purposes)
            Room room6 = roomService.createRoom("Test Room",
                    Arrays.asList("default-user"));

            // Room 7: Management - Senior team members
            Room room7 = roomService.createRoom("Management",
                    Arrays.asList(user2.getUserId(), user5.getUserId(), user6.getUserId()));

            System.out.println("\nCreated rooms with memberships:");
            System.out.println("Room 1: " + room1.getRoomId() + " - " + room1.getRoomName()
                    + " (Members: " + room1.getMembers().size() + ")");
            System.out.println("Room 2: " + room2.getRoomId() + " - " + room2.getRoomName()
                    + " (Members: " + room2.getMembers().size() + ")");
            System.out.println("Room 3: " + room3.getRoomId() + " - " + room3.getRoomName()
                    + " (Members: " + room3.getMembers().size() + ")");
            System.out.println("Room 4: " + room4.getRoomId() + " - " + room4.getRoomName()
                    + " (Members: " + room4.getMembers().size() + ")");
            System.out.println("Room 5: " + room5.getRoomId() + " - " + room5.getRoomName()
                    + " (Members: " + room5.getMembers().size() + ")");
            System.out.println("Room 6: " + room6.getRoomId() + " - " + room6.getRoomName()
                    + " (Members: " + room6.getMembers().size() + ")");
            System.out.println("Room 7: " + room7.getRoomId() + " - " + room7.getRoomName()
                    + " (Members: " + room7.getMembers().size() + ")");

            // Print membership summary
            System.out.println("\nMembership Summary:");
            System.out.println("john (" + user1.getUserId() + ") is in: General Chat, Dev Team, Project Alpha");
            System.out.println("alice (" + user2.getUserId() + ") is in: General Chat, Marketing Team, Project Alpha, Management");
            System.out.println("bob (" + user3.getUserId() + ") is in: General Chat, Dev Team, Random Chat");
            System.out.println("emma (" + user4.getUserId() + ") is in: General Chat, Marketing Team, Random Chat");
            System.out.println("david (" + user5.getUserId() + ") is in: General Chat, Dev Team, Random Chat, Management");
            System.out.println("sarah (" + user6.getUserId() + ") is in: Marketing Team, Project Alpha, Management");
            System.out.println("default-user is in: General Chat, Dev Team, Project Alpha, Test Room");
        };
    }
}
