package com.example.pdfgen.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Header information for cover letter (candidate details)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoverLetterHeader {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Phone is required")
    private String phone;

    @NotBlank(message = "Date is required")
    private String date;
}
