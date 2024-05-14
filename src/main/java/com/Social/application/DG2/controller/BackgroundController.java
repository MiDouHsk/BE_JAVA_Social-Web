package com.Social.application.DG2.controller;

import com.Social.application.DG2.service.AvatarService;
import com.Social.application.DG2.util.annotation.CheckLogin;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/background")
public class BackgroundController {

    private final AvatarService avatarService;

    public BackgroundController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @CheckLogin
    @PostMapping(value = "/upload", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<String> uploadBackground(@RequestParam("file") MultipartFile file) {
        try {
            avatarService.uploadBackground(file);
            return ResponseEntity.ok("File uploaded successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file: " + e.getMessage());
        }
    }
    @CheckLogin
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteBackground(@RequestParam("objectName") String objectName) {
        try {
            avatarService.deleteBackground(objectName);
            return ResponseEntity.ok("File deleted successfully!");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete file!");
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }
}