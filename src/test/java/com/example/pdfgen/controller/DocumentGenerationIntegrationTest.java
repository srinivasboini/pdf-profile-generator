package com.example.pdfgen.controller;

import com.example.pdfgen.dto.CoverLetterRequest;
import com.example.pdfgen.dto.ProfileRequest;
import com.example.pdfgen.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for PDF and DOCX document generation
 * Tests the segregated controller structure
 */
@SpringBootTest
@AutoConfigureMockMvc
public class DocumentGenerationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private ProfileRequest testProfileRequest;
    private CoverLetterRequest testCoverLetterRequest;

    @BeforeEach
    public void setUp() {
        setupResumeTestData();
        setupCoverLetterTestData();
    }

    private void setupResumeTestData() {
        CandidateProfile profile = new CandidateProfile();
        profile.setName("John Doe");
        profile.setEmail("john.doe@example.com");
        profile.setPhone("+1-555-123-4567");
        profile.setLocation("San Francisco, CA");
        profile.setSummary("Experienced Software Engineer with 8+ years of expertise in full-stack development.");

        profile.setSkills(Arrays.asList("Java", "Spring Boot", "Python", "React", "AWS", "Docker"));

        Experience exp1 = new Experience();
        exp1.setTitle("Senior Software Engineer");
        exp1.setCompany("Tech Corp");
        exp1.setDuration("Jan 2020 - Present");
        exp1.setDescription("Led development of microservices architecture serving 5M+ users.");

        profile.setExperience(Arrays.asList(exp1));

        Education edu1 = new Education();
        edu1.setDegree("Master of Science in Computer Science");
        edu1.setInstitution("Stanford University");
        edu1.setYear("2015");

        profile.setEducation(Arrays.asList(edu1));
        profile.setCertifications(Arrays.asList("AWS Certified Solutions Architect"));

        testProfileRequest = new ProfileRequest();
        testProfileRequest.setTemplateId("resume_template_001");
        testProfileRequest.setProfile(profile);
    }

    private void setupCoverLetterTestData() {
        CoverLetterHeader header = new CoverLetterHeader();
        header.setName("Sarah Johnson");
        header.setEmail("sarah.johnson@example.com");
        header.setPhone("+1-555-987-6543");
        header.setDate(LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")));

        CoverLetterRecipient recipient = new CoverLetterRecipient();
        recipient.setName("Michael Chen");
        recipient.setPosition("Head of Engineering");
        recipient.setCompany("TechVentures Inc.");

        List<String> content = Arrays.asList(
                "I am writing to express my strong interest in the Senior Software Engineer position.",
                "In my current role, I have led the development of scalable systems."
        );

        CoverLetterClosing closing = new CoverLetterClosing();
        closing.setValediction("Sincerely,");
        closing.setName("Sarah Johnson");

        CoverLetter coverLetter = new CoverLetter();
        coverLetter.setHeader(header);
        coverLetter.setRecipient(recipient);
        coverLetter.setSalutation("Dear Mr. Chen,");
        coverLetter.setContent(content);
        coverLetter.setClosing(closing);

        testCoverLetterRequest = new CoverLetterRequest();
        testCoverLetterRequest.setTemplateId("cover_letter_template_001");
        testCoverLetterRequest.setCoverLetter(coverLetter);
    }

    // ============== PDF TESTS ==============

    @Test
    public void testResumePdfGeneration() throws Exception {
        String jsonRequest = objectMapper.writeValueAsString(testProfileRequest);

        MvcResult result = mockMvc.perform(post("/api/pdf/resume/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andReturn();

        byte[] pdfContent = result.getResponse().getContentAsByteArray();
        new java.io.File("target/test-output").mkdirs();
        try (FileOutputStream fos = new FileOutputStream("target/test-output/resume_pdf_test.pdf")) {
            fos.write(pdfContent);
        }

        System.out.println("✅ Resume PDF generated: target/test-output/resume_pdf_test.pdf");
    }

    @Test
    public void testCoverLetterPdfGeneration() throws Exception {
        String jsonRequest = objectMapper.writeValueAsString(testCoverLetterRequest);

        MvcResult result = mockMvc.perform(post("/api/pdf/cover-letter/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andReturn();

        byte[] pdfContent = result.getResponse().getContentAsByteArray();
        new java.io.File("target/test-output").mkdirs();
        try (FileOutputStream fos = new FileOutputStream("target/test-output/cover_letter_pdf_test.pdf")) {
            fos.write(pdfContent);
        }

        System.out.println("✅ Cover Letter PDF generated: target/test-output/cover_letter_pdf_test.pdf");
    }

    // ============== DOCX TESTS ==============

    @Test
    public void testResumeDocxGeneration() throws Exception {
        String jsonRequest = objectMapper.writeValueAsString(testProfileRequest);

        MvcResult result = mockMvc.perform(post("/api/docx/resume/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                .andReturn();

        byte[] docxContent = result.getResponse().getContentAsByteArray();
        new java.io.File("target/test-output").mkdirs();
        try (FileOutputStream fos = new FileOutputStream("target/test-output/resume_docx_test.docx")) {
            fos.write(docxContent);
        }

        System.out.println("✅ Resume DOCX generated: target/test-output/resume_docx_test.docx");
    }

    @Test
    public void testCoverLetterDocxGeneration() throws Exception {
        String jsonRequest = objectMapper.writeValueAsString(testCoverLetterRequest);

        MvcResult result = mockMvc.perform(post("/api/docx/cover-letter/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                .andReturn();

        byte[] docxContent = result.getResponse().getContentAsByteArray();
        new java.io.File("target/test-output").mkdirs();
        try (FileOutputStream fos = new FileOutputStream("target/test-output/cover_letter_docx_test.docx")) {
            fos.write(docxContent);
        }

        System.out.println("✅ Cover Letter DOCX generated: target/test-output/cover_letter_docx_test.docx");
    }
}
