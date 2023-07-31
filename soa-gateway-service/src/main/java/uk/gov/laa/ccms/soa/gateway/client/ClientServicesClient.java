package uk.gov.laa.ccms.soa.gateway.client;

import jakarta.xml.bind.JAXBElement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbim.ClientInqRQ;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbim.ClientInqRS;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbim.ObjectFactory;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbio.ClientInfo;


@Slf4j
@SuppressWarnings("unchecked")
@Component
public class ClientServicesClient extends AbstractSoaClient{

    private static final ObjectFactory CASE_FACTORY = new ObjectFactory();

    public ClientServicesClient(final WebServiceTemplate webServiceTemplate,
                                @Value("${laa.ccms.soa-gateway.client.service-name}") final String serviceName,
                                @Value("${laa.ccms.soa-gateway.client.service-url}") final String serviceUrl) {
        this.webServiceTemplate = webServiceTemplate;
        this.serviceName = serviceName;
        this.serviceUrl= serviceUrl;
    }

    public ClientInqRS getClientDetails(
            final String loggedInUserId,
            final String loggedInUserType,
            final Integer maxRecords,
            final ClientInfo clientInfo
    ) {

        final String soapAction = String.format("%s/GetClientDetails", serviceName);
        ClientInqRQ clientInqRQ = CASE_FACTORY.createClientInqRQ();
        clientInqRQ.setHeaderRQ(createHeaderRQ(loggedInUserId, loggedInUserType));

        ClientInqRQ.SearchCriteria searchCriteria = CASE_FACTORY
                .createClientInqRQSearchCriteria();

        searchCriteria.setClientInfo(clientInfo);

        return getClientInqRS(maxRecords, soapAction, clientInqRQ, searchCriteria);
    }

    public ClientInqRS getClientDetail(
            final String loggedInUserId,
            final String loggedInUserType,
            final Integer maxRecords,
            final String clientReferenceNumber
    ) {

        final String soapAction = String.format("%s/GetClientDetails", serviceName);
        ClientInqRQ clientInqRQ = CASE_FACTORY.createClientInqRQ();
        clientInqRQ.setHeaderRQ(createHeaderRQ(loggedInUserId, loggedInUserType));

        ClientInqRQ.SearchCriteria searchCriteria = CASE_FACTORY
                .createClientInqRQSearchCriteria();

        searchCriteria.setClientReferenceNumber(clientReferenceNumber);

        return getClientInqRS(maxRecords, soapAction, clientInqRQ, searchCriteria);
    }

    private ClientInqRS getClientInqRS(Integer maxRecords, String soapAction, ClientInqRQ clientInqRQ, ClientInqRQ.SearchCriteria searchCriteria) {
        clientInqRQ.setSearchCriteria(searchCriteria);
        clientInqRQ.setRecordCount(createRecordCount(maxRecords));

        JAXBElement<ClientInqRS> response =
                (JAXBElement<ClientInqRS>) getWebServiceTemplate()
                        .marshalSendAndReceive(
                                serviceUrl,
                                CASE_FACTORY.createClientInqRQ(clientInqRQ),
                                new SoapActionCallback(soapAction));


        // Check and throw exception if the SOA call was not successful
        checkSoaCallSuccess(serviceName, response.getValue().getHeaderRS());

        return response.getValue();
    }
}
