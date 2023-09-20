package uk.gov.laa.ccms.soa.gateway.client;

import jakarta.xml.bind.JAXBElement;
import java.util.Optional;
import javax.xml.datatype.XMLGregorianCalendar;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import uk.gov.laa.ccms.soa.gateway.model.Notification;
import uk.gov.laa.ccms.soa.gateway.model.NotificationDetail;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.NotificationCntInqRQ;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.NotificationCntInqRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.NotificationInqRQ;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.NotificationInqRQ.SearchCriteria;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.NotificationInqRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.ObjectFactory;

/**
 * Provides a client interface for interacting with Notification services in the SOA-based system.
 *
 * <p>This client extends the foundational utilities provided by {@link AbstractSoaClient} and
 * specifically focuses on retrieving notification counts based on certain search criteria. Service
 * name and URL details are injected at runtime.</p>
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
   * @param searchLoginId    ID associated with the login/search operation.
   * @param loggedInUserId   The logged-in user's ID.
   * @param loggedInUserType Type of user that is logged in.
   * @param maxSearchResults Maximum results for the search operation.
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
   * @param notification  The {#{@link Notification}} builder for the search criteria.
   * @param includeClosed Include closed notifications (optional) default: false.
   * @param dateFrom      The  date from which to search (optional).
   * @param dateTo        The to date for the search (optional).
   * @param maxRecords    Maximum results for the search operation.
   * @return NotificationInqRS    Response containing the list of notifications.
   */
  public NotificationInqRS getNotifications(
      final Notification notification,
      final Boolean includeClosed,
      final XMLGregorianCalendar dateFrom,
      final XMLGregorianCalendar dateTo,
      final Integer maxRecords) {

    SearchCriteria searchCriteria = buildSearchCriteria(
        notification,
        dateFrom,
        dateTo,
        includeClosed);
    final String soapAction = String.format("%s/GetNotifications", serviceName);
    NotificationInqRQ inquiryRequest = CASE_FACTORY.createNotificationInqRQ();
    inquiryRequest.setHeaderRQ(createHeaderRq(notification.getUser().getUserLoginId(),
        notification.getUser().getUserType()));
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
      final Notification notification,
      final XMLGregorianCalendar dateFrom,
      final XMLGregorianCalendar dateTo,
      final Boolean includeClosed) {
    SearchCriteria criteria = CASE_FACTORY.createNotificationInqRQSearchCriteria();
    criteria.setAssignedToUserID(notification.getUser().getUserName());
    criteria.setDateFrom(dateFrom);
    criteria.setDateTo(dateTo);
    criteria.setCaseReferenceNumber(notification.getCaseReferenceNumber());
    criteria.setClientSurName(notification.getClientName());
    criteria.setFeeEarnerID(
        Optional.ofNullable(notification.getFeeEarner()).map(String::valueOf).orElse(""));
    criteria.setProviderCaseReferenceNumber(notification.getProviderCaseReferenceNumber());
    criteria.setNotitificationType(
        Optional.ofNullable(notification.getNotificationDetail()).map(
            NotificationDetail::getNotificationType).map(String::valueOf).orElse(null)
    );
    criteria.setIncludeClosedNotification(includeClosed);
    return criteria;
  }
}
