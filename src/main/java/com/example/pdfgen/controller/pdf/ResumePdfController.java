package com.example.pdfgen.controller.pdf;

import com.example.pdfgen.dto.ProfileRequest;
import com.example.pdfgen.model.CandidateProfile;
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
 * REST Controller for Resume PDF generation
 */
@RestController
@RequestMapping("/api/pdf/resume")
@RequiredArgsConstructor
@Slf4j
public class ResumePdfController {

    private final TemplateService templateService;
    private final PdfGeneratorService pdfGeneratorService;

    /**
     * Generate resume PDF from profile data
     *
     * @param request Profile request containing template ID and profile data
     * @return PDF file as byte array
     */
    @PostMapping(value = "/generate", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> generateResumePdf(@Valid @RequestBody ProfileRequest request) {
        ByteArrayOutputStream outputStream = null;

        try {
            log.info("=== Resume PDF Generation Request Received ===");
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

            // Process template with profile data
            String processedHtml = templateService.processTemplate(request.getTemplateId(), profile);

            // Generate PDF
            outputStream = new ByteArrayOutputStream();
            pdfGeneratorService.generatePdfToStream(processedHtml, outputStream);

            byte[] pdfBytes = outputStream.toByteArray();

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment",
                    profile.getName().replaceAll("\\s+", "_") + "_resume.pdf");
            headers.setContentLength(pdfBytes.length);

            log.info("Resume PDF generated successfully for: {}", profile.getName());

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            log.error("Invalid input for resume PDF generation: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (IOException e) {
            log.error("IO error generating resume PDF: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            log.error("Unexpected error generating resume PDF: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } finally {
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
     * Get available resume template IDs
     */
    @GetMapping("/templates")
    public ResponseEntity<?> getAvailableTemplates() {
        return ResponseEntity.ok(new String[]{
                "resume_template_001",
                "resume_template_002",
                "resume_template_003"
        });
    }
}
