package uk.gov.laa.ccms.soa.gateway.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import uk.gov.laa.ccms.soa.gateway.model.Document;
import uk.gov.laa.ccms.soa.gateway.model.Notification;
import uk.gov.laa.ccms.soa.gateway.model.NotificationSummary;
import uk.gov.laa.ccms.soa.gateway.model.Notifications;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.NotificationCntInqRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.NotificationInqRS;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebim.NotificationInqRS.NotificationList;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.Notes;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.NotesElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.NotificationCntList;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.NotificationElementType;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.NotificationElementType.AttachedDocuments;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.NotificationElementType.AvailableResponses;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.NotificationElementType.UploadedDocuments;
import uk.gov.legalservices.ccms.casemanagement._case._1_0.casebio.NotificationListElementType;
import uk.gov.legalservices.enterprise.common._1_0.common.DocumentElementType;
import uk.gov.legalservices.enterprise.common._1_0.common.User;

@ExtendWith(MockitoExtension.class)
public class NotificationMapperTest {

  @Mock
  CommonMapper commonMapper;

  @InjectMocks
  NotificationMapperImpl notificationMapper;

  private static NotificationElementType getNotificationElementTypeWithNullProviderFirmIdAndEmptyNotes()
      throws DatatypeConfigurationException {
    NotificationElementType notification = new NotificationElementType();
    notification.setNotificationID("1234");
    XMLGregorianCalendar xmlGreg = getXmlGregorianCalendar();
    notification.setAssignDate(xmlGreg);
    notification.setNotificationOpenInd(true);
    notification.setSubject("Please Review");
    notification.setNotitificationType("N");
    notification.setAvailableResponses(getAvailableResponses());
    Notes notes = new Notes();
    notification.setNotes(notes);
    return notification;
  }

  private static AvailableResponses getAvailableResponses() {
    AvailableResponses responses = new AvailableResponses();
    responses.getResponse().add("available response");
    return responses;
  }

  private static XMLGregorianCalendar getXmlGregorianCalendar()
      throws DatatypeConfigurationException {
    ZoneId zone = ZoneId.of("Europe/London");
    ZonedDateTime zdt = ZonedDateTime.now(zone);
    GregorianCalendar grep = GregorianCalendar.from(zdt);
    return DatatypeFactory.newInstance().newXMLGregorianCalendar(grep);
  }

  private static DocumentElementType getDocumentElementType() {
    DocumentElementType documentElementType = new DocumentElementType();
    documentElementType.setDocumentID("1234");
    documentElementType.setDocumentType("sausage");
    documentElementType.setBinData(new byte[8]);
    documentElementType.setFileExtension("doc");
    return documentElementType;
  }

  @Test
  public void testMap() {
    NotificationCntInqRS response = new NotificationCntInqRS();
    NotificationCntInqRS.NotificationCntLists notificationCntLists = new NotificationCntInqRS.NotificationCntLists();
    response.setNotificationCntLists(notificationCntLists);

    NotificationCntList notificationCntList = new NotificationCntList();
    notificationCntList.setNotificationCount("5");
    notificationCntList.setStandardActionCount("3");
    notificationCntList.setOverdueActionCount("2");

    notificationCntLists.getNotificationsCnt().add(notificationCntList);

    NotificationSummary result = notificationMapper.toNotificationSummary(response);

    assertNotNull(result);
    assertEquals(5, result.getNotifications());
    assertEquals(3, result.getStandardActions());
    assertEquals(2, result.getOverdueActions());
  }

  @Test
  public void testMapNoNotificationCntListsThrowsException() {
    NotificationCntInqRS response = new NotificationCntInqRS();

    assertThrows(RuntimeException.class, () -> notificationMapper.toNotificationSummary(response),
        "notificationCntList not found in response");
  }

  @Test
  public void testMapNoNotificationCntListThrowsException() {
    NotificationCntInqRS response = new NotificationCntInqRS();
    NotificationCntInqRS.NotificationCntLists notificationCntLists = new NotificationCntInqRS.NotificationCntLists();
    response.setNotificationCntLists(notificationCntLists);

    assertThrows(RuntimeException.class, () -> notificationMapper.toNotificationSummary(response),
        "notificationCntList not found in response");
  }

  @Test
  public void testMapNoNotificationCountThrowsException() {
    NotificationCntInqRS response = new NotificationCntInqRS();
    NotificationCntInqRS.NotificationCntLists notificationCntLists = new NotificationCntInqRS.NotificationCntLists();
    response.setNotificationCntLists(notificationCntLists);

    NotificationCntList notificationCntList = new NotificationCntList();
    notificationCntList.setStandardActionCount("3");
    notificationCntList.setOverdueActionCount("2");

    notificationCntLists.getNotificationsCnt().add(notificationCntList);

    assertThrows(RuntimeException.class, () -> notificationMapper.toNotificationSummary(response),
        "notificationCount not found in response");
  }

  @Test
  public void testMapNoStandardActionCountThrowsException() {
    NotificationCntInqRS response = new NotificationCntInqRS();
    NotificationCntInqRS.NotificationCntLists notificationCntLists = new NotificationCntInqRS.NotificationCntLists();
    response.setNotificationCntLists(notificationCntLists);

    NotificationCntList notificationCntList = new NotificationCntList();
    notificationCntList.setNotificationCount("5");
    notificationCntList.setOverdueActionCount("2");

    notificationCntLists.getNotificationsCnt().add(notificationCntList);

    assertThrows(RuntimeException.class, () -> notificationMapper.toNotificationSummary(response),
        "standardActionCount not found in response");
  }

  @Test
  public void testMapNoOverdueActionCountThrowsException() {
    NotificationCntInqRS response = new NotificationCntInqRS();
    NotificationCntInqRS.NotificationCntLists notificationCntLists = new NotificationCntInqRS.NotificationCntLists();
    response.setNotificationCntLists(notificationCntLists);

    NotificationCntList notificationCntList = new NotificationCntList();
    notificationCntList.setStandardActionCount("3");
    notificationCntList.setNotificationCount("5");

    notificationCntLists.getNotificationsCnt().add(notificationCntList);

    assertThrows(RuntimeException.class, () -> notificationMapper.toNotificationSummary(response),
        "overdueActionCount not found in response");
  }

  /*
   * NOTIFICATIONS TESTS
   */
  @Test
  public void testToNotifications() {
    List<Notification> notificationList = Collections.singletonList(new Notification());
    Page<Notification> notificationsPage = new PageImpl<>(notificationList, Pageable.unpaged(),
        notificationList.size());

    Notifications result = notificationMapper.toNotifications(notificationsPage);

    assertNotNull(result);

    assertEquals(1, result.getContent().size());
  }

  @Test
  public void testMapNotificationSuccess() throws DatatypeConfigurationException {
    NotificationInqRS response = new NotificationInqRS();
    NotificationElementType notification = getNotificationElementType();
    NotificationList notificationList = new NotificationList();
    NotificationListElementType listElementType = getNotificationListElementType();
    listElementType.setNotification(notification);
    notificationList.getNotifications().add(listElementType);
    response.setNotificationList(notificationList);
    List<Notification> result = notificationMapper.toNotificationsList(response);
    assertNotNull(result);
    assertEquals(1, result.size());
  }

  @Test
  public void testEmptyListReturnedWhenNotificationElementIsNull() {
    NotificationInqRS response = new NotificationInqRS();
    response.setNotificationList(null);
    List<Notification> result = notificationMapper.toNotificationsList(response);
    assertEquals(0, result.size());
  }

  @Test
  public void testToNotificationsListWithNoProviderFirmId()
      throws DatatypeConfigurationException {
    NotificationInqRS response = new NotificationInqRS();
    NotificationElementType notification = getNotificationElementTypeWithNullProviderFirmIdAndEmptyNotes();
    NotificationList notificationList = new NotificationList();
    NotificationListElementType listElementType = getNotificationListElementType();
    listElementType.setNotification(notification);
    notificationList.getNotifications().add(listElementType);
    response.setNotificationList(notificationList);
    List<Notification> result = notificationMapper.toNotificationsList(response);
    assertNotNull(result);
    assertEquals(1, result.size());
  }

  @Test
  public void testNullNotificationsListReturnsNull() {
    assertNull(notificationMapper.toNotificationList(null));
  }

  @Test
  public void testNullNotificationObjectReturnsNull() {
    assertNull(notificationMapper.toNotification(null));
  }

  @Test
  public void testNotesElementTypeListToNoteListWithEmptyListReturnsNull() {
    assertNull(notificationMapper.notesElementTypeListToNoteList(null));
  }



  @Test
  public void testToNoteReturnsNullWithNullNotes() {
    assertNull(notificationMapper.toNote(null));
  }

  @Test
  public void testDocumentElementTypeListToDocumentListSuccess() {
    List<DocumentElementType> docs = new ArrayList<>();
    DocumentElementType documentElementType = getDocumentElementType();
    docs.add(documentElementType);

    when(commonMapper.toDocument(documentElementType))
        .thenReturn(new Document());

    List<Document> transformedDocs = notificationMapper.documentElementTypeListToDocumentList(docs);
    assertEquals(1, transformedDocs.size());
    assertNotNull(transformedDocs.get(0));
  }



  private NotificationListElementType getNotificationListElementType() {
    NotificationListElementType listElementType = new NotificationListElementType();
    listElementType.setClientName("jeff");
    listElementType.setCategoryOfLaw("law");
    listElementType.setUser(buildUser());
    listElementType.setFeeEarner("1234");
    return listElementType;
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

  private User buildUser() {
    User user = new User();
    user.setUserLoginID("loginId");
    user.setUserName("username");
    user.setUserType("usertype");

    return user;
  }
}
