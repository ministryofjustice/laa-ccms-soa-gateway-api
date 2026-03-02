package uk.gov.laa.ccms.soa.gateway.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.xml.bind.JAXBElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import uk.gov.gsi.legalaid.ccms.common.usermanagement._1_0.usermanagementbim.ObjectFactory;
import uk.gov.gsi.legalaid.ccms.common.usermanagement._1_0.usermanagementbim.UpdateUserRQ;
import uk.gov.gsi.legalaid.ccms.common.usermanagement._1_0.usermanagementbim.UpdateUserRS;
import uk.gov.gsi.legalaid.ccms.common.usermanagement._1_0.usermanagementbio.CCMSUser;
import uk.gov.legalservices.enterprise.common._1_0.header.HeaderRSType;

@ExtendWith(MockitoExtension.class)
public class UserClientTest {

  public static final String SERVICE_NAME = "myService";
  public static final String SERVICE_URL = "myUrl";
  private static final String SOA_GATEWAY_USER_LOGIN_ID = "user";
  private static final String SOA_GATEWAY_USER_ROLE = "EXTERNAL";

  @Mock Logger mockLogger;

  @Mock WebServiceTemplate webServiceTemplate;

  @Captor ArgumentCaptor<JAXBElement<UpdateUserRQ>> requestCaptor;

  private UserClient client;

  @BeforeEach
  void setup() {
    this.client = new UserClient(webServiceTemplate, SERVICE_NAME, SERVICE_URL);
  }

  @Test
  public void testUpdateUser() {
    ObjectFactory objectFactory = new ObjectFactory();

    HeaderRSType headerRSType = new HeaderRSType();
    headerRSType.setTransactionID("12345");

    UpdateUserRS updateUserRS = new UpdateUserRS();
    updateUserRS.setHeaderRS(headerRSType);

    // Mock the response of the WebServiceTemplate
    when(webServiceTemplate.marshalSendAndReceive(
            eq(SERVICE_URL), any(JAXBElement.class), any(SoapActionCallback.class)))
        .thenReturn(objectFactory.createUpdateUserRS(updateUserRS));

    CCMSUser ccmsUser = new CCMSUser();

    UpdateUserRS actual =
        client.updateUser(SOA_GATEWAY_USER_LOGIN_ID, SOA_GATEWAY_USER_ROLE, ccmsUser);

    // Verify interactions
    verify(webServiceTemplate, times(1))
        .marshalSendAndReceive(
            eq(SERVICE_URL), requestCaptor.capture(), any(SoapActionCallback.class));

    JAXBElement<UpdateUserRQ> payload = requestCaptor.getValue();
    assertNotNull(payload.getValue().getHeaderRQ().getTimeStamp());
    assertEquals(SOA_GATEWAY_USER_LOGIN_ID, payload.getValue().getHeaderRQ().getUserLoginID());
    assertEquals(SOA_GATEWAY_USER_ROLE, payload.getValue().getHeaderRQ().getUserRole());
    assertNotNull(actual);
    assertEquals("12345", actual.getHeaderRS().getTransactionID());
  }
}
