package com.example.pdfgen.controller;

import com.example.pdfgen.dto.CoverLetterRequest;
import com.example.pdfgen.dto.ProfileRequest;
import com.example.pdfgen.model.CandidateProfile;
import com.example.pdfgen.model.CoverLetter;
import com.example.pdfgen.service.PdfGeneratorService;
import com.example.pdfgen.service.TemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * REST Controller for PDF generation
 */
@RestController
@RequestMapping("/api/pdf")
@RequiredArgsConstructor
@Slf4j
public class PdfController {

    private final TemplateService templateService;
    private final PdfGeneratorService pdfGeneratorService;

    /**
     * Hello World test endpoint
     */
    @GetMapping("/hello")
    public ResponseEntity<String> helloWorld() {
        log.info("Hello World endpoint called");
        return ResponseEntity.ok("Hello World! PDF Generator API is working.");
    }

    /**
     * Generate PDF from profile data
     *
     * @param request Profile request containing template ID and profile data
     * @return PDF file as byte array
     */
    @PostMapping(value = "/generate", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> generatePdf(@Valid @RequestBody ProfileRequest request) {
        // Each request gets its own stream - thread-safe
        ByteArrayOutputStream outputStream = null;

        try {
            log.info("=== PDF Generation Request Received ===");
            log.info("Template ID: {}", request.getTemplateId());

            CandidateProfile profile = request.getProfile();
            log.info("Profile Name: {}", profile.getName());
            log.info("Profile Email: {}", profile.getEmail());
            log.info("Profile Location: {}", profile.getLocation());
            log.info("Skills Count: {}", profile.getSkills() != null ? profile.getSkills().size() : 0);
            log.info("Experience Count: {}", profile.getExperience() != null ? profile.getExperience().size() : 0);
            log.info("Education Count: {}", profile.getEducation() != null ? profile.getEducation().size() : 0);
            log.info("Certifications Count: {}", profile.getCertifications() != null ? profile.getCertifications().size() : 0);
            log.info("=======================================");

            // Process template with profile data (thread-safe with cached templates)
            String processedHtml = templateService.processTemplate(request.getTemplateId(), profile);

            // Generate PDF with request-scoped resources
            outputStream = new ByteArrayOutputStream();
            pdfGeneratorService.generatePdfToStream(processedHtml, outputStream);

            byte[] pdfBytes = outputStream.toByteArray();

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment",
                    profile.getName().replaceAll("\\s+", "_") + "_profile.pdf");
            headers.setContentLength(pdfBytes.length);

            log.info("PDF generated successfully for: {}", profile.getName());

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            log.error("Invalid input for PDF generation: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (IOException e) {
            log.error("IO error generating PDF: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            log.error("Unexpected error generating PDF: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } finally {
            // Ensure stream is properly closed
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    log.error("Error closing output stream: {}", e.getMessage());
                }
            }
        }
    }

    /**
     * Get available template IDs
     */
    @GetMapping("/templates")
    public ResponseEntity<?> getAvailableTemplates() {
        return ResponseEntity.ok(new String[]{
                "profile_template",
                "modern_profile_template",
                "minimalist_profile_template"
        });
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("PDF Generator Service is running");
    }

    /**
     * Generate cover letter PDF from cover letter data
     * Thread-safe endpoint with request-scoped resources
     *
     * @param request Cover letter request containing template ID and cover letter data
     * @return PDF file as byte array
     */
    @PostMapping(value = "/generate-cover-letter", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> generateCoverLetterPdf(@Valid @RequestBody CoverLetterRequest request) {
        // Each request gets its own stream - thread-safe
        ByteArrayOutputStream outputStream = null;

        try {
            log.info("=== Cover Letter PDF Generation Request Received ===");
            log.info("Template ID (original): {}", request.getTemplateId());

            CoverLetter coverLetter = request.getCoverLetter();

            // Apply smart defaults for optional fields (e.g., "Dear Hiring Manager" when recipient unknown)
            coverLetter.applyDefaults();

            // Normalize template ID: add "cover_letter_" prefix if not present
            String normalizedTemplateId = normalizeTemplateId(request.getTemplateId());
            log.info("Template ID (normalized): {}", normalizedTemplateId);

            log.info("Candidate Name: {}", coverLetter.getHeader().getName());
            log.info("Company: {}", coverLetter.getRecipient().getCompany());
            log.info("Recipient: {}", coverLetter.getRecipient().getName() != null ? coverLetter.getRecipient().getName() : "Generic (Hiring Manager)");
            log.info("Position: {}", coverLetter.getRecipient().getPosition());
            log.info("Salutation: {}", coverLetter.getSalutation());
            log.info("Content Paragraphs Count: {}", coverLetter.getContent() != null ? coverLetter.getContent().size() : 0);
            log.info("====================================================");

            // Process template with cover letter data (thread-safe with cached templates)
            String processedHtml = templateService.processCoverLetterTemplate(normalizedTemplateId, coverLetter);

            // Generate PDF with request-scoped resources
            outputStream = new ByteArrayOutputStream();
            pdfGeneratorService.generatePdfToStream(processedHtml, outputStream);

            byte[] pdfBytes = outputStream.toByteArray();

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment",
                    coverLetter.getHeader().getName().replaceAll("\\s+", "_") + "_cover_letter.pdf");
            headers.setContentLength(pdfBytes.length);

            log.info("Cover letter PDF generated successfully for: {}", coverLetter.getHeader().getName());

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            log.error("Invalid input for cover letter PDF generation: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (IOException e) {
            log.error("IO error generating cover letter PDF: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            log.error("Unexpected error generating cover letter PDF: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } finally {
            // Ensure stream is properly closed
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    log.error("Error closing output stream: {}", e.getMessage());
                }
            }
        }
    }

    /**
     * Get available cover letter template IDs
     */
    @GetMapping("/cover-letter-templates")
    public ResponseEntity<?> getAvailableCoverLetterTemplates() {
        return ResponseEntity.ok(new String[]{
                // Starter Templates
                "cover_letter_starter_001",
                "cover_letter_starter_002",
                "cover_letter_starter_003",
                // Professional Templates
                "cover_letter_professional_001",
                "cover_letter_professional_002",
                "cover_letter_professional_003",
                // Expert Templates
                "cover_letter_expert_001",
                "cover_letter_expert_002",
                "cover_letter_expert_003"
        });
    }

    /**
     * Normalize template ID to ensure it has the "cover_letter_" prefix
     * Supports both formats:
     * - "starter_template_001" -> "cover_letter_starter_001"
     * - "cover_letter_starter_001" -> "cover_letter_starter_001" (no change)
     *
     * @param templateId Original template ID from UI
     * @return Normalized template ID with cover_letter_ prefix
     */
    private String normalizeTemplateId(String templateId) {
        if (templateId == null || templateId.isEmpty()) {
            return templateId;
        }

        // Already has the cover_letter_ prefix
        if (templateId.startsWith("cover_letter_")) {
            return templateId;
        }

        // Convert patterns like "starter_template_001" to "cover_letter_starter_001"
        if (templateId.matches("(starter|professional|expert)_template_\\d{3}")) {
            return "cover_letter_" + templateId.replace("_template_", "_");
        }

        // If no match, just prepend cover_letter_
        return "cover_letter_" + templateId;
    }
}