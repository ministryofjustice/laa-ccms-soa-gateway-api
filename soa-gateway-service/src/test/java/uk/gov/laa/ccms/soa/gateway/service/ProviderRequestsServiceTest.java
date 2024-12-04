package uk.gov.laa.ccms.soa.gateway.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.laa.ccms.soa.gateway.client.ProviderRequestClient;
import uk.gov.laa.ccms.soa.gateway.mapper.ProviderRequestsMapper;
import uk.gov.laa.ccms.soa.gateway.model.ProviderRequestDetail;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.ProviderRequestAddRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.ProviderRequestElementType;

@ExtendWith(MockitoExtension.class)
class ProviderRequestsServiceTest {

  @Mock
  private ProviderRequestClient providerRequestClient;

  @Mock
  private ProviderRequestsMapper providerRequestsMapper;

  @InjectMocks
  private ProviderRequestsService providerRequestsService;

  @Test
  @DisplayName("submitProviderRequest - Successful Submission")
  void submitProviderRequest_Success() {
    final String userLoginId = "testUser";
    final String userRole = "testRole";
    final ProviderRequestDetail providerRequestDetail = new ProviderRequestDetail();
    final ProviderRequestElementType elementType = new ProviderRequestElementType();
    final ProviderRequestAddRS mockResponse = new ProviderRequestAddRS();
    mockResponse.setNotificationID("notif123");

    when(providerRequestsMapper.toProviderRequestElementType(any())).thenReturn(elementType);
    when(providerRequestClient.submitProviderRequest(any(), any(), any())).thenReturn(mockResponse);

    final String notificationId = providerRequestsService.submitProviderRequest(
        userLoginId, userRole, providerRequestDetail);

    assertEquals("notif123", notificationId);

    verify(providerRequestClient).submitProviderRequest(userLoginId, userRole, elementType);
  }

  @Test
  @DisplayName("submitProviderRequest - Exception Handling")
  void submitProviderRequest_Exception() {
    final String userLoginId = "testUser";
    final String userRole = "testRole";
    final ProviderRequestDetail providerRequestDetail = new ProviderRequestDetail();

    when(providerRequestsMapper.toProviderRequestElementType(any())).thenReturn(new ProviderRequestElementType());
    when(providerRequestClient.submitProviderRequest(any(), any(), any()))
        .thenThrow(new RuntimeException("Test exception"));

    final RuntimeException exception = org.junit.jupiter.api.Assertions.assertThrows(
        RuntimeException.class,
        () -> providerRequestsService.submitProviderRequest(userLoginId, userRole, providerRequestDetail)
    );

    assertEquals("Test exception", exception.getMessage());

    verify(providerRequestClient).submitProviderRequest(any(), any(), any());
  }

}