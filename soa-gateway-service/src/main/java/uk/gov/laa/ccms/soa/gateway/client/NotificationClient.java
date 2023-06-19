package uk.gov.laa.ccms.soa.gateway.client;

import jakarta.xml.bind.JAXBElement;
import java.math.BigInteger;
import java.time.LocalDate;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import uk.gov.laa.ccms.soa.gateway.notificationservice.HeaderRQType;
import uk.gov.laa.ccms.soa.gateway.notificationservice.NotificationCntInqRQ;
import uk.gov.laa.ccms.soa.gateway.notificationservice.NotificationCntInqRS;
import uk.gov.laa.ccms.soa.gateway.notificationservice.ObjectFactory;
import uk.gov.laa.ccms.soa.gateway.notificationservice.RecordCount;
import uk.gov.laa.ccms.soa.gateway.notificationservice.SystemsList;

@Slf4j
public class NotificationClient extends AbstractSoaClient {

    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    public NotificationCntInqRS getNotificationCount(
        String loggedInUserId,
        String loggedInUserType,
        String searchLoginId,
        BigInteger maxSearchResults) {

        NotificationCntInqRQ inquiryRequest = OBJECT_FACTORY.createNotificationCntInqRQ();
        inquiryRequest.setHeaderRQ(createHeaderRQ(loggedInUserId, loggedInUserType));

        RecordCount recordCount = OBJECT_FACTORY.createRecordCount();
        recordCount.setMaxRecordsToFetch(maxSearchResults);
        recordCount.setRetriveDataOnMaxCount(Boolean.FALSE);
        inquiryRequest.setRecordCount(recordCount);

        NotificationCntInqRQ.SearchCriteria searchCriteria = OBJECT_FACTORY
            .createNotificationCntInqRQSearchCriteria();
        searchCriteria.setUserID(searchLoginId);
        inquiryRequest.setSearchCriteria(searchCriteria);

        JAXBElement<NotificationCntInqRS> response = (JAXBElement<NotificationCntInqRS>)getWebServiceTemplate()
                .marshalSendAndReceive(OBJECT_FACTORY.createNotificationCntInqRQ(inquiryRequest),
                        new SoapActionCallback("http://legalservices.gov.uk/CCMS/CaseManagement/Case/1.0/NotificationService/GetNotificationCount"));

        return response.getValue();
    }

    /**
     * @param loggedInUserId The logged in user id
     * @param loggedInUserType The type of the logged in user
     * @return HeaderRQType
     */
    private HeaderRQType createHeaderRQ(final String loggedInUserId, final String loggedInUserType) {
        HeaderRQType headerRQType = OBJECT_FACTORY.createHeaderRQType();

//        if (userInfo.getLocale().getLanguage().equalsIgnoreCase("en")) {
            headerRQType.setLanguage("ENG");
//        } else {
//            // must be welsh
//            headerRQType.setLanguage("CYM");
//        }
        headerRQType.setUserLoginID(loggedInUserId);
        headerRQType.setUserRole(loggedInUserType);
        headerRQType.setTransactionRequestID(generateTransactionId());
        headerRQType.setSource(SystemsList.PUI);
        headerRQType.setTarget(SystemsList.ORACLE_E_BUSINESS);

        try {
            headerRQType.setTimeStamp(DatatypeFactory.newInstance().newXMLGregorianCalendar(
                LocalDate.now().toString()));
        } catch (DatatypeConfigurationException e) {
            log.error("Failed to create DatatypeFactory", e);
            throw new RuntimeException(e);
        }

        return headerRQType;
    }
}
