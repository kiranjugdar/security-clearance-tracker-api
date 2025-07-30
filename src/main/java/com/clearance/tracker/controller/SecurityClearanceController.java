package com.clearance.tracker.controller;

import com.clearance.tracker.dto.CaseDetailsAndHistoryResponse;
import com.clearance.tracker.dto.CombinedCaseResponse;
import com.clearance.tracker.dto.ErrorResponse;
import com.clearance.tracker.exception.ApplicationException;
import com.clearance.tracker.service.ExternalApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/clearance")
@Validated
public class SecurityClearanceController {

    private static final Logger logger = LoggerFactory.getLogger(SecurityClearanceController.class);

    @Autowired
    private ExternalApiService externalApiService;

    @GetMapping("/case-history")
    public ResponseEntity<CombinedCaseResponse> getCaseHistory(
            @RequestParam 
            @NotBlank(message = "Subject Persona Object ID cannot be blank")
            @Size(min = 36, max = 36, message = "Subject Persona Object ID must be exactly 36 characters")
            @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$", 
                    message = "Subject Persona Object ID must be a valid UUID format")
            String subjectPersonaObjectId, 
            HttpServletRequest request) throws ApplicationException {
        logger.info("Received request to get combined case history for Subject Persona Object ID: {}", subjectPersonaObjectId);
        
        CombinedCaseResponse combinedResponse = externalApiService.getCaseHistory(subjectPersonaObjectId);
        logger.info("Successfully processed combined case history request for Subject Persona Object ID: {}. Selected case: {}, Total cases: {}, History items: {}", 
                   subjectPersonaObjectId,
                   combinedResponse.getSelectedCaseId(),
                   combinedResponse.getCasesList() != null && combinedResponse.getCasesList().getCases() != null ? combinedResponse.getCasesList().getCases().size() : 0,
                   combinedResponse.getCaseHistory() != null && combinedResponse.getCaseHistory().getHistory() != null ? combinedResponse.getCaseHistory().getHistory().size() : 0);
        return ResponseEntity.ok(combinedResponse);
    }


    @GetMapping("/pdf-download/{caseId}")
    public ResponseEntity<?> downloadPdf(
            @PathVariable 
            @NotBlank(message = "Case ID cannot be blank")
            @Size(min = 5, max = 50, message = "Case ID must be between 5 and 50 characters")
            @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Case ID can only contain alphanumeric characters")
            String caseId, 
            HttpServletRequest request) throws ApplicationException {
        logger.info("Received request to download latest PDF for case {}", caseId);
        
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
    }

    @GetMapping("/case-details-history/{caseId}")
    public ResponseEntity<CaseDetailsAndHistoryResponse> getCaseDetailsAndHistory(
            @PathVariable 
            @NotBlank(message = "Case ID cannot be blank")
            @Size(min = 5, max = 50, message = "Case ID must be between 5 and 50 characters")
            @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Case ID can only contain alphanumeric characters")
            String caseId, 
            HttpServletRequest request) throws ApplicationException {
        logger.info("Received request to get case details and history for case {}", caseId);
        
        CaseDetailsAndHistoryResponse response = externalApiService.getCaseDetailsAndHistory(caseId);
        logger.info("Successfully processed case details and history request for case {}. History items: {}", 
                   caseId, 
                   response.getCaseHistory() != null && response.getCaseHistory().getHistory() != null ? response.getCaseHistory().getHistory().size() : 0);
        return ResponseEntity.ok(response);
    }
}