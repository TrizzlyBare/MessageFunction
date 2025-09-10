package com.chatmessage.chat.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.chatmessage.chat.service.StorageService;

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
    @PostMapping(value = "/upload", consumes = {"multipart/form-data"})
    @io.swagger.v3.oas.annotations.Operation(
            summary = "Test file upload",
            description = "Test endpoint for uploading files to verify storage functionality"
    )
    public ResponseEntity<?> testUpload(
            @io.swagger.v3.oas.annotations.Parameter(
                    description = "File to upload",
                    required = true,
                    schema = @io.swagger.v3.oas.annotations.media.Schema(type = "string", format = "binary")
            )
            @RequestPart("file") MultipartFile file) {

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

    /**
     * Simple endpoint to test both text and file upload together
     */
    @PostMapping(value = "/message-test", consumes = {"multipart/form-data"})
    @io.swagger.v3.oas.annotations.Operation(
            summary = "Test sending message with file",
            description = "Test endpoint for sending a message with optional file attachment"
    )
    public ResponseEntity<?> testSendMessageWithImage(
            @RequestParam(value = "text", required = false) String text,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        logger.info("Received test message: text={}, file={}",
                text, (file != null ? file.getOriginalFilename() : "null"));

        try {
            // Validate that at least one is provided
            if ((text == null || text.trim().isEmpty()) && (file == null || file.isEmpty())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Either text or file must be provided"));
            }

            Map<String, Object> response = new HashMap<>();
            response.put("text", text);

            if (file != null && !file.isEmpty()) {
                String fileUrl = storageService.uploadImage(file);
                response.put("file", Map.of(
                        "originalFilename", file.getOriginalFilename(),
                        "contentType", file.getContentType(),
                        "size", file.getSize(),
                        "fileUrl", fileUrl
                ));
            }

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            logger.error("Error processing request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Request processing failed",
                    "message", e.getMessage()
            ));
        }
    }
}
