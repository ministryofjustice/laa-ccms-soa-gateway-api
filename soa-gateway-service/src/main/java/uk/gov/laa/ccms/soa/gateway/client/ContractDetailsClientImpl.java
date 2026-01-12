package uk.gov.laa.ccms.soa.gateway.client;

import jakarta.xml.bind.JAXBElement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabim.ContractDetailsInqRQ;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabim.ContractDetailsInqRS;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabim.ObjectFactory;

/**
 * Provides a client interface for interacting with Contract Details services in the SOA-based
 * system.
 *
 * <p>This client extends the foundational utilities provided by {@link AbstractSoaClient} and
 * specifically focuses on retrieving contract details based on certain search criteria.
 * Service name and URL details are injected at runtime.</p>
 */
@Slf4j
@SuppressWarnings("unchecked")
@Component
@Profile("!dev & !local")
public class ContractDetailsClientImpl extends AbstractSoaClient implements ContractDetailsClient  {
  private static final ObjectFactory CASE_FACTORY = new ObjectFactory();

  /**
   * Constructs a new {@link ContractDetailsClientImpl} with the given service details.
   *
   * @param webServiceTemplate The web service template for SOAP communication.
   * @param serviceName        The name of the contract details service.
   * @param serviceUrl         The URL endpoint for the contract details service.
   */
  public ContractDetailsClientImpl(
          final WebServiceTemplate webServiceTemplate,
          @Value("${laa.ccms.soa-gateway.contract-details.service-name}")final  String serviceName,
          @Value("${laa.ccms.soa-gateway.contract-details.service-url}")final  String serviceUrl) {
    this.webServiceTemplate = webServiceTemplate;
    this.serviceName = serviceName;
    this.serviceUrl = serviceUrl;
  }

  /**
   * Retrieve contract details based on the provided search criteria.
   *
   * @param searchFirmId       The firm ID for the search.
   * @param searchOfficeId     The office ID for the search.
   * @param loggedInUserId     The logged in user ID.
   * @param loggedInUserType   The type of the logged in user.
   * @param maxRecords         Maximum number of records to fetch.
   * @return ContractDetailsInqRS  Response containing contract details.
   */
  public ContractDetailsInqRS getContractDetails(
          final String searchFirmId,
          final String searchOfficeId,
          final String loggedInUserId,
          final String loggedInUserType,
          final Integer maxRecords) {

    final String soapAction = String.format("%s/process", serviceName);
    ContractDetailsInqRQ contractDetailsInqRq = CASE_FACTORY.createContractDetailsInqRQ();
    contractDetailsInqRq.setHeaderRQ(createHeaderRq(loggedInUserId, loggedInUserType));

    ContractDetailsInqRQ.SearchCriteria searchCriteria = CASE_FACTORY
            .createContractDetailsInqRQSearchCriteria();
    searchCriteria.setFirmID(searchFirmId);
    searchCriteria.setOfficeID(searchOfficeId);
    contractDetailsInqRq.setSearchCriteria(searchCriteria);
    contractDetailsInqRq.setRecordCount(createRecordCount(maxRecords));

    JAXBElement<ContractDetailsInqRS> response =
            (JAXBElement<ContractDetailsInqRS>) getWebServiceTemplate()
                    .marshalSendAndReceive(
                            serviceUrl,
                            CASE_FACTORY.createContractDetailsRQ(contractDetailsInqRq),
                            new SoapActionCallback(soapAction));

    // Check and throw exception if the SOA call was not successful
    isSuccessOrThrowException(serviceName, response.getValue().getHeaderRS());

    return response.getValue();
  }


}
