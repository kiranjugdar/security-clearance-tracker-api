package com.clearance.tracker.controller;

import com.clearance.tracker.dto.CaseDetailsAndHistoryResponse;
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
    public ResponseEntity<?> getCaseHistory(@RequestParam String subjectPersonaObjectId, HttpServletRequest request) {
        logger.info("Received request to get combined case history for Subject Persona Object ID: {} from client: {}", subjectPersonaObjectId, request.getRemoteAddr());
        
        try {
            CombinedCaseResponse combinedResponse = externalApiService.getCaseHistory(subjectPersonaObjectId);
            logger.info("Successfully processed combined case history request for Subject Persona Object ID: {}. Selected case: {}, Total cases: {}, History items: {}", 
                       subjectPersonaObjectId,
                       combinedResponse.getSelectedCaseId(),
                       combinedResponse.getCasesList() != null && combinedResponse.getCasesList().getCases() != null ? combinedResponse.getCasesList().getCases().size() : 0,
                       combinedResponse.getCaseHistory() != null && combinedResponse.getCaseHistory().getHistory() != null ? combinedResponse.getCaseHistory().getHistory().size() : 0);
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


    @GetMapping("/pdf-download/{caseId}")
    public ResponseEntity<?> downloadPdf(@PathVariable String caseId, HttpServletRequest request) {
        logger.info("Received request to download latest PDF for case {} from client: {}", caseId, request.getRemoteAddr());
        
        try {
            // Get latest PDF for the case
            PdfContent latestPdf = externalApiService.getLatestPdf(caseId);
            
            if (latestPdf == null) {
                logger.warn("No PDF found for case {}", caseId);
                ErrorResponse errorResponse = new ErrorResponse(
                    404,
                    "No PDF found for case: " + caseId,
                    request.getRequestURI()
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
            
            // Generate PDF
            byte[] pdfBytes = pdfGeneratorService.generatePdf(latestPdf);
            
            // Set headers for PDF download
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", latestPdf.getFileName());
            headers.setContentLength(pdfBytes.length);
            
            logger.info("Successfully generated latest PDF for case {}. File: {}, Size: {} bytes", 
                       caseId, latestPdf.getFileName(), pdfBytes.length);
            
            return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
            
        } catch (ApplicationException e) {
            logger.error("Application error processing latest PDF download for case {}: {}", caseId, e.getMessage(), e);
            
            ErrorResponse errorResponse = new ErrorResponse(
                e.getErrorCode(),
                "External service failed: " + e.getMessage(),
                request.getRequestURI()
            );
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        } catch (Exception e) {
            logger.error("Unexpected error in latest PDF download controller for case {}: {}", caseId, e.getMessage(), e);
            
            ErrorResponse errorResponse = new ErrorResponse(
                9999,
                "System error occurred",
                request.getRequestURI()
            );
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/case-details-history/{caseId}")
    public ResponseEntity<?> getCaseDetailsAndHistory(@PathVariable String caseId, HttpServletRequest request) {
        logger.info("Received request to get case details and history for case {} from client: {}", caseId, request.getRemoteAddr());
        
        try {
            CaseDetailsAndHistoryResponse response = externalApiService.getCaseDetailsAndHistory(caseId);
            logger.info("Successfully processed case details and history request for case {}. History items: {}", 
                       caseId, 
                       response.getCaseHistory() != null && response.getCaseHistory().getHistory() != null ? response.getCaseHistory().getHistory().size() : 0);
            return ResponseEntity.ok(response);
            
        } catch (ApplicationException e) {
            logger.error("Application error processing case details and history request for case {}: {}", caseId, e.getMessage(), e);
            
            ErrorResponse errorResponse = new ErrorResponse(
                e.getErrorCode(),
                "External service failed: " + e.getMessage(),
                request.getRequestURI()
            );
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        } catch (Exception e) {
            logger.error("Unexpected error in case details and history controller for case {}: {}", caseId, e.getMessage(), e);
            
            ErrorResponse errorResponse = new ErrorResponse(
                9999,
                "System error occurred",
                request.getRequestURI()
            );
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}