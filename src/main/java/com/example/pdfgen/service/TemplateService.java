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
     */
    private TemplateEngine createTemplateEngine() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML");
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setCacheable(false); // Disable cache for development

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
}