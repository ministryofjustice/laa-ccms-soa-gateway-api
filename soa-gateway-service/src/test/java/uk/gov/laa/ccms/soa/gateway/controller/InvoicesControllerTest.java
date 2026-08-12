package uk.gov.laa.ccms.soa.gateway.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.laa.ccms.soa.gateway.model.BillDetail;
import uk.gov.laa.ccms.soa.gateway.model.InvoiceDetail;
import uk.gov.laa.ccms.soa.gateway.model.OpaAttribute;
import uk.gov.laa.ccms.soa.gateway.model.OpaEntity;
import uk.gov.laa.ccms.soa.gateway.model.OpaInstance;
import uk.gov.laa.ccms.soa.gateway.model.PaymentOnAccountDetail;
import uk.gov.laa.ccms.soa.gateway.service.InvoicesService;

@ExtendWith(MockitoExtension.class)
class InvoicesControllerTest {

  private MockMvc mockMvc;

  @Mock private InvoicesService invoicesService;

  @InjectMocks private InvoicesController invoicesController;

  private final ObjectMapper objectMapper = new ObjectMapper();

  private static final String SOA_GATEWAY_USER_LOGIN_ID = "user";
  private static final String SOA_GATEWAY_USER_ROLE = "EXTERNAL";
  private static final String BILLING_ID = "1234567890";

  @BeforeEach
  public void setup() {
    this.mockMvc =
        MockMvcBuilders.standaloneSetup(invoicesController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();
  }

  @Test
  @DisplayName("POST /invoices - Successful POA Submission")
  void testCreateInvoice_Poa_Success() throws Exception {
    final InvoiceDetail invoiceDetail =
        new InvoiceDetail().poa(new PaymentOnAccountDetail().caseReferenceNumber("300000123456"));

    Mockito.when(
            invoicesService.createInvoice(
                eq(SOA_GATEWAY_USER_LOGIN_ID), eq(SOA_GATEWAY_USER_ROLE), eq(invoiceDetail)))
        .thenReturn("invoice123");

    mockMvc
        .perform(
            post("/invoices")
                .content(objectMapper.writeValueAsString(invoiceDetail))
                .contentType(MediaType.APPLICATION_JSON)
                .header("SoaGateway-User-Login-Id", SOA_GATEWAY_USER_LOGIN_ID)
                .header("SoaGateway-User-Role", SOA_GATEWAY_USER_ROLE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.invoice_reference_id").value("invoice123"));

    Mockito.verify(invoicesService)
        .createInvoice(eq(SOA_GATEWAY_USER_LOGIN_ID), eq(SOA_GATEWAY_USER_ROLE), eq(invoiceDetail));
  }

  @Test
  @DisplayName("POST /invoices - Successful Bill Submission")
  void testCreateInvoice_Bill_Success() throws Exception {
    final InvoiceDetail invoiceDetail =
        new InvoiceDetail().bill(new BillDetail().caseReferenceNumber("300000123456"));

    Mockito.when(invoicesService.createInvoice(any(), any(), any())).thenReturn("invoice123");

    mockMvc
        .perform(
            post("/invoices")
                .content(objectMapper.writeValueAsString(invoiceDetail))
                .contentType(MediaType.APPLICATION_JSON)
                .header("SoaGateway-User-Login-Id", SOA_GATEWAY_USER_LOGIN_ID)
                .header("SoaGateway-User-Role", SOA_GATEWAY_USER_ROLE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.invoice_reference_id").value("invoice123"));

    Mockito.verify(invoicesService).createInvoice(any(), any(), any());
  }

  @Test
  @DisplayName("POST /invoices - Bad Request When The Body Is A JSON Null")
  void testCreateInvoice_NullBody() throws Exception {
    mockMvc
        .perform(
            post("/invoices")
                .content("null")
                .contentType(MediaType.APPLICATION_JSON)
                .header("SoaGateway-User-Login-Id", SOA_GATEWAY_USER_LOGIN_ID)
                .header("SoaGateway-User-Role", SOA_GATEWAY_USER_ROLE))
        .andExpect(status().isBadRequest());

    Mockito.verifyNoInteractions(invoicesService);
  }

  @Test
  @DisplayName("POST /invoices - Bad Request When The Body Is Absent")
  void testCreateInvoice_NoBody() throws Exception {
    mockMvc
        .perform(
            post("/invoices")
                .contentType(MediaType.APPLICATION_JSON)
                .header("SoaGateway-User-Login-Id", SOA_GATEWAY_USER_LOGIN_ID)
                .header("SoaGateway-User-Role", SOA_GATEWAY_USER_ROLE))
        .andExpect(status().isBadRequest());

    Mockito.verifyNoInteractions(invoicesService);
  }

  @Test
  @DisplayName("POST /invoices - Bad Request When Neither Bill Nor POA Supplied")
  void testCreateInvoice_NeitherSupplied() throws Exception {
    mockMvc
        .perform(
            post("/invoices")
                .content(objectMapper.writeValueAsString(new InvoiceDetail()))
                .contentType(MediaType.APPLICATION_JSON)
                .header("SoaGateway-User-Login-Id", SOA_GATEWAY_USER_LOGIN_ID)
                .header("SoaGateway-User-Role", SOA_GATEWAY_USER_ROLE))
        .andExpect(status().isBadRequest());

    Mockito.verifyNoInteractions(invoicesService);
  }

  @Test
  @DisplayName("POST /invoices - Bad Request When Both Bill And POA Supplied")
  void testCreateInvoice_BothSupplied() throws Exception {
    final InvoiceDetail invoiceDetail =
        new InvoiceDetail().bill(new BillDetail()).poa(new PaymentOnAccountDetail());

    mockMvc
        .perform(
            post("/invoices")
                .content(objectMapper.writeValueAsString(invoiceDetail))
                .contentType(MediaType.APPLICATION_JSON)
                .header("SoaGateway-User-Login-Id", SOA_GATEWAY_USER_LOGIN_ID)
                .header("SoaGateway-User-Role", SOA_GATEWAY_USER_ROLE))
        .andExpect(status().isBadRequest());

    Mockito.verifyNoInteractions(invoicesService);
  }

  @Test
  @DisplayName("GET /invoices/{billing-id} - Successful Retrieval")
  void testGetInvoiceData_Success() throws Exception {
    Mockito.when(
            invoicesService.getInvoiceData(
                eq(SOA_GATEWAY_USER_LOGIN_ID), eq(SOA_GATEWAY_USER_ROLE), eq(BILLING_ID)))
        .thenReturn(
            List.of(
                new OpaEntity()
                    .entityName("global")
                    .addInstancesItem(
                        new OpaInstance()
                            .instanceLabel("global")
                            .addAttributesItem(
                                new OpaAttribute()
                                    .attribute("BILLING_IS_COMPLETE")
                                    .responseValue("true")
                                    .responseType("boolean")))));

    mockMvc
        .perform(
            get("/invoices/{billing-id}", BILLING_ID)
                .header("SoaGateway-User-Login-Id", SOA_GATEWAY_USER_LOGIN_ID)
                .header("SoaGateway-User-Role", SOA_GATEWAY_USER_ROLE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.opa_response[0].entity_name").value("global"))
        .andExpect(jsonPath("$.opa_response[0].instances[0].instance_label").value("global"))
        .andExpect(
            jsonPath("$.opa_response[0].instances[0].attributes[0].attribute")
                .value("BILLING_IS_COMPLETE"))
        .andExpect(
            jsonPath("$.opa_response[0].instances[0].attributes[0].response_value").value("true"));
  }

  @Test
  @DisplayName("GET /invoices/{billing-id} - Not Found For An Unknown Billing Id")
  void testGetInvoiceData_NotFound() throws Exception {
    Mockito.when(invoicesService.getInvoiceData(any(), any(), any())).thenReturn(null);

    mockMvc
        .perform(
            get("/invoices/{billing-id}", BILLING_ID)
                .header("SoaGateway-User-Login-Id", SOA_GATEWAY_USER_LOGIN_ID)
                .header("SoaGateway-User-Role", SOA_GATEWAY_USER_ROLE))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("GET /invoices/{billing-id} - Exception Handling")
  void testGetInvoiceData_Exception() throws Exception {
    Mockito.when(invoicesService.getInvoiceData(any(), any(), any()))
        .thenThrow(new RuntimeException("Test exception"));

    mockMvc
        .perform(
            get("/invoices/{billing-id}", BILLING_ID)
                .header("SoaGateway-User-Login-Id", SOA_GATEWAY_USER_LOGIN_ID)
                .header("SoaGateway-User-Role", SOA_GATEWAY_USER_ROLE))
        .andExpect(status().isInternalServerError());
  }

  @Test
  @DisplayName("POST /invoices - Exception Handling")
  void testCreateInvoice_Exception() throws Exception {
    final InvoiceDetail invoiceDetail = new InvoiceDetail().poa(new PaymentOnAccountDetail());

    Mockito.when(invoicesService.createInvoice(any(), any(), any()))
        .thenThrow(new RuntimeException("Test exception"));

    mockMvc
        .perform(
            post("/invoices")
                .content(objectMapper.writeValueAsString(invoiceDetail))
                .contentType(MediaType.APPLICATION_JSON)
                .header("SoaGateway-User-Login-Id", SOA_GATEWAY_USER_LOGIN_ID)
                .header("SoaGateway-User-Role", SOA_GATEWAY_USER_ROLE))
        .andExpect(status().isInternalServerError());

    Mockito.verify(invoicesService).createInvoice(any(), any(), any());
  }
}
