package uk.gov.laa.ccms.soa.gateway.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.laa.ccms.soa.gateway.model.ClientDetail;
import uk.gov.laa.ccms.soa.gateway.model.ClientDetails;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbim.ClientInqRS;
import uk.gov.legalservices.ccms.clientmanagement.client._1_0.clientbio.ClientList;
import uk.gov.legalservices.enterprise.common._1_0.common.Name;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ClientDetailsMapperTest {

    @InjectMocks
    ClientDetailsMapperImpl clientDetailsMapper;

    @Test
    public void testToClientDetails() {
        ClientInqRS response = new ClientInqRS();
        ClientInqRS.ClientList clientList = new ClientInqRS.ClientList();

        ClientList client = new ClientList();
        clientList.getClient().add(client);

        response.setClientList(clientList);

        ClientDetails result = clientDetailsMapper.toClientDetails(response);

        assertNotNull(result);
        assertEquals(1, result.getClients().size());
    }

    @Test
    public void testToClientDetail() {
        ClientList clientList = new ClientList();
        Name name = new Name();
        name.setFirstName("John");
        name.setSurname("Doe");
        name.setSurnameAtBirth("Smith");
        name.setFullName("John Doe");
        clientList.setName(name);
        clientList.setHomeOfficeReference("HO12345");
        clientList.setNINumber("AB123456C");

        ClientDetail result = clientDetailsMapper.toClientDetail(clientList);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getSurname());
        assertEquals("Smith", result.getSurnameAtBirth());
        assertEquals("John Doe", result.getFullName());
        assertEquals("HO12345", result.getHomeOfficeReference());
        assertEquals("AB123456C", result.getNationalInsuranceNumber());
    }
}
