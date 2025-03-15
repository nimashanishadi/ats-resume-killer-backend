package com.example.atskiller.controller;

import com.example.atskiller.service.UserInputService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173") // Allow requests from React app
public class UserInputController {

    private final UserInputService userInputService;

    @Autowired
    public UserInputController(UserInputService userInputService) {
        this.userInputService = userInputService;
    }

    @PostMapping(value = "/scan", consumes = {"multipart/form-data"})
    public ResponseEntity<Map<String, String>> processUserInput(
            @RequestPart("resume") MultipartFile resumeFile,
            @RequestPart("jobDescription") String jobDescription) {
        try {
            Map<String, String> response = userInputService.processUserInput(resumeFile, jobDescription);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
