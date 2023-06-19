package uk.gov.laa.ccms.soa.gateway.client;

import jakarta.xml.bind.JAXBElement;
import java.math.BigInteger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.NotificationCntInqRQ;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.NotificationCntInqRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.ObjectFactory;

@Slf4j
@SuppressWarnings("unchecked")
public class NotificationClient extends AbstractSoaClient {

    @Value("${laa.ccms.soa-gateway.notification.service-name}")
    private String serviceName;

    private static final ObjectFactory CASE_FACTORY = new ObjectFactory();

    public NotificationCntInqRS getNotificationCount(
        String loggedInUserId,
        String loggedInUserType,
        String searchLoginId,
        BigInteger maxSearchResults) {

        final String soapAction = String.format("%s/GetNotificationCount", serviceName);
        NotificationCntInqRQ inquiryRequest = CASE_FACTORY.createNotificationCntInqRQ();
        inquiryRequest.setHeaderRQ(createHeaderRQ(loggedInUserId, loggedInUserType));
        inquiryRequest.setRecordCount(createRecordCount(maxSearchResults));

        NotificationCntInqRQ.SearchCriteria searchCriteria = CASE_FACTORY
            .createNotificationCntInqRQSearchCriteria();
        searchCriteria.setUserID(searchLoginId);
        inquiryRequest.setSearchCriteria(searchCriteria);

        JAXBElement<NotificationCntInqRS> response = (JAXBElement<NotificationCntInqRS>)getWebServiceTemplate()
                .marshalSendAndReceive(CASE_FACTORY.createNotificationCntInqRQ(inquiryRequest),
                        new SoapActionCallback(soapAction));

        return response.getValue();
    }


}
