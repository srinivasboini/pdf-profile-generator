package com.example.pdfgen.service;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Service class for generating PDFs from HTML using iText 7 pdfHTML
 */
@Service
@Slf4j
public class PdfGeneratorService {

    /**
     * Generates a PDF from HTML content and saves it to the specified file path
     *
     * @param htmlContent  The HTML content to convert to PDF
     * @param outputPath   The file path where the PDF should be saved
     * @throws IOException If there's an error writing the PDF file
     */
    public void generatePdf(String htmlContent, String outputPath) throws IOException {
        System.out.println("Generating PDF from HTML...");

        // Create a PdfWriter
        PdfWriter writer = new PdfWriter(new FileOutputStream(outputPath));

        // Create a PdfDocument
        PdfDocument pdfDocument = new PdfDocument(writer);

        // Set up converter properties (optional - for advanced configurations)
        ConverterProperties converterProperties = new ConverterProperties();

        // Convert HTML to PDF
        HtmlConverter.convertToPdf(htmlContent, pdfDocument, converterProperties);

        // Close the document
        pdfDocument.close();

        System.out.println("PDF generated successfully: " + outputPath);
    }

    /**
     * Generates a PDF directly from an HTML file
     *
     * @param htmlFilePath The path to the HTML file
     * @param outputPath   The file path where the PDF should be saved
     * @throws IOException If there's an error reading the HTML file or writing the PDF
     */
    public void generatePdfFromFile(String htmlFilePath, String outputPath) throws IOException {
        log.info("Generating PDF from HTML file: {}", htmlFilePath);

        HtmlConverter.convertToPdf(
            new java.io.FileInputStream(htmlFilePath),
            new FileOutputStream(outputPath)
        );

        log.info("PDF generated successfully: {}", outputPath);
    }

    /**
     * Generates a PDF from HTML content and writes it to an OutputStream
     *
     * @param htmlContent  The HTML content to convert to PDF
     * @param outputStream The output stream where the PDF should be written
     * @throws IOException If there's an error writing the PDF
     */
    public void generatePdfToStream(String htmlContent, OutputStream outputStream) throws IOException {
        log.debug("Generating PDF from HTML to stream");

        // Create a PdfWriter
        PdfWriter writer = new PdfWriter(outputStream);

        // Create a PdfDocument
        PdfDocument pdfDocument = new PdfDocument(writer);

        // Set up converter properties
        ConverterProperties converterProperties = new ConverterProperties();

        // Convert HTML to PDF
        HtmlConverter.convertToPdf(htmlContent, pdfDocument, converterProperties);

        // Close the document
        pdfDocument.close();

        log.debug("PDF generated successfully to stream");
    }
}