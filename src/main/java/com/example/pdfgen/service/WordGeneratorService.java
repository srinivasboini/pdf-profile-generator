package com.example.pdfgen.service;

import com.example.pdfgen.model.CandidateProfile;
import com.example.pdfgen.model.CoverLetter;
import com.example.pdfgen.model.Education;
import com.example.pdfgen.model.Experience;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;

/**
 * Service for generating Word documents (.docx) from profile and cover letter data
 */
@Service
@Slf4j
public class WordGeneratorService {

    /**
     * Strip HTML tags from text and convert to plain text
     * Handles common HTML entities and formatting
     */
    private String stripHtml(String html) {
        if (html == null || html.isEmpty()) {
            return html;
        }

        // Replace <br>, <br/>, <br /> with newlines
        String text = html.replaceAll("<br\\s*/?>", "\n");

        // Replace </li> with newline for list items
        text = text.replaceAll("</li>", "\n");

        // Replace <p> closing tags with double newline
        text = text.replaceAll("</p>", "\n\n");

        // Remove all remaining HTML tags
        text = text.replaceAll("<[^>]+>", "");

        // Decode common HTML entities
        text = text.replace("&nbsp;", " ");
        text = text.replace("&amp;", "&");
        text = text.replace("&lt;", "<");
        text = text.replace("&gt;", ">");
        text = text.replace("&quot;", "\"");
        text = text.replace("&#39;", "'");

        // Clean up multiple newlines
        text = text.replaceAll("\n{3,}", "\n\n");

        // Trim whitespace
        text = text.trim();

        return text;
    }

    /**
     * Generate resume Word document from candidate profile
     */
    public void generateResumeDocx(CandidateProfile profile, ByteArrayOutputStream outputStream) throws IOException {
        log.debug("Generating resume DOCX for: {}", profile.getName());

        XWPFDocument document = new XWPFDocument();

        try {
            // Header with name
            XWPFParagraph namePara = document.createParagraph();
            namePara.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun nameRun = namePara.createRun();
            nameRun.setText(profile.getName());
            nameRun.setBold(true);
            nameRun.setFontSize(24);
            nameRun.setFontFamily("Arial");

            // Contact information
            XWPFParagraph contactPara = document.createParagraph();
            contactPara.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun contactRun = contactPara.createRun();
            contactRun.setText(profile.getEmail() + " | " + profile.getPhone() + " | " + profile.getLocation());
            contactRun.setFontSize(11);
            contactRun.setFontFamily("Arial");

            addSpacing(document);

            // Professional Summary
            if (profile.getSummary() != null && !profile.getSummary().isEmpty()) {
                addSectionHeading(document, "PROFESSIONAL SUMMARY");
                XWPFParagraph summaryPara = document.createParagraph();
                XWPFRun summaryRun = summaryPara.createRun();
                summaryRun.setText(stripHtml(profile.getSummary()));
                summaryRun.setFontSize(11);
                summaryRun.setFontFamily("Arial");
                addSpacing(document);
            }

            // Skills
            if (profile.getSkills() != null && !profile.getSkills().isEmpty()) {
                addSectionHeading(document, "SKILLS");
                XWPFParagraph skillsPara = document.createParagraph();
                XWPFRun skillsRun = skillsPara.createRun();
                skillsRun.setText(String.join(" • ", profile.getSkills()));
                skillsRun.setFontSize(11);
                skillsRun.setFontFamily("Arial");
                addSpacing(document);
            }

            // Experience
            if (profile.getExperience() != null && !profile.getExperience().isEmpty()) {
                addSectionHeading(document, "PROFESSIONAL EXPERIENCE");
                for (Experience exp : profile.getExperience()) {
                    // Job title and company
                    XWPFParagraph expPara = document.createParagraph();
                    XWPFRun titleRun = expPara.createRun();
                    titleRun.setText(exp.getTitle() + " - " + exp.getCompany());
                    titleRun.setBold(true);
                    titleRun.setFontSize(12);
                    titleRun.setFontFamily("Arial");

                    // Duration
                    XWPFParagraph durPara = document.createParagraph();
                    XWPFRun durRun = durPara.createRun();
                    durRun.setText(exp.getDuration());
                    durRun.setItalic(true);
                    durRun.setFontSize(10);
                    durRun.setFontFamily("Arial");

                    // Description (strip HTML and handle bullet points)
                    if (exp.getDescription() != null && !exp.getDescription().isEmpty()) {
                        String cleanDescription = stripHtml(exp.getDescription());

                        // Split by newlines to handle multiple bullet points
                        String[] lines = cleanDescription.split("\n");
                        for (String line : lines) {
                            line = line.trim();
                            if (!line.isEmpty()) {
                                XWPFParagraph descPara = document.createParagraph();
                                descPara.setIndentationLeft(720); // 0.5 inch indent
                                XWPFRun descRun = descPara.createRun();
                                // Add bullet if not already present
                                if (!line.startsWith("•") && !line.startsWith("-") && !line.startsWith("*")) {
                                    descRun.setText("• " + line);
                                } else {
                                    descRun.setText(line.replaceFirst("^[-*]", "•"));
                                }
                                descRun.setFontSize(11);
                                descRun.setFontFamily("Arial");
                            }
                        }
                    }
                    addSpacing(document);
                }
            }

            // Education
            if (profile.getEducation() != null && !profile.getEducation().isEmpty()) {
                addSectionHeading(document, "EDUCATION");
                for (Education edu : profile.getEducation()) {
                    XWPFParagraph eduPara = document.createParagraph();
                    XWPFRun degreeRun = eduPara.createRun();
                    degreeRun.setText(edu.getDegree() + " - " + edu.getInstitution());
                    degreeRun.setBold(true);
                    degreeRun.setFontSize(12);
                    degreeRun.setFontFamily("Arial");

                    XWPFParagraph gradPara = document.createParagraph();
                    XWPFRun gradRun = gradPara.createRun();
                    gradRun.setText(edu.getYear());
                    gradRun.setItalic(true);
                    gradRun.setFontSize(10);
                    gradRun.setFontFamily("Arial");
                    addSpacing(document);
                }
            }

            // Certifications
            if (profile.getCertifications() != null && !profile.getCertifications().isEmpty()) {
                addSectionHeading(document, "CERTIFICATIONS");
                for (String cert : profile.getCertifications()) {
                    XWPFParagraph certPara = document.createParagraph();
                    certPara.setIndentationLeft(360);
                    XWPFRun certRun = certPara.createRun();
                    certRun.setText("• " + cert);
                    certRun.setFontSize(11);
                    certRun.setFontFamily("Arial");
                }
            }

            document.write(outputStream);
            log.debug("Resume DOCX generated successfully");

        } finally {
            document.close();
        }
    }

    /**
     * Generate cover letter Word document
     */
    public void generateCoverLetterDocx(CoverLetter coverLetter, ByteArrayOutputStream outputStream) throws IOException {
        log.debug("Generating cover letter DOCX for: {}", coverLetter.getHeader().getName());

        XWPFDocument document = new XWPFDocument();

        try {
            // Header - Candidate info
            XWPFParagraph headerPara = document.createParagraph();
            headerPara.setAlignment(ParagraphAlignment.LEFT);
            XWPFRun nameRun = headerPara.createRun();
            nameRun.setText(coverLetter.getHeader().getName());
            nameRun.setBold(true);
            nameRun.setFontSize(14);
            nameRun.setFontFamily("Arial");
            nameRun.addBreak();

            XWPFRun contactRun = headerPara.createRun();
            contactRun.setText(coverLetter.getHeader().getEmail());
            contactRun.setFontSize(11);
            contactRun.setFontFamily("Arial");
            contactRun.addBreak();

            if (coverLetter.getHeader().getPhone() != null) {
                contactRun.setText(coverLetter.getHeader().getPhone());
                contactRun.addBreak();
            }

            addSpacing(document);

            // Date
            XWPFParagraph datePara = document.createParagraph();
            XWPFRun dateRun = datePara.createRun();
            dateRun.setText(coverLetter.getHeader().getDate());
            dateRun.setFontSize(11);
            dateRun.setFontFamily("Arial");

            addSpacing(document);

            // Recipient
            if (coverLetter.getRecipient() != null) {
                XWPFParagraph recipientPara = document.createParagraph();
                XWPFRun recipientRun = recipientPara.createRun();

                if (coverLetter.getRecipient().getName() != null) {
                    recipientRun.setText(coverLetter.getRecipient().getName());
                    recipientRun.addBreak();
                }

                if (coverLetter.getRecipient().getPosition() != null) {
                    recipientRun.setText(coverLetter.getRecipient().getPosition());
                    recipientRun.addBreak();
                }

                if (coverLetter.getRecipient().getCompany() != null) {
                    recipientRun.setText(coverLetter.getRecipient().getCompany());
                    recipientRun.addBreak();
                }

                recipientRun.setFontSize(11);
                recipientRun.setFontFamily("Arial");

                addSpacing(document);
            }

            // Salutation
            XWPFParagraph salutationPara = document.createParagraph();
            XWPFRun salutationRun = salutationPara.createRun();
            salutationRun.setText(coverLetter.getSalutation());
            salutationRun.setFontSize(11);
            salutationRun.setFontFamily("Arial");

            addSpacing(document);

            // Body paragraphs
            if (coverLetter.getContent() != null) {
                for (String paragraph : coverLetter.getContent()) {
                    XWPFParagraph bodyPara = document.createParagraph();
                    bodyPara.setAlignment(ParagraphAlignment.BOTH);
                    XWPFRun bodyRun = bodyPara.createRun();
                    bodyRun.setText(stripHtml(paragraph));
                    bodyRun.setFontSize(11);
                    bodyRun.setFontFamily("Arial");
                    addSpacing(document);
                }
            }

            // Closing
            XWPFParagraph closingPara = document.createParagraph();
            XWPFRun closingRun = closingPara.createRun();
            String valediction = coverLetter.getClosing() != null ?
                    coverLetter.getClosing().getValediction() : "Sincerely,";
            closingRun.setText(valediction);
            closingRun.setFontSize(11);
            closingRun.setFontFamily("Arial");

            addSpacing(document);
            addSpacing(document);

            // Signature
            XWPFParagraph signaturePara = document.createParagraph();
            XWPFRun signatureRun = signaturePara.createRun();
            String signature = coverLetter.getClosing() != null ?
                    coverLetter.getClosing().getName() : coverLetter.getHeader().getName();
            signatureRun.setText(signature);
            signatureRun.setFontSize(11);
            signatureRun.setFontFamily("Arial");

            document.write(outputStream);
            log.debug("Cover letter DOCX generated successfully");

        } finally {
            document.close();
        }
    }

    /**
     * Add a section heading with underline
     */
    private void addSectionHeading(XWPFDocument document, String headingText) {
        XWPFParagraph heading = document.createParagraph();
        XWPFRun headingRun = heading.createRun();
        headingRun.setText(headingText);
        headingRun.setBold(true);
        headingRun.setFontSize(14);
        headingRun.setFontFamily("Arial");

        // Add bottom border (underline effect)
        CTPPr pPr = heading.getCTP().getPPr();
        if (pPr == null) pPr = heading.getCTP().addNewPPr();
        CTPBdr pBdr = pPr.getPBdr();
        if (pBdr == null) pBdr = pPr.addNewPBdr();
        CTBorder border = pBdr.addNewBottom();
        border.setVal(STBorder.SINGLE);
        border.setSz(BigInteger.valueOf(6));
        border.setSpace(BigInteger.valueOf(1));
        border.setColor("000000");
    }

    /**
     * Add spacing between sections
     */
    private void addSpacing(XWPFDocument document) {
        XWPFParagraph spacer = document.createParagraph();
        XWPFRun spacerRun = spacer.createRun();
        spacerRun.setFontSize(6);
    }
}
