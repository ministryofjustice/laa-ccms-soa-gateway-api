package uk.gov.laa.ccms.soa.gateway.client;

import jakarta.xml.bind.JAXBElement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.CaseInqRQ;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.CaseInqRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.CaseInfo;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.ObjectFactory;


@Slf4j
@SuppressWarnings("unchecked")
@Component
public class CaseServicesClient extends AbstractSoaClient{

    private final String serviceName;

    private final String serviceUrl;

    private static final ObjectFactory CASE_FACTORY = new ObjectFactory();

    public CaseServicesClient(WebServiceTemplate webServiceTemplate,
                                @Value("${laa.ccms.soa-gateway.case.service-name}") String serviceName,
                                @Value("${laa.ccms.soa-gateway.case.service-url}") String serviceUrl) {
        this.webServiceTemplate = webServiceTemplate;
        this.serviceName = serviceName;
        this.serviceUrl= serviceUrl;
    }

    /**
     * Retrieve a List of Cases based on the supplied CaseInfo search criteria
     *
     * @param loggedInUserId - the logged in UserId
     * @param loggedInUserType - the logged in UserType
     * @param maxRecords - the maximum records to be returned
     * @param caseInfo - the search criteria
     * @return List of Cases
     */
    public CaseInqRS getCaseDetails(
            String loggedInUserId,
            String loggedInUserType,
            Integer maxRecords,
            CaseInfo caseInfo
    ) {

        final String soapAction = String.format("%s/GetCaseDetails", serviceName);
        CaseInqRQ caseInqRQ = CASE_FACTORY.createCaseInqRQ();
        caseInqRQ.setHeaderRQ(createHeaderRQ(loggedInUserId, loggedInUserType));

        CaseInqRQ.SearchCriteria searchCriteria = CASE_FACTORY.createCaseInqRQSearchCriteria();
        searchCriteria.setCaseInfo(caseInfo);

        caseInqRQ.setSearchCriteria(searchCriteria);
        caseInqRQ.setRecordCount(createRecordCount(maxRecords));

        JAXBElement<CaseInqRS> response =
                (JAXBElement<CaseInqRS>) getWebServiceTemplate()
                        .marshalSendAndReceive(
                                serviceUrl,
                                CASE_FACTORY.createCaseInqRQ(caseInqRQ),
                                new SoapActionCallback(soapAction));

        // Check and throw exception if the SOA call was not successful
        checkSoaCallSuccess(serviceName, response.getValue().getHeaderRS());

        return response.getValue();
    }
}
