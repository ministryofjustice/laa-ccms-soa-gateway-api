package uk.gov.laa.ccms.soa.gateway.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.laa.ccms.soa.gateway.client.CreateInvoiceClient;
import uk.gov.laa.ccms.soa.gateway.mapper.InvoiceMapper;
import uk.gov.laa.ccms.soa.gateway.model.InvoiceDetail;
import uk.gov.laa.ccms.soa.gateway.model.PaymentOnAccountDetail;
import uk.gov.legalservices.ccms.finance.payables._1_0.billingbim.InvoiceAddRQ;
import uk.gov.legalservices.ccms.finance.payables._1_0.billingbim.InvoiceAddRS;

@ExtendWith(MockitoExtension.class)
class InvoicesServiceTest {

  @Mock private CreateInvoiceClient createInvoiceClient;

  @Mock private InvoiceMapper invoiceMapper;

  @InjectMocks private InvoicesService invoicesService;

  private final String userLoginId = "testUser";
  private final String userRole = "testRole";

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
