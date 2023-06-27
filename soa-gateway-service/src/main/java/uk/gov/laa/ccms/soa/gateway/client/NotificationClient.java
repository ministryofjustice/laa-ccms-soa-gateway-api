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

@Slf4j
@SuppressWarnings("unchecked")
@Component
public class NotificationClient extends AbstractSoaClient {

    private final String serviceName;

    private final String serviceUrl;

    private static final ObjectFactory CASE_FACTORY = new ObjectFactory();

    public NotificationClient(WebServiceTemplate webServiceTemplate,
        @Value("${laa.ccms.soa-gateway.notification.service-name}") String serviceName,
        @Value("${laa.ccms.soa-gateway.notification.service-url}") String serviceUrl) {
        this.webServiceTemplate = webServiceTemplate;
        this.serviceName = serviceName;
        this.serviceUrl = serviceUrl;
    }

    public NotificationCntInqRS getNotificationCount(
        String searchLoginId,
        String loggedInUserId,
        String loggedInUserType,
        Integer maxSearchResults) {

        final String soapAction = String.format("%s/GetNotificationCount", serviceName);
        NotificationCntInqRQ inquiryRequest = CASE_FACTORY.createNotificationCntInqRQ();
        inquiryRequest.setHeaderRQ(createHeaderRQ(loggedInUserId, loggedInUserType));
        inquiryRequest.setRecordCount(createRecordCount(maxSearchResults));

        NotificationCntInqRQ.SearchCriteria searchCriteria = CASE_FACTORY
            .createNotificationCntInqRQSearchCriteria();
        searchCriteria.setUserID(searchLoginId);
        inquiryRequest.setSearchCriteria(searchCriteria);

        JAXBElement<NotificationCntInqRS> response = (JAXBElement<NotificationCntInqRS>)getWebServiceTemplate()
                .marshalSendAndReceive(
                    serviceUrl,
                    CASE_FACTORY.createNotificationCntInqRQ(inquiryRequest),
                        new SoapActionCallback(soapAction));

        checkSoaCallSuccess(serviceName, response.getValue().getHeaderRS());

        return response.getValue();
    }


}
