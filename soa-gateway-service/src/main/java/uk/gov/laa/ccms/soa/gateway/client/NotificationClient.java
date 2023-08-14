package uk.gov.laa.ccms.soa.gateway.client;

import jakarta.xml.bind.JAXBElement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.NotificationCntInqRQ;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.NotificationCntInqRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.ObjectFactory;

/**
 * Provides a client interface for interacting with Notification services in the SOA-based
 * system.
 *
 * <p>This client extends the foundational utilities provided by {@link AbstractSoaClient} and
 * specifically focuses on retrieving notification counts based on certain search criteria.
 * Service name and URL details are injected at runtime.</p>
 */
@Slf4j
@SuppressWarnings("unchecked")
@Component
public class NotificationClient extends AbstractSoaClient {
  private static final ObjectFactory CASE_FACTORY = new ObjectFactory();

  /**
   * Constructs a new {@link NotificationClient} with the specified service details.
   *
   * @param webServiceTemplate The web service template for SOAP communication.
   * @param serviceName        The name of the notification service.
   * @param serviceUrl         The URL endpoint for the notification service.
   */
  public NotificationClient(
          final WebServiceTemplate webServiceTemplate,
          @Value("${laa.ccms.soa-gateway.notification.service-name}") final String serviceName,
          @Value("${laa.ccms.soa-gateway.notification.service-url}") final String serviceUrl) {
    this.webServiceTemplate = webServiceTemplate;
    this.serviceName = serviceName;
    this.serviceUrl = serviceUrl;
  }

  /**
   * Retrieves the notification count based on provided search criteria.
   *
   * @param searchLoginId       ID associated with the login/search operation.
   * @param loggedInUserId     The logged in user's ID.
   * @param loggedInUserType   Type of user that is logged in.
   * @param maxSearchResults   Maximum results for the search operation.
   * @return NotificationCntInqRS  Response containing the notification count details.
   */
  public NotificationCntInqRS getNotificationCount(
          final String searchLoginId,
          final String loggedInUserId,
          final String loggedInUserType,
          final Integer maxSearchResults) {

    final String soapAction = String.format("%s/GetNotificationCount", serviceName);
    NotificationCntInqRQ inquiryRequest = CASE_FACTORY.createNotificationCntInqRQ();
    inquiryRequest.setHeaderRQ(createHeaderRq(loggedInUserId, loggedInUserType));
    inquiryRequest.setRecordCount(createRecordCount(maxSearchResults));

    NotificationCntInqRQ.SearchCriteria searchCriteria = CASE_FACTORY
            .createNotificationCntInqRQSearchCriteria();
    searchCriteria.setUserID(searchLoginId);
    inquiryRequest.setSearchCriteria(searchCriteria);

    JAXBElement<NotificationCntInqRS> response =
            (JAXBElement<NotificationCntInqRS>) getWebServiceTemplate()
            .marshalSendAndReceive(
                    serviceUrl,
                    CASE_FACTORY.createNotificationCntInqRQ(inquiryRequest),
                    new SoapActionCallback(soapAction));

    checkSoaCallSuccess(serviceName, response.getValue().getHeaderRS());

    return response.getValue();
  }


}
