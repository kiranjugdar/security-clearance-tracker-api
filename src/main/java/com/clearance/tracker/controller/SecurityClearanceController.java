package com.clearance.tracker.controller;

import com.clearance.tracker.dto.CombinedCaseResponse;
import com.clearance.tracker.dto.ErrorResponse;
import com.clearance.tracker.dto.PdfContent;
import com.clearance.tracker.exception.ApplicationException;
import com.clearance.tracker.service.ExternalApiService;
import com.clearance.tracker.service.PdfGeneratorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/clearance")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class SecurityClearanceController {

    private static final Logger logger = LoggerFactory.getLogger(SecurityClearanceController.class);

    @Autowired
    private ExternalApiService externalApiService;

    @Autowired
    private PdfGeneratorService pdfGeneratorService;

    @GetMapping("/case-history")
    public ResponseEntity<?> getCaseHistory(HttpServletRequest request) {
        logger.info("Received request to get combined case history from client: {}", request.getRemoteAddr());
        
        try {
            CombinedCaseResponse combinedResponse = externalApiService.getCaseHistory();
            logger.info("Successfully processed combined case history request. Selected case: {}, Total cases: {}, Status history items: {}", 
                       combinedResponse.getSelectedCaseId(),
                       combinedResponse.getCaseHistory() != null ? combinedResponse.getCaseHistory().size() : 0,
                       combinedResponse.getStatusHistory() != null ? combinedResponse.getStatusHistory().size() : 0);
            return ResponseEntity.ok(combinedResponse);
            
        } catch (ApplicationException e) {
            logger.error("Application error processing combined case history request: {}", e.getMessage(), e);
            
            ErrorResponse errorResponse = new ErrorResponse(
                e.getErrorCode(),
                "External service failed: " + e.getMessage(),
                request.getRequestURI()
            );
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        } catch (Exception e) {
            logger.error("Unexpected error in combined case history controller: {}", e.getMessage(), e);
            
            ErrorResponse errorResponse = new ErrorResponse(
                9999,
                "System error occurred",
                request.getRequestURI()
            );
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/pdf-contents/{caseId}")
    public ResponseEntity<?> getPdfContents(@PathVariable String caseId, HttpServletRequest request) {
        logger.info("Received request to get PDF contents for case {} from client: {}", caseId, request.getRemoteAddr());
        
        try {
            List<PdfContent> pdfContents = externalApiService.getPdfContents(caseId);
            logger.info("Successfully processed PDF contents request for case {}. Returning {} documents", 
                       caseId, pdfContents != null ? pdfContents.size() : 0);
            return ResponseEntity.ok(pdfContents);
            
        } catch (ApplicationException e) {
            logger.error("Application error processing PDF contents request for case {}: {}", caseId, e.getMessage(), e);
            
            ErrorResponse errorResponse = new ErrorResponse(
                e.getErrorCode(),
                "External service failed: " + e.getMessage(),
                request.getRequestURI()
            );
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        } catch (Exception e) {
            logger.error("Unexpected error in PDF contents controller for case {}: {}", caseId, e.getMessage(), e);
            
            ErrorResponse errorResponse = new ErrorResponse(
                9999,
                "System error occurred",
                request.getRequestURI()
            );
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/pdf-download/{caseId}/{documentId}")
    public ResponseEntity<?> downloadPdf(@PathVariable String caseId, @PathVariable Long documentId, HttpServletRequest request) {
        logger.info("Received request to download PDF for case {} document {} from client: {}", caseId, documentId, request.getRemoteAddr());
        
        try {
            // Get all PDF contents for the case
            List<PdfContent> pdfContents = externalApiService.getPdfContents(caseId);
            
            // Find the specific document
            PdfContent targetDocument = pdfContents.stream()
                .filter(pdf -> pdf.getId().equals(documentId))
                .findFirst()
                .orElse(null);
            
            if (targetDocument == null) {
                logger.warn("Document {} not found for case {}", documentId, caseId);
                ErrorResponse errorResponse = new ErrorResponse(
                    404,
                    "Document not found for case: " + caseId + ", document ID: " + documentId,
                    request.getRequestURI()
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
            
            // Generate PDF
            byte[] pdfBytes = pdfGeneratorService.generatePdf(targetDocument);
            
            // Set headers for PDF download
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", targetDocument.getFileName());
            headers.setContentLength(pdfBytes.length);
            
            logger.info("Successfully generated PDF for case {} document {}. Size: {} bytes", 
                       caseId, documentId, pdfBytes.length);
            
            return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
            
        } catch (ApplicationException e) {
            logger.error("Application error processing PDF download for case {} document {}: {}", caseId, documentId, e.getMessage(), e);
            
            ErrorResponse errorResponse = new ErrorResponse(
                e.getErrorCode(),
                "External service failed: " + e.getMessage(),
                request.getRequestURI()
            );
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        } catch (Exception e) {
            logger.error("Unexpected error in PDF download controller for case {} document {}: {}", caseId, documentId, e.getMessage(), e);
            
            ErrorResponse errorResponse = new ErrorResponse(
                9999,
                "System error occurred",
                request.getRequestURI()
            );
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}