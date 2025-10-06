package com.example.pdfgen.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Model representing a cover letter for PDF generation
 * Matches UI JSON structure with nested objects
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoverLetter {

    @NotNull(message = "Header information is required")
    @Valid
    private CoverLetterHeader header;

    @Valid
    private CoverLetterRecipient recipient;  // Optional

    private String salutation;  // Optional: auto-generated if not provided (e.g., "Dear Hiring Manager")

    @NotEmpty(message = "Content paragraphs are required")
    private List<String> content;  // Body paragraphs

    @Valid
    private CoverLetterClosing closing;  // Optional

    /**
     * Apply smart defaults for optional fields when details are missing
     */
    public void applyDefaults() {
        // Initialize recipient if null
        if (recipient == null) {
            recipient = new CoverLetterRecipient();
        }

        // Default company name
        if (recipient.getCompany() == null || recipient.getCompany().trim().isEmpty()) {
            recipient.setCompany("Hiring Team");
        }

        // Default salutation based on available information
        if (salutation == null || salutation.trim().isEmpty()) {
            if (recipient.getName() != null && !recipient.getName().trim().isEmpty()) {
                salutation = "Dear " + recipient.getName();
            } else {
                salutation = "Dear Hiring Manager";
            }
        }

        // Ensure salutation doesn't have comma (UI might not include it)
        if (!salutation.endsWith(",") && !salutation.endsWith(":")) {
            salutation = salutation + ",";
        }

        // Initialize closing if null
        if (closing == null) {
            closing = new CoverLetterClosing();
        }

        // Default valediction
        if (closing.getValediction() == null || closing.getValediction().trim().isEmpty()) {
            closing.setValediction("Sincerely");
        }

        // Ensure valediction has comma
        if (!closing.getValediction().endsWith(",")) {
            closing.setValediction(closing.getValediction() + ",");
        }

        // Default closing name to header name
        if (closing.getName() == null || closing.getName().trim().isEmpty()) {
            closing.setName(header.getName());
        }
    }
}
