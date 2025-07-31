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
        return new CombinedCaseResponse();
    }




    public byte[] getLatestPdf(String caseId) throws ApplicationException {
        return new byte[1];
    }

    public CaseListResponseDto getAllCases(String subjectPersonaObjectId) throws ApplicationException {
        return new CaseListResponseDto();
    }

    public CaseDetailsDto getCaseDetails(String nbisId) throws ApplicationException {
        return new CaseDetailsDto();
    }

    public CaseHistoryResponseDto getCaseHistoryFromV1Api(String nbisId) throws ApplicationException {
       return new CaseHistoryResponseDto();
    }

    public CaseDetailsAndHistoryResponse getCaseDetailsAndHistory(String caseId) throws ApplicationException {
        return new CaseDetailsAndHistoryResponse();
    }

    
}