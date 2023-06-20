package uk.gov.laa.ccms.soa.gateway.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.laa.ccms.soa.gateway.api.NotificationsApi;
import uk.gov.laa.ccms.soa.gateway.model.NotificationSummary;
import uk.gov.laa.ccms.soa.gateway.service.NotificationService;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class NotificationController implements NotificationsApi {

    private final NotificationService notificationService;

    @Override
    public ResponseEntity<NotificationSummary> getUserNotificationSummary(String userId,
                                                                          @jakarta.validation.constraints.NotNull String soaGatewayUserLoginId,
                                                                          @jakarta.validation.constraints.NotNull String soaGatewayUserRole,
                                                                          Integer soaGatewayMaxRecords) {
        return null;
    }
}
