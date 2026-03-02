package uk.gov.laa.ccms.soa.gateway.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.laa.ccms.soa.gateway.mapper.context.ProviderRequestMappingContext;
import uk.gov.laa.ccms.soa.gateway.model.ProviderRequestAttribute;
import uk.gov.laa.ccms.soa.gateway.model.ProviderRequestDetail;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.ProviderRequestElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.ProviderRequestTextElementType;
import uk.gov.legalservices.enterprise.common._1_0.common.User;

@ExtendWith(MockitoExtension.class)
class ProviderRequestsMapperTest {

  @Mock CommonMapper commonMapper;

  @InjectMocks ProviderRequestsMapperImpl mapper;

  @Test
  @DisplayName(
      "toProviderRequestElementType - Maps ProviderRequestMappingContext to ProviderRequestElementType")
  void testToProviderRequestElementType() {
    ProviderRequestDetail providerRequestDetail = new ProviderRequestDetail();
    providerRequestDetail.setCaseReferenceNumber("CR123");
    providerRequestDetail.setUsername("testUser");

    ProviderRequestMappingContext context =
        ProviderRequestMappingContext.builder()
            .userLoginId("userLoginId")
            .userRole("userRole")
            .providerRequestDetail(providerRequestDetail)
            .build();

    ProviderRequestElementType result = mapper.toProviderRequestElementType(context);

    assertNotNull(result);
    assertEquals("CR123", result.getCaseReferenceNumber());
    assertNotNull(result.getUser());
    assertEquals("testUser", result.getUser().getUserName());
    assertEquals("userLoginId", result.getUser().getUserLoginID());
    assertEquals("userRole", result.getUser().getUserType());
  }

  @Test
  @DisplayName("toUser - Maps ProviderRequestMappingContext to User")
  void testToUser() {
    ProviderRequestDetail providerRequestDetail = new ProviderRequestDetail();
    providerRequestDetail.setUsername("testUser");

    ProviderRequestMappingContext context =
        ProviderRequestMappingContext.builder()
            .userLoginId("userLoginId")
            .userRole("userRole")
            .providerRequestDetail(providerRequestDetail)
            .build();

    User user = mapper.toUser(context);

    assertNotNull(user);
    assertEquals("testUser", user.getUserName());
    assertEquals("userLoginId", user.getUserLoginID());
    assertEquals("userRole", user.getUserType());
  }

  @Test
  @DisplayName(
      "mapAttributes - Maps list of ProviderRequestAttribute to ProviderRequestTextElementType")
  void testMapAttributes() {
    ProviderRequestAttribute attribute1 = new ProviderRequestAttribute();
    attribute1.setLabel("attr1");
    attribute1.setText("value1");

    ProviderRequestAttribute attribute2 = new ProviderRequestAttribute();
    attribute2.setLabel("attr2");
    attribute2.setText("value2");

    List<ProviderRequestAttribute> attributes = List.of(attribute1, attribute2);

    List<ProviderRequestTextElementType> result = mapper.mapAttributes(attributes);

    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals("attr1", result.get(0).getLabel());
    assertEquals("value1", result.get(0).getText());
    assertEquals("attr2", result.get(1).getLabel());
    assertEquals("value2", result.get(1).getText());
  }

  @Test
  @DisplayName("toRequestDetails - Maps ProviderRequestDetail to RequestDetails")
  void testToRequestDetails() {
    ProviderRequestDetail providerRequestDetail = new ProviderRequestDetail();
    providerRequestDetail.setRequestType("REQ_TYPE");

    ProviderRequestElementType.RequestDetails result =
        mapper.toRequestDetails(providerRequestDetail);

    assertNotNull(result);
    assertEquals("REQ_TYPE", result.getRequest().getRequestType());
  }

  @Test
  @DisplayName("toProviderRequestElementType - Handles Null Input Gracefully")
  void testToProviderRequestElementType_NullInput() {
    assertNull(mapper.toProviderRequestElementType(null));
  }

  @Test
  @DisplayName("toUser - Handles Null Input Gracefully")
  void testToUser_NullInput() {
    assertNull(mapper.toUser(null));
  }

  @Test
  @DisplayName("mapAttributes - Handles Null Input Gracefully")
  void testMapAttributes_NullInput() {
    assertNull(mapper.mapAttributes(null));
  }

  @Test
  @DisplayName("toRequestDetailsList - Handles Null Input Gracefully")
  void testToRequestDetailsList_NullInput() {
    assertNull(mapper.toRequestDetailsList(null));
  }

  @Test
  @DisplayName("toRequestDetails - Handles Null Input Gracefully")
  void testToRequestDetails_NullInput() {
    assertNull(mapper.toRequestDetails(null));
  }

  @Test
  @DisplayName("toProviderRequestTextElementType - Handles Null Input Gracefully")
  void testToProviderRequestTextElementType_NullInput() {
    assertNull(mapper.toProviderRequestTextElementType(null));
  }
}
