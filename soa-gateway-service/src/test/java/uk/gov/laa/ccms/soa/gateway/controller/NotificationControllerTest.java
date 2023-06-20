package uk.gov.laa.ccms.soa.gateway.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ws.client.WebServiceIOException;
import uk.gov.laa.ccms.soa.gateway.service.NotificationService;
import uk.gov.laa.ccms.soa.gateway.model.NotificationSummary;

import java.util.stream.Stream;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
@WebAppConfiguration
class NotificationControllerTest {
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationController notificationController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(notificationController).build();
    }

    @Test
    public void testGetUserNotificationSummary_Success() throws Exception {
        // Mock input parameters
        String userId = "123";
        String soaGatewayUserLoginId = "user";
        String soaGatewayUserRole = "EXTERNAL";
        Integer soaGatewayMaxRecords = 100;

        // Create a mock notification summary
        NotificationSummary notificationSummary = new NotificationSummary();
        notificationSummary.setNotifications(1);
        notificationSummary.setStandardActions(2);
        notificationSummary.setOverdueActions(3);

        // Mock the notificationService to return the mock notification summary
        when(notificationService.getNotificationSummary(userId, soaGatewayUserLoginId, soaGatewayUserRole, soaGatewayMaxRecords))
                .thenReturn(notificationSummary);

        // Call the getUserNotificationSummary method
        mockMvc.perform(get("/users/{user-id}/notifications/summary", userId)
                        .header("SoaGateway-User-Login-Id", soaGatewayUserLoginId)
                        .header("SoaGateway-User-Role", soaGatewayUserRole)
                        .header("soaGateway-Max-Records", String.valueOf(soaGatewayMaxRecords)))
                .andExpect(status().isOk());

        // Verify that the notificationService method was called with the correct parameters
        verify(notificationService).getNotificationSummary(userId, soaGatewayUserLoginId, soaGatewayUserRole, soaGatewayMaxRecords);
    }

    @Test
    public void testGetUserNotificationSummary_Exception() throws Exception {
        // Mock input parameters
        String userId = "123";
        String soaGatewayUserLoginId = "user";
        String soaGatewayUserRole = "EXTERNAL";
        Integer soaGatewayMaxRecords = 100;

        // Mock the notificationService to throw an exception
        when(notificationService.getNotificationSummary(userId, soaGatewayUserLoginId, soaGatewayUserRole, soaGatewayMaxRecords))
                .thenThrow(new WebServiceIOException("Test exception"));

        // Call the getUserNotificationSummary method
        mockMvc.perform(get("/users/{user-id}/notifications/summary", userId)
                        .header("SoaGateway-User-Login-Id", soaGatewayUserLoginId)
                        .header("SoaGateway-User-Role", soaGatewayUserRole)
                        .header("soaGateway-Max-Records", String.valueOf(soaGatewayMaxRecords)))
                .andExpect(status().isInternalServerError());

        // Verify that the notificationService method was called with the correct parameters
        verify(notificationService).getNotificationSummary(userId, soaGatewayUserLoginId, soaGatewayUserRole, soaGatewayMaxRecords);
    }

    @ParameterizedTest
    @CsvSource({
            "null, EXTERNAL", // SoaGateway-User-Login-Id is null
            "user, null" // SoaGateway-User-Role is null
    })
    public void testGetUserNotificationSummary_HeaderBadRequest(String userLoginId, String userRole) throws Exception {
        // Call the getUserNotificationSummary method with null headers
        MockHttpServletRequestBuilder requestBuilder = get("/users/{user-id}/notifications/summary", "userId");

        if (userLoginId != null) {
            requestBuilder.header("SoaGateway-User-Login-Id", userLoginId);
        }
        if (userRole != null) {
            requestBuilder.header("SoaGateway-User-Role", userRole);
        }

        // Call the getUserNotificationSummary method with optional headers
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());
    }
}