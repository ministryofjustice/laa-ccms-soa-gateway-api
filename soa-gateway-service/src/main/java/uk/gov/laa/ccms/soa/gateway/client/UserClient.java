package uk.gov.laa.ccms.soa.gateway.client;

import jakarta.xml.bind.JAXBElement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import uk.gov.gsi.legalaid.ccms.common.usermanagement._1_0.usermanagementbim.ObjectFactory;
import uk.gov.gsi.legalaid.ccms.common.usermanagement._1_0.usermanagementbim.UpdateUserRQ;
import uk.gov.gsi.legalaid.ccms.common.usermanagement._1_0.usermanagementbim.UpdateUserRS;
import uk.gov.gsi.legalaid.ccms.common.usermanagement._1_0.usermanagementbio.CCMSUser;

/**
 * Provides a client interface for interacting with User Services in the SOA-based system.
 *
 * <p>This client extends the foundational utilities provided by {@link AbstractSoaClient} and
 * specifically focuses on user services. It allows for the update of user profile options. Service
 * name and URL details are injected at runtime.</p>
 */
@Slf4j
@SuppressWarnings("unchecked")
@Component
public class UserClient extends AbstractSoaClient {

  private final String serviceName;

  private final String serviceUrl;

  private static final ObjectFactory USER_BIM_FACTORY = new ObjectFactory();

  /**
   * Constructs a new {@link UserClient} with the given service details.
   *
   * @param webServiceTemplate The web service template for SOAP communication.
   * @param serviceName        The name of the update user service.
   * @param serviceUrl         The URL endpoint for the update user service.
   */
  public UserClient(WebServiceTemplate webServiceTemplate,
      @Value("${laa.ccms.soa-gateway.user.service-name}") String serviceName,
      @Value("${laa.ccms.soa-gateway.user.service-url}") String serviceUrl) {
    this.webServiceTemplate = webServiceTemplate;
    this.serviceName = serviceName;
    this.serviceUrl = serviceUrl;
  }

  /**
   * Download a document cover sheet by ebs registered document id.
   *
   * @param loggedInUserId       - the logged in UserId
   * @param loggedInUserType     - the logged in UserType
   * @param user                 - the user details
   * @return Response object containing the cover sheet for the document.
   */
  public UpdateUserRS updateUser(final String loggedInUserId,
      final String loggedInUserType, final CCMSUser user) {

    final String soapAction = String.format("%s/UpdateUser", serviceName);
    UpdateUserRQ updateUserRq = USER_BIM_FACTORY.createUpdateUserRQ();
    updateUserRq.setHeaderRQ(createHeaderRq(loggedInUserId, loggedInUserType));
    updateUserRq.setCCMSUser(user);

    JAXBElement<UpdateUserRS> response =
        (JAXBElement<UpdateUserRS>) getWebServiceTemplate().marshalSendAndReceive(
            serviceUrl, USER_BIM_FACTORY.createUpdateUserRQ(updateUserRq),
            new SoapActionCallback(soapAction));

    // Check and throw exception if the SOA call was not successful
    checkSoaCallSuccess(serviceName, response.getValue().getHeaderRS());

    return response.getValue();
  }

}
