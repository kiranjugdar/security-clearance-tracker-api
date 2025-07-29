package com.clearance.tracker.service;

import com.clearance.tracker.dto.PdfContent;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class MockPdfGeneratorService {

    private static final Logger logger = LoggerFactory.getLogger(MockPdfGeneratorService.class);

    public byte[] generatePdf(PdfContent pdfContent) {
        logger.info("Generating PDF for document: {} (Case: {})", pdfContent.getDocumentName(), pdfContent.getCaseId());
        
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Add document header
            document.add(new Paragraph(pdfContent.getDocumentName())
                .setFontSize(18)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph("\n"));

            // Add document metadata
            document.add(new Paragraph("Document Type: " + pdfContent.getDocumentType())
                .setFontSize(12));
            document.add(new Paragraph("Case ID: " + pdfContent.getCaseId())
                .setFontSize(12));
            document.add(new Paragraph("File Name: " + pdfContent.getFileName())
                .setFontSize(12));
            
            if (pdfContent.getUploadDate() != null) {
                document.add(new Paragraph("Upload Date: " + 
                    pdfContent.getUploadDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    .setFontSize(12));
            }
            
            if (pdfContent.getUploadedBy() != null) {
                document.add(new Paragraph("Uploaded By: " + pdfContent.getUploadedBy())
                    .setFontSize(12));
            }
            
            document.add(new Paragraph("Status: " + pdfContent.getStatus())
                .setFontSize(12));

            document.add(new Paragraph("\n" + "─".repeat(80) + "\n")
                .setFontSize(10));

            // Add document content
            String[] lines = pdfContent.getContent().split("\n");
            for (String line : lines) {
                if (line.trim().isEmpty()) {
                    document.add(new Paragraph(" ").setFontSize(8));
                } else if (line.trim().matches("^[A-Z\\s]+:?$")) {
                    // Headers in all caps
                    document.add(new Paragraph(line)
                        .setFontSize(14)
                        .setBold());
                } else if (line.startsWith("SECTION ") || line.startsWith("CONCLUSION") || 
                          line.startsWith("FINDINGS") || line.startsWith("RECOMMENDATA")) {
                    // Section headers
                    document.add(new Paragraph(line)
                        .setFontSize(12)
                        .setBold());
                } else {
                    // Regular content
                    document.add(new Paragraph(line)
                        .setFontSize(10));
                }
            }

            document.close();
            logger.info("Successfully generated PDF for document: {} ({} bytes)", 
                       pdfContent.getDocumentName(), baos.size());
            
            return baos.toByteArray();
            
        } catch (Exception e) {
            logger.error("Error generating PDF for document: {} (Case: {}). Error: {}", 
                        pdfContent.getDocumentName(), pdfContent.getCaseId(), e.getMessage(), e);
            throw new RuntimeException("Failed to generate PDF: " + e.getMessage(), e);
        }
    }
}