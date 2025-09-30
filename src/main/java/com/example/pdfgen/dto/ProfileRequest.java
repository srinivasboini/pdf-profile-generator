package com.example.pdfgen.dto;

import com.example.pdfgen.model.CandidateProfile;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for PDF generation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileRequest {

    @NotBlank(message = "Template ID is required")
    private String templateId;

    @NotNull(message = "Profile data is required")
    private CandidateProfile profile;
}