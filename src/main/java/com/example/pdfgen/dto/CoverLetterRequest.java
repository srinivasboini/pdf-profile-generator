package com.example.pdfgen.dto;

import com.example.pdfgen.model.CoverLetter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for cover letter PDF generation
 * Supports generic cover letters when recipient details are unknown
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoverLetterRequest {

    @NotBlank(message = "Template ID is required")
    private String templateId;

    @NotNull(message = "Cover letter data is required")
    @Valid
    private CoverLetter coverLetter;
}
