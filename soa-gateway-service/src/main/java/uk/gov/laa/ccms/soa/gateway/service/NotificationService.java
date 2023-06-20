package uk.gov.laa.ccms.soa.gateway.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.laa.ccms.soa.gateway.client.NotificationClient;
import uk.gov.laa.ccms.soa.gateway.model.NotificationSummary;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.NotificationCntInqRS;

import java.math.BigInteger;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationClient notificationClient;

    public NotificationSummary getNotificationSummary(){

        NotificationCntInqRS response = notificationClient.getNotificationCount(
                "MARILYN@DESORANDCO.CO.UK",
                "EXTERNAL",
                "MARILYN@DESORANDCO.CO.UK",
                new BigInteger("100"));

        System.out.println(response.getNotificationCntLists().getNotificationsCnt().get(0).getNotificationCount());

        //todo some mapping

        return null;
    }




}
