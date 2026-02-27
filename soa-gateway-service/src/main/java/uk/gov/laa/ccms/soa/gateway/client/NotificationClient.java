package uk.gov.laa.ccms.soa.gateway.client;

import jakarta.xml.bind.JAXBElement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import uk.gov.laa.ccms.soa.gateway.model.Notification;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.NotificationUpdateRQ;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.NotificationUpdateRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.ObjectFactory;

/**
 * Provides a client interface for interacting with Notification Management Services in the
 * SOA-based system.
 *
 * <p>This client extends the foundational utilities provided by {@link AbstractSoaClient} and
 * specifically focuses on notification management services. It provides facilities for updating
 * notifications with response details. Service name and URL details are injected at runtime.
 */
@Slf4j
@SuppressWarnings("unchecked")
@Component
public class NotificationClient extends AbstractSoaClient {

  private static final ObjectFactory CASE_FACTORY = new ObjectFactory();

  /**
   * Constructs a new {@link NotificationClient} with the given service details.
   *
   * @param webServiceTemplate The web service template for SOAP communication.
   * @param serviceName The name of the notification management service.
   * @param serviceUrl The URL endpoint for the notification management service.
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
   * Updates a notification with details of the response to the notification.
   *
   * @param loggedInUserId - the logged in UserId
   * @param loggedInUserType - the logged in UserType
   * @param notification - the notification details to update
   * @param notificationId - the id of the notification to update
   * @return a {@link NotificationUpdateRS} containing the notification update response details.
   */
  public NotificationUpdateRS updateNotification(
      final String loggedInUserId,
      final String loggedInUserType,
      final Notification notification,
      final String notificationId) {

    final String soapAction = String.format("%s/UpdateNotification", serviceName);

    NotificationUpdateRQ notificationUpdateRq = CASE_FACTORY.createNotificationUpdateRQ();
    notificationUpdateRq.setHeaderRQ(createHeaderRq(loggedInUserId, loggedInUserType));

    notificationUpdateRq.setNotificationID(notificationId);
    notificationUpdateRq.setAction(notification.getAction());
    notificationUpdateRq.setMessage(notification.getMessage());
    notificationUpdateRq.setUserID(notification.getUserId());

    JAXBElement<NotificationUpdateRS> response =
        (JAXBElement<NotificationUpdateRS>)
            getWebServiceTemplate()
                .marshalSendAndReceive(
                    serviceUrl,
                    CASE_FACTORY.createNotificationUpdateRQ(notificationUpdateRq),
                    new SoapActionCallback(soapAction));

    // Check and throw exception if the SOA call was not successful
    isSuccessOrThrowException(serviceName, response.getValue().getHeaderRS());

    return response.getValue();
  }
}
