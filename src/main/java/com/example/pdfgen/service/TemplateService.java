package com.example.pdfgen.service;

import com.example.pdfgen.model.CandidateProfile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.util.Locale;

/**
 * Service class for processing HTML templates using Thymeleaf
 */
@Service
@Slf4j
public class TemplateService {
    private final TemplateEngine templateEngine;

    public TemplateService() {
        this.templateEngine = createTemplateEngine();
        log.info("TemplateService initialized");
    }

    /**
     * Configures and creates a Thymeleaf template engine
     * Thread-safe with caching enabled for production use
     */
    private TemplateEngine createTemplateEngine() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML");
        templateResolver.setCharacterEncoding("UTF-8");
        // Enable caching for thread-safe concurrent access and better performance
        templateResolver.setCacheable(true);
        templateResolver.setCacheTTLMs(3600000L); // 1 hour cache TTL

        TemplateEngine engine = new TemplateEngine();
        engine.setTemplateResolver(templateResolver);

        return engine;
    }

    /**
     * Processes a template with the provided candidate profile data
     *
     * @param templateName    The name of the template file (without extension)
     * @param candidateProfile The candidate profile data to inject into the template
     * @return Processed HTML string with data injected
     */
    public String processTemplate(String templateName, CandidateProfile candidateProfile) {
        Context context = new Context(Locale.getDefault());

        // Add all profile fields to the context
        context.setVariable("name", candidateProfile.getName());
        context.setVariable("email", candidateProfile.getEmail());
        context.setVariable("phone", candidateProfile.getPhone());
        context.setVariable("location", candidateProfile.getLocation());
        context.setVariable("summary", candidateProfile.getSummary());
        context.setVariable("skills", candidateProfile.getSkills());
        context.setVariable("experience", candidateProfile.getExperience());
        context.setVariable("education", candidateProfile.getEducation());
        context.setVariable("certifications", candidateProfile.getCertifications());

        // Process the template
        return templateEngine.process(templateName, context);
    }

    /**
     * Processes a cover letter template with the provided data
     * Thread-safe: Creates new Context object per request
     * Supports UI JSON structure with nested objects
     *
     * @param templateName The name of the cover letter template file (without extension)
     * @param coverLetter  The cover letter data to inject into the template
     * @return Processed HTML string with data injected
     */
    public String processCoverLetterTemplate(String templateName, com.example.pdfgen.model.CoverLetter coverLetter) {
        Context context = new Context(Locale.getDefault());

        // Add header fields (candidate info)
        context.setVariable("name", coverLetter.getHeader().getName());
        context.setVariable("email", coverLetter.getHeader().getEmail());
        context.setVariable("phone", coverLetter.getHeader().getPhone());
        context.setVariable("date", coverLetter.getHeader().getDate());

        // Add recipient fields (optional)
        context.setVariable("recipientName", coverLetter.getRecipient() != null ? coverLetter.getRecipient().getName() : null);
        context.setVariable("companyName", coverLetter.getRecipient() != null ? coverLetter.getRecipient().getCompany() : null);
        context.setVariable("position", coverLetter.getRecipient() != null ? coverLetter.getRecipient().getPosition() : null);

        // Add salutation
        context.setVariable("salutation", coverLetter.getSalutation());

        // Add content (body paragraphs)
        context.setVariable("content", coverLetter.getContent());

        // Add closing fields
        context.setVariable("valediction", coverLetter.getClosing() != null ? coverLetter.getClosing().getValediction() : "Sincerely,");
        context.setVariable("signature", coverLetter.getClosing() != null ? coverLetter.getClosing().getName() : coverLetter.getHeader().getName());

        // Process the template
        return templateEngine.process(templateName, context);
    }
}