package uk.gov.laa.ccms.soa.gateway.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import uk.gov.laa.ccms.soa.gateway.client.CaseServicesClient;
import uk.gov.laa.ccms.soa.gateway.mapper.CaseDetailsMapper;
import uk.gov.laa.ccms.soa.gateway.model.CaseDetails;
import uk.gov.laa.ccms.soa.gateway.model.CaseSummary;
import uk.gov.laa.ccms.soa.gateway.model.TransactionStatus;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.CaseAddUpdtStatusRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.CaseInqRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.Case;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.CaseInfo;
import uk.gov.legalservices.enterprise.common._1_0.common.RecordCount;
import uk.gov.legalservices.enterprise.common._1_0.header.HeaderRSType;
import uk.gov.legalservices.enterprise.common._1_0.header.Status;
import uk.gov.legalservices.enterprise.common._1_0.header.StatusTextType;


@ExtendWith(MockitoExtension.class)
public class CaseDetailsServiceTest {

    @Mock
    private CaseServicesClient caseServicesClient;

    @Mock
    private CaseDetailsMapper caseDetailsMapper;

    @InjectMocks
    private CaseDetailsService caseDetailsService;

    private static final String SOA_GATEWAY_USER_LOGIN_ID = "user";
    private static final String SOA_GATEWAY_USER_ROLE = "EXTERNAL";
    private static final String CASE_REFERENCE_NUMBER = "1234567890";
    private static final String PROVIDER_CASE_REFERENCE_NUMBER = "ABCDEF";
    private static final String CLIENT_SURNAME = "Doe";
    private static final String CASE_STATUS = "APPL";
    private static final Integer FEE_EARNER_ID = 123;
    private static final String FEE_EARNER_NAME = "feeearner";
    private static final String CATEGORY_OF_LAW = "CAT1";
    private static final Integer OFFICE_ID = 987;
    
    private static final Integer MAX_RECORDS = 50;

    private static final Pageable PAGEABLE = Pageable.ofSize(20);

    @Captor
    ArgumentCaptor<CaseInfo> requestCaptor;

    @Test
    public void testGetCaseDetails() {
        // Create a mocked instance of the mapped client details
        final CaseDetails caseDetails = new CaseDetails();

        final CaseInfo caseInfo = buildCaseInfo();
        final CaseSummary caseSummary = buildCaseSummary();

        final List<CaseSummary> caseSummaryList = Collections.singletonList(caseSummary);
        final Page<CaseSummary> caseSummaryPage = new PageImpl<>(caseSummaryList, PAGEABLE, caseSummaryList.size());

        // Create a mocked instance of the response object
        final CaseInqRS response = new CaseInqRS();
        response.setRecordCount(new RecordCount());
        response.getRecordCount().setRecordsFetched(
            BigInteger.valueOf(caseSummaryList.size()));

        // Stub the client to return the mocked response
        when(caseServicesClient.getCaseDetails(
                eq(SOA_GATEWAY_USER_LOGIN_ID),
                eq(SOA_GATEWAY_USER_ROLE),
                eq(MAX_RECORDS),
                any(CaseInfo.class)
                ))
                .thenReturn(response);

        // Stub the mapper to return the mocked client details
        when(caseDetailsMapper.toCaseSummaryList(response)).thenReturn(caseSummaryList);
        when(caseDetailsMapper.toCaseDetails(caseSummaryPage)).thenReturn(caseDetails);

        // Call the service method
        final CaseDetails result = caseDetailsService.getCaseDetails(
            SOA_GATEWAY_USER_LOGIN_ID,
            SOA_GATEWAY_USER_ROLE,
            CASE_REFERENCE_NUMBER,
            PROVIDER_CASE_REFERENCE_NUMBER,
            CASE_STATUS,
            CLIENT_SURNAME,
            FEE_EARNER_ID,
            OFFICE_ID,
            MAX_RECORDS,
            PAGEABLE
        );

        // Verify that the client method was called with the expected arguments
        verify(caseServicesClient).getCaseDetails(
            eq(SOA_GATEWAY_USER_LOGIN_ID),
            eq(SOA_GATEWAY_USER_ROLE),
            eq(MAX_RECORDS),
            requestCaptor.capture()
        );

        // Verify that the mapper methods were called
        verify(caseDetailsMapper).toCaseSummaryList(response);
        verify(caseDetailsMapper).toCaseDetails(caseSummaryPage);

        // Check the captured CaseInfo
        assertEquals(caseInfo.getCaseReferenceNumber(), requestCaptor.getValue().getCaseReferenceNumber());
        assertEquals(caseInfo.getProviderCaseReferenceNumber(), requestCaptor.getValue().getProviderCaseReferenceNumber());
        assertEquals(caseInfo.getCaseStatus(), requestCaptor.getValue().getCaseStatus());
        assertEquals(caseInfo.getFeeEarnerContactID(), requestCaptor.getValue().getFeeEarnerContactID());
        assertEquals(caseInfo.getOfficeID(), requestCaptor.getValue().getOfficeID());
        assertEquals(caseInfo.getClientSurname(), requestCaptor.getValue().getClientSurname());

        // Assert the result
        assertEquals(caseDetails, result);
    }

    @Test
    void getCaseDetail_returnsMappedCaseDetail() {
        // Arrange
        String caseReferenceNumber = "ref123";

        CaseInqRS mockResponse = new CaseInqRS();
        Case mockCase = new Case();
        mockResponse.setCase(mockCase);

        uk.gov.laa.ccms.soa.gateway.model.CaseDetail expectedDetail = new uk.gov.laa.ccms.soa.gateway.model.CaseDetail();

        when(caseServicesClient.getCaseDetail(SOA_GATEWAY_USER_LOGIN_ID, SOA_GATEWAY_USER_ROLE, caseReferenceNumber))
            .thenReturn(mockResponse);
        when(caseDetailsMapper.toCaseDetail(mockCase)).thenReturn(expectedDetail);

        // Act
        uk.gov.laa.ccms.soa.gateway.model.CaseDetail
            result = caseDetailsService.getCaseDetail(SOA_GATEWAY_USER_LOGIN_ID, SOA_GATEWAY_USER_ROLE, caseReferenceNumber);

        // Assert
        assertEquals(expectedDetail, result);
    }

    @Test
    public void testGetCaseStatus() {
        String transactionId = "sampleTransactionId";
        String soaGatewayUserLoginId = "user";
        String soaGatewayUserRole = "EXTERNAL";

        TransactionStatus mockTransactionStatus
            = new TransactionStatus().submissionStatus(StatusTextType.SUCCESS.name());

        Status mockStatus =  new Status();
        mockStatus.setStatus(StatusTextType.SUCCESS);

        HeaderRSType mockHeader = new HeaderRSType();
        mockHeader.setStatus(mockStatus);

        CaseAddUpdtStatusRS mockCaseAddUpdtStatusRS = new CaseAddUpdtStatusRS();
        mockCaseAddUpdtStatusRS.setHeaderRS(mockHeader);

        when(caseServicesClient.getCaseTransactionStatus(
            soaGatewayUserLoginId,
            soaGatewayUserRole,
            transactionId)).thenReturn(mockCaseAddUpdtStatusRS);

        when(caseDetailsMapper.toTransactionStatus(mockCaseAddUpdtStatusRS))
            .thenReturn(mockTransactionStatus);

        TransactionStatus result
            = caseDetailsService.getCaseTransactionStatus(
                soaGatewayUserLoginId, soaGatewayUserRole, transactionId);

        assertEquals(mockTransactionStatus, result);

        verify(caseServicesClient).getCaseTransactionStatus(
            soaGatewayUserLoginId,soaGatewayUserRole, transactionId);
    }

    private CaseSummary buildCaseSummary(){
        return new CaseSummary()
            .caseReferenceNumber(CASE_REFERENCE_NUMBER)
            .providerCaseReferenceNumber(PROVIDER_CASE_REFERENCE_NUMBER)
            .caseStatusDisplay(CASE_STATUS)
            .feeEarnerName(FEE_EARNER_NAME)
            .categoryOfLaw(CATEGORY_OF_LAW);
    }

    private CaseInfo buildCaseInfo(){
        CaseInfo caseInfo = new CaseInfo();
        caseInfo.setCaseReferenceNumber(CASE_REFERENCE_NUMBER);
        caseInfo.setCaseStatus(CASE_STATUS);
        caseInfo.setClientSurname(CLIENT_SURNAME);
        caseInfo.setProviderCaseReferenceNumber(PROVIDER_CASE_REFERENCE_NUMBER);
        caseInfo.setFeeEarnerContactID(String.valueOf(FEE_EARNER_ID));
        caseInfo.setOfficeID(String.valueOf(OFFICE_ID));
        return caseInfo;
    }
}
