package com.example.atskiller.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.json.JSONObject;

import java.util.Map;

@Service
public class KeywordExtractionService {

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";  // Correct API endpoint
    private static final String OPENAI_API_KEY = "key"; // Use a secure method for storing the key

    public String getKeywordsFromJobDescription(String jobDescription) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            // Prepare OpenAI API request
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + OPENAI_API_KEY);

            // Create JSON body manually using JSONObject to ensure proper formatting
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", "gpt-3.5-turbo");
            requestBody.put("max_tokens", 150);

            // Construct the messages array inside the request
            JSONObject message = new JSONObject();
            message.put("role", "user");
            message.put("content", "Extract relevant keywords from this job description: " + jobDescription);

            requestBody.put("messages", new JSONObject[] { message });

            // Convert JSONObject to string
            String requestBodyStr = requestBody.toString();

            HttpEntity<String> entity = new HttpEntity<>(requestBodyStr, headers);

            // Make the API call
            ResponseEntity<Map> response = restTemplate.exchange(
                    OPENAI_API_URL, HttpMethod.POST, entity, Map.class);

            // Log the OpenAI API response for debugging
            System.out.println("OpenAI Response: " + response.getBody());

            // Extract and return the keywords from the response
            if (response.getBody() != null && response.getBody().containsKey("choices")) {
                return response.getBody().get("choices").toString();
            } else {
                return "No keywords found";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error extracting keywords";
        }
    }
}
