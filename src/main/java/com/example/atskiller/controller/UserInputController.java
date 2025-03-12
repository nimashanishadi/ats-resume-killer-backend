package com.example.atskiller.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.Loader;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173") // Allow requests from React app
public class UserInputController {

    @PostMapping(value = "/scan", consumes = {"multipart/form-data"})
    public ResponseEntity<String> processUserInput(
            @RequestPart("resume") MultipartFile resumeFile,
            @RequestPart("jobDescription") String jobDescription) {
        try {
            String extractedText = extractPdfText(resumeFile);
            System.out.println("Extracted Resume Text: " + extractedText);
            System.out.println("Received Job Description: " + jobDescription);

            return ResponseEntity.ok("Resume and Job Description received successfully!");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing PDF file");
        }
    }


    public static String extractPdfText(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream();
             PDDocument document = Loader.loadPDF(inputStream.readAllBytes())) {  // Use correct method for PDFBox 3.x
            return new PDFTextStripper().getText(document);
        } catch (IOException e) {
            throw new IOException("Error reading PDF file", e);
        }
    }
}
