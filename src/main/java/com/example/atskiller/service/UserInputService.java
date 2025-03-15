package com.example.atskiller.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.Loader;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserInputService {

    private final KeywordExtractionService keywordExtractionService;  // Injecting the KeywordExtractionService

    // Constructor-based injection for KeywordExtractionService
    public UserInputService(KeywordExtractionService keywordExtractionService) {
        this.keywordExtractionService = keywordExtractionService;
    }

    public Map<String, String> processUserInput(MultipartFile resumeFile, String jobDescription) throws IOException {
        String extractedText = extractPdfText(resumeFile);
        System.out.println("Extracted Resume Text: " + extractedText);
        System.out.println("Received Job Description: " + jobDescription);

        // Call the KeywordExtractionService to get keywords from the job description
        String keywords = keywordExtractionService.getKeywordsFromJobDescription(jobDescription);

        // Prepare response data
        Map<String, String> response = new HashMap<>();
        response.put("resumeText", extractedText);
        response.put("jobDescription", jobDescription);
        response.put("keywords", keywords);

        return response;
    }

    private String extractPdfText(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream();
             PDDocument document = Loader.loadPDF(inputStream.readAllBytes())) {
            return new PDFTextStripper().getText(document);
        } catch (IOException e) {
            throw new IOException("Error reading PDF file", e);
        }
    }
}
