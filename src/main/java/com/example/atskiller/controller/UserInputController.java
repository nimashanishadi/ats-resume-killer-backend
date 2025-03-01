package com.example.atskiller.controller;


import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173") // Allow requests from React app
public class UserInputController {

    @PostMapping("/scan")
    public ResponseEntity<String> processUserInput(@RequestBody Map<String, String> requestData) {
        String resume = requestData.get("resume");
        String jobDescription = requestData.get("jobDescription");

        // Process the resume and job description here
        System.out.println("Received Resume: " + resume);
        System.out.println("Received Job Description: " + jobDescription);

        return ResponseEntity.status(HttpStatus.OK).body("Data received successfully!");
    }
}

