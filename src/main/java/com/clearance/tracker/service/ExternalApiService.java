package com.clearance.tracker.service;

import com.clearance.tracker.dto.CaseHistoryItem;
import com.clearance.tracker.dto.CombinedCaseResponse;
import com.clearance.tracker.dto.CurrentStatus;
import com.clearance.tracker.dto.PdfContent;
import com.clearance.tracker.dto.StatusHistoryItem;
import com.clearance.tracker.exception.ApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Profile("!mock")
public class ExternalApiService {

    private static final Logger logger = LoggerFactory.getLogger(ExternalApiService.class);
    private static final String IN_PROGRESS_STATUS = "In Progress";

    @Autowired
    private RestTemplate restTemplate;

    @Value("${external.api.base-url:http://localhost:8080}")
    private String baseUrl;

    public CombinedCaseResponse getCaseHistory() throws ApplicationException {
        logger.info("Starting complex case history retrieval process");
        
        try {
            // Step 1: Call external /case-history API to get case list
            List<CaseHistoryItem> allCases = fetchCaseHistoryFromExternal();
            
            // Step 2: Filter cases with "In Progress" status and pick first one
            String selectedCaseId = filterAndSelectFirstInProgressCase(allCases);
            
            // Step 3: Call external /status-history API with selected caseID
            List<StatusHistoryItem> statusHistory = fetchStatusHistoryForCase(selectedCaseId);
            
            // Step 4: Filter status history to get current status
            CurrentStatus currentStatus = extractCurrentStatusFromHistory(statusHistory, selectedCaseId);
            
            // Step 5: Combine all data and return
            CombinedCaseResponse response = new CombinedCaseResponse(allCases, currentStatus, statusHistory, selectedCaseId);
            
            logger.info("Successfully completed complex case history retrieval. Selected case: {}, Total cases: {}, Status history items: {}", 
                       selectedCaseId, allCases.size(), statusHistory.size());
            
            return response;
            
        } catch (RestClientException e) {
            logger.error("Failed to call external API during complex case history retrieval. Error: {}", e.getMessage(), e);
            throw new ApplicationException("External service call failed during case history retrieval: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error during complex case history retrieval. Error: {}", e.getMessage(), e);
            throw new ApplicationException("Unexpected error during case history retrieval: " + e.getMessage(), e);
        }
    }

    private List<CaseHistoryItem> fetchCaseHistoryFromExternal() throws ApplicationException {
        String url = baseUrl + "/api/case-history";
        logger.info("Step 1: Calling external API to get case history from URL: {}", url);
        
        ResponseEntity<List<CaseHistoryItem>> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<CaseHistoryItem>>() {}
        );
        
        List<CaseHistoryItem> cases = response.getBody();
        logger.info("Step 1 completed: Retrieved {} cases from external API", cases != null ? cases.size() : 0);
        return cases;
    }

    private String filterAndSelectFirstInProgressCase(List<CaseHistoryItem> allCases) throws ApplicationException {
        logger.info("Step 2: Filtering cases with '{}' status", IN_PROGRESS_STATUS);
        
        Optional<CaseHistoryItem> inProgressCase = allCases.stream()
            .filter(caseItem -> IN_PROGRESS_STATUS.equalsIgnoreCase(caseItem.getCaseStatus()))
            .findFirst();
        
        if (inProgressCase.isEmpty()) {
            logger.warn("No cases found with '{}' status. Available cases: {}", IN_PROGRESS_STATUS, 
                       allCases.stream().map(c -> c.getCaseId() + ":" + c.getCaseStatus()).toList());
            throw new ApplicationException("No cases found with 'In Progress' status");
        }
        
        String selectedCaseId = inProgressCase.get().getCaseId();
        logger.info("Step 2 completed: Selected first 'In Progress' case: {}", selectedCaseId);
        return selectedCaseId;
    }

    private List<StatusHistoryItem> fetchStatusHistoryForCase(String caseId) throws ApplicationException {
        String url = baseUrl + "/api/status-history?caseId=" + caseId;
        logger.info("Step 3: Calling external API to get status history for case {} from URL: {}", caseId, url);
        
        ResponseEntity<List<StatusHistoryItem>> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<StatusHistoryItem>>() {}
        );
        
        List<StatusHistoryItem> statusHistory = response.getBody();
        logger.info("Step 3 completed: Retrieved {} status history items for case {}", 
                   statusHistory != null ? statusHistory.size() : 0, caseId);
        return statusHistory;
    }

    private CurrentStatus extractCurrentStatusFromHistory(List<StatusHistoryItem> statusHistory, String caseId) {
        logger.info("Step 4: Filtering status history to extract current status for case {}", caseId);
        
        // Find the most recent status (assuming the list is ordered or find by latest date)
        Optional<StatusHistoryItem> latestStatus = statusHistory.stream()
            .max((s1, s2) -> s1.getDate().compareTo(s2.getDate()));
        
        CurrentStatus currentStatus;
        if (latestStatus.isPresent()) {
            StatusHistoryItem latest = latestStatus.get();
            currentStatus = new CurrentStatus(
                caseId,
                latest.getName(),
                latest.getStatus(),
                latest.getDate(),
                latest.getDescription()
            );
            logger.info("Step 4 completed: Current status for case {} is '{}' with stage '{}'", 
                       caseId, latest.getStatus(), latest.getName());
        } else {
            // Fallback if no status history found
            currentStatus = new CurrentStatus(
                caseId,
                "Unknown",
                "Unknown",
                LocalDateTime.now(),
                "No status history available"
            );
            logger.warn("Step 4 completed with fallback: No status history found for case {}", caseId);
        }
        
        return currentStatus;
    }

    public List<PdfContent> getPdfContents(String caseId) throws ApplicationException {
        String url = baseUrl + "/api/pdf-contents?caseId=" + caseId;
        logger.info("Calling external API to get PDF contents for case {} from URL: {}", caseId, url);
        
        try {
            ResponseEntity<List<PdfContent>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<PdfContent>>() {}
            );
            
            List<PdfContent> pdfContents = response.getBody();
            logger.info("Successfully retrieved {} PDF contents for case {}", 
                       pdfContents != null ? pdfContents.size() : 0, caseId);
            
            return pdfContents;
            
        } catch (RestClientException e) {
            logger.error("Failed to call external API for PDF contents. Case: {}, URL: {}, Error: {}", 
                        caseId, url, e.getMessage(), e);
            throw new ApplicationException("External service call failed for PDF contents: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error occurred while calling external API for PDF contents. Case: {}, URL: {}, Error: {}", 
                        caseId, url, e.getMessage(), e);
            throw new ApplicationException("Unexpected error during PDF contents retrieval: " + e.getMessage(), e);
        }
    }
}