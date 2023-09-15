package uk.gov.laa.ccms.soa.gateway.client;

import jakarta.xml.bind.JAXBElement;
import java.util.Optional;
import javax.xml.datatype.XMLGregorianCalendar;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.NotificationCntInqRQ;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.NotificationCntInqRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.NotificationInqRQ;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.NotificationInqRQ.SearchCriteria;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.NotificationInqRS;
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




  /**
   * Retrieves the notification list based on the provided search criteria.
   *
   * @param soaGatewayUserLoginId The soa-gateway logged-in user Id.
   * @param soaGatewayUserRole    The type of user logged-in
   * @param caseReferenceNumber   The case reference number (optional).
   * @param providerCaseReference The provider case reference number (optional).
   * @param assignedToUserId      The user to whom the notifications are assigned (mandatory).
   * @param clientSurname         The surname of the Client (optional).
   * @param feeEarnerId           The Fee Earner ID (optional).
   * @param includeClosed         Include closed notifications (optional) default: false.
   * @param notificationType      The notification type to retrieve: Values "A" | "N"
   * @param dateFrom              The from date for the search (optional).
   * @param dateTo                The to date for the search (optional).
   * @param maxRecords            Maximum results for the search operation.
   * @return NotificationInqRS    Response containing the list of notifications.
   */
  public NotificationInqRS getNotifications(
      final String soaGatewayUserLoginId,
      final String soaGatewayUserRole,
      final String caseReferenceNumber,
      final String providerCaseReference,
      final String assignedToUserId,
      final String clientSurname,
      final Integer feeEarnerId,
      final Boolean includeClosed,
      final String notificationType,
      final XMLGregorianCalendar dateFrom,
      final XMLGregorianCalendar dateTo,
      final Integer maxRecords) {

    SearchCriteria searchCriteria = buildSearchCriteria(assignedToUserId,
        dateFrom,
        dateTo,
        caseReferenceNumber,
        clientSurname,
        feeEarnerId,
        providerCaseReference,
        notificationType,
        includeClosed);
    final String soapAction = String.format("%s/GetNotifications", serviceName);
    NotificationInqRQ inquiryRequest = CASE_FACTORY.createNotificationInqRQ();
    inquiryRequest.setHeaderRQ(createHeaderRq(soaGatewayUserLoginId, soaGatewayUserRole));
    inquiryRequest.setRecordCount(createRecordCount(maxRecords));
    inquiryRequest.setSearchCriteria(searchCriteria);

    JAXBElement<NotificationInqRS> response =
            (JAXBElement<NotificationInqRS>) getWebServiceTemplate()
            .marshalSendAndReceive(
                    serviceUrl,
                    CASE_FACTORY.createNotificationInqRQ(inquiryRequest),
                    new SoapActionCallback(soapAction));

    checkSoaCallSuccess(serviceName, response.getValue().getHeaderRS());
    return response.getValue();
  }

  private SearchCriteria buildSearchCriteria(
      String assignedToUserId,
      XMLGregorianCalendar dateFrom,
      XMLGregorianCalendar dateTo,
      String caseReferenceNumber,
      String clientSurname,
      Integer feeEarnerId,
      String providerCaseReference,
      String notificationType,
      Boolean includeClosed) {
    SearchCriteria criteria = CASE_FACTORY.createNotificationInqRQSearchCriteria();
    criteria.setAssignedToUserID(assignedToUserId);
    criteria.setDateFrom(dateFrom);
    criteria.setDateTo(dateTo);
    criteria.setCaseReferenceNumber(caseReferenceNumber);
    criteria.setClientSurName(clientSurname);
    criteria.setFeeEarnerID(Optional.ofNullable(feeEarnerId).map(String::valueOf).orElse(null));
    criteria.setProviderCaseReferenceNumber(providerCaseReference);
    criteria.setNotitificationType(notificationType);
    criteria.setIncludeClosedNotification(includeClosed);
    return criteria;
  }
}
