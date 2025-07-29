package com.clearance.tracker.service;

import com.clearance.tracker.dto.CaseDetailsAndHistoryResponse;
import com.clearance.tracker.dto.CaseDto;
import com.clearance.tracker.dto.CaseDetailsDto;
import com.clearance.tracker.dto.CaseHistoryDto;
import com.clearance.tracker.dto.CaseHistoryItem;
import com.clearance.tracker.dto.CaseHistoryResponseDto;
import com.clearance.tracker.dto.CaseListResponseDto;
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
import java.util.concurrent.CompletableFuture;

@Service
@Profile("!mock")
public class ExternalApiService {

    private static final Logger logger = LoggerFactory.getLogger(ExternalApiService.class);
    private static final String IN_PROGRESS_STATUS = "In Progress";

    @Autowired
    private RestTemplate restTemplate;

    @Value("${external.api.base-url:http://localhost:8080}")
    private String baseUrl;

    public CombinedCaseResponse getCaseHistory(String subjectPersonaObjectId) throws ApplicationException {
        logger.info("Starting complex case history retrieval process using external v1 APIs asynchronously for Subject Persona Object ID: {} on thread: {}", 
                   subjectPersonaObjectId, Thread.currentThread().getName());
        
        try {
            // Step 1: Call external /api/v1/cases to get case list for the subject
            CaseListResponseDto caseListResponse = getAllCases(subjectPersonaObjectId);
            
            // Step 2: Filter cases with "In Progress" status and pick first one
            String selectedCaseId = filterAndSelectFirstInProgressCaseFromV1(caseListResponse.getCases());
            
            // Step 3 & 4: Execute case details and history calls asynchronously using shared method
            Object[] results = getCaseDetailsAndHistoryAsync(selectedCaseId);
            CaseDetailsDto caseDetails = (CaseDetailsDto) results[0];
            CaseHistoryResponseDto caseHistoryResponse = (CaseHistoryResponseDto) results[1];
            
            // Step 5: Combine all data and return
            CombinedCaseResponse response = new CombinedCaseResponse(caseListResponse, caseDetails, caseHistoryResponse, selectedCaseId);
            
            logger.info("Successfully completed complex case history retrieval using v1 APIs asynchronously for Subject Persona Object ID: {} on thread: {}. Selected case: {}, Total cases: {}, History items: {}", 
                       subjectPersonaObjectId, Thread.currentThread().getName(), selectedCaseId, 
                       caseListResponse.getCases() != null ? caseListResponse.getCases().size() : 0,
                       caseHistoryResponse.getHistory() != null ? caseHistoryResponse.getHistory().size() : 0);
            
            return response;
            
        } catch (RuntimeException e) {
            if (e.getCause() instanceof ApplicationException) {
                throw (ApplicationException) e.getCause();
            }
            logger.error("Unexpected runtime error during async complex case history retrieval for Subject Persona Object ID: {}. Error: {}", subjectPersonaObjectId, e.getMessage(), e);
            throw new ApplicationException("Unexpected error during async v1 case history retrieval: " + e.getMessage(), e);
        } catch (Exception e) {
            // This catches RestClientException and other exceptions from getAllCases call
            logger.error("Unexpected error during complex case history retrieval with v1 APIs for Subject Persona Object ID: {}. Error: {}", subjectPersonaObjectId, e.getMessage(), e);
            throw new ApplicationException("Unexpected error during v1 case history retrieval: " + e.getMessage(), e);
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

    private String filterAndSelectFirstInProgressCaseFromV1(List<CaseDto> allCases) throws ApplicationException {
        logger.info("Step 2: Filtering v1 cases with '{}' status", IN_PROGRESS_STATUS);
        
        Optional<CaseDto> inProgressCase = allCases.stream()
            .filter(caseItem -> IN_PROGRESS_STATUS.equalsIgnoreCase(caseItem.getDISAStatus()))
            .findFirst();
        
        if (inProgressCase.isEmpty()) {
            logger.warn("No v1 cases found with '{}' status. Available cases: {}", IN_PROGRESS_STATUS, 
                       allCases.stream().map(c -> c.getNBISCaseID() + ":" + c.getDISAStatus()).toList());
            throw new ApplicationException("No cases found with 'In Progress' status");
        }
        
        String selectedCaseId = inProgressCase.get().getNBISCaseID();
        logger.info("Step 2 completed: Selected first 'In Progress' v1 case: {}", selectedCaseId);
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



    public PdfContent getLatestPdf(String caseId) throws ApplicationException {
        String url = baseUrl + "/api/latest-pdf?caseId=" + caseId;
        logger.info("Calling external API to get latest PDF for case {} from URL: {}", caseId, url);
        
        try {
            ResponseEntity<PdfContent> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                PdfContent.class
            );
            
            PdfContent latestPdf = response.getBody();
            logger.info("Successfully retrieved latest PDF for case {}: {}", 
                       caseId, latestPdf != null ? latestPdf.getFileName() : "null");
            
            return latestPdf;
            
        } catch (RestClientException e) {
            logger.error("Failed to call external API for latest PDF. Case: {}, URL: {}, Error: {}", 
                        caseId, url, e.getMessage(), e);
            throw new ApplicationException("External service call failed for latest PDF: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error occurred while calling external API for latest PDF. Case: {}, URL: {}, Error: {}", 
                        caseId, url, e.getMessage(), e);
            throw new ApplicationException("Unexpected error during latest PDF retrieval: " + e.getMessage(), e);
        }
    }

    public CaseListResponseDto getAllCases(String subjectPersonaObjectId) throws ApplicationException {
        String url = baseUrl + "/api/v1/cases?subjectPersonaObjectId=" + subjectPersonaObjectId;
        logger.info("Calling external API to get all cases for Subject Persona Object ID {} from URL: {}", subjectPersonaObjectId, url);
        
        try {
            ResponseEntity<CaseListResponseDto> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                CaseListResponseDto.class
            );
            
            CaseListResponseDto caseListResponse = response.getBody();
            logger.info("Successfully retrieved {} cases for Subject Persona Object ID {} from external API", 
                       caseListResponse != null && caseListResponse.getCases() != null ? caseListResponse.getCases().size() : 0, subjectPersonaObjectId);
            
            return caseListResponse;
            
        } catch (RestClientException e) {
            logger.error("Failed to call external API for cases list. Subject Persona Object ID: {}, URL: {}, Error: {}", 
                        subjectPersonaObjectId, url, e.getMessage(), e);
            throw new ApplicationException("External service call failed for cases list: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error occurred while calling external API for cases list. Subject Persona Object ID: {}, URL: {}, Error: {}", 
                        subjectPersonaObjectId, url, e.getMessage(), e);
            throw new ApplicationException("Unexpected error during cases list retrieval: " + e.getMessage(), e);
        }
    }

    public CaseDetailsDto getCaseDetails(String nbisId) throws ApplicationException {
        String url = baseUrl + "/api/v1/cases/" + nbisId;
        logger.info("Calling external API to get case details for NBIS ID {} from URL: {}", nbisId, url);
        
        try {
            ResponseEntity<CaseDetailsDto> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                CaseDetailsDto.class
            );
            
            CaseDetailsDto caseDetails = response.getBody();
            logger.info("Successfully retrieved case details for NBIS ID {}", nbisId);
            
            return caseDetails;
            
        } catch (RestClientException e) {
            logger.error("Failed to call external API for case details. NBIS ID: {}, URL: {}, Error: {}", 
                        nbisId, url, e.getMessage(), e);
            throw new ApplicationException("External service call failed for case details: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error occurred while calling external API for case details. NBIS ID: {}, URL: {}, Error: {}", 
                        nbisId, url, e.getMessage(), e);
            throw new ApplicationException("Unexpected error during case details retrieval: " + e.getMessage(), e);
        }
    }

    public CaseHistoryResponseDto getCaseHistoryFromV1Api(String nbisId) throws ApplicationException {
        String url = baseUrl + "/api/v1/cases/" + nbisId + "/history";
        logger.info("Calling external API to get case history for NBIS ID {} from URL: {}", nbisId, url);
        
        try {
            ResponseEntity<CaseHistoryResponseDto> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                CaseHistoryResponseDto.class
            );
            
            CaseHistoryResponseDto caseHistoryResponse = response.getBody();
            logger.info("Successfully retrieved {} history items for NBIS ID {}", 
                       caseHistoryResponse != null && caseHistoryResponse.getHistory() != null ? caseHistoryResponse.getHistory().size() : 0, nbisId);
            
            return caseHistoryResponse;
            
        } catch (RestClientException e) {
            logger.error("Failed to call external API for case history. NBIS ID: {}, URL: {}, Error: {}", 
                        nbisId, url, e.getMessage(), e);
            throw new ApplicationException("External service call failed for case history: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error occurred while calling external API for case history. NBIS ID: {}, URL: {}, Error: {}", 
                        nbisId, url, e.getMessage(), e);
            throw new ApplicationException("Unexpected error during case history retrieval: " + e.getMessage(), e);
        }
    }

    public CaseDetailsAndHistoryResponse getCaseDetailsAndHistory(String caseId) throws ApplicationException {
        logger.info("Getting case details and history asynchronously for case ID: {} on thread: {}", 
                   caseId, Thread.currentThread().getName());
        
        try {
            // Execute both API calls asynchronously using shared method
            Object[] results = getCaseDetailsAndHistoryAsync(caseId);
            CaseDetailsDto caseDetails = (CaseDetailsDto) results[0];
            CaseHistoryResponseDto caseHistory = (CaseHistoryResponseDto) results[1];
            
            // Combine both into response
            CaseDetailsAndHistoryResponse response = new CaseDetailsAndHistoryResponse(caseId, caseDetails, caseHistory);
            
            logger.info("Successfully retrieved case details and history asynchronously for case {} on thread: {}. History items: {}", 
                       caseId, Thread.currentThread().getName(), 
                       caseHistory != null && caseHistory.getHistory() != null ? caseHistory.getHistory().size() : 0);
            
            return response;
            
        } catch (RuntimeException e) {
            if (e.getCause() instanceof ApplicationException) {
                throw (ApplicationException) e.getCause();
            }
            logger.error("Unexpected runtime error during async case details and history retrieval. Case: {}, Error: {}", caseId, e.getMessage(), e);
            throw new ApplicationException("Unexpected error during async case details and history retrieval: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error during async case details and history retrieval. Case: {}, Error: {}", caseId, e.getMessage(), e);
            throw new ApplicationException("Unexpected error during async case details and history retrieval: " + e.getMessage(), e);
        }
    }

    /**
     * Private method to asynchronously retrieve case details and history for a given case ID
     * @param caseId The case ID to retrieve data for
     * @return Array containing [CaseDetailsDto, CaseHistoryResponseDto]
     * @throws ApplicationException if any error occurs during retrieval
     */
    private Object[] getCaseDetailsAndHistoryAsync(String caseId) throws ApplicationException {
        logger.debug("Starting async retrieval for case details and history: {} on thread: {} (ID: {})", 
                   caseId, Thread.currentThread().getName(), Thread.currentThread().getId());
        
        try {
            // Execute both API calls asynchronously
            CompletableFuture<CaseDetailsDto> caseDetailsFuture = CompletableFuture.supplyAsync(() -> {
                try {
                    logger.debug("Starting async call for case details: {} on thread: {} (ID: {})", 
                               caseId, Thread.currentThread().getName(), Thread.currentThread().getId());
                    return getCaseDetails(caseId);
                } catch (ApplicationException e) {
                    logger.error("Error in async case details call for case {} on thread {}: {}", 
                               caseId, Thread.currentThread().getName(), e.getMessage());
                    throw new RuntimeException("Case details retrieval failed", e);
                }
            });
            
            CompletableFuture<CaseHistoryResponseDto> caseHistoryFuture = CompletableFuture.supplyAsync(() -> {
                try {
                    logger.debug("Starting async call for case history: {} on thread: {} (ID: {})", 
                               caseId, Thread.currentThread().getName(), Thread.currentThread().getId());
                    return getCaseHistoryFromV1Api(caseId);
                } catch (ApplicationException e) {
                    logger.error("Error in async case history call for case {} on thread {}: {}", 
                               caseId, Thread.currentThread().getName(), e.getMessage());
                    throw new RuntimeException("Case history retrieval failed", e);
                }
            });
            
            // Wait for both futures to complete and get results
            logger.debug("Waiting for async calls to complete on main thread: {} (ID: {})", 
                       Thread.currentThread().getName(), Thread.currentThread().getId());
            CaseDetailsDto caseDetails = caseDetailsFuture.join();
            CaseHistoryResponseDto caseHistory = caseHistoryFuture.join();
            
            return new Object[]{caseDetails, caseHistory};
            
        } catch (RuntimeException e) {
            if (e.getCause() instanceof ApplicationException) {
                throw (ApplicationException) e.getCause();
            }
            logger.error("Unexpected runtime error during async case details and history retrieval. Case: {}, Error: {}", caseId, e.getMessage(), e);
            throw new ApplicationException("Unexpected error during async retrieval: " + e.getMessage(), e);
        }
    }
}