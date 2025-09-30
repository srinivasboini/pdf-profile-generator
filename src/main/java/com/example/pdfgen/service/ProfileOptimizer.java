package com.example.pdfgen.service;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for optimizing candidate profiles using OpenAI API
 */
@Service
@Slf4j
public class ProfileOptimizer {
    private final OpenAiService openAiService;
    private final String model;

    public ProfileOptimizer(
            @Value("${openai.api.key:DUMMY_KEY}") String apiKey,
            @Value("${openai.api.model:gpt-5}") String model,
            @Value("${openai.api.timeout:180}") int timeoutSeconds) {
        this.openAiService = new OpenAiService(apiKey, Duration.ofSeconds(timeoutSeconds));
        this.model = model;
        log.info("ProfileOptimizer initialized with model: {}", model);
    }

    /**
     * Optimizes a candidate profile based on a job description using OpenAI
     *
     * @param candidateProfile The original candidate profile as JSON string
     * @param jobDescription   The target job description
     * @return Optimized profile content with enhanced summary, skills, and experience descriptions
     */
    public String optimizeProfile(String candidateProfile, String jobDescription) {
        log.info("Optimizing profile with OpenAI using model: {}", model);

        List<ChatMessage> messages = new ArrayList<>();

        // System message to set the context
        ChatMessage systemMessage = new ChatMessage(
            ChatMessageRole.SYSTEM.value(),
            "You are an expert career coach and resume writer. Your task is to optimize candidate profiles " +
            "to match job descriptions while maintaining truthfulness and highlighting relevant experience and skills."
        );
        messages.add(systemMessage);

        // User message with the optimization request
        String userPrompt = String.format(
            "Please optimize the following candidate profile for this job description:\n\n" +
            "=== JOB DESCRIPTION ===\n%s\n\n" +
            "=== CANDIDATE PROFILE ===\n%s\n\n" +
            "=== INSTRUCTIONS ===\n" +
            "1. Enhance the professional summary to highlight skills and experience most relevant to this job\n" +
            "2. Prioritize skills that match the job requirements\n" +
            "3. Rewrite experience descriptions to emphasize relevant achievements and technologies\n" +
            "4. Keep all information truthful - only reframe and emphasize, don't add false information\n" +
            "5. Return ONLY the optimized profile content as a JSON object with the same structure as the input\n" +
            "6. Maintain professional tone throughout\n\n" +
            "Return the complete optimized profile in JSON format.",
            jobDescription,
            candidateProfile
        );

        ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(), userPrompt);
        messages.add(userMessage);

        // Create the chat completion request
        ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
            .model(model)
            .messages(messages)
            //.temperature(0.7)
            //.maxTokens(2000)
            .build();

        // Execute the request and get the response
        try {
            String response = openAiService.createChatCompletion(completionRequest)
                .getChoices()
                .get(0)
                .getMessage()
                .getContent();

            log.info("Profile optimization complete!");
            return response;

        } catch (Exception e) {
            log.error("Error calling OpenAI API: {}", e.getMessage());
            throw new RuntimeException("Failed to optimize profile with OpenAI", e);
        }
    }

    /**
     * Closes the OpenAI service and releases resources
     */
    public void close() {
        if (openAiService != null) {
            openAiService.shutdownExecutor();
        }
    }
}