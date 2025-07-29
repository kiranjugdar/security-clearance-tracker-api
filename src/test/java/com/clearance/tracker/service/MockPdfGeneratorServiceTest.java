package com.clearance.tracker.service;

import com.clearance.tracker.dto.PdfContent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MockPdfGeneratorServiceTest {

    private MockPdfGeneratorService mockPdfGeneratorService;

    private static final String CASE_ID = "25092CASE1329752";
    private static final String DOCUMENT_NAME = "Security Clearance Application Form";
    private static final String DOCUMENT_TYPE = "Application";
    private static final String FILE_NAME = "SF-86_" + CASE_ID + ".pdf";
    private static final String UPLOADED_BY = "John Smith";
    private static final String STATUS = "submitted";

    @BeforeEach
    void setUp() {
        mockPdfGeneratorService = new MockPdfGeneratorService();
    }

    @Test
    void testGeneratePdf_Success() {
        // Arrange
        String content = "SECURITY CLEARANCE APPLICATION FORM\n\n" +
                        "Case ID: " + CASE_ID + "\n\n" +
                        "SECTION 1: PERSONAL INFORMATION\n" +
                        "Full Name: John A. Smith\n" +
                        "Date of Birth: 01/15/1985\n" +
                        "SSN: XXX-XX-1234\n" +
                        "Place of Birth: Washington, DC\n\n" +
                        "SECTION 2: EMPLOYMENT HISTORY\n" +
                        "Current Employer: Defense Contractor Inc.\n" +
                        "Position: Software Engineer\n" +
                        "Start Date: 03/2020\n\n" +
                        "CONCLUSION\n" +
                        "Application submitted for review.";

        PdfContent pdfContent = new PdfContent(1L, CASE_ID, DOCUMENT_NAME, DOCUMENT_TYPE, FILE_NAME,
                                              content, LocalDateTime.now(), UPLOADED_BY, STATUS);

        // Act
        byte[] result = mockPdfGeneratorService.generatePdf(pdfContent);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0, "PDF should contain data");
        
        // Verify PDF header (starts with %PDF)
        String pdfHeader = new String(result, 0, Math.min(8, result.length));
        assertTrue(pdfHeader.startsWith("%PDF"), "Result should be a valid PDF file");
        
        // Verify minimum PDF size (should be at least a few hundred bytes for a basic PDF)
        assertTrue(result.length > 200, "PDF should be reasonably sized, got " + result.length + " bytes");
    }

    @Test
    void testGeneratePdf_WithAllFields() {
        // Arrange
        LocalDateTime uploadDate = LocalDateTime.of(2025, 7, 29, 10, 30, 0);
        String content = "BACKGROUND INVESTIGATION REPORT\n\n" +
                        "Subject: John A. Smith\n" +
                        "Case Number: " + CASE_ID + "\n\n" +
                        "FINDINGS:\n" +
                        "1. EMPLOYMENT VERIFICATION\n" +
                        "- All employment history verified\n" +
                        "- No gaps in employment found\n\n" +
                        "RECOMMENDATA: Subject meets all requirements.";

        PdfContent pdfContent = new PdfContent(2L, CASE_ID, "Background Investigation Report", 
                                              "Investigation", "BIR_" + CASE_ID + ".pdf",
                                              content, uploadDate, "Agent Mary Johnson", "completed");

        // Act
        byte[] result = mockPdfGeneratorService.generatePdf(pdfContent);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        
        // Verify PDF structure
        String pdfHeader = new String(result, 0, Math.min(8, result.length));
        assertTrue(pdfHeader.startsWith("%PDF"));
        
        // Should be larger than basic PDF due to more content
        assertTrue(result.length > 500, "PDF with full content should be larger");
    }

    @Test
    void testGeneratePdf_WithNullUploadDate() {
        // Arrange
        String content = "MEDICAL CLEARANCE CERTIFICATE\n\n" +
                        "Patient: John A. Smith\n" +
                        "Examination Date: February 28, 2025\n\n" +
                        "CONCLUSION:\n" +
                        "Patient is medically cleared.";

        PdfContent pdfContent = new PdfContent(3L, CASE_ID, "Medical Clearance Certificate", 
                                              "Medical", "MCC_" + CASE_ID + ".pdf",
                                              content, null, "Dr. Emily Carter", "approved");

        // Act
        byte[] result = mockPdfGeneratorService.generatePdf(pdfContent);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        
        String pdfHeader = new String(result, 0, Math.min(8, result.length));
        assertTrue(pdfHeader.startsWith("%PDF"));
    }

    @Test
    void testGeneratePdf_WithNullUploadedBy() {
        // Arrange
        String content = "SECURITY INTERVIEW TRANSCRIPT\n\n" +
                        "Date: February 25, 2025\n" +
                        "Subject: John A. Smith\n\n" +
                        "INTERVIEW TRANSCRIPT:\n" +
                        "AGENT: Please state your name.\n" +
                        "SMITH: John Alexander Smith\n\n" +
                        "CONCLUSION: No security concerns identified.";

        PdfContent pdfContent = new PdfContent(4L, CASE_ID, "Security Interview Transcript", 
                                              "Interview", "SIT_" + CASE_ID + ".pdf",
                                              content, LocalDateTime.now(), null, "completed");

        // Act
        byte[] result = mockPdfGeneratorService.generatePdf(pdfContent);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        
        String pdfHeader = new String(result, 0, Math.min(8, result.length));
        assertTrue(pdfHeader.startsWith("%PDF"));
    }

    @Test
    void testGeneratePdf_WithEmptyContent() {
        // Arrange
        PdfContent pdfContent = new PdfContent(5L, CASE_ID, "Empty Document", 
                                              "Test", "EMPTY_" + CASE_ID + ".pdf",
                                              "", LocalDateTime.now(), UPLOADED_BY, STATUS);

        // Act
        byte[] result = mockPdfGeneratorService.generatePdf(pdfContent);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        
        String pdfHeader = new String(result, 0, Math.min(8, result.length));
        assertTrue(pdfHeader.startsWith("%PDF"));
        
        // Should still generate a valid PDF even with empty content
        assertTrue(result.length > 100, "PDF should still have basic structure");
    }

    @Test
    void testGeneratePdf_WithSpecialCharacters() {
        // Arrange
        String content = "SPECIAL CHARACTERS TEST\n\n" +
                        "Name: José María González-Smith\n" +
                        "Address: 123 Main St, Apt #4B\n" +
                        "Notes: Subject has traveled to café in Montréal\n" +
                        "Status: 100% cleared ✓\n\n" +
                        "Special symbols: @#$%^&*()_+-=[]{}|;':\",./<>?";

        PdfContent pdfContent = new PdfContent(6L, CASE_ID, "Special Characters Document", 
                                              "Test", "SPECIAL_" + CASE_ID + ".pdf",
                                              content, LocalDateTime.now(), UPLOADED_BY, STATUS);

        // Act
        byte[] result = mockPdfGeneratorService.generatePdf(pdfContent);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        
        String pdfHeader = new String(result, 0, Math.min(8, result.length));
        assertTrue(pdfHeader.startsWith("%PDF"));
    }

    @Test
    void testGeneratePdf_WithLongContent() {
        // Arrange
        StringBuilder longContent = new StringBuilder();
        longContent.append("LONG DOCUMENT TEST\n\n");
        
        for (int i = 1; i <= 50; i++) {
            longContent.append("SECTION ").append(i).append(": TEST SECTION\n");
            longContent.append("This is section number ").append(i).append(" of the test document.\n");
            longContent.append("It contains multiple lines of text to test PDF generation with longer content.\n");
            longContent.append("Line 1 of section ").append(i).append("\n");
            longContent.append("Line 2 of section ").append(i).append("\n");
            longContent.append("Line 3 of section ").append(i).append("\n\n");
        }
        
        longContent.append("CONCLUSION\n");
        longContent.append("This document contains ").append(50).append(" sections for testing purposes.");

        PdfContent pdfContent = new PdfContent(7L, CASE_ID, "Long Document Test", 
                                              "Test", "LONG_" + CASE_ID + ".pdf",
                                              longContent.toString(), LocalDateTime.now(), UPLOADED_BY, STATUS);

        // Act
        byte[] result = mockPdfGeneratorService.generatePdf(pdfContent);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        
        String pdfHeader = new String(result, 0, Math.min(8, result.length));
        assertTrue(pdfHeader.startsWith("%PDF"));
        
        // Should be significantly larger due to long content
        assertTrue(result.length > 2000, "Long document should generate larger PDF, got " + result.length + " bytes");
    }

    @Test
    void testGeneratePdf_WithDifferentFormattingPatterns() {
        // Arrange
        String content = "FORMATTING TEST DOCUMENT\n\n" +
                        "SECTION 1: HEADERS\n" +
                        "This tests section headers.\n\n" +
                        "FINDINGS:\n" +
                        "This tests findings headers.\n\n" +
                        "CONCLUSION:\n" +
                        "This tests conclusion headers.\n\n" +
                        "RECOMMENDATA:\n" +
                        "This tests recommendation headers.\n\n" +
                        "Regular paragraph text that should be formatted normally.\n" +
                        "Another regular line.\n\n" +
                        "ALL CAPS HEADER WITHOUT COLON\n" +
                        "Text after all caps header.\n\n" +
                        "Mixed Case Header\n" +
                        "This should be regular text formatting.";

        PdfContent pdfContent = new PdfContent(8L, CASE_ID, "Formatting Test Document", 
                                              "Test", "FORMAT_" + CASE_ID + ".pdf",
                                              content, LocalDateTime.now(), UPLOADED_BY, STATUS);

        // Act
        byte[] result = mockPdfGeneratorService.generatePdf(pdfContent);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        
        String pdfHeader = new String(result, 0, Math.min(8, result.length));
        assertTrue(pdfHeader.startsWith("%PDF"));
    }

    @Test
    void testGeneratePdf_WithOnlyWhitespaceContent() {
        // Arrange
        String content = "   \n\n  \t  \n    \n\n   ";

        PdfContent pdfContent = new PdfContent(9L, CASE_ID, "Whitespace Document", 
                                              "Test", "WHITESPACE_" + CASE_ID + ".pdf",
                                              content, LocalDateTime.now(), UPLOADED_BY, STATUS);

        // Act
        byte[] result = mockPdfGeneratorService.generatePdf(pdfContent);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        
        String pdfHeader = new String(result, 0, Math.min(8, result.length));
        assertTrue(pdfHeader.startsWith("%PDF"));
    }

    @Test
    void testGeneratePdf_NullPdfContent() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            mockPdfGeneratorService.generatePdf(null);
        });
    }

    @Test
    void testGeneratePdf_NullDocumentName() {
        // Arrange
        PdfContent pdfContent = new PdfContent(10L, CASE_ID, null, DOCUMENT_TYPE, FILE_NAME,
                                              "Test content", LocalDateTime.now(), UPLOADED_BY, STATUS);

        // Act & Assert - Should handle null document name gracefully
        assertThrows(RuntimeException.class, () -> {
            mockPdfGeneratorService.generatePdf(pdfContent);
        });
    }

    @Test
    void testGeneratePdf_NullCaseId() {
        // Arrange
        PdfContent pdfContent = new PdfContent(11L, null, DOCUMENT_NAME, DOCUMENT_TYPE, FILE_NAME,
                                              "Test content", LocalDateTime.now(), UPLOADED_BY, STATUS);

        // Act - Should handle null case ID gracefully by treating it as "null" string
        byte[] result = mockPdfGeneratorService.generatePdf(pdfContent);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        
        String pdfHeader = new String(result, 0, Math.min(8, result.length));
        assertTrue(pdfHeader.startsWith("%PDF"));
    }

    @Test
    void testGeneratePdf_ConsistentOutput() {
        // Arrange - Same input should produce same output
        String content = "CONSISTENCY TEST\n\nThis content should produce consistent PDF output.";
        LocalDateTime fixedDate = LocalDateTime.of(2025, 7, 29, 12, 0, 0);
        
        PdfContent pdfContent1 = new PdfContent(12L, CASE_ID, "Consistency Test 1", 
                                               DOCUMENT_TYPE, FILE_NAME, content, fixedDate, UPLOADED_BY, STATUS);
        PdfContent pdfContent2 = new PdfContent(12L, CASE_ID, "Consistency Test 1", 
                                               DOCUMENT_TYPE, FILE_NAME, content, fixedDate, UPLOADED_BY, STATUS);

        // Act
        byte[] result1 = mockPdfGeneratorService.generatePdf(pdfContent1);
        byte[] result2 = mockPdfGeneratorService.generatePdf(pdfContent2);

        // Assert
        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(result1.length, result2.length, "Same input should produce same output size");
        
        // Note: Due to PDF generation timestamps or other metadata, byte-for-byte comparison might not work
        // But the size should be the same for identical content
    }

    @Test
    void testGeneratePdf_PerformanceTest() {
        // Arrange
        String content = "PERFORMANCE TEST DOCUMENT\n\n" +
                        "This is a test to ensure PDF generation completes in reasonable time.\n" +
                        "Content should be processed efficiently.";

        PdfContent pdfContent = new PdfContent(13L, CASE_ID, "Performance Test", 
                                              "Test", "PERF_" + CASE_ID + ".pdf",
                                              content, LocalDateTime.now(), UPLOADED_BY, STATUS);

        // Act
        long startTime = System.currentTimeMillis();
        byte[] result = mockPdfGeneratorService.generatePdf(pdfContent);
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        
        // PDF generation should complete within reasonable time (less than 5 seconds)
        assertTrue(executionTime < 5000, "PDF generation took too long: " + executionTime + "ms");
        
        String pdfHeader = new String(result, 0, Math.min(8, result.length));
        assertTrue(pdfHeader.startsWith("%PDF"));
    }
}