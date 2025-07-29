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
import com.clearance.tracker.dto.CurrentStatusDto;
import com.clearance.tracker.dto.EAppAccountInfoDto;
import com.clearance.tracker.dto.MetadataDto;
import com.clearance.tracker.dto.PIPSStatusCheckResponseDto;
import com.clearance.tracker.dto.PdfContent;
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


    public List<PdfContent> getPdfContents(String caseId) throws ApplicationException {
        logger.info("Using MOCK service - Getting PDF contents for case: {}", caseId);
        
        try {
            List<PdfContent> mockPdfContents = createMockPdfContents(caseId);
            logger.info("Successfully retrieved {} mock PDF contents for case {}", mockPdfContents.size(), caseId);
            return mockPdfContents;
            
        } catch (Exception e) {
            logger.error("Error in MOCK service during PDF contents retrieval for case {}. Error: {}", caseId, e.getMessage(), e);
            throw new ApplicationException("Mock service error during PDF contents retrieval: " + e.getMessage(), e);
        }
    }

    private List<PdfContent> createMockPdfContents(String caseId) {
        logger.info("Creating mock PDF contents for case: {}", caseId);
        return Arrays.asList(
            new PdfContent(1L, caseId, "Security Clearance Application Form", "Application", 
                "SF-86_" + caseId + ".pdf",
                "SECURITY CLEARANCE APPLICATION FORM\n\nCase ID: " + caseId + "\n\nSECTION 1: PERSONAL INFORMATION\nFull Name: John A. Smith\nDate of Birth: 01/15/1985\nSSN: XXX-XX-1234\nPlace of Birth: Washington, DC\n\nSECTION 2: EMPLOYMENT HISTORY\nCurrent Employer: Defense Contractor Inc.\nPosition: Software Engineer\nStart Date: 03/2020\nSecurity Officer: Jane Doe\n\nSECTION 3: EDUCATION\nUniversity: Georgetown University\nDegree: Bachelor of Science in Computer Science\nGraduation: May 2007\n\nSECTION 4: REFERENCES\n1. Michael Johnson - Former Supervisor\n2. Sarah Williams - Colleague\n3. Robert Brown - Academic Reference",
                LocalDateTime.of(2025, 2, 1, 9, 30), "John Smith", "submitted"),
            
            new PdfContent(2L, caseId, "Background Investigation Report", "Investigation", 
                "BIR_" + caseId + ".pdf",
                "BACKGROUND INVESTIGATION REPORT\n\nSubject: John A. Smith\nCase Number: " + caseId + "\nInvestigation Type: Top Secret Clearance\nInvestigator: Agent Mary Johnson\nDate: February 20, 2025\n\nFINDINGS:\n\n1. EMPLOYMENT VERIFICATION\n- All employment history verified\n- No gaps in employment found\n- Positive recommendations from supervisors\n\n2. EDUCATION VERIFICATION\n- Georgetown University degree confirmed\n- Academic records reviewed\n- No disciplinary actions found\n\n3. CRIMINAL HISTORY CHECK\n- No criminal records found\n- Clean driving record\n- No outstanding warrants\n\n4. FINANCIAL REVIEW\n- Credit score: 750\n- No bankruptcies or foreclosures\n- Current on all financial obligations\n\n5. PERSONAL REFERENCES\n- All references contacted and interviewed\n- Unanimous positive feedback\n- No security concerns raised\n\nRECOMMENDATA: Subject meets all requirements for Top Secret clearance.",
                LocalDateTime.of(2025, 2, 20, 14, 15), "Agent Mary Johnson", "completed"),
            
            new PdfContent(3L, caseId, "Security Interview Transcript", "Interview", 
                "SIT_" + caseId + ".pdf",
                "SECURITY INTERVIEW TRANSCRIPT\n\nDate: February 25, 2025\nTime: 10:00 AM\nLocation: Federal Building, Room 305\nInterviewer: Special Agent Robert Davis\nSubject: John A. Smith\nCase: " + caseId + "\n\nINTERVIEW TRANSCRIPT:\n\nAGENT DAVIS: Please state your full name for the record.\n\nSMITH: John Alexander Smith\n\nAGENT DAVIS: Are you applying for a Top Secret security clearance?\n\nSMITH: Yes, that's correct.\n\nAGENT DAVIS: Have you ever been contacted by foreign intelligence services?\n\nSMITH: No, never.\n\nAGENT DAVIS: Do you have any foreign contacts or travel?\n\nSMITH: I traveled to Canada for vacation in 2023, but no foreign contacts related to my work.\n\nAGENT DAVIS: Any concerns about your ability to protect classified information?\n\nSMITH: None whatsoever. I understand the responsibility and take it very seriously.\n\n[Interview continued for 45 minutes]\n\nCONCLUSION: Subject demonstrated good knowledge of security requirements and showed no indicators of security risk.",
                LocalDateTime.of(2025, 2, 25, 11, 30), "Special Agent Robert Davis", "completed"),
            
            new PdfContent(4L, caseId, "Medical Clearance Certificate", "Medical", 
                "MCC_" + caseId + ".pdf",
                "MEDICAL CLEARANCE CERTIFICATE\n\nPatient: John A. Smith\nDate of Examination: February 28, 2025\nPhysician: Dr. Emily Carter, MD\nFacility: Federal Medical Center\nCase Reference: " + caseId + "\n\nEXAMINATION RESULTS:\n\nVITAL SIGNS:\n- Blood Pressure: 120/80 mmHg\n- Heart Rate: 72 bpm\n- Temperature: 98.6°F\n- Weight: 180 lbs\n- Height: 6'0\"\n\nMEDICAL HISTORY:\n- No significant medical conditions\n- No history of mental health treatment\n- No current medications\n- No allergies reported\n\nPHYSICAL EXAMINATION:\n- General appearance: Healthy adult male\n- Cardiovascular: Normal heart sounds\n- Respiratory: Clear lung sounds\n- Neurological: Normal reflexes and mental status\n\nLABORATORY RESULTS:\n- Complete Blood Count: Normal\n- Comprehensive Metabolic Panel: Normal\n- Drug Screen: Negative\n\nCONCLUSION:\nPatient is medically cleared for security clearance duties. No medical conditions that would impair judgment or reliability.\n\nDr. Emily Carter, MD\nLicense #: MD12345",
                LocalDateTime.of(2025, 2, 28, 16, 0), "Dr. Emily Carter", "approved")
        );
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