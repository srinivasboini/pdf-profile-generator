package com.example.pdfgen.controller.pdf;

import com.example.pdfgen.dto.CoverLetterRequest;
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
 * REST Controller for Cover Letter PDF generation
 */
@RestController
@RequestMapping("/api/pdf/cover-letter")
@RequiredArgsConstructor
@Slf4j
public class CoverLetterPdfController {

    private final TemplateService templateService;
    private final PdfGeneratorService pdfGeneratorService;

    /**
     * Generate cover letter PDF from cover letter data
     *
     * @param request Cover letter request containing template ID and cover letter data
     * @return PDF file as byte array
     */
    @PostMapping(value = "/generate", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> generateCoverLetterPdf(@Valid @RequestBody CoverLetterRequest request) {
        ByteArrayOutputStream outputStream = null;

        try {
            log.info("=== Cover Letter PDF Generation Request Received ===");
            log.info("Template ID: {}", request.getTemplateId());

            CoverLetter coverLetter = request.getCoverLetter();

            // Apply smart defaults for optional fields
            coverLetter.applyDefaults();

            log.info("Candidate Name: {}", coverLetter.getHeader().getName());
            log.info("Company: {}", coverLetter.getRecipient().getCompany());
            log.info("Recipient: {}", coverLetter.getRecipient().getName() != null ? coverLetter.getRecipient().getName() : "Generic (Hiring Manager)");
            log.info("Position: {}", coverLetter.getRecipient().getPosition());
            log.info("Salutation: {}", coverLetter.getSalutation());
            log.info("Content Paragraphs Count: {}", coverLetter.getContent() != null ? coverLetter.getContent().size() : 0);
            log.info("====================================================");

            // Process template with cover letter data
            String processedHtml = templateService.processCoverLetterTemplate(request.getTemplateId(), coverLetter);

            // Generate PDF
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
    @GetMapping("/templates")
    public ResponseEntity<?> getAvailableTemplates() {
        return ResponseEntity.ok(new String[]{
                "cover_letter_template_001",
                "cover_letter_template_002",
                "cover_letter_template_003"
        });
    }
}
