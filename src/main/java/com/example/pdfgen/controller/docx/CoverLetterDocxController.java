package com.example.pdfgen.controller.docx;

import com.example.pdfgen.dto.CoverLetterRequest;
import com.example.pdfgen.model.CoverLetter;
import com.example.pdfgen.service.WordGeneratorService;
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
 * REST Controller for Cover Letter DOCX (Microsoft Word) generation
 */
@RestController
@RequestMapping("/api/docx/cover-letter")
@RequiredArgsConstructor
@Slf4j
public class CoverLetterDocxController {

    private final WordGeneratorService wordGeneratorService;

    /**
     * Generate cover letter Word document from cover letter data
     *
     * @param request Cover letter request containing cover letter data
     * @return DOCX file as byte array
     */
    @PostMapping(value = "/generate", produces = "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
    public ResponseEntity<byte[]> generateCoverLetterDocx(@Valid @RequestBody CoverLetterRequest request) {
        ByteArrayOutputStream outputStream = null;

        try {
            log.info("=== Cover Letter DOCX Generation Request Received ===");
            CoverLetter coverLetter = request.getCoverLetter();

            // Apply smart defaults for optional fields
            coverLetter.applyDefaults();

            log.info("Candidate Name: {}", coverLetter.getHeader().getName());
            log.info("Company: {}", coverLetter.getRecipient().getCompany());
            log.info("====================================================");

            // Generate DOCX
            outputStream = new ByteArrayOutputStream();
            wordGeneratorService.generateCoverLetterDocx(coverLetter, outputStream);

            byte[] docxBytes = outputStream.toByteArray();

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
            headers.setContentDispositionFormData("attachment",
                    coverLetter.getHeader().getName().replaceAll("\\s+", "_") + "_cover_letter.docx");
            headers.setContentLength(docxBytes.length);

            log.info("Cover letter DOCX generated successfully for: {}", coverLetter.getHeader().getName());

            return new ResponseEntity<>(docxBytes, headers, HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            log.error("Invalid input for cover letter DOCX generation: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (IOException e) {
            log.error("IO error generating cover letter DOCX: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            log.error("Unexpected error generating cover letter DOCX: {}", e.getMessage(), e);
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
}
