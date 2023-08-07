package uk.gov.laa.ccms.soa.gateway.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.laa.ccms.soa.gateway.model.CaseReferenceSummary;
import uk.gov.legalservices.ccms.common.referencedata._1_0.referencedatabim.ReferenceDataInqRS;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class ReferenceDataMapperTest {

    @InjectMocks
    private ReferenceDataMapperImpl referenceDataMapper;

    @Test
    public void testToCaseReferenceSummaryWithResult() {
        // Create a mocked instance of the response object
        String caseReferenceNumber = "1234567890";
        ReferenceDataInqRS response = ReferenceDataInqRSBuilder.newBuilder()
                .withResults(Collections.singletonList(caseReferenceNumber))
                .build();

        CaseReferenceSummary result = referenceDataMapper.toCaseReferenceSummary(response);

        assertNotNull(result);
        assertEquals(caseReferenceNumber, result.getCaseReferenceNumber());
    }

    @Test
    public void testToCaseReferenceSummaryWithEmptyResult() {
        // Create a mocked instance of the response object with empty results
        ReferenceDataInqRS response = ReferenceDataInqRSBuilder.newBuilder().build();

        CaseReferenceSummary result = referenceDataMapper.toCaseReferenceSummary(response);

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

        CaseReferenceSummary result = referenceDataMapper.toCaseReferenceSummary(response);

        assertNotNull(result);
        assertEquals("111111", result.getCaseReferenceNumber());
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
