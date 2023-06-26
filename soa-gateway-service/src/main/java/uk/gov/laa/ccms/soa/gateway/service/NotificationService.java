package uk.gov.laa.ccms.soa.gateway.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.laa.ccms.soa.gateway.client.NotificationClient;
import uk.gov.laa.ccms.soa.gateway.mapper.NotificationMapper;
import uk.gov.laa.ccms.soa.gateway.model.NotificationSummary;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.NotificationCntInqRS;

import java.math.BigInteger;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationClient notificationClient;

    private final NotificationMapper notificationMapper;

    public NotificationSummary getNotificationSummary(String searchLoginId,
                                                      String soaGatewayUserLoginId,
                                                      String soaGatewayUserRole,
                                                      Integer soaGatewayMaxRecords){
        NotificationCntInqRS response = notificationClient.getNotificationCount(
                searchLoginId,
                soaGatewayUserLoginId,
                soaGatewayUserRole,
                BigInteger.valueOf(soaGatewayMaxRecords));

        return notificationMapper.toNotificationSummary(response);
    }




}
