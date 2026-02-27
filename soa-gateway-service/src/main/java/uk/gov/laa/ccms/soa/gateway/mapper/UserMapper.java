package uk.gov.laa.ccms.soa.gateway.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.gsi.legalaid.ccms.common.usermanagement._1_0.usermanagementbio.CCMSUser;
import uk.gov.laa.ccms.soa.gateway.model.UserOptions;

/**
 * Mapper for transforming data related to users.
 *
 * <p>Uses the MapStruct framework to facilitate the conversion between the CAAB user data models
 * and the internal SoA gateway's {@link CCMSUser} model.
 */
@Mapper(componentModel = "spring", uses = CommonMapper.class)
public interface UserMapper {

  /**
   * Converts a {@link UserOptions} to a {@link CCMSUser}.
   *
   * @param userOptions The user selected profile options.
   * @return The transformed {@link CCMSUser}.
   */
  @Mapping(target = "providerFirmID", source = "providerFirmId")
  @Mapping(target = "loginID", source = "userLoginId")
  CCMSUser toCcmsUser(UserOptions userOptions);
}
