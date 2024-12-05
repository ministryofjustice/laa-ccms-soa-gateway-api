package uk.gov.laa.ccms.soa.gateway.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import uk.gov.laa.ccms.soa.gateway.client.NotificationClient;
import uk.gov.laa.ccms.soa.gateway.mapper.NotificationMapper;
import uk.gov.laa.ccms.soa.gateway.model.Notification;
import uk.gov.laa.ccms.soa.gateway.model.NotificationSummary;
import uk.gov.laa.ccms.soa.gateway.model.Notifications;
import uk.gov.laa.ccms.soa.gateway.model.UserDetail;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.NotificationCntInqRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.NotificationInqRQ.SearchCriteria;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.NotificationInqRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.NotificationInqRS.NotificationList;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.ObjectFactory;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.Notes;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.NotesElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.NotificationCntList;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.NotificationElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.NotificationElementType.AttachedDocuments;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.NotificationElementType.AvailableResponses;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.NotificationElementType.UploadedDocuments;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.NotificationListElementType;
import uk.gov.legalservices.enterprise.common._1_0.common.DocumentElementType;
import uk.gov.legalservices.enterprise.common._1_0.common.RecordCount;
import uk.gov.legalservices.enterprise.common._1_0.common.User;

/**
 * Test class for {@link NotificationService}.
 */
@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {


  private static final Pageable PAGEABLE = Pageable.ofSize(20);
  @InjectMocks
  private NotificationService notificationService;
  @Mock
  private NotificationClient notificationClient;
  @Mock
  private NotificationMapper notificationMapper;

  private static AvailableResponses getAvailableResponses() {
    AvailableResponses responses = new AvailableResponses();
    responses.getResponse().add("available response");
    return responses;
  }

  private static DocumentElementType getDocumentElementType() {
    DocumentElementType documentElementType = new DocumentElementType();
    documentElementType.setDocumentID("1234");
    documentElementType.setDocumentType("sausage");
    documentElementType.setBinData(new byte[8]);
    documentElementType.setFileExtension("doc");
    return documentElementType;
  }

  private static XMLGregorianCalendar getXmlGregorianCalendar()
      throws DatatypeConfigurationException {
    ZoneId zone = ZoneId.of("Europe/London");
    ZonedDateTime zdt = ZonedDateTime.now(zone);
    GregorianCalendar grep = GregorianCalendar.from(zdt);
    XMLGregorianCalendar xmlGreg = DatatypeFactory.newInstance().newXMLGregorianCalendar(grep);
    return xmlGreg;
  }

  @Test
  void getNotifications() throws DatatypeConfigurationException {
    //creeate mock NotificationInqRS
    NotificationInqRS response = new NotificationInqRS();
    NotificationElementType notification = getNotificationElementType();
    NotificationList notificationList = new NotificationList();
    NotificationListElementType listElementType = getNotificationListElementType();
    listElementType.setNotification(notification);
    notificationList.getNotifications().add(listElementType);
    response.setNotificationList(notificationList);
    response.setRecordCount(new RecordCount());
    response.getRecordCount().setRecordsFetched(
        BigInteger.valueOf(notificationList.getNotifications().size()));
    ObjectFactory objectFactory = new ObjectFactory();

    String soaGatewayUserLoginId = "soaGatewayUserLoginId";
    String soaGatewayUserRole = "soaGatewayUserRole";
    Integer maxRecords = 10;
    // Create a mocked instance of Notifications Object
    Notifications expectedObject = new Notifications();
    List<Notification> testNotificationList = Collections.singletonList(new Notification());

    //Stub NotficationClient getNotifications method to return mocked response
    when(notificationClient.getNotifications(any(), any(), any(), any(), any()))
        .thenReturn(response);

    // Stub NotificationMapper to return mock
    when(notificationMapper.toNotifications(any())).thenReturn(expectedObject);
    when(notificationMapper.toNotificationsList(response)).thenReturn(testNotificationList);

    // Call the service method

    Notifications result = notificationService.getNotifications(
        new Notification()
            .user(
            new UserDetail()
                .userLoginId(soaGatewayUserLoginId)
                .userType(soaGatewayUserRole)
                .userName("ding ding")
            )
            .caseReferenceNumber(null),
    false,
    null,
    null,
    maxRecords, PAGEABLE);


    // Verify correct call to NotificationClient
    verify(notificationClient, times(1)).getNotifications(any(), any(),
        any(), any(), any());

    verify(notificationMapper).toNotificationsList(response);

    // Assert the response
    assertEquals(expectedObject, result);

  }

  private NotificationListElementType getNotificationListElementType()
      throws DatatypeConfigurationException {
    NotificationListElementType listElementType = new NotificationListElementType();
    listElementType.setClientName("jeff");
    listElementType.setCategoryOfLaw("law");
    listElementType.setUser(buildUser());
    listElementType.setFeeEarner("1234");
    return listElementType;
  }

  private NotificationElementType getNotificationElementType()
      throws DatatypeConfigurationException {
    NotificationElementType notification = new NotificationElementType();
    notification.setNotificationID("1234");
    XMLGregorianCalendar xmlGreg = getXmlGregorianCalendar();
    notification.setAssignDate(xmlGreg);
    notification.setNotificationOpenInd(true);
    notification.setProviderFirmID(new BigDecimal(123));
    notification.setSubject("Please Review");
    notification.setNotitificationType("N");
    notification.setAvailableResponses(getAvailableResponses());
    notification.setAttachedDocuments(getAttachedDocuments());
    notification.setUploadedDocuments(getUploadedDocuments());
    List<NotesElementType> notes = new ArrayList<>();
    notification.setNotes(setNotes(notes));
    return notification;
  }

  private AttachedDocuments getAttachedDocuments() {
    AttachedDocuments attachedDocuments = new AttachedDocuments();
    attachedDocuments.getDocument().add(getDocumentElementType());
    return attachedDocuments;
  }

  private UploadedDocuments getUploadedDocuments() {
    UploadedDocuments uploadedDocuments = new UploadedDocuments();
    uploadedDocuments.getDocument().add(getDocumentElementType());
    return uploadedDocuments;
  }

  private Notes setNotes(List<NotesElementType> notes) throws DatatypeConfigurationException {
    NotesElementType notesElementType = new NotesElementType();
    notesElementType.setMessage("ding ding");
    notesElementType.setNotesID("1234");
    notesElementType.setDate(getXmlGregorianCalendar());
    notesElementType.setUser(buildUser());
    notes.add(notesElementType);
    Notes notesObj = new Notes();
    notesObj.getNote().addAll(notes);
    return notesObj;
  }

  private User buildUser() {
    User user = new User();
    user.setUserLoginID("loginId");
    user.setUserName("username");
    user.setUserType("usertype");

    return user;
  }

  private SearchCriteria createSearchCriteria(SearchCriteria criteria) {
    criteria.setAssignedToUserID("ding ding");
    criteria.setDateFrom(null);
    criteria.setDateTo(null);
    criteria.setCaseReferenceNumber(null);
    criteria.setClientSurName(null);
    criteria.setFeeEarnerID("1");
    criteria.setProviderCaseReferenceNumber(null);
    criteria.setNotitificationType(null);
    criteria.setIncludeClosedNotification(false);
    return criteria;

  }

}

