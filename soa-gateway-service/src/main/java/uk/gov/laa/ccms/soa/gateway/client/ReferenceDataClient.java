package uk.gov.laa.ccms.soa.gateway.client;

import jakarta.xml.bind.JAXBElement;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabim.ObjectFactory;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabim.ReferenceDataInqRQ;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabim.ReferenceDataInqRS;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabio.KeyType;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabio.SearchContext;

@Slf4j
@SuppressWarnings("unchecked")
@Component
public class ReferenceDataClient extends AbstractSoaClient{

    private static final ObjectFactory CASE_FACTORY = new ObjectFactory();

    public ReferenceDataClient(final WebServiceTemplate webServiceTemplate,
                              @Value("${laa.ccms.soa-gateway.reference-data.service-name}")final  String serviceName,
                              @Value("${laa.ccms.soa-gateway.reference-data.service-url}")final  String serviceUrl) {
        this.webServiceTemplate = webServiceTemplate;
        this.serviceName = serviceName;
        this.serviceUrl = serviceUrl;
    }

    public ReferenceDataInqRS getCaseReference(final String loggedInUserId,
                                               final String loggedInUserType){

        final String soapAction = String.format("%s/process", serviceName);
        ReferenceDataInqRQ referenceDataInqRQ = CASE_FACTORY.createReferenceDataInqRQ();
        referenceDataInqRQ.setHeaderRQ(createHeaderRQ(loggedInUserId, loggedInUserType));
        List<SearchContext> contexts = referenceDataInqRQ.getSearchCriteria();
        SearchContext context = new SearchContext();
        List<KeyType> keyTypes = context.getContextKey();
        keyTypes.add(KeyType.CASE_REFERENCE_NUMBER);
        contexts.add(context);

        JAXBElement<ReferenceDataInqRS> response = (JAXBElement<ReferenceDataInqRS>)getWebServiceTemplate()
                .marshalSendAndReceive(
                        serviceUrl,
                        CASE_FACTORY.createReferenceDataInqRQ(referenceDataInqRQ),
                        new SoapActionCallback(soapAction));

        checkSoaCallSuccess(serviceName, response.getValue().getHeaderRS());

        return response.getValue();

    }
}
