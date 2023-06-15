package uk.gov.laa.ccms.soa.gateway.client;

import jakarta.xml.bind.JAXBElement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.NotificationCntInqRQ;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.NotificationCntInqRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.ObjectFactory;

@Slf4j
public class NotificationClient extends WebServiceGatewaySupport {


    public NotificationCntInqRS getNotificationCount() {

        NotificationCntInqRQ request = new NotificationCntInqRQ();

        ObjectFactory factory = new ObjectFactory();
        JAXBElement<NotificationCntInqRQ> jaxbRequest = factory.createNotificationCntInqRQ(request);

        NotificationCntInqRS response = (NotificationCntInqRS) getWebServiceTemplate()
                .marshalSendAndReceive(jaxbRequest,
                        new SoapActionCallback("http://legalservices.gov.uk/CCMS/CaseManagement/Case/1.0/NotificationService/GetNotificationCount"));

        return response;

    }
}
