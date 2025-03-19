package com.example.atskiller.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;

import java.util.Map;

@Service
public class KeywordExtractionService {

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String OPENAI_API_KEY = "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"; // Store securely

    public JSONObject getKeywordsFromJobDescription(String jobDescription, String extractedText) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            // Prepare headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + OPENAI_API_KEY);

            // Construct request body
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", "gpt-4-turbo");
            requestBody.put("max_tokens", 4000);

            // Construct messages array
            JSONArray messages = new JSONArray();
            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");
            userMessage.put("content",
                    "Extract relevant keywords from this job description. " +
                            "Only job-specific keywords. Use key 'keywordsjd'.\n" +
                            "Extract soft skills with key 'softskillsjd'.\n" +
                            "Extract hard skills with key 'hardskillsjd'.\n" +
                            "Total soft skills count: 'noofSoftskillsjd'.\n" +
                            "Total hard skills count: 'noofHardskillsjd'.\n" +
                            "Now extract relevant keywords from this extractedText (Resume). " +
                            "Use key 'keywordsre'.\n" +
                            "Extract soft skills from Resume: 'softskillsre'.\n" +
                            "Extract hard skills from Resume: 'hardskillsre'.\n" +
                            "Total soft skills count: 'noofSoftskillsre'.\n" +
                            "Total hard skills count: 'noofHardskillsre'.\n" +
                            "Find matching keywords from job description & resume: 'matchingjdre'.\n" +
                            "Extract address if possible, else return 'nodata': 'address'.\n" +
                            "Extract email: 'email'.\n" +
                            "Extract LinkedIn URL: 'linkedin'.\n" +
                            "Extract phone number: 'phone'.\n" +
                            "Extract word count from Resume: 'wordcount'.\n" +
                            "Job Description: " + jobDescription + "\n" +
                            "Resume: " + extractedText);

            messages.put(userMessage);
            requestBody.put("messages", messages);

            // Send request
            HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);
            ResponseEntity<Map> response = restTemplate.exchange(
                    OPENAI_API_URL, HttpMethod.POST, entity, Map.class);

            // Extract JSON response
            if (response.getBody() != null && response.getBody().containsKey("choices")) {
                Map<String, Object> choice = (Map<String, Object>) ((java.util.List) response.getBody().get("choices")).get(0);
                Map<String, Object> message = (Map<String, Object>) choice.get("message");

                if (message.containsKey("content")) {
                    String jsonResponse = (String) message.get("content");

                    // Ensure the response is valid JSON (remove backticks if needed)
                    jsonResponse = jsonResponse.replaceAll("```json", "").replaceAll("```", "").trim();

                    // Parse JSON response to access structured data
                    JSONObject jsonObject = new JSONObject(jsonResponse);

                    // Print out the structured JSON object for debugging
                    System.out.println("Extracted JSON Response:");
                    System.out.println(jsonObject.toString(4));  // Pretty print with indentation of 4 spaces

                    return jsonObject; // Return the parsed JSON object
                }
            }
            return new JSONObject().put("error", "No valid JSON response found");
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject().put("error", "Error extracting keywords");
        }
    }

}
