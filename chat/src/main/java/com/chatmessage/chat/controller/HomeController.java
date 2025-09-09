package com.chatmessage.chat.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class HomeController {
    
    @GetMapping("/")
    public Map<String, String> home() {
        return Map.of(
            "message", "Welcome to Chat Application!",
            "status", "running",
            "endpoints", "Try /api/messages for messages API"
        );
    }
    
    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of(
            "status", "UP",
            "application", "Chat Application"
        );
    }
}
