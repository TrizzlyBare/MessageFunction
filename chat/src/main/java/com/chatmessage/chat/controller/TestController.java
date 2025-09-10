package com.chatmessage.chat.controller;

import com.chatmessage.chat.service.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    private static final Logger logger = LoggerFactory.getLogger(TestController.class);
    private final StorageService storageService;

    public TestController(StorageService storageService) {
        this.storageService = storageService;
    }

    /**
     * Simple endpoint to test file uploads
     */
    @PostMapping("/upload")
    public ResponseEntity<?> testUpload(@RequestParam("file") MultipartFile file) {
        logger.info("Received test file upload: {}", file.getOriginalFilename());
        
        try {
            String fileUrl = storageService.uploadImage(file);
            
            Map<String, Object> response = new HashMap<>();
            response.put("originalFilename", file.getOriginalFilename());
            response.put("contentType", file.getContentType());
            response.put("size", file.getSize());
            response.put("fileUrl", fileUrl);
            
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            logger.error("Error uploading file: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "error", "File upload failed", 
                "message", e.getMessage()
            ));
        }
    }
}