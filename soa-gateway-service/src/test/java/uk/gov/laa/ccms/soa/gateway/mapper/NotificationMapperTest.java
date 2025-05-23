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
  public void testToNoteReturnsNullWithNullNotes() {
    assertNull(notificationMapper.toNote(null));
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
