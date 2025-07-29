package com.clearance.tracker.service;

import com.clearance.tracker.dto.*;
import com.clearance.tracker.exception.ApplicationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExternalApiServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ExternalApiService externalApiService;

    private static final String BASE_URL = "http://localhost:8080";
    private static final String CASE_ID = "25092CASE1329752";
    private static final String SUBJECT_PERSONA_OBJECT_ID = "272ad768-ea92-4972-a8a5-2c270fdddd33";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(externalApiService, "baseUrl", BASE_URL);
    }

    @Test
    void testGetAllCases_Success() throws ApplicationException {
        // Arrange
        List<CaseDto> cases = Arrays.asList(
            new CaseDto("25092CASE1329752", "In Progress", "272ad768-ea92-4972-a8a5-2c270fdddd33", 
                       "2025-04-02T17:20:19.943Z", "2025-07-18T17:06:45.517Z", "Yes"),
            new CaseDto("25092CASE1329753", "Completed", "272ad768-ea92-4972-a8a5-2c270fdddd34", 
                       "2025-04-03T09:15:00.123Z", "2025-07-19T12:30:00.456Z", "No")
        );
        MetadataDto metadata = new MetadataDto(2);
        CaseListResponseDto expectedResponse = new CaseListResponseDto(cases, metadata);
        
        when(restTemplate.exchange(
            eq(BASE_URL + "/api/v1/cases?subjectPersonaObjectId=" + SUBJECT_PERSONA_OBJECT_ID),
            eq(HttpMethod.GET),
            eq(null),
            eq(CaseListResponseDto.class)
        )).thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        // Act
        CaseListResponseDto result = externalApiService.getAllCases(SUBJECT_PERSONA_OBJECT_ID);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getCases().size());
        assertEquals("25092CASE1329752", result.getCases().get(0).getNBISCaseID());
        assertEquals("In Progress", result.getCases().get(0).getDISAStatus());
        assertEquals(2, result.getMetadata().getTotalCases());
        
        verify(restTemplate, times(1)).exchange(
            eq(BASE_URL + "/api/v1/cases?subjectPersonaObjectId=" + SUBJECT_PERSONA_OBJECT_ID),
            eq(HttpMethod.GET),
            eq(null),
            eq(CaseListResponseDto.class)
        );
    }

    @Test
    void testGetAllCases_RestClientException() {
        // Arrange
        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            eq(null),
            eq(CaseListResponseDto.class)
        )).thenThrow(new RestClientException("Connection failed"));

        // Act & Assert
        ApplicationException exception = assertThrows(ApplicationException.class, 
            () -> externalApiService.getAllCases(SUBJECT_PERSONA_OBJECT_ID));
        
        assertTrue(exception.getMessage().contains("External service call failed for cases list"));
        assertTrue(exception.getCause() instanceof RestClientException);
    }

    @Test
    void testGetCaseDetails_Success() throws ApplicationException {
        // Arrange
        EAppAccountInfoDto eAppInfo = new EAppAccountInfoDto(
            "Initiated/Untouched by Applicant", "Released to Agency");
        CurrentStatusDto currentStatus = new CurrentStatusDto("RLTP", "Released to Parent Agency");
        PIPSStatusCheckResponseDto pipsResponse = new PIPSStatusCheckResponseDto("N", "Y", currentStatus);
        PyWorkPageDto pyWorkPage = new PyWorkPageDto(
            "dcas884617ORG1121PVQABC", "Review - eApp Received", SUBJECT_PERSONA_OBJECT_ID, CASE_ID,
            "2025-04-02T17:20:19.943Z", "System", "2025-07-18T17:06:45.517Z", "System",
            "Example Org", "/Example/Org/Path", "High", "PVQ-A-B-C", "2023",
            eAppInfo, pipsResponse, "Completed", "2025-07-20", "Yes"
        );
        CaseDetailsDto expectedResponse = new CaseDetailsDto(pyWorkPage);
        
        when(restTemplate.exchange(
            eq(BASE_URL + "/api/v1/cases/" + CASE_ID),
            eq(HttpMethod.GET),
            eq(null),
            eq(CaseDetailsDto.class)
        )).thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        // Act
        CaseDetailsDto result = externalApiService.getCaseDetails(CASE_ID);

        // Assert
        assertNotNull(result);
        assertEquals(CASE_ID, result.getPyWorkPage().getNBISCaseID());
        assertEquals("Review - eApp Received", result.getPyWorkPage().getDISAStatus());
        
        verify(restTemplate, times(1)).exchange(
            eq(BASE_URL + "/api/v1/cases/" + CASE_ID),
            eq(HttpMethod.GET),
            eq(null),
            eq(CaseDetailsDto.class)
        );
    }

    @Test
    void testGetCaseDetails_ApplicationException() {
        // Arrange
        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            eq(null),
            eq(CaseDetailsDto.class)
        )).thenThrow(new RestClientException("Service unavailable"));

        // Act & Assert
        ApplicationException exception = assertThrows(ApplicationException.class, 
            () -> externalApiService.getCaseDetails(CASE_ID));
        
        assertTrue(exception.getMessage().contains("External service call failed for case details"));
    }

    @Test
    void testGetCaseHistoryFromV1Api_Success() throws ApplicationException {
        // Arrange
        List<CaseHistoryDto> history = Arrays.asList(
            new CaseHistoryDto("2025-06-06T10:00:00Z", "Agency Initiated Investigation Request.", "System"),
            new CaseHistoryDto("2025-06-10T14:30:00Z", "e-QIP data received.", "e-QIP Integration"),
            new CaseHistoryDto("2025-07-18T16:00:00Z", "Case status updated to 'Review - eApp Received'.", "System")
        );
        CaseHistoryResponseDto expectedResponse = new CaseHistoryResponseDto(CASE_ID, history);
        
        when(restTemplate.exchange(
            eq(BASE_URL + "/api/v1/cases/" + CASE_ID + "/history"),
            eq(HttpMethod.GET),
            eq(null),
            eq(CaseHistoryResponseDto.class)
        )).thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        // Act
        CaseHistoryResponseDto result = externalApiService.getCaseHistoryFromV1Api(CASE_ID);

        // Assert
        assertNotNull(result);
        assertEquals(CASE_ID, result.getNBISCaseID());
        assertEquals(3, result.getHistory().size());
        assertEquals("Agency Initiated Investigation Request.", result.getHistory().get(0).getDescription());
        
        verify(restTemplate, times(1)).exchange(
            eq(BASE_URL + "/api/v1/cases/" + CASE_ID + "/history"),
            eq(HttpMethod.GET),
            eq(null),
            eq(CaseHistoryResponseDto.class)
        );
    }

    @Test
    void testGetLatestPdf_Success() throws ApplicationException {
        // Arrange
        byte[] expectedPdfBytes = "PDF content bytes here".getBytes();
        
        when(restTemplate.exchange(
            eq(BASE_URL + "/api/latest-pdf?caseId=" + CASE_ID),
            eq(HttpMethod.GET),
            eq(null),
            eq(byte[].class)
        )).thenReturn(new ResponseEntity<>(expectedPdfBytes, HttpStatus.OK));

        // Act
        byte[] result = externalApiService.getLatestPdf(CASE_ID);

        // Assert
        assertNotNull(result);
        assertArrayEquals(expectedPdfBytes, result);
        assertEquals(expectedPdfBytes.length, result.length);
        
        verify(restTemplate, times(1)).exchange(
            eq(BASE_URL + "/api/latest-pdf?caseId=" + CASE_ID),
            eq(HttpMethod.GET),
            eq(null),
            eq(byte[].class)
        );
    }

    @Test
    void testGetLatestPdf_RestClientException() {
        // Arrange
        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            eq(null),
            eq(byte[].class)
        )).thenThrow(new RestClientException("PDF service down"));

        // Act & Assert
        ApplicationException exception = assertThrows(ApplicationException.class, 
            () -> externalApiService.getLatestPdf(CASE_ID));
        
        assertTrue(exception.getMessage().contains("External service call failed for latest PDF"));
    }

    @Test
    void testGetCaseHistory_Success() throws ApplicationException {
        // Arrange
        List<CaseDto> cases = Arrays.asList(
            new CaseDto("25092CASE1329752", "In Progress", SUBJECT_PERSONA_OBJECT_ID, 
                       "2025-04-02T17:20:19.943Z", "2025-07-18T17:06:45.517Z", "Yes"),
            new CaseDto("25092CASE1329753", "Completed", "272ad768-ea92-4972-a8a5-2c270fdddd34", 
                       "2025-04-03T09:15:00.123Z", "2025-07-19T12:30:00.456Z", "No")
        );
        MetadataDto metadata = new MetadataDto(2);
        CaseListResponseDto caseListResponse = new CaseListResponseDto(cases, metadata);
        
        // Mock case details
        EAppAccountInfoDto eAppInfo = new EAppAccountInfoDto("Initiated/Untouched by Applicant", "Released to Agency");
        CurrentStatusDto currentStatus = new CurrentStatusDto("RLTP", "Released to Parent Agency");
        PIPSStatusCheckResponseDto pipsResponse = new PIPSStatusCheckResponseDto("N", "Y", currentStatus);
        PyWorkPageDto pyWorkPage = new PyWorkPageDto(
            "dcas884617ORG1121PVQABC", "Review - eApp Received", SUBJECT_PERSONA_OBJECT_ID, CASE_ID,
            "2025-04-02T17:20:19.943Z", "System", "2025-07-18T17:06:45.517Z", "System",
            "Example Org", "/Example/Org/Path", "High", "PVQ-A-B-C", "2023",
            eAppInfo, pipsResponse, "Completed", "2025-07-20", "Yes"
        );
        CaseDetailsDto caseDetails = new CaseDetailsDto(pyWorkPage);
        
        // Mock case history
        List<CaseHistoryDto> history = Arrays.asList(
            new CaseHistoryDto("2025-06-06T10:00:00Z", "Agency Initiated Investigation Request.", "System")
        );
        CaseHistoryResponseDto caseHistoryResponse = new CaseHistoryResponseDto(CASE_ID, history);

        // Mock API calls
        when(restTemplate.exchange(
            eq(BASE_URL + "/api/v1/cases?subjectPersonaObjectId=" + SUBJECT_PERSONA_OBJECT_ID),
            eq(HttpMethod.GET),
            eq(null),
            eq(CaseListResponseDto.class)
        )).thenReturn(new ResponseEntity<>(caseListResponse, HttpStatus.OK));
        
        when(restTemplate.exchange(
            eq(BASE_URL + "/api/v1/cases/" + CASE_ID),
            eq(HttpMethod.GET),
            eq(null),
            eq(CaseDetailsDto.class)
        )).thenReturn(new ResponseEntity<>(caseDetails, HttpStatus.OK));
        
        when(restTemplate.exchange(
            eq(BASE_URL + "/api/v1/cases/" + CASE_ID + "/history"),
            eq(HttpMethod.GET),
            eq(null),
            eq(CaseHistoryResponseDto.class)
        )).thenReturn(new ResponseEntity<>(caseHistoryResponse, HttpStatus.OK));

        // Act
        CombinedCaseResponse result = externalApiService.getCaseHistory(SUBJECT_PERSONA_OBJECT_ID);

        // Assert
        assertNotNull(result);
        assertEquals(CASE_ID, result.getSelectedCaseId());
        assertNotNull(result.getCasesList());
        assertEquals(2, result.getCasesList().getCases().size());
        assertNotNull(result.getSelectedCaseDetails());
        assertEquals(CASE_ID, result.getSelectedCaseDetails().getPyWorkPage().getNBISCaseID());
        assertNotNull(result.getCaseHistory());
        assertEquals(1, result.getCaseHistory().getHistory().size());
    }

    @Test
    void testGetCaseHistory_NoInProgressCases() {
        // Arrange
        List<CaseDto> cases = Arrays.asList(
            new CaseDto("25092CASE1329752", "Completed", SUBJECT_PERSONA_OBJECT_ID, 
                       "2025-04-02T17:20:19.943Z", "2025-07-18T17:06:45.517Z", "Yes")
        );
        MetadataDto metadata = new MetadataDto(1);
        CaseListResponseDto caseListResponse = new CaseListResponseDto(cases, metadata);
        
        when(restTemplate.exchange(
            eq(BASE_URL + "/api/v1/cases?subjectPersonaObjectId=" + SUBJECT_PERSONA_OBJECT_ID),
            eq(HttpMethod.GET),
            eq(null),
            eq(CaseListResponseDto.class)
        )).thenReturn(new ResponseEntity<>(caseListResponse, HttpStatus.OK));

        // Act & Assert
        ApplicationException exception = assertThrows(ApplicationException.class, 
            () -> externalApiService.getCaseHistory(SUBJECT_PERSONA_OBJECT_ID));
        
        assertTrue(exception.getMessage().contains("No cases found with 'In Progress' status"));
    }

    @Test
    void testGetCaseDetailsAndHistory_Success() throws ApplicationException {
        // Arrange
        EAppAccountInfoDto eAppInfo = new EAppAccountInfoDto("Initiated/Untouched by Applicant", "Released to Agency");
        CurrentStatusDto currentStatus = new CurrentStatusDto("RLTP", "Released to Parent Agency");
        PIPSStatusCheckResponseDto pipsResponse = new PIPSStatusCheckResponseDto("N", "Y", currentStatus);
        PyWorkPageDto pyWorkPage = new PyWorkPageDto(
            "dcas884617ORG1121PVQABC", "Review - eApp Received", SUBJECT_PERSONA_OBJECT_ID, CASE_ID,
            "2025-04-02T17:20:19.943Z", "System", "2025-07-18T17:06:45.517Z", "System",
            "Example Org", "/Example/Org/Path", "High", "PVQ-A-B-C", "2023",
            eAppInfo, pipsResponse, "Completed", "2025-07-20", "Yes"
        );
        CaseDetailsDto caseDetails = new CaseDetailsDto(pyWorkPage);
        
        List<CaseHistoryDto> history = Arrays.asList(
            new CaseHistoryDto("2025-06-06T10:00:00Z", "Agency Initiated Investigation Request.", "System")
        );
        CaseHistoryResponseDto caseHistoryResponse = new CaseHistoryResponseDto(CASE_ID, history);

        when(restTemplate.exchange(
            eq(BASE_URL + "/api/v1/cases/" + CASE_ID),
            eq(HttpMethod.GET),
            eq(null),
            eq(CaseDetailsDto.class)
        )).thenReturn(new ResponseEntity<>(caseDetails, HttpStatus.OK));
        
        when(restTemplate.exchange(
            eq(BASE_URL + "/api/v1/cases/" + CASE_ID + "/history"),
            eq(HttpMethod.GET),
            eq(null),
            eq(CaseHistoryResponseDto.class)
        )).thenReturn(new ResponseEntity<>(caseHistoryResponse, HttpStatus.OK));

        // Act
        CaseDetailsAndHistoryResponse result = externalApiService.getCaseDetailsAndHistory(CASE_ID);

        // Assert
        assertNotNull(result);
        assertEquals(CASE_ID, result.getCaseId());
        assertNotNull(result.getCaseDetails());
        assertEquals(CASE_ID, result.getCaseDetails().getPyWorkPage().getNBISCaseID());
        assertNotNull(result.getCaseHistory());
        assertEquals(1, result.getCaseHistory().getHistory().size());
        
        // Verify both async calls were made
        verify(restTemplate, times(1)).exchange(
            eq(BASE_URL + "/api/v1/cases/" + CASE_ID),
            eq(HttpMethod.GET),
            eq(null),
            eq(CaseDetailsDto.class)
        );
        verify(restTemplate, times(1)).exchange(
            eq(BASE_URL + "/api/v1/cases/" + CASE_ID + "/history"),
            eq(HttpMethod.GET),
            eq(null),
            eq(CaseHistoryResponseDto.class)
        );
    }

    @Test
    void testGetCaseDetailsAndHistory_AsyncException() {
        // Arrange
        when(restTemplate.exchange(
            eq(BASE_URL + "/api/v1/cases/" + CASE_ID),
            eq(HttpMethod.GET),
            eq(null),
            eq(CaseDetailsDto.class)
        )).thenThrow(new RestClientException("Service unavailable"));

        // Act & Assert
        ApplicationException exception = assertThrows(ApplicationException.class, 
            () -> externalApiService.getCaseDetailsAndHistory(CASE_ID));
        
        assertTrue(exception.getMessage().contains("Unexpected error during async case details and history retrieval"));
    }
}