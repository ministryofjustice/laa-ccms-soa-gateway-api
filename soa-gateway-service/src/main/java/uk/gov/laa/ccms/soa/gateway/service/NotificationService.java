package uk.gov.laa.ccms.soa.gateway.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.NotificationCntInqRQ;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.NotificationCntInqRS;

@Slf4j
public class NotificationService extends WebServiceGatewaySupport{

    public NotificationCntInqRS getNotificationCount(String country) {

        NotificationCntInqRQ request = new NotificationCntInqRQ();

        log.info("Requesting location for " + country);

        NotificationCntInqRS response = (NotificationCntInqRS) getWebServiceTemplate()
                .marshalSendAndReceive("http://legalservices.gov.uk/CCMS/CaseManagement/Case/1.0/NotificationService", request,
                        new SoapActionCallback(
                                "http://legalservices.gov.uk/CCMS/CaseManagement/Case/1.0/NotificationService/GetNotificationCount"));

        return response;

        //http://localhost:8051/soa-infra/services/default/NotificationServices/NotificationServices_ep
    }
   /** NotificationCntInqRS inquiryResponse = getNotificationServices()
            .getNotificationCount(inquiryRequest,
                    createSecurityHeader(userSettings));    /**
     *
     */
}


/**
 *     @WebMethod(operationName = "GetNotificationCount", action = "http://legalservices.gov.uk/CCMS/CaseManagement/Case/1.0/NotificationService/GetNotificationCount")
 *     @WebResult(name = "NotificationCntInqRS", targetNamespace = "http://legalservices.gov.uk/CCMS/CaseManagement/Case/1.0/CaseBIM", partName = "payload")
 *     public NotificationCntInqRS getNotificationCount(
 *         @WebParam(name = "NotificationCntInqRQ", targetNamespace = "http://legalservices.gov.uk/CCMS/CaseManagement/Case/1.0/CaseBIM", partName = "payload")
 *         NotificationCntInqRQ payload,
 *         @WebParam(name = "Security", targetNamespace = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", header = true, partName = "Security")
 *         SecurityHeaderType security);
 */


/**
 *   private SecurityHeaderType createSecurityHeader(final CcmsUserSettings userInfo) {
 *     SecurityHeaderType securityHeader = EBS_FACTORY.createSecurityHeaderType();
 *     UsernameTokenType userNameToken = EBS_FACTORY.createUsernameTokenType();
 *     PasswordString password = EBS_FACTORY.createPasswordString();
 *     password.setType(
 *         "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText");
 *     password.setValue(userInfo.getSoapHeaderPassword());
 *     userNameToken.setPassword(password);
 *     AttributedString username = EBS_FACTORY.createAttributedString();
 *     username.setValue(userInfo.getSoapHeaderUserName());
 *     userNameToken.setUsername(username);
 *     securityHeader.setUsernameToken(userNameToken);
 *     return securityHeader;
 *   }
 */