package uk.gov.laa.ccms.soa.gateway.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.laa.ccms.soa.gateway.client.CreateInvoiceClient;
import uk.gov.laa.ccms.soa.gateway.client.GetInvoiceDetailsClient;
import uk.gov.laa.ccms.soa.gateway.mapper.InvoiceMapper;
import uk.gov.laa.ccms.soa.gateway.model.InvoiceDetail;
import uk.gov.laa.ccms.soa.gateway.model.OpaEntity;
import uk.gov.laa.ccms.soa.gateway.model.PaymentOnAccountDetail;
import uk.gov.legalservices.ccms.finance.payables._1_0.billingbim.GetInvoiceDetailsRS;
import uk.gov.legalservices.ccms.finance.payables._1_0.billingbim.InvoiceAddRQ;
import uk.gov.legalservices.ccms.finance.payables._1_0.billingbim.InvoiceAddRS;
import uk.gov.legalservices.ccms.finance.payables._1_0.billingbio.AssessmentResultsElementType;
import uk.gov.legalservices.ccms.finance.payables._1_0.billingbio.OPAEntityType;

@ExtendWith(MockitoExtension.class)
class InvoicesServiceTest {

  @Mock private CreateInvoiceClient createInvoiceClient;

  @Mock private GetInvoiceDetailsClient getInvoiceDetailsClient;

  @Mock private InvoiceMapper invoiceMapper;

  @InjectMocks private InvoicesService invoicesService;

  private final String userLoginId = "testUser";
  private final String userRole = "testRole";
  private static final String BILLING_ID = "1234567890";

  @Test
  @DisplayName("createInvoice - Successful Submission")
  void createInvoice_Success() {
    final InvoiceDetail invoiceDetail = new InvoiceDetail().poa(new PaymentOnAccountDetail());
    final InvoiceAddRQ.InvoiceDetails invoiceDetails = new InvoiceAddRQ.InvoiceDetails();
    final InvoiceAddRS mockResponse = new InvoiceAddRS();
    mockResponse.setInvoiceReferenceID("invoice123");

    when(invoiceMapper.toInvoiceDetails(any())).thenReturn(invoiceDetails);
    when(createInvoiceClient.createInvoice(any(), any(), any())).thenReturn(mockResponse);

    final String invoiceReferenceId =
        invoicesService.createInvoice(userLoginId, userRole, invoiceDetail);

    assertEquals("invoice123", invoiceReferenceId);

    verify(invoiceMapper).toInvoiceDetails(invoiceDetail);
    verify(createInvoiceClient).createInvoice(userLoginId, userRole, invoiceDetails);
  }

  @Test
  @DisplayName("getInvoiceData - Returns The Mapped Entities")
  void getInvoiceData_Success() {
    final AssessmentResultsElementType results = new AssessmentResultsElementType();
    final OPAEntityType entity = new OPAEntityType();
    entity.setEntityName("global");
    results.getEntity().add(entity);

    final GetInvoiceDetailsRS mockResponse = new GetInvoiceDetailsRS();
    mockResponse.setAssessmentResultsElement(results);

    final List<OpaEntity> mapped = List.of(new OpaEntity().entityName("global"));

    when(getInvoiceDetailsClient.getInvoiceData(any(), any(), any())).thenReturn(mockResponse);
    when(invoiceMapper.toOpaEntities(results.getEntity())).thenReturn(mapped);

    final List<OpaEntity> result =
        invoicesService.getInvoiceData(userLoginId, userRole, BILLING_ID);

    assertEquals(mapped, result);
    verify(getInvoiceDetailsClient).getInvoiceData(userLoginId, userRole, BILLING_ID);
  }

  @Test
  @DisplayName("getInvoiceData - Null When EBS Holds No Assessment Results")
  void getInvoiceData_NoResults() {
    // EBS makes the assessment results optional, and omits them for an unknown billing id.
    when(getInvoiceDetailsClient.getInvoiceData(any(), any(), any()))
        .thenReturn(new GetInvoiceDetailsRS());

    assertNull(invoicesService.getInvoiceData(userLoginId, userRole, BILLING_ID));

    verifyNoInteractions(invoiceMapper);
  }

  @Test
  @DisplayName("getInvoiceData - Exception Handling")
  void getInvoiceData_Exception() {
    when(getInvoiceDetailsClient.getInvoiceData(any(), any(), any()))
        .thenThrow(new RuntimeException("Test exception"));

    final RuntimeException exception =
        assertThrows(
            RuntimeException.class,
            () -> invoicesService.getInvoiceData(userLoginId, userRole, BILLING_ID));

    assertEquals("Test exception", exception.getMessage());
  }

  @Test
  @DisplayName("createInvoice - Exception Handling")
  void createInvoice_Exception() {
    final InvoiceDetail invoiceDetail = new InvoiceDetail().poa(new PaymentOnAccountDetail());

    when(invoiceMapper.toInvoiceDetails(any())).thenReturn(new InvoiceAddRQ.InvoiceDetails());
    when(createInvoiceClient.createInvoice(any(), any(), any()))
        .thenThrow(new RuntimeException("Test exception"));

    final RuntimeException exception =
        assertThrows(
            RuntimeException.class,
            () -> invoicesService.createInvoice(userLoginId, userRole, invoiceDetail));

    assertEquals("Test exception", exception.getMessage());

    verify(createInvoiceClient).createInvoice(any(), any(), any());
  }
}
