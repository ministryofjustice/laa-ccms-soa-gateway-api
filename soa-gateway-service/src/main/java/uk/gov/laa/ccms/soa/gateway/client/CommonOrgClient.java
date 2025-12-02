package uk.gov.laa.ccms.soa.gateway.client;

import jakarta.xml.bind.JAXBElement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabim.CommonOrgInqRQ;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabim.CommonOrgInqRQ.SearchCriteria.Organization;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabim.CommonOrgInqRS;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabim.ObjectFactory;

/**
 * Provides a client interface for interacting with CommonOrg Management Services in the SOA-based
 * system.
 *
 * <p>This client extends the foundational utilities provided by {@link AbstractSoaClient} and
 * specifically focuses on common organisation services.
 * It provides facilities for retrieving organisation details based on search criteria provided.
 * Service name and URL details are injected at runtime.</p>
 */
@Slf4j
@SuppressWarnings("unchecked")
@Component
public class CommonOrgClient extends AbstractSoaClient {

  private static final ObjectFactory COMMONORG_FACTORY = new ObjectFactory();

  /**
   * Constructs a new {@link CommonOrgClient} with the given service details.
   *
   * @param webServiceTemplate The web service template for SOAP communication.
   * @param serviceName        The name of the common org management service.
   * @param serviceUrl         The URL endpoint for the common org management service.
   */
  public CommonOrgClient(
          final WebServiceTemplate webServiceTemplate,
          @Value("${laa.ccms.soa-gateway.commonorg.service-name}") final String serviceName,
          @Value("${laa.ccms.soa-gateway.commonorg.service-url}") final String serviceUrl) {
    this.webServiceTemplate = webServiceTemplate;
    this.serviceName = serviceName;
    this.serviceUrl = serviceUrl;
  }

  /**
   * Retrieve organisation details based on the provided search criteria.
   *
   * @param loggedInUserId      The logged-in user ID.
   * @param loggedInUserType    The type of the logged-in user.
   * @param maxRecords          Maximum number of records to fetch.
   * @param searchOrganisation  The organisation information used for search.
   * @return CommonOrgInqRS     Response containing organisation details.
   */
  public CommonOrgInqRS getOrganisations(
          final String loggedInUserId,
          final String loggedInUserType,
          final Integer maxRecords,
          final Organization searchOrganisation
  ) {

    final String soapAction = String.format("%s/GetCommonOrg", serviceName);
    CommonOrgInqRQ commonOrgInqRq = COMMONORG_FACTORY.createCommonOrgInqRQ();
    commonOrgInqRq.setHeaderRQ(createHeaderRq(loggedInUserId, loggedInUserType));

    CommonOrgInqRQ.SearchCriteria searchCriteria = COMMONORG_FACTORY
            .createCommonOrgInqRQSearchCriteria();

    searchCriteria.setOrganization(searchOrganisation);

    return getCommonOrgInqRs(maxRecords, soapAction, commonOrgInqRq, searchCriteria);
  }

  /**
   * Retrieve the full details of an organisation based on its partyId.
   *
   * @param loggedInUserId      The logged-in user ID.
   * @param loggedInUserType    The type of the logged-in user.
   * @param maxRecords          Maximum number of records to fetch.
   * @param organisationId      The organisation party id.
   * @return CommonOrgInqRS     Response containing organisation details.
   */
  public CommonOrgInqRS getOrganisation(
      final String loggedInUserId,
      final String loggedInUserType,
      final Integer maxRecords,
      final String organisationId) {

    final String soapAction = String.format("%s/GetCommonOrg", serviceName);
    CommonOrgInqRQ commonOrgInqRq = COMMONORG_FACTORY.createCommonOrgInqRQ();
    commonOrgInqRq.setHeaderRQ(createHeaderRq(loggedInUserId, loggedInUserType));

    CommonOrgInqRQ.SearchCriteria searchCriteria = COMMONORG_FACTORY
        .createCommonOrgInqRQSearchCriteria();

    searchCriteria.setOrganizationPartyID(organisationId);

    return getCommonOrgInqRs(maxRecords, soapAction, commonOrgInqRq, searchCriteria);
  }

  private CommonOrgInqRS getCommonOrgInqRs(
          Integer maxRecords,
          String soapAction,
          CommonOrgInqRQ commonOrgInqRq,
          CommonOrgInqRQ.SearchCriteria searchCriteria) {
    commonOrgInqRq.setSearchCriteria(searchCriteria);
    commonOrgInqRq.setRecordCount(createRecordCount(maxRecords));

    JAXBElement<CommonOrgInqRS> response =
            (JAXBElement<CommonOrgInqRS>) getWebServiceTemplate()
                    .marshalSendAndReceive(
                            serviceUrl,
                            COMMONORG_FACTORY.createCommonOrgInqRQ(commonOrgInqRq),
                            new SoapActionCallback(soapAction));

    // Check and throw exception if the SOA call was not successful
    isSuccessOrThrowException(serviceName, response.getValue().getHeaderRS());

    return response.getValue();
  }
}
