package com.example.pdfgen.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Recipient information for cover letter (optional)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoverLetterRecipient {

    private String name;      // Optional: e.g., "Hiring Manager" or "Sarah Johnson"
    private String company;   // Optional: e.g., "TechCorp Inc."
    private String position;  // Optional: e.g., "Software Engineer" (the position applied for)
}
