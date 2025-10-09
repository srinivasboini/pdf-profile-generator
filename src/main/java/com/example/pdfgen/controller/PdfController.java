package com.example.pdfgen.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for PDF generation - Main entry point
 */
@RestController
@RequestMapping("/api/pdf")
@Slf4j
public class PdfController {

    /**
     * Hello World test endpoint
     */
    @GetMapping("/hello")
    public ResponseEntity<String> helloWorld() {
        log.info("Hello World endpoint called");
        return ResponseEntity.ok("Hello World! PDF Generator API is working.");
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("PDF Generator Service is running");
    }
}
