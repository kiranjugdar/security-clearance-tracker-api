package com.clearance.tracker.service;

import com.clearance.tracker.dto.CaseHistoryItem;
import com.clearance.tracker.dto.CombinedCaseResponse;
import com.clearance.tracker.dto.CurrentStatus;
import com.clearance.tracker.dto.PdfContent;
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

@Service
@Profile("mock")
public class MockExternalApiService extends ExternalApiService {

    private static final Logger logger = LoggerFactory.getLogger(MockExternalApiService.class);
    private static final String IN_PROGRESS_STATUS = "In Progress";

    @Override
    public CombinedCaseResponse getCaseHistory() throws ApplicationException {
        logger.info("Using MOCK service - Starting complex case history retrieval process");
        
        try {
            // Step 1: Mock case history data
            List<CaseHistoryItem> allCases = createMockCaseHistory();
            logger.info("Step 1 completed: Retrieved {} mock cases", allCases.size());
            
            // Step 2: Filter cases with "In Progress" status and pick first one
            String selectedCaseId = filterAndSelectFirstInProgressCase(allCases);
            
            // Step 3: Mock status history for selected case
            List<StatusHistoryItem> statusHistory = createMockStatusHistory(selectedCaseId);
            logger.info("Step 3 completed: Retrieved {} mock status history items for case {}", 
                       statusHistory.size(), selectedCaseId);
            
            // Step 4: Extract current status from mock data
            CurrentStatus currentStatus = extractCurrentStatusFromHistory(statusHistory, selectedCaseId);
            
            // Step 5: Combine all mock data and return
            CombinedCaseResponse response = new CombinedCaseResponse(allCases, currentStatus, statusHistory, selectedCaseId);
            
            logger.info("Successfully completed MOCK case history retrieval. Selected case: {}, Total cases: {}, Status history items: {}", 
                       selectedCaseId, allCases.size(), statusHistory.size());
            
            return response;
            
        } catch (Exception e) {
            logger.error("Error in MOCK service during case history retrieval. Error: {}", e.getMessage(), e);
            throw new ApplicationException("Mock service error during case history retrieval: " + e.getMessage(), e);
        }
    }

    private List<CaseHistoryItem> createMockCaseHistory() {
        logger.info("Creating mock case history data");
        return Arrays.asList(
            new CaseHistoryItem(1L, "SCT-2024-001", "In Progress", "/case/SCT-2024-001"),
            new CaseHistoryItem(2L, "SCT-2024-002", "Under Review", "/case/SCT-2024-002"),
            new CaseHistoryItem(3L, "SCT-2024-003", "Approved", "/case/SCT-2024-003"),
            new CaseHistoryItem(4L, "SCT-2024-004", "In Progress", "/case/SCT-2024-004"),
            new CaseHistoryItem(5L, "SCT-2024-005", "Closed", "/case/SCT-2024-005")
        );
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

    private String filterAndSelectFirstInProgressCase(List<CaseHistoryItem> allCases) throws ApplicationException {
        logger.info("Step 2: Filtering mock cases with '{}' status", IN_PROGRESS_STATUS);
        
        Optional<CaseHistoryItem> inProgressCase = allCases.stream()
            .filter(caseItem -> IN_PROGRESS_STATUS.equalsIgnoreCase(caseItem.getCaseStatus()))
            .findFirst();
        
        if (inProgressCase.isEmpty()) {
            logger.warn("No mock cases found with '{}' status. Available cases: {}", IN_PROGRESS_STATUS, 
                       allCases.stream().map(c -> c.getCaseId() + ":" + c.getCaseStatus()).toList());
            throw new ApplicationException("No cases found with 'In Progress' status");
        }
        
        String selectedCaseId = inProgressCase.get().getCaseId();
        logger.info("Step 2 completed: Selected first 'In Progress' mock case: {}", selectedCaseId);
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
}