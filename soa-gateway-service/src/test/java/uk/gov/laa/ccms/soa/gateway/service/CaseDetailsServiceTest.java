package uk.gov.laa.ccms.soa.gateway.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.laa.ccms.soa.gateway.client.CaseServicesClient;
import uk.gov.laa.ccms.soa.gateway.mapper.CaseDetailsMapper;
import uk.gov.laa.ccms.soa.gateway.model.CaseDetail;
import uk.gov.laa.ccms.soa.gateway.model.TransactionStatus;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.CaseAddUpdtStatusRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.CaseInqRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.CaseUpdateRQ;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.CaseUpdateRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.Case;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.CaseInfo;
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

    @Captor
    ArgumentCaptor<CaseInfo> requestCaptor;

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
    void amendCase_returnsTransactionId() {

        CaseDetail caseDetail = new CaseDetail();
        CaseUpdateRQ caseUpdateRQ = new CaseUpdateRQ();

        CaseUpdateRS caseUpdateRS = new CaseUpdateRS();
        caseUpdateRS.setTransactionID("transactionId");

        when(caseDetailsMapper.toCaseUpdateRq(caseDetail, "caseUpdateType")).thenReturn(caseUpdateRQ);
        when(caseServicesClient.updateCase(SOA_GATEWAY_USER_LOGIN_ID, SOA_GATEWAY_USER_ROLE, caseUpdateRQ)).thenReturn(caseUpdateRS);

        String result =
            caseDetailsService.amendCase(SOA_GATEWAY_USER_LOGIN_ID, SOA_GATEWAY_USER_ROLE, caseDetail, "caseUpdateType");

        assertEquals(caseUpdateRS.getTransactionID(), result);
    }

}
