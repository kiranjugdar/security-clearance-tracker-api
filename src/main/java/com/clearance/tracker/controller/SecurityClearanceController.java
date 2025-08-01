package com.clearance.tracker.controller;

import com.clearance.tracker.dto.CaseDetailsAndHistoryResponse;
import com.clearance.tracker.dto.CombinedCaseResponse;
import com.clearance.tracker.dto.ErrorResponse;
import com.clearance.tracker.exception.ApplicationException;
import com.clearance.tracker.security.SecurityContextHelper;
import com.clearance.tracker.service.ExternalApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/clearance")
public class SecurityClearanceController {

    private static final Logger logger = LoggerFactory.getLogger(SecurityClearanceController.class);

    @Autowired
    private ExternalApiService externalApiService;

    @Autowired
    private SecurityContextHelper securityContextHelper;

    @GetMapping("/case-history")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> getCaseHistory(@RequestParam(required = false) String subjectPersonaObjectId, HttpServletRequest request) {
        
        // Use JWT subjectPersonaObjectId if not provided in request parameter
        String effectiveSubjectPersonaObjectId = subjectPersonaObjectId;
        if (effectiveSubjectPersonaObjectId == null || effectiveSubjectPersonaObjectId.trim().isEmpty()) {
            effectiveSubjectPersonaObjectId = securityContextHelper.getCurrentSubjectPersonaObjectId();
        }
        
        // Validate that user can only access their own data (unless admin)
        String jwtSubjectPersonaObjectId = securityContextHelper.getCurrentSubjectPersonaObjectId();
        if (!securityContextHelper.hasRole("ADMIN") && 
            !effectiveSubjectPersonaObjectId.equals(jwtSubjectPersonaObjectId)) {
            logger.warn("User {} attempted to access data for different subject: {} vs {}", 
                       securityContextHelper.getCurrentUsername(), effectiveSubjectPersonaObjectId, jwtSubjectPersonaObjectId);
            
            ErrorResponse errorResponse = new ErrorResponse(
                403,
                "Access denied: Cannot access data for different subject",
                request.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        }

        logger.info("Received request to get combined case history for Subject Persona Object ID: {} from user: {} (role: {}) from client: {}", 
                   effectiveSubjectPersonaObjectId, securityContextHelper.getCurrentUsername(), 
                   securityContextHelper.getCurrentUserPrimaryRole(), request.getRemoteAddr());
        
        try {
            CombinedCaseResponse combinedResponse = externalApiService.getCaseHistory(effectiveSubjectPersonaObjectId);
            logger.info("Successfully processed combined case history request for Subject Persona Object ID: {}. Selected case: {}, Total cases: {}, History items: {}", 
                       effectiveSubjectPersonaObjectId,
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
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> downloadPdf(@PathVariable String caseId, HttpServletRequest request) {
        logger.info("Received request to download latest PDF for case {} from user: {} (role: {}) from client: {}", 
                   caseId, securityContextHelper.getCurrentUsername(), 
                   securityContextHelper.getCurrentUserPrimaryRole(), request.getRemoteAddr());
        
        try {
            // Get latest PDF bytes directly from external API
            byte[] pdfBytes = externalApiService.getLatestPdf(caseId);
            
            if (pdfBytes == null || pdfBytes.length == 0) {
                logger.warn("No PDF found for case {}", caseId);
                ErrorResponse errorResponse = new ErrorResponse(
                    404,
                    "No PDF found for case: " + caseId,
                    request.getRequestURI()
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
            
            // Set headers for PDF download
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", caseId + ".pdf");
            headers.setContentLength(pdfBytes.length);
            
            logger.info("Successfully retrieved latest PDF for case {}. Size: {} bytes", 
                       caseId, pdfBytes.length);
            
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
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> getCaseDetailsAndHistory(@PathVariable String caseId, HttpServletRequest request) {
        logger.info("Received request to get case details and history for case {} from user: {} (role: {}) from client: {}", 
                   caseId, securityContextHelper.getCurrentUsername(), 
                   securityContextHelper.getCurrentUserPrimaryRole(), request.getRemoteAddr());
        
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