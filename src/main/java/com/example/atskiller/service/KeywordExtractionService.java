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
    private static final String OPENAI_API_KEY = "key"; // Store securely

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
                    "Extract folllowing keywords and phrases from this job description and resume as much as you can but meaningful." +
                            "name with key name,if no name then 'nodata' ," +
                            "structure of the resume. use keyword 'structure' : one of words(good,bad,normal)," +
                            "" +
                            "Only job related words. Use key 'keywordsjd'." +
                            "get all the keyword list(soft skills ,hard skills and job specific key words" +
                            " that have in job description but not in resume.Use key missingKeywords)\n" +
                            "Extract soft skills with key 'softskillsjd'.\n" +
                            "Extract hard skills with key 'hardskillsjd'.\n" +
                            "Total soft skills count: 'noofSoftskillsjd'.\n" +
                            "Total hard skills count: 'noofHardskillsjd'.\n" +
                            "Now extract relevant keywords from this extractedText (Resume). " +
                            "Use key 'keywordsre'.\n" +
                            "Extract matching soft skills from Resume to job description: 'softskillsre'.\n" +
                            "Extract matching hard skills from Resume to job description: 'hardskillsre'.\n" +
                            "Total matching soft skills count from Resume to job description: 'noofSoftskillsre'.\n" +
                            "Total matching hard skills count from Resume to job description: 'noofHardskillsre'.\n" +
                            "Find all the matching job specific words from both job description & resume (all skills" +
                            ",soft skills,hard skills,keywords).dont add if not available that word in job description or resume: 'matchingjdre'.\n" +
                            "Extract address if possible, else return 'nodata': 'address'.\n" +
                            "Extract email: 'email' if no data then 'nodata'.\n" +
                            "Extract LinkedIn URL: 'linkedin' if no data then 'nodata'.\n" +
                            "Extract phone number: 'phone' if no data then 'nodata'.\n" +
                            "Extract word count from Resume: 'wordcount'" +
                            "" +
                            "match and give overall keyword matching score .use key overallScore\n" +
                            "\n" +
                            "score should be out of 100%\n" +
                            "5% for structure of the resume\n" +
                            "50% for keyword matching. look strictly\n" +
                            "25% experience matching. look strictly\n" +
                            "5% education matching\n" +
                            "10% for better to have qualifications.look strictly " +
                            ".\n" +
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
