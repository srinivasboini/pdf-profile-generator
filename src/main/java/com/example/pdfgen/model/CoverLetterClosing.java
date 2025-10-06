package com.example.pdfgen.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Closing information for cover letter
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoverLetterClosing {

    private String valediction;  // e.g., "Best regards", "Sincerely"
    private String name;         // Signature name (defaults to header.name if not provided)
}
