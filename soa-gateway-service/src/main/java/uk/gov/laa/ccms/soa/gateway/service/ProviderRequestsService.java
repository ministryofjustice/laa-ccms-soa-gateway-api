package uk.gov.laa.ccms.soa.gateway.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.laa.ccms.soa.gateway.client.ProviderRequestClient;
import uk.gov.laa.ccms.soa.gateway.mapper.ProviderRequestsMapper;
import uk.gov.laa.ccms.soa.gateway.mapper.context.ProviderRequestMappingContext;
import uk.gov.laa.ccms.soa.gateway.model.ProviderRequestDetail;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.ProviderRequestAddRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.ProviderRequestElementType;

/** Service for handling provider request operations. */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProviderRequestsService extends AbstractSoaService {

  private final ProviderRequestClient providerRequestClient;

  private final ProviderRequestsMapper providerRequestsMapper;

  /**
   * Submits a provider request and returns the notification ID.
   *
   * @param soaGatewayUserLoginId the login ID of the SOA gateway user
   * @param soaGatewayUserRole the role of the SOA gateway user
   * @param providerRequestDetail the details of the provider request
   * @return the notification ID of the submitted provider request
   */
  public String submitProviderRequest(
      final String soaGatewayUserLoginId,
      final String soaGatewayUserRole,
      final ProviderRequestDetail providerRequestDetail) {
    log.info("ProviderRequestsService - submitProviderRequest");

    final ProviderRequestMappingContext providerRequestMappingContext =
        ProviderRequestMappingContext.builder()
            .userLoginId(soaGatewayUserLoginId)
            .userRole(soaGatewayUserRole)
            .providerRequestDetail(providerRequestDetail)
            .build();

    final ProviderRequestElementType providerRequestElementType =
        providerRequestsMapper.toProviderRequestElementType(providerRequestMappingContext);

    final ProviderRequestAddRS response =
        providerRequestClient.submitProviderRequest(
            soaGatewayUserLoginId, soaGatewayUserRole, providerRequestElementType);

    return response.getNotificationID();
  }
}
