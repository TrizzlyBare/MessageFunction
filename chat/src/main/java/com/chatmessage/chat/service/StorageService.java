package com.chatmessage.chat.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    String uploadImage(MultipartFile file) throws IOException;

    void deleteImage(String imageUrl) throws IOException;
}
