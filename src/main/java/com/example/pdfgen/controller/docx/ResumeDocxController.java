package com.example.pdfgen.controller.docx;

import com.example.pdfgen.dto.ProfileRequest;
import com.example.pdfgen.model.CandidateProfile;
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
 * REST Controller for Resume DOCX (Microsoft Word) generation
 */
@RestController
@RequestMapping("/api/docx/resume")
@RequiredArgsConstructor
@Slf4j
public class ResumeDocxController {

    private final WordGeneratorService wordGeneratorService;

    /**
     * Generate resume Word document from profile data
     *
     * @param request Profile request containing profile data
     * @return DOCX file as byte array
     */
    @PostMapping(value = "/generate", produces = "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
    public ResponseEntity<byte[]> generateResumeDocx(@Valid @RequestBody ProfileRequest request) {
        ByteArrayOutputStream outputStream = null;

        try {
            log.info("=== Resume DOCX Generation Request Received ===");
            CandidateProfile profile = request.getProfile();
            log.info("Profile Name: {}", profile.getName());
            log.info("=======================================");

            // Generate DOCX
            outputStream = new ByteArrayOutputStream();
            wordGeneratorService.generateResumeDocx(profile, outputStream);

            byte[] docxBytes = outputStream.toByteArray();

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
            headers.setContentDispositionFormData("attachment",
                    profile.getName().replaceAll("\\s+", "_") + "_resume.docx");
            headers.setContentLength(docxBytes.length);

            log.info("Resume DOCX generated successfully for: {}", profile.getName());

            return new ResponseEntity<>(docxBytes, headers, HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            log.error("Invalid input for resume DOCX generation: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (IOException e) {
            log.error("IO error generating resume DOCX: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            log.error("Unexpected error generating resume DOCX: {}", e.getMessage(), e);
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
