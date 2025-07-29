package com.clearance.tracker.service;

import com.clearance.tracker.dto.CaseDetailsAndHistoryResponse;
import com.clearance.tracker.dto.CaseDto;
import com.clearance.tracker.dto.CaseDetailsDto;
import com.clearance.tracker.dto.CaseHistoryDto;
import com.clearance.tracker.dto.CaseHistoryResponseDto;
import com.clearance.tracker.dto.CaseListResponseDto;
import com.clearance.tracker.dto.CombinedCaseResponse;
import com.clearance.tracker.dto.CurrentStatus;
import com.clearance.tracker.dto.CurrentStatusDto;
import com.clearance.tracker.dto.EAppAccountInfoDto;
import com.clearance.tracker.dto.MetadataDto;
import com.clearance.tracker.dto.PIPSStatusCheckResponseDto;
import com.clearance.tracker.dto.PyWorkPageDto;
import com.clearance.tracker.dto.StatusHistoryItem;
import com.clearance.tracker.exception.ApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@Profile("mock")
public class MockExternalApiService extends ExternalApiService {

    private static final Logger logger = LoggerFactory.getLogger(MockExternalApiService.class);
    private static final String IN_PROGRESS_STATUS = "In Progress";

    @Override
    public CombinedCaseResponse getCaseHistory(String subjectPersonaObjectId) throws ApplicationException {
        logger.info("Using MOCK service - Starting complex case history retrieval process asynchronously for Subject Persona Object ID: {} on thread: {}", 
                   subjectPersonaObjectId, Thread.currentThread().getName());
        
        try {
            // Step 1: Mock case list data for subject
            CaseListResponseDto casesList = createMockCasesList(subjectPersonaObjectId);
            logger.info("Step 1 completed: Retrieved {} mock cases for Subject Persona Object ID: {}", 
                       casesList.getCases().size(), subjectPersonaObjectId);
            
            // Step 2: Filter cases with "In Progress" status and pick first one
            String selectedCaseId = filterAndSelectFirstInProgressCaseFromV1(casesList.getCases());
            
            // Step 3 & 4: Execute case details and history calls asynchronously using shared method
            Object[] results = getMockCaseDetailsAndHistoryAsync(selectedCaseId);
            CaseDetailsDto caseDetails = (CaseDetailsDto) results[0];
            CaseHistoryResponseDto caseHistory = (CaseHistoryResponseDto) results[1];
            logger.info("Step 4 completed: Retrieved {} mock case history items for case {}", 
                       caseHistory.getHistory().size(), selectedCaseId);
            
            // Step 5: Combine all mock data and return
            CombinedCaseResponse response = new CombinedCaseResponse(casesList, caseDetails, caseHistory, selectedCaseId);
            
            logger.info("Successfully completed MOCK case history retrieval asynchronously for Subject Persona Object ID: {} on thread: {}. Selected case: {}, Total cases: {}, History items: {}", 
                       subjectPersonaObjectId, Thread.currentThread().getName(), selectedCaseId, casesList.getCases().size(), caseHistory.getHistory().size());
            
            return response;
            
        } catch (Exception e) {
            logger.error("Error in MOCK service during async case history retrieval. Error: {}", e.getMessage(), e);
            throw new ApplicationException("Mock service error during async case history retrieval: " + e.getMessage(), e);
        }
    }

    private CaseListResponseDto createMockCasesList(String subjectPersonaObjectId) {
        logger.info("Creating mock case list data for Subject Persona Object ID: {}", subjectPersonaObjectId);
        
        List<CaseDto> cases = Arrays.asList(
            new CaseDto("25092CASE1329752", "In Progress", "272ad768-ea92-4972-a8a5-2c270fdddd33", 
                "2025-04-02T17:20:19.943Z", "2025-07-18T17:06:45.517Z", "Yes"),
            new CaseDto("25092CASE1329753", "Pending Investigation", "272ad768-ea92-4972-a8a5-2c270fdddd34", 
                "2025-04-03T09:15:00.123Z", "2025-07-19T12:30:00.456Z", "Yes"),
            new CaseDto("25092CASE1329754", "Review - eApp Received", "272ad768-ea92-4972-a8a5-2c270fdddd35", 
                "2025-04-04T14:30:00.789Z", "2025-07-20T16:45:00.123Z", "No"),
            new CaseDto("25092CASE1329755", "In Progress", "272ad768-ea92-4972-a8a5-2c270fdddd36", 
                "2025-04-05T08:00:00.456Z", "2025-07-21T09:30:00.789Z", "Yes"),
            new CaseDto("25092CASE1329756", "Completed", "272ad768-ea92-4972-a8a5-2c270fdddd37", 
                "2025-04-06T11:45:00.123Z", "2025-07-22T14:15:00.456Z", "Yes")
        );
        
        MetadataDto metadata = new MetadataDto(cases.size());
        return new CaseListResponseDto(cases, metadata);
    }

    private CaseDetailsDto createMockCaseDetails(String nbisId) {
        logger.info("Creating mock case details for NBIS ID: {}", nbisId);
        
        EAppAccountInfoDto eAppInfo = new EAppAccountInfoDto(
            "Initiated/Untouched by Applicant", 
            "Released to Agency"
        );
        
        CurrentStatusDto currentStatus = new CurrentStatusDto("RLTP", "Released to Parent Agency");
        
        PIPSStatusCheckResponseDto pipsResponse = new PIPSStatusCheckResponseDto(
            "N", "Y", currentStatus
        );
        
        PyWorkPageDto pyWorkPage = new PyWorkPageDto(
            "dcas884617ORG1121PVQABC",
            "Review - eApp Received",
            "272ad768-ea92-4972-a8a5-2c270fdddd33",
            nbisId,
            "2025-04-02T17:20:19.943Z",
            "System",
            "2025-07-18T17:06:45.517Z",
            "System",
            "Example Org",
            "/Example/Org/Path",
            "High",
            "PVQ-A-B-C",
            "2023",
            eAppInfo,
            pipsResponse,
            "Completed",
            "2025-07-20",
            "Yes"
        );
        
        return new CaseDetailsDto(pyWorkPage);
    }

    private CaseHistoryResponseDto createMockCaseHistoryResponse(String nbisId) {
        logger.info("Creating mock case history response for NBIS ID: {}", nbisId);
        
        List<CaseHistoryDto> history = Arrays.asList(
            new CaseHistoryDto("2025-06-06T10:00:00Z", 
                "Agency Initiated Investigation Request.", "System"),
            new CaseHistoryDto("2025-06-10T14:30:00Z", 
                "e-QIP data received.", "e-QIP Integration"),
            new CaseHistoryDto("2025-07-18T16:00:00Z", 
                "Case status updated to 'Review - eApp Received'.", "System")
        );
        
        return new CaseHistoryResponseDto(nbisId, history);
    }

    private List<StatusHistoryItem> createMockStatusHistory(String caseId) {
        logger.info("Creating mock status history data for case: {}", caseId);
        return Arrays.asList(
            new StatusHistoryItem(1L, "Application Submitted", 
                LocalDateTime.of(2025, 2, 1, 10, 23), "completed", 
                "Security officer has submitted Top Secret questionnaire"),
            new StatusHistoryItem(2L, "Initiated", 
                LocalDateTime.of(2025, 2, 7, 9, 0), "completed", 
                "Application assigned to security specialist"),
            new StatusHistoryItem(3L, "Questionnaire Submitted", 
                LocalDateTime.of(2025, 2, 14, 14, 30), "completed", 
                "Questionnaire reviewed by investigator"),
            new StatusHistoryItem(4L, "Review & Investigation", 
                LocalDateTime.of(2025, 2, 20, 11, 15), "in_progress", 
                "Background investigation in progress"),
            new StatusHistoryItem(5L, "Final Review", 
                LocalDateTime.of(2025, 7, 26, 10, 0), "pending", 
                "Awaiting final security review and approval")
        );
    }

    private String filterAndSelectFirstInProgressCaseFromV1(List<CaseDto> allCases) throws ApplicationException {
        logger.info("Step 2: Filtering mock v1 cases with '{}' status", IN_PROGRESS_STATUS);
        
        Optional<CaseDto> inProgressCase = allCases.stream()
            .filter(caseItem -> IN_PROGRESS_STATUS.equalsIgnoreCase(caseItem.getDISAStatus()))
            .findFirst();
        
        if (inProgressCase.isEmpty()) {
            logger.warn("No mock v1 cases found with '{}' status. Available cases: {}", IN_PROGRESS_STATUS, 
                       allCases.stream().map(c -> c.getNBISCaseID() + ":" + c.getDISAStatus()).toList());
            throw new ApplicationException("No cases found with 'In Progress' status");
        }
        
        String selectedCaseId = inProgressCase.get().getNBISCaseID();
        logger.info("Step 2 completed: Selected first 'In Progress' mock v1 case: {}", selectedCaseId);
        return selectedCaseId;
    }

    private CurrentStatus extractCurrentStatusFromHistory(List<StatusHistoryItem> statusHistory, String caseId) {
        logger.info("Step 4: Filtering mock status history to extract current status for case {}", caseId);
        
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
            logger.info("Step 4 completed: Current status for mock case {} is '{}' with stage '{}'", 
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
            logger.warn("Step 4 completed with fallback: No mock status history found for case {}", caseId);
        }
        
        return currentStatus;
    }



    @Override
    public byte[] getLatestPdf(String caseId) throws ApplicationException {
        logger.info("Using MOCK service - Getting latest PDF bytes for case: {}", caseId);
        
        try {
            // Create simple mock PDF bytes that represent a basic PDF structure
            String mockPdfContent = "%PDF-1.4\n" +
                "1 0 obj\n" +
                "<<\n" +
                "/Type /Catalog\n" +
                "/Pages 2 0 R\n" +
                ">>\n" +
                "endobj\n\n" +
                "2 0 obj\n" +
                "<<\n" +
                "/Type /Pages\n" +
                "/Kids [3 0 R]\n" +
                "/Count 1\n" +
                ">>\n" +
                "endobj\n\n" +
                "3 0 obj\n" +
                "<<\n" +
                "/Type /Page\n" +
                "/Parent 2 0 R\n" +
                "/MediaBox [0 0 612 792]\n" +
                "/Contents 4 0 R\n" +
                ">>\n" +
                "endobj\n\n" +
                "4 0 obj\n" +
                "<<\n" +
                "/Length 54\n" +
                ">>\n" +
                "stream\n" +
                "BT\n" +
                "/F1 12 Tf\n" +
                "72 720 Td\n" +
                "(Security Clearance Document - Case: " + caseId + ") Tj\n" +
                "ET\n" +
                "endstream\n" +
                "endobj\n\n" +
                "xref\n" +
                "0 5\n" +
                "0000000000 65535 f\n" +
                "0000000009 00000 n\n" +
                "0000000074 00000 n\n" +
                "0000000120 00000 n\n" +
                "0000000179 00000 n\n" +
                "trailer\n" +
                "<<\n" +
                "/Size 5\n" +
                "/Root 1 0 R\n" +
                ">>\n" +
                "startxref\n" +
                "280\n" +
                "%%EOF\n";
            
            byte[] pdfBytes = mockPdfContent.getBytes();
            
            logger.info("Successfully generated mock PDF bytes for case {}: {} bytes", 
                       caseId, pdfBytes.length);
            return pdfBytes;
            
        } catch (Exception e) {
            logger.error("Error in MOCK service during mock PDF bytes generation for case {}. Error: {}", caseId, e.getMessage(), e);
            throw new ApplicationException("Mock service error during latest PDF retrieval: " + e.getMessage(), e);
        }
    }


    @Override
    public CaseListResponseDto getAllCases(String subjectPersonaObjectId) throws ApplicationException {
        logger.info("Using MOCK service - Getting all cases for Subject Persona Object ID: {}", subjectPersonaObjectId);
        return createMockCasesList(subjectPersonaObjectId);
    }

    @Override
    public CaseDetailsDto getCaseDetails(String nbisId) throws ApplicationException {
        logger.info("Using MOCK service - Getting case details for NBIS ID: {}", nbisId);
        return createMockCaseDetails(nbisId);
    }

    @Override
    public CaseHistoryResponseDto getCaseHistoryFromV1Api(String nbisId) throws ApplicationException {
        logger.info("Using MOCK service - Getting case history for NBIS ID: {}", nbisId);
        return createMockCaseHistoryResponse(nbisId);
    }

    @Override
    public CaseDetailsAndHistoryResponse getCaseDetailsAndHistory(String caseId) throws ApplicationException {
        logger.info("Using MOCK service - Getting case details and history asynchronously for case ID: {} on thread: {}", 
                   caseId, Thread.currentThread().getName());
        
        try {
            // Execute both mock calls asynchronously using shared method
            Object[] results = getMockCaseDetailsAndHistoryAsync(caseId);
            CaseDetailsDto caseDetails = (CaseDetailsDto) results[0];
            CaseHistoryResponseDto caseHistory = (CaseHistoryResponseDto) results[1];
            
            // Combine both into response
            CaseDetailsAndHistoryResponse response = new CaseDetailsAndHistoryResponse(caseId, caseDetails, caseHistory);
            
            logger.info("Successfully retrieved MOCK case details and history asynchronously for case {} on thread: {}. History items: {}", 
                       caseId, Thread.currentThread().getName(), 
                       caseHistory != null && caseHistory.getHistory() != null ? caseHistory.getHistory().size() : 0);
            
            return response;
            
        } catch (Exception e) {
            logger.error("Error in MOCK service during async case details and history retrieval for case {}. Error: {}", caseId, e.getMessage(), e);
            throw new ApplicationException("Mock service error during async case details and history retrieval: " + e.getMessage(), e);
        }
    }

    /**
     * Private method to asynchronously retrieve mock case details and history for a given case ID
     * @param caseId The case ID to retrieve data for
     * @return Array containing [CaseDetailsDto, CaseHistoryResponseDto]
     */
    private Object[] getMockCaseDetailsAndHistoryAsync(String caseId) {
        logger.debug("Starting async MOCK retrieval for case details and history: {} on thread: {} (ID: {})", 
                   caseId, Thread.currentThread().getName(), Thread.currentThread().getId());
        
        // Execute both mock calls asynchronously
        CompletableFuture<CaseDetailsDto> caseDetailsFuture = CompletableFuture.supplyAsync(() -> {
            logger.debug("Starting async MOCK call for case details: {} on thread: {} (ID: {})", 
                       caseId, Thread.currentThread().getName(), Thread.currentThread().getId());
            return createMockCaseDetails(caseId);
        });
        
        CompletableFuture<CaseHistoryResponseDto> caseHistoryFuture = CompletableFuture.supplyAsync(() -> {
            logger.debug("Starting async MOCK call for case history: {} on thread: {} (ID: {})", 
                       caseId, Thread.currentThread().getName(), Thread.currentThread().getId());
            return createMockCaseHistoryResponse(caseId);
        });
        
        // Wait for both futures to complete and get results
        logger.debug("MOCK service - Waiting for async calls to complete on main thread: {} (ID: {})", 
                   Thread.currentThread().getName(), Thread.currentThread().getId());
        CaseDetailsDto caseDetails = caseDetailsFuture.join();
        CaseHistoryResponseDto caseHistory = caseHistoryFuture.join();
        
        return new Object[]{caseDetails, caseHistory};
    }
}