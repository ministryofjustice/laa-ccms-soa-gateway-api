package uk.gov.laa.ccms.soa.gateway.mapper.context;

import lombok.Builder;
import lombok.Getter;
import uk.gov.laa.ccms.soa.gateway.model.ProviderRequestDetail;

/**
 * Context class for mapping provider requests.
 */
@Builder
@Getter
public class ProviderRequestMappingContext {

  /**
   * The login ID of the user.
   */
  final String userLoginId;

  /**
   * The role of the user.
   */
  final String userRole;

  /**
   * The details of the provider request.
   */
  final ProviderRequestDetail providerRequestDetail;
}
