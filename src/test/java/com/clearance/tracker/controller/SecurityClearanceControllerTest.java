package com.clearance.tracker.controller;

import com.clearance.tracker.dto.CaseDetailsAndHistoryResponse;
import com.clearance.tracker.dto.CombinedCaseResponse;
import com.clearance.tracker.exception.ApplicationException;
import com.clearance.tracker.service.ExternalApiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;

@WebMvcTest(SecurityClearanceController.class)
class SecurityClearanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExternalApiService externalApiService;

    // ObjectMapper can be used for JSON serialization in future tests

    private static final String VALID_UUID = "272ad768-ea92-4972-a8a5-2c270fdddd33";
    private static final String VALID_CASE_ID = "25092CASE1329752";

    @Test
    void testGetCaseHistory_Success() throws Exception {
        // Arrange
        CombinedCaseResponse mockResponse = new CombinedCaseResponse();
        when(externalApiService.getCaseHistory(VALID_UUID)).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(get("/clearance/case-history")
                .param("subjectPersonaObjectId", VALID_UUID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetCaseHistory_InvalidUUID() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/clearance/case-history")
                .param("subjectPersonaObjectId", "invalid-uuid")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errorCode").exists());
    }

    @Test
    void testGetCaseHistory_BlankParameter() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/clearance/case-history")
                .param("subjectPersonaObjectId", "")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errorCode").exists());
    }

    @Test
    void testGetCaseHistory_ApplicationException() throws Exception {
        // Arrange
        when(externalApiService.getCaseHistory(VALID_UUID))
                .thenThrow(new ApplicationException("Service unavailable"));

        // Act & Assert
        mockMvc.perform(get("/clearance/case-history")
                .param("subjectPersonaObjectId", VALID_UUID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errorCode").exists())
                .andExpect(jsonPath("$.errorMessage").value(containsString("External service failed")));
    }

    @Test
    void testDownloadPdf_Success() throws Exception {
        // Arrange
        byte[] mockPdfBytes = "Mock PDF content".getBytes();
        when(externalApiService.getLatestPdf(VALID_CASE_ID)).thenReturn(mockPdfBytes);

        // Act & Assert
        mockMvc.perform(get("/clearance/pdf-download/{caseId}", VALID_CASE_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(header().string("Content-Disposition", "form-data; name=\"attachment\"; filename=\"" + VALID_CASE_ID + ".pdf\""))
                .andExpect(content().bytes(mockPdfBytes));
    }

    @Test
    void testDownloadPdf_NotFound() throws Exception {
        // Arrange
        when(externalApiService.getLatestPdf(VALID_CASE_ID)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/clearance/pdf-download/{caseId}", VALID_CASE_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value(404))
                .andExpect(jsonPath("$.errorMessage").value(containsString("No PDF found for case")));
    }

    @Test
    void testDownloadPdf_EmptyPdf() throws Exception {
        // Arrange
        when(externalApiService.getLatestPdf(VALID_CASE_ID)).thenReturn(new byte[0]);

        // Act & Assert
        mockMvc.perform(get("/clearance/pdf-download/{caseId}", VALID_CASE_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value(404));
    }

    @Test
    void testDownloadPdf_InvalidCaseId() throws Exception {
        // Act & Assert - Path variables with special characters return 404 in Spring
        mockMvc.perform(get("/clearance/pdf-download/{caseId}", "invalid@case#id"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDownloadPdf_CaseIdTooShort() throws Exception {
        // Act & Assert - Spring handles path variable validation differently
        mockMvc.perform(get("/clearance/pdf-download/{caseId}", "123"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetCaseDetailsAndHistory_Success() throws Exception {
        // Arrange
        CaseDetailsAndHistoryResponse mockResponse = new CaseDetailsAndHistoryResponse();
        when(externalApiService.getCaseDetailsAndHistory(VALID_CASE_ID)).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(get("/clearance/case-details-history/{caseId}", VALID_CASE_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetCaseDetailsAndHistory_ApplicationException() throws Exception {
        // Arrange
        when(externalApiService.getCaseDetailsAndHistory(VALID_CASE_ID))
                .thenThrow(new ApplicationException("Service error"));

        // Act & Assert
        mockMvc.perform(get("/clearance/case-details-history/{caseId}", VALID_CASE_ID))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errorCode").exists())
                .andExpect(jsonPath("$.errorMessage").value(containsString("External service failed")));
    }

    @Test
    void testGetCaseDetailsAndHistory_InvalidCaseId() throws Exception {
        // Act & Assert - Empty path variables cause internal server error in some cases
        mockMvc.perform(get("/clearance/case-details-history/{caseId}", ""))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testCorsHeaders() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/clearance/case-history")
                .param("subjectPersonaObjectId", VALID_UUID)
                .header("Origin", "http://localhost:3000"))
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"));
    }
}