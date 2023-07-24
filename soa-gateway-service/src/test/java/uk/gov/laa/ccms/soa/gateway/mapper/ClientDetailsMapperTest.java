package uk.gov.laa.ccms.soa.gateway.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import uk.gov.laa.ccms.soa.gateway.model.ClientDetail;
import uk.gov.laa.ccms.soa.gateway.model.ClientDetails;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbim.ClientInqRS;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbio.ClientInfo;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbio.ClientList;
import uk.gov.legalservices.enterprise.common._1_0.common.Name;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class ClientDetailsMapperTest {

    @InjectMocks
    ClientDetailsMapperImpl clientDetailsMapper;

    private DatatypeFactory datatypeFactory;

    @BeforeEach
    public void setup() throws DatatypeConfigurationException {
        datatypeFactory = DatatypeFactory.newInstance();
    }

    @Test
    public void testToClientDetails() {
        ClientInqRS response = new ClientInqRS();
        ClientInqRS.ClientList clientListObject = new ClientInqRS.ClientList();
        List<ClientList> clientList = Collections.singletonList(new ClientList());
        clientListObject.getClient().addAll(clientList);
        response.setClientList(clientListObject);

        List<ClientDetail> clientDetailList = Collections.singletonList(new ClientDetail());
        Page<ClientDetail> clientDetailPage = new PageImpl<>(clientDetailList, Pageable.unpaged(), clientDetailList.size());

        ClientDetails result = clientDetailsMapper.toClientDetails(clientDetailPage);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    public void testToClientDetailList() {
        ClientInqRS response = new ClientInqRS();
        ClientInqRS.ClientList clientListObject = new ClientInqRS.ClientList();
        List<ClientList> clientList = Collections.singletonList(buildClientList());
        clientListObject.getClient().addAll(clientList);
        response.setClientList(clientListObject);

        List<ClientDetail> result = clientDetailsMapper.toClientDetailList(response);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void testToClientDetail() {
        ClientList clientList = buildClientList();

        ClientDetail result = clientDetailsMapper.toClientDetail(clientList);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getSurname());
        assertEquals("Smith", result.getSurnameAtBirth());
        assertEquals("John Doe", result.getFullName());
        assertEquals("HO12345", result.getHomeOfficeReference());
        assertEquals("AB123456C", result.getNationalInsuranceNumber());
        assertEquals("CR123", result.getCaseReferenceNumber());
    }

    @Test
    public void testToClientInfo() {
        // Test setup
        ClientDetail clientDetail = new ClientDetail();
        clientDetail.setNationalInsuranceNumber("AB123456C");
        clientDetail.setGender("Male");
        clientDetail.setFirstName("John");
        clientDetail.setSurname("Doe");
        clientDetail.setDateOfBirth(new Date());
        clientDetail.setHomeOfficeReference("HO12345");
        clientDetail.setCaseReferenceNumber("CR123");

        // Test execution
        ClientInfo result = clientDetailsMapper.toClientInfo(clientDetail);

        // Test verification
        assertNotNull(result);
        assertEquals("AB123456C", result.getNINumber());
        assertEquals("Male", result.getGender());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getSurname());
        assertNotNull(result.getDateOfBirth());
        assertEquals("HO12345", result.getHomeOfficeReference());
        assertEquals("CR123", result.getCaseReferenceNumber());
    }

    private ClientList buildClientList() {
        ClientList clientList = new ClientList();
        Name name = new Name();
        name.setFirstName("John");
        name.setSurname("Doe");
        name.setSurnameAtBirth("Smith");
        name.setFullName("John Doe");
        clientList.setName(name);
        clientList.setHomeOfficeReference("HO12345");
        clientList.setNINumber("AB123456C");
        clientList.setClientReferenceNumber("CR123");
        return clientList;
    }
}
