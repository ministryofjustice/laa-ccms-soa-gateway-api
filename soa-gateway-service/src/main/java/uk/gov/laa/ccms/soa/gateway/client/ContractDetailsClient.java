package uk.gov.laa.ccms.soa.gateway.client;

import jakarta.xml.bind.JAXBElement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabim.ContractDetailsInqRQ;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabim.ContractDetailsInqRS;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabim.ObjectFactory;

@Slf4j
@SuppressWarnings("unchecked")
@Component
public class ContractDetailsClient extends AbstractSoaClient {
    private static final ObjectFactory CASE_FACTORY = new ObjectFactory();

    public ContractDetailsClient(final WebServiceTemplate webServiceTemplate,
        @Value("${laa.ccms.soa-gateway.contract-details.service-name}")final  String serviceName,
        @Value("${laa.ccms.soa-gateway.contract-details.service-url}")final  String serviceUrl) {
        this.webServiceTemplate = webServiceTemplate;
        this.serviceName = serviceName;
        this.serviceUrl= serviceUrl;
    }

    public ContractDetailsInqRS getContractDetails(
            final String searchFirmId,
            final String searchOfficeId,
            final String loggedInUserId,
            final String loggedInUserType,
            final Integer maxRecords) {

        final String soapAction = String.format("%s/process", serviceName);
        ContractDetailsInqRQ contractDetailsInqRQ = CASE_FACTORY.createContractDetailsInqRQ();
        contractDetailsInqRQ.setHeaderRQ(createHeaderRQ(loggedInUserId, loggedInUserType));

        ContractDetailsInqRQ.SearchCriteria searchCriteria = CASE_FACTORY
            .createContractDetailsInqRQSearchCriteria();
        searchCriteria.setFirmID(searchFirmId);
        searchCriteria.setOfficeID(searchOfficeId);
        contractDetailsInqRQ.setSearchCriteria(searchCriteria);
        contractDetailsInqRQ.setRecordCount(createRecordCount(maxRecords));

        JAXBElement<ContractDetailsInqRS> response =
            (JAXBElement<ContractDetailsInqRS>)getWebServiceTemplate()
                .marshalSendAndReceive(
                    serviceUrl,
                    CASE_FACTORY.createContractDetailsRQ(contractDetailsInqRQ),
                        new SoapActionCallback(soapAction));

        // Check and throw exception if the SOA call was not successful
        checkSoaCallSuccess(serviceName, response.getValue().getHeaderRS());

        return response.getValue();
    }


}
