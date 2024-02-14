package uk.gov.laa.ccms.soa.gateway.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static uk.gov.laa.ccms.soa.gateway.util.SoaModelUtils.buildAddress;
import static uk.gov.laa.ccms.soa.gateway.util.SoaModelUtils.buildAssesmentResultType;
import static uk.gov.laa.ccms.soa.gateway.util.SoaModelUtils.buildDocumentElementType;
import static uk.gov.laa.ccms.soa.gateway.util.SoaModelUtils.buildOpaInstanceType;
import static uk.gov.laa.ccms.soa.gateway.util.SoaModelUtils.buildRecordHistory;
import static uk.gov.laa.ccms.soa.gateway.util.SoaModelUtils.buildUser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.laa.ccms.soa.gateway.model.AddressDetail;
import uk.gov.laa.ccms.soa.gateway.model.AssessmentResult;
import uk.gov.laa.ccms.soa.gateway.model.AssessmentScreen;
import uk.gov.laa.ccms.soa.gateway.model.CaseReferenceSummary;
import uk.gov.laa.ccms.soa.gateway.model.Document;
import uk.gov.laa.ccms.soa.gateway.model.OpaAttribute;
import uk.gov.laa.ccms.soa.gateway.model.OpaEntity;
import uk.gov.laa.ccms.soa.gateway.model.OpaGoal;
import uk.gov.laa.ccms.soa.gateway.model.OpaInstance;
import uk.gov.laa.ccms.soa.gateway.model.RecordHistory;
import uk.gov.laa.ccms.soa.gateway.model.UserDetail;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabim.ReferenceDataInqRS;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import uk.gov.legalservices.enterprise.common._1_0.common.Address;
import uk.gov.legalservices.enterprise.common._1_0.common.AssesmentResultType;
import uk.gov.legalservices.enterprise.common._1_0.common.AssessmentScreenType;
import uk.gov.legalservices.enterprise.common._1_0.common.DocumentElementType;
import uk.gov.legalservices.enterprise.common._1_0.common.OPAAttributesType;
import uk.gov.legalservices.enterprise.common._1_0.common.OPAEntityType;
import uk.gov.legalservices.enterprise.common._1_0.common.OPAGoalType;
import uk.gov.legalservices.enterprise.common._1_0.common.OPAInstanceType;
import uk.gov.legalservices.enterprise.common._1_0.common.User;

@ExtendWith(MockitoExtension.class)
public class CommonMapperTest {

    @InjectMocks
    private CommonMapperImpl commonMapper;

    @Test
    public void testToDocument() {
        DocumentElementType documentElementType = buildDocumentElementType();
        Document document = commonMapper.toDocument(documentElementType);
        assertEquals("1234", document.getDocumentId());
        assertEquals("sausage", document.getDocumentType());
        assertEquals("doc", document.getFileExtension());
    }

    @Test
    public void testNullDocType() {
        assertNull(commonMapper.toDocument(null));
    }

    @Test
    public void testDocWithNullBinData() {
        DocumentElementType documentElementType = new DocumentElementType();
        documentElementType.setDocumentID("1234");
        documentElementType.setDocumentType("sausage");
        documentElementType.setFileExtension("doc");
        Document document = commonMapper.toDocument(documentElementType);
        assertEquals("1234", document.getDocumentId());
        assertEquals("sausage", document.getDocumentType());
        assertEquals("doc", document.getFileExtension());
        assertNull(document.getBinData());
    }

    @Test
    public void testToCaseReferenceSummaryWithResult() {
        // Create a mocked instance of the response object
        String caseReferenceNumber = "1234567890";
        ReferenceDataInqRS response = ReferenceDataInqRSBuilder.newBuilder()
                .withResults(Collections.singletonList(caseReferenceNumber))
                .build();

        CaseReferenceSummary result = commonMapper.toCaseReferenceSummary(response);

        assertNotNull(result);
        assertEquals(caseReferenceNumber, result.getCaseReferenceNumber());
    }

    @Test
    public void testToCaseReferenceSummaryWithEmptyResult() {
        // Create a mocked instance of the response object with empty results
        ReferenceDataInqRS response = ReferenceDataInqRSBuilder.newBuilder().build();

        CaseReferenceSummary result = commonMapper.toCaseReferenceSummary(response);

        assertNotNull(result);
        assertNull(result.getCaseReferenceNumber());
    }

    @Test
    public void testToCaseReferenceSummaryWithMultipleResults() {
        // Create a mocked instance of the response object with multiple results
        List<String> results = Arrays.asList("111111", "222222", "333333");
        ReferenceDataInqRS response = ReferenceDataInqRSBuilder.newBuilder()
                .withResults(results)
                .build();

        CaseReferenceSummary result = commonMapper.toCaseReferenceSummary(response);

        assertNotNull(result);
        assertEquals("111111", result.getCaseReferenceNumber());
    }

    @Test
    public void testToUserDetail() {
        User user = buildUser();

        UserDetail result = commonMapper.toUserDetail(user);

        assertNotNull(result);
        assertEquals(user.getUserLoginID(), result.getUserLoginId());
        assertEquals(user.getUserName(), result.getUserName());
        assertEquals(user.getUserType(), result.getUserType());
    }

    @Test
    public void testToAddressDetail() {
        Address address = buildAddress();

        AddressDetail result = commonMapper.toAddressDetail(address);

        compareAddress(address, result);
    }

    @Test
    public void whenAddressDetailIsNotNull_ReturnAddress() {
        AddressDetail addressDetail = new AddressDetail();
        addressDetail.setAddressId("Id");
        addressDetail.setCareOfName("John Doe");
        addressDetail.setHouse("100");
        addressDetail.setAddressLine1("Street 1");
        addressDetail.setAddressLine2("Street 2");
        addressDetail.setAddressLine3("Street 3");
        addressDetail.setAddressLine4("Street 4");
        addressDetail.setCity("City");
        addressDetail.setCountry("Country");
        addressDetail.setCounty("County");
        addressDetail.setState("State");
        addressDetail.setProvince("Province");
        addressDetail.setPostalCode("12345");

        Address address = commonMapper.toAddress(addressDetail);
        assertNotNull(address);
        assertEquals("Id", address.getAddressID());
        assertEquals("John Doe", address.getCoffName());
        assertEquals("100", address.getHouse());
        assertEquals("Street 1", address.getAddressLine1());
        assertEquals("Street 2", address.getAddressLine2());
        assertEquals("Street 3", address.getAddressLine3());
        assertEquals("Street 4", address.getAddressLine4());
        assertEquals("City", address.getCity());
        assertEquals("Country", address.getCountry());
        assertEquals("County", address.getCounty());
        assertEquals("State", address.getState());
        assertEquals("Province", address.getProvince());
        assertEquals("12345", address.getPostalCode());
    }

    @Test
    public void whenAddressDetailIsNull_ReturnNull() {
        assertNull(commonMapper.toAddress(null));
    }

    @Test
    public void testToAssessmentResult() {
        AssesmentResultType assesmentResultType = buildAssesmentResultType();

        AssessmentResult result = commonMapper.toAssessmentResult(assesmentResultType);

        assertNotNull(result);
        assertEquals(assesmentResultType.getAssesmentID(), result.getAssessmentId());
        assertEquals(assesmentResultType.getDate().toGregorianCalendar().getTime(), result.getDate());

        OPAGoalType opaGoalType = assesmentResultType.getResults().getGoal().get(0);
        OpaGoal opaGoal = result.getResults().get(0);
        assertEquals(opaGoalType.getAttribute(), opaGoal.getAttribute());
        assertEquals(opaGoalType.getAttributeValue(), opaGoal.getAttributeValue());

        AssessmentScreenType assessmentScreenType = assesmentResultType.getAssesmentDetails()
            .getAssessmentScreens().get(0);
        AssessmentScreen assessmentScreen = result.getAssessmentDetails().get(0);

        assertEquals(assessmentScreenType.getCaption(), assessmentScreen.getCaption());

        OPAEntityType opaEntityType = assessmentScreenType.getEntity().get(0);
        OpaEntity opaEntity = assessmentScreen.getEntity().get(0);
        assertEquals(opaEntityType.getCaption(), opaEntity.getCaption());
        assertEquals(opaEntityType.getEntityName(), opaEntity.getEntityName());
        assertEquals(opaEntityType.getSequenceNumber().intValue(), opaEntity.getSequenceNumber());

        // OPAInstanceType compared in separate test.
    }

    @Test
    public void testToRecordHistory() {
        uk.gov.legalservices.enterprise.common._1_0.common.RecordHistory recordHistory = buildRecordHistory();

        RecordHistory result = commonMapper.toRecordHistory(recordHistory);

        assertNotNull(result);
        compareUser(recordHistory.getCreatedBy(), result.getCreatedBy());
        assertEquals(recordHistory.getDateCreated().toGregorianCalendar().getTime(),
            result.getDateCreated());
        compareUser(recordHistory.getLastUpdatedBy(), result.getLastUpdatedBy());
        assertEquals(recordHistory.getDateLastUpdated().toGregorianCalendar().getTime(),
            result.getDateLastUpdated());
    }

    @Test
    public void testToOpaInstance() {
        OPAInstanceType opaInstanceType = buildOpaInstanceType();

        OpaInstance result = commonMapper.toOpaInstance(opaInstanceType);
        assertNotNull(result);
        assertEquals(opaInstanceType.getInstanceLabel(), result.getInstanceLabel());
        assertEquals(opaInstanceType.getCaption(), result.getCaption());

        OPAAttributesType opaAttributesType = opaInstanceType.getAttributes().getAttribute().get(0);
        OpaAttribute opaAttribute = result.getAttributes().get(0);
        assertEquals(opaAttributesType.getAttribute(), opaAttribute.getAttribute());
        assertEquals(opaAttributesType.getCaption(), opaAttribute.getCaption());
        assertEquals(opaAttributesType.getResponseType(), opaAttribute.getResponseType());
        assertEquals(opaAttributesType.getResponseText(), opaAttribute.getResponseText());
        assertEquals(opaAttributesType.getResponseValue(), opaAttribute.getResponseValue());
    }

    private void compareAddress(Address address, AddressDetail addressDetail) {
        assertNotNull(address);
        assertEquals(address.getAddressID(), addressDetail.getAddressId());
        assertEquals(address.getHouse(), addressDetail.getHouse());
        assertEquals(address.getAddressLine1(), addressDetail.getAddressLine1());
        assertEquals(address.getAddressLine2(), addressDetail.getAddressLine2());
        assertEquals(address.getAddressLine3(), addressDetail.getAddressLine3());
        assertEquals(address.getAddressLine4(), addressDetail.getAddressLine4());
        assertEquals(address.getCity(), addressDetail.getCity());
        assertEquals(address.getCounty(), addressDetail.getCounty());
        assertEquals(address.getCoffName(), addressDetail.getCareOfName());
        assertEquals(address.getPostalCode(), addressDetail.getPostalCode());
        assertEquals(address.getProvince(), addressDetail.getProvince());
        assertEquals(address.getState(), addressDetail.getState());
        assertEquals(address.getCountry(), addressDetail.getCountry());
    }

    private void compareUser(User user, UserDetail result) {
        assertNotNull(result);
        assertEquals(user.getUserType(), result.getUserType());
        assertEquals(user.getUserName(), result.getUserName());
        assertEquals(user.getUserLoginID(), result.getUserLoginId());
    }

    /**
     * A builder class to create instances of {@link ReferenceDataInqRS} for testing purposes.
     * This builder provides a convenient way to construct a mocked instance of the response object
     * {@link ReferenceDataInqRS} with desired attributes and values for testing the mapper.
     */
    private static class ReferenceDataInqRSBuilder {
        private final ReferenceDataInqRS response = new ReferenceDataInqRS();

        /**
         * Creates a new instance of the builder.
         * @return A new instance of the builder.
         */
        public static ReferenceDataInqRSBuilder newBuilder() {
            return new ReferenceDataInqRSBuilder();
        }

        /**
         * Sets the list of results in the {@link ReferenceDataInqRS} response object.
         * @param results The list of results to set in the response object.
         * @return The builder instance.
         */
        public ReferenceDataInqRSBuilder withResults(List<String> results) {
            response.getResults().addAll(results);
            return this;
        }

        /**
         * Builds and returns the {@link ReferenceDataInqRS} response object with the specified attributes.
         * @return The constructed {@link ReferenceDataInqRS} response object.
         */
        public ReferenceDataInqRS build() {
            return response;
        }
    }

}
