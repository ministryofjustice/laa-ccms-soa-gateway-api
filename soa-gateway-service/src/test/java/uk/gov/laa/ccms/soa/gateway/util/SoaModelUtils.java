package uk.gov.laa.ccms.soa.gateway.util;

import java.math.BigInteger;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import uk.gov.legalservices.enterprise.common._1_0.common.Address;
import uk.gov.legalservices.enterprise.common._1_0.common.AssesmentResultType;
import uk.gov.legalservices.enterprise.common._1_0.common.AssessmentDetailType;
import uk.gov.legalservices.enterprise.common._1_0.common.AssessmentScreenType;
import uk.gov.legalservices.enterprise.common._1_0.common.DocumentElementType;
import uk.gov.legalservices.enterprise.common._1_0.common.OPAAttributesType;
import uk.gov.legalservices.enterprise.common._1_0.common.OPAEntityType;
import uk.gov.legalservices.enterprise.common._1_0.common.OPAGoalType;
import uk.gov.legalservices.enterprise.common._1_0.common.OPAInstanceType;
import uk.gov.legalservices.enterprise.common._1_0.common.OPAInstanceType.Attributes;
import uk.gov.legalservices.enterprise.common._1_0.common.OPAResultType;
import uk.gov.legalservices.enterprise.common._1_0.common.RecordHistory;
import uk.gov.legalservices.enterprise.common._1_0.common.User;

public class SoaModelUtils {

  public static RecordHistory buildRecordHistory() {
    DatatypeFactory datatypeFactory;
    try {
      datatypeFactory = DatatypeFactory.newInstance();
    } catch (DatatypeConfigurationException e) {
      throw new RuntimeException(e);
    }

    uk.gov.legalservices.enterprise.common._1_0.common.RecordHistory recordHistory =
        new uk.gov.legalservices.enterprise.common._1_0.common.RecordHistory();
    recordHistory.setCreatedBy(buildUser());
    recordHistory.setDateCreated(datatypeFactory.newXMLGregorianCalendar());
    recordHistory.setDateLastUpdated(datatypeFactory.newXMLGregorianCalendar());
    recordHistory.setLastUpdatedBy(buildUser());

    return recordHistory;
  }

  public static User buildUser() {
    User user = new User();
    user.setUserLoginID("loginId");
    user.setUserName("username");
    user.setUserType("usertype");

    return user;
  }

  public static Address buildAddress() {
    Address address = new Address();
    address.setAddressID("addid");
    address.setAddressLine1("addline1");
    address.setAddressLine2("addline2");
    address.setAddressLine3("addline3");
    address.setAddressLine4("addline4");
    address.setCity("city");
    address.setCoffName("careof");
    address.setCountry("country");
    address.setCounty("county");
    address.setHouse("house");
    address.setPostalCode("postcode");
    address.setProvince("province");
    address.setState("state");

    return address;
  }

  public static AssesmentResultType buildAssesmentResultType() {
    OPAGoalType opaGoalType = new OPAGoalType();
    opaGoalType.setAttribute("att");
    opaGoalType.setAttributeValue("val");

    OPAResultType opaResultType = new OPAResultType();
    opaResultType.getGoal().add(opaGoalType);

    OPAInstanceType opaInstanceType = buildOpaInstanceType();

    OPAEntityType opaEntityType = new OPAEntityType();
    opaEntityType.setEntityName("name");
    opaEntityType.setCaption("caption");
    opaEntityType.setSequenceNumber(BigInteger.TEN);
    opaEntityType.getInstances().add(opaInstanceType);

    AssessmentScreenType assessmentScreenType = new AssessmentScreenType();
    assessmentScreenType.setCaption("cap");
    assessmentScreenType.setScreenName("name");
    assessmentScreenType.getEntity().add(opaEntityType);

    AssessmentDetailType assessmentDetailType = new AssessmentDetailType();
    assessmentDetailType.getAssessmentScreens().add(assessmentScreenType);

    AssesmentResultType assessmentResultType = new AssesmentResultType();
    assessmentResultType.setResults(opaResultType);
    assessmentResultType.setAssesmentID("assessid");

    DatatypeFactory datatypeFactory;
    try {
      datatypeFactory = DatatypeFactory.newInstance();
    } catch (DatatypeConfigurationException e) {
      throw new RuntimeException(e);
    }

    assessmentResultType.setDate(datatypeFactory.newXMLGregorianCalendar());
    assessmentResultType.setDefault(Boolean.TRUE);
    assessmentResultType.setAssesmentDetails(assessmentDetailType);

    return assessmentResultType;
  }

  public static OPAInstanceType buildOpaInstanceType() {
    OPAAttributesType opaAttributesType = new OPAAttributesType();
    opaAttributesType.setAttribute("attr");
    opaAttributesType.setCaption("caption");
    opaAttributesType.setResponseType("respType");
    opaAttributesType.setResponseText("responseText");
    opaAttributesType.setResponseValue("respVal");
    opaAttributesType.setUserDefinedInd(Boolean.TRUE);

    Attributes attributes = new Attributes();
    attributes.getAttribute().add(opaAttributesType);

    OPAInstanceType opaInstanceType = new OPAInstanceType();
    opaInstanceType.setInstanceLabel("label");
    opaInstanceType.setAttributes(attributes);

    return opaInstanceType;
  }

  public static DocumentElementType buildDocumentElementType() {
    DocumentElementType documentElementType = new DocumentElementType();
    documentElementType.setDocumentID("1234");
    documentElementType.setDocumentType("sausage");
    documentElementType.setBinData(new byte[8]);
    documentElementType.setFileExtension("doc");
    return documentElementType;
  }

}
