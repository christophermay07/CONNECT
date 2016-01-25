/*
 * Copyright (c) 2009-2015, United States Government, as represented by the Secretary of Health and Human Services.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above
 *       copyright notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the documentation
 *       and/or other materials provided with the distribution.
 *     * Neither the name of the United States Government nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE UNITED STATES GOVERNMENT BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package gov.hhs.fha.nhinc.policyengine.adapter.pip;

import gov.hhs.fha.nhinc.common.nhinccommonadapter.PatientPreferencesType;
import gov.hhs.fha.nhinc.nhinclib.NhincConstants;
import gov.hhs.fha.nhinc.properties.PropertyAccessor;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.xml.bind.JAXBElement;
import org.hl7.v3.ADExplicit;
import org.hl7.v3.ActClassClinicalDocument;
import org.hl7.v3.AdxpExplicitCity;
import org.hl7.v3.AdxpExplicitCountry;
import org.hl7.v3.AdxpExplicitPostalCode;
import org.hl7.v3.AdxpExplicitState;
import org.hl7.v3.AdxpExplicitStreetAddressLine;
import org.hl7.v3.CE;
import org.hl7.v3.CS;
import org.hl7.v3.EnExplicitFamily;
import org.hl7.v3.EnExplicitGiven;
import org.hl7.v3.EnExplicitPrefix;
import org.hl7.v3.EnExplicitSuffix;
import org.hl7.v3.II;
import org.hl7.v3.IVLTSExplicit;
import org.hl7.v3.IVXBTSExplicit;
import org.hl7.v3.ONExplicit;
import org.hl7.v3.PNExplicit;
import org.hl7.v3.POCDMT000040Author;
import org.hl7.v3.POCDMT000040ClinicalDocument;
import org.hl7.v3.POCDMT000040Patient;
import org.hl7.v3.POCDMT000040PatientRole;
import org.hl7.v3.SCExplicit;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * This class is used to test the CdaPdfCreator class.
 *
 * @author Les Westberg
 */
@Ignore
// Refactor or move this test to Integration test suite
@RunWith(JMock.class)
public class CdaPdfCreatorTest {
    Mockery context = new JUnit4Mockery();

    private static final String HL7_DATE_ONLY_FORMAT = "yyyyMMdd";
    private static final SimpleDateFormat oHL7DateOnlyFormatter = new SimpleDateFormat(HL7_DATE_ONLY_FORMAT);
    private static final String HL7_DATE_TIME_FORMAT = "yyyyMMddHHmmssZ";
    private static final SimpleDateFormat oHL7DateTimeFormatter = new SimpleDateFormat(HL7_DATE_TIME_FORMAT);

    private static String PT_PREF_PART1 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
            + "<PatientPreferences xmlns:ns16=\"urn:oasis:names:tc:ebxml-regrep:xsd:lcm:3.0\" xmlns=\"urn:gov:hhs:fha:nhinc:common:nhinccommonadapter\" xmlns:ns17=\"http://nhinc.services.com/schema/auditmessage\" xmlns:ns14=\"urn:oasis:names:tc:xacml:2.0:policy:schema:os\" xmlns:ns15=\"urn:gov:hhs:fha:nhinc:common:subscriptionb2overridefordocuments\" xmlns:ns9=\"http://www.hhs.gov/healthit/nhin/cdc\" xmlns:ns5=\"urn:ihe:iti:xds-b:2007\" xmlns:ns12=\"http://docs.oasis-open.org/wsn/t-1\" xmlns:ns6=\"urn:oasis:names:tc:ebxml-regrep:xsd:query:3.0\" xmlns:ns13=\"urn:oasis:names:tc:xacml:2.0:context:schema:os\" xmlns:ns7=\"http://www.w3.org/2005/08/addressing\" xmlns:ns10=\"urn:gov:hhs:fha:nhinc:common:subscriptionb2overrideforcdc\" xmlns:ns8=\"http://docs.oasis-open.org/wsn/b-2\" xmlns:ns11=\"http://docs.oasis-open.org/wsrf/bf-2\" xmlns:ns2=\"urn:gov:hhs:fha:nhinc:common:nhinccommon\" xmlns:ns4=\"urn:oasis:names:tc:ebxml-regrep:xsd:rs:3.0\" xmlns:ns3=\"urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0\">"
            + "    <patientId>1111</patientId>"
            + "       <assigningAuthority>1.1</assigningAuthority>"
            + "    <optIn>true</optIn>"
            + "    <binaryDocumentPolicyCriteria>"
            + "        <binaryDocumentPolicyCriterion>"
            + "                       <documentUniqueId>20.200.1.1</documentUniqueId>"
            + "            <documentTypeCode>"
            + "                <ns2:code>34133-9</ns2:code>"
            + "                <ns2:codeSystem>2.16.840.1.113883.6.1</ns2:codeSystem>"
            + "                <ns2:codeSystemName>LOINC</ns2:codeSystemName>"
            + "                <ns2:displayName>SUMMARIZATION OF EPISODE NOTE</ns2:displayName>"
            + "            </documentTypeCode>"
            + "            <documentTitle>Good Health Clinic Care Record Summary</documentTitle>"
            + "            <effectiveTime>20050329204411-0700</effectiveTime>"
            + "            <confidentialityCode>"
            + "                <ns2:code>N</ns2:code>"
            + "                <ns2:codeSystem>2.16.840.1.113883.5.25</ns2:codeSystem>"
            + "            </confidentialityCode>"
            + "            <patientInfo>"
            + "                <name>"
            + "                    <ns2:familyName>Ross</ns2:familyName>"
            + "                    <ns2:givenName>Ellen</ns2:givenName>"
            + "                    <ns2:prefix>Mrs.</ns2:prefix>"
            + "                </name>"
            + "                <addr>"
            + "                    <ns2:address>"
            + "                        <ns2:city>Blue Bell</ns2:city>"
            + "                        <ns2:country>USA</ns2:country>"
            + "                        <ns2:state>MA</ns2:state>"
            + "                        <ns2:streetAddress>17 Daws Rd.</ns2:streetAddress>"
            + "                        <ns2:zipCode>02368</ns2:zipCode>"
            + "                    </ns2:address>"
            + "                </addr>"
            + "                <gender>"
            + "                    <ns2:code>F</ns2:code>"
            + "                    <ns2:codeSystem>2.16.840.1.113883.5.1</ns2:codeSystem>"
            + "                </gender>"
            + "                <birthTime>19600127</birthTime>"
            + "            </patientInfo>"
            + "            <authorOriginal>"
            + "                <authorTime>19990522</authorTime>"
            + "                <authorIdAssigningAuthority>1.3.5.35.1.4436.7</authorIdAssigningAuthority>"
            + "                <authorId>11111111</authorId>"
            + "                <name>"
            + "                    <ns2:familyName>Wiseman</ns2:familyName>"
            + "                    <ns2:givenName>Bernard</ns2:givenName>"
            + "                    <ns2:prefix>Dr.</ns2:prefix>"
            + "                    <ns2:suffix>Sr.</ns2:suffix>"
            + "                </name>"
            + "                <representedOrganizationIdAssigningAuthority>1.3.5.35.1.4436.7</representedOrganizationIdAssigningAuthority>"
            + "                <representedOrganizationId>aaaaabbbbb</representedOrganizationId>"
            + "                <representedOrganizationName>Dr. Wiseman\"s Clinic</representedOrganizationName>"
            + "            </authorOriginal>"
            + "            <authorScanner>"
            + "                <authorTime>20050329204411-0700</authorTime>"
            + "                <authorIdAssigningAuthority>1.3.6.4.1.4.1.2835.2.1234</authorIdAssigningAuthority>"
            + "                <authoringDevice>"
            + "                    <ns2:code>CAPTURE</ns2:code>"
            + "                    <ns2:codeSystem>1.2.840.10008.2.16.4</ns2:codeSystem>"
            + "                    <ns2:displayName>Image Capture</ns2:displayName>"
            + "                </authoringDevice>"
            + "                <deviceManufactureModelName>SOME SCANNER NAME AND MODEL </deviceManufactureModelName>"
            + "                <deviceSoftwareName>SCAN SOFTWARE NAME v0.0</deviceSoftwareName>"
            + "                <representedOrganizationIdAssigningAuthority>1.3.6.4.1.4.1.2835.2</representedOrganizationIdAssigningAuthority>"
            + "                <representedOrganizationName>SOME Scanning Facility</representedOrganizationName>"
            + "                <representedOrganizationAddress>"
            + "                    <ns2:city>Burlington</ns2:city>"
            + "                    <ns2:country>USA</ns2:country>"
            + "                    <ns2:state>MA</ns2:state>"
            + "                    <ns2:streetAddress>21 North Ave</ns2:streetAddress>"
            + "                    <ns2:zipCode>01803</ns2:zipCode>"
            + "                </representedOrganizationAddress>"
            + "            </authorScanner>"
            + "            <dataEnterer>"
            + "                <dataEntererTime>20050329204411-0700</dataEntererTime>"
            + "                <dataEntererIdAssigningAuthority>1.3.6.4.1.4.1.2835.2</dataEntererIdAssigningAuthority>"
            + "                <dataEntererId>22222222</dataEntererId>"
            + "                <name>"
            + "                    <ns2:familyName>Smith</ns2:familyName>"
            + "                    <ns2:givenName>Bernice</ns2:givenName>"
            + "                    <ns2:prefix>Mrs.</ns2:prefix>"
            + "                </name>"
            + "            </dataEnterer>"
            + "            <custodian>"
            + "                <organizationIdAssigningAuthority>1.3.6.4.1.4.1.2835.2</organizationIdAssigningAuthority>"
            + "                <organizationName>SOME Scanning Facility</organizationName>"
            + "                <organizationAddress>"
            + "                    <ns2:city>Burlington</ns2:city>"
            + "                    <ns2:country>USA</ns2:country>"
            + "                    <ns2:state>MA</ns2:state>"
            + "                    <ns2:streetAddress>21 North Ave</ns2:streetAddress>"
            + "                    <ns2:zipCode>01803</ns2:zipCode>"
            + "                </organizationAddress>"
            + "            </custodian>"
            + "            <legalAuthenticator>"
            + "                <authenticationTime>19990522</authenticationTime>"
            + "                <authenticatorIdAssigningAuthority>1.3.5.35.1.4436.7</authenticatorIdAssigningAuthority>"
            + "                <authenticatorId>11111111</authenticatorId>"
            + "                <signatureCode>"
            + "                    <ns2:code>S</ns2:code>"
            + "                </signatureCode>"
            + "                <authenticatorPersonName>"
            + "                    <ns2:familyName>Wiseman</ns2:familyName>"
            + "                    <ns2:givenName>Bernard</ns2:givenName>"
            + "                    <ns2:prefix>Dr.</ns2:prefix>"
            + "                    <ns2:suffix>Sr.</ns2:suffix>"
            + "                </authenticatorPersonName>"
            + "            </legalAuthenticator>"
            + "            <startDate>19800127</startDate>"
            + "            <endDate>19990522</endDate>"
            + "            <mimeType>application/pdf</mimeType>"
            + "            <binaryDocument>SlZCRVJpMHhMalVOQ2lXMXRiVzFEUW94SURBZ2IySnFEUW84UEM5VWVYQmxMME5oZEdGc2IyY3ZVR0ZuWlhNZ01pQXdJRkl2VEdGdVp5aGxiaTFWVXlrZ0wxTjBjblZqZEZSeVpXVlNiMjkwSURnZ01DQlNMMDFoY210SmJtWnZQRHd2VFdGeWEyVmtJSFJ5ZFdVK1BqNCtEUXBsYm1Sdlltb05DaklnTUNCdlltb05Danc4TDFSNWNHVXZVR0ZuWlhNdlEyOTFiblFnTVM5TGFXUnpXeUF6SURBZ1VsMGdQajROQ21WdVpHOWlhZzBLTXlBd0lHOWlhZzBLUER3dlZIbHdaUzlRWVdkbEwxQmhjbVZ1ZENBeUlEQWdVaTlTWlhOdmRYSmpaWE04UEM5R2IyNTBQRHd2UmpFZ05TQXdJRkkrUGk5UWNtOWpVMlYwV3k5UVJFWXZWR1Y0ZEM5SmJXRm5aVUl2U1cxaFoyVkRMMGx0WVdkbFNWMGdQajR2VFdWa2FXRkNiM2hiSURBZ01DQTJNVElnTnpreVhTQXZRMjl1ZEdWdWRITWdOQ0F3SUZJdlIzSnZkWEE4UEM5VWVYQmxMMGR5YjNWd0wxTXZWSEpoYm5Od1lYSmxibU41TDBOVEwwUmxkbWxqWlZKSFFqNCtMMVJoWW5NdlV5OVRkSEoxWTNSUVlYSmxiblJ6SURBK1BnMEtaVzVrYjJKcURRbzBJREFnYjJKcURRbzhQQzlHYVd4MFpYSXZSbXhoZEdWRVpXTnZaR1V2VEdWdVozUm9JREUxT1Q0K0RRcHpkSEpsWVcwTkNuaWNUWTA5QzhJd0dJVDNRUDdEamU4N05FMXFhZzJVRHYwU2hZSkR0dUlnV2orRzZ0RDYvMDBIb2R4eEhOekJnL2lFUEkrNzZsQkRGd1hLdWtMcHBZaGJBMk9VdHZCM0tReDBrRUdXS0oxWVpOcXBOQ3lqRkJxUEpmWlM5T1NmcnduQkY3Q2plZUJvUTlNY0trNXNxZWJJVW9zYkcwc2ZqbEs2ZmtmZTBmQm1rOUNzK0F4L2xLSUo2QVgvQnhxWHFhMWJBM3ZDNm91bXEvQUR6dHNvOWcwS1pXNWtjM1J5WldGdERRcGxibVJ2WW1vTkNqVWdNQ0J2WW1vTkNqdzhMMVI1Y0dVdlJtOXVkQzlUZFdKMGVYQmxMMVJ5ZFdWVWVYQmxMMDVoYldVdlJqRXZRbUZ6WlVadmJuUXZRVUpEUkVWRkswTmhiR2xpY21rdlJXNWpiMlJwYm1jdlYybHVRVzV6YVVWdVkyOWthVzVuTDBadmJuUkVaWE5qY21sd2RHOXlJRFlnTUNCU0wwWnBjbk4wUTJoaGNpQXpNaTlNWVhOMFEyaGhjaUF4TVRjdlYybGtkR2h6SURFMElEQWdVajQrRFFwbGJtUnZZbW9OQ2pZZ01DQnZZbW9OQ2p3OEwxUjVjR1V2Um05dWRFUmxjMk55YVhCMGIzSXZSbTl1ZEU1aGJXVXZRVUpEUkVWRkswTmhiR2xpY21rdlJteGhaM01nTXpJdlNYUmhiR2xqUVc1bmJHVWdNQzlCYzJObGJuUWdOelV3TDBSbGMyTmxiblFnTFRJMU1DOURZWEJJWldsbmFIUWdOelV3TDBGMloxZHBaSFJvSURVd015OU5ZWGhYYVdSMGFDQXhOamt3TDBadmJuUlhaV2xuYUhRZ05EQXdMMWhJWldsbmFIUWdNalV3TDFOMFpXMVdJRFV3TDBadmJuUkNRbTk0V3lBdE5EYzJJQzB5TlRBZ01USXhOQ0EzTlRCZElDOUdiMjUwUm1sc1pUSWdNVFVnTUNCU1BqNE5DbVZ1Wkc5aWFnMEtOeUF3SUc5aWFnMEtQRHd2UVhWMGFHOXlLSGRsYzNSaVpYSm5LUzlEY21WaGRHOXlLUDcvQUUwQWFRQmpBSElBYndCekFHOEFaZ0IwQUs0QUlBQlBBR1lBWmdCcEFHTUFaUUFnQUZjQWJ3QnlBR1FBSUFBeUFEQUFNQUEzS1M5RGNtVmhkR2x2YmtSaGRHVW9SRG95TURBNU1UQXlOakU0TXpNek5pa2dMMDF2WkVSaGRHVW9SRG95TURBNU1UQXlOakU0TXpNek5pa2dMMUJ5YjJSMVkyVnlLUDcvQUUwQWFRQmpBSElBYndCekFHOEFaZ0IwQUs0QUlBQlBBR1lBWmdCcEFHTUFaUUFnQUZjQWJ3QnlBR1FBSUFBeUFEQUFNQUEzS1Q0K0RRcGxibVJ2WW1vTkNqRXpJREFnYjJKcURRbzhQQzlVZVhCbEwwOWlhbE4wYlM5T0lEVXZSbWx5YzNRZ01qa3ZSbWxzZEdWeUwwWnNZWFJsUkdWamIyUmxMMHhsYm1kMGFDQXhORE0rUGcwS2MzUnlaV0Z0RFFwNG5FMk9UUXFETUJDRjk0SjNlRGVZakdrbEJmRUNnb1RvVGx3VUdicHFMV21FZXZ0MnJHQ1hNKzk3UDh3d1lBTjdoa1BwY0FHZkxMZ0FseVdxaXJ5S0JvRTY4dFFNTUNQSTMyRDFWOWQ1dGlGdUp6cVpra0tzb1dIRUR2VHJVNmhMY1psU0gwWENQQ2Z5MXlpUDdmeFdxbHR0NW1mN0UxdDVwMFpXOEI3Vkx2ZlhvSXVMbytIb3k3TVB6bHd6VkEwS1pXNWtjM1J5WldGdERRcGxibVJ2WW1vTkNqRTBJREFnYjJKcURRcGJJREl5TmlBd0lEQWdNQ0F3SURBZ01DQXdJREFnTUNBd0lEQWdNQ0F3SURJMU1pQXdJREFnTUNBd0lEQWdNQ0F3SURBZ01DQXdJREFnTUNBd0lEQWdNQ0F3SURBZ01DQXdJREFnTUNBMk1UVWdNQ0EwTlRrZ01DQXdJREFnTUNBd0lEQWdNQ0F3SURBZ05URTNJREFnTUNBd0lEUTROeUF3SURBZ01DQXdJREFnTUNBd0lEQWdNQ0F3SURBZ01DQTBOemtnTUNBME1qTWdOVEkxSURRNU9DQXdJREFnTlRJMUlESXpNQ0F3SURBZ01DQTNPVGtnTlRJMUlEVXlOeUF3SURBZ01DQXpPVEVnTXpNMUlEVXlOVjBnRFFwbGJtUnZZbW9OQ2pFMUlEQWdiMkpxRFFvOFBDOUdhV3gwWlhJdlJteGhkR1ZFWldOdlpHVXZUR1Z1WjNSb0lEVXdPVFF5TDB4bGJtZDBhREVnT1RZM01EQStQZzBLYzNSeVpXRnREUXA0bk94Y0NYaFRWZG8rNTk1c2JaSW1hZE0yYlpvbWFXaEthVXVCRm1qWkdycFN5bGJhUUZ1MmxpNFVCVUYyRUxDS2F4VkZjZDkzSFhGSkEwaFJSOUhCZlIzSEdXZDBkTVJ4d1FYRVhRVGEvNzMzeTRHQzZNenZNL1BQNy9QTWJkLzd2dWM3eXozcmQ3OUFnSEhHV0R4dUdqYWx0S2F5SW5mVmpwbU16MjVoekZsWFZseGFxMTNBT3huYnZKWXg3Uk5seFJOS1B0dTc0VG5HTnRrWms5c3JTc3ZLbVYvVGh2S2xhQ1cxWXNya21yRzdmYldNWFJ0ZzdMNzNLbXFDeFYrK2F1eGl2THdmWXc3UDVKcmNJUXZhbDNRenh2K004bzNOQzVzV082M0ZLRzhMTVNiZDFyeGltZWZCelRzT01qWldlVjUwMitKNUM3LzlkcUtKTVR2S1JDWFBhMXE2bUtVd0g1NnZRWDNydkFXcjI5ejN2djRRWStVWE1EWjRkbnRyVTh0NzVRKytqL1puSW45WU93em1CNktISW4wRjB2M2FGeTViOVVZNEFYMlRDaGl6ckRpMWRjbHAyWi9rMkJqcnVBOWxQQXNXTlRkWmw2V2kvWlY3R1RQc1h0aTBhbkhHd2ZSeHlFT2ZtZWUwcG9XdC92YkpCeGc3NjBYR1loSVdMMXE2ck5mSnptUHMwdmVWL01WTFdoZjNmdkxodTR5NUZ1TnhScWJNclpZeFhVWk8xaHpMcUc5WWtvRXAxeU9mcm4xUjRTZUs5MFlkSHR5ek5Pb2gvVTFJUmpHSjBZVjZPdGJEK083b1NjaS9PT29odGFVK2w5eWdXRFJOYkRQYVg4VmsxTFN5WE5iS21NK0I1MHJJbFRWNnZnbTVCdTIxMmp3MG1Vb3N2OHJPazVpQlNSYXRKRWthV2RMY3dxVFBBOHh6aG1oN1lvM0h3MkE0cEtFKzZHK1MvQjdHYmxhZnUwTWJvNHdVcmNjYzZ3MS9oZjNMTDgxbjdMNWZVaysrLzVmVisvOTBhZEwrOTJPUVAyU1dmMGRmL3FsbmY0M2Q5MHZxdmNBV25zeXVhV1czSGxldTQvajBUN1kzNWVUbE5LdU8yZmxuUDk4VzhtMy96TFA2WGpvZHUxVnoyVTg4KzE3VzlyOXBTMzdxcC9zblQyYVZKN1hYd3l2MmZlWkdPcTMvRjVmOFJ6YnpGOVdieXhwT1p0Y3ZJcnVPRTJ2eVdlTng5UTZ4V2Iva2ViL1dDK08vVm1qK0ozYnV2L05adWhaMmJkL24vYWd2QlNkZnMxOXl5VjVXZlRLNzlvSGo3ZElEelB1amZpei9zZTFrWmJSeFZFNzNsMzljWGltRHNWL3hqOHI5T3k3cFBsYXE4aVJXS24zQXhrbmRyT0pvM3Nkc0FXOW1UWDNMYTZheEJkS0hLc3FPYTJjUUsrWi9aejZsenY5TnovOS9YVGdmakwvOG4rN0ZmNi8vWHYrOS9udlJKVjNQbzM4eXI1SHQrOGs4TGJ2NjM5T2ovMTYvOGt1T0lDWHlwd0lQSWNYVnRJWmRwN0lWRmhOaW5vR0lLOHJaRkZhTCtLR1Z6V2VMMkhKMmkrZk0zbDZtZklidm05dk0ydkZaYktuSWxRZjB2dDc3THRPanBhT2ZSbnFiOXpUaE9jZDkwbUN4TElFNWp0cGtlVHkzOG1TZXlodjRMTDZjcitEcitDWitIZCtPY1A0enRjUVhKLzVKQnRKUzVNODlKUGJ6RnhmUCtNbUpxZnNITGZTOUhJRC9KUGJwRVc3NXlXNmduelJLTllXUlJ1enFlQ09hUnYxcnVlUi9iWE9CaXBZNXMyZk5uTkZRWHhlc3JabGFQV1h5cElrVHFzWlhqcXNvTHlzdEtSNGJLQm96ZXRUSUVZVUZ3NGNOelIyWWs5M2ZuOTdQbCtaMjJHMVdpOWtZSFdYUTY3UWFXZUlzdTh4WDN1Z0orUnRER3I5djNMZ2NKZTFyZ3FHcGo2RXg1SUdwL1BneUlVK2pXc3h6Zk1rQVNyYWRVREpBSlFOSFMzS3JaeFFibFpQdEtmTjVRaStWK2p6ZHZLRzZEbnBqcWEvZUU5cW42b21xMXZqVmhCa0pyeGMxUEdXTzlsSlBpRGQ2eWtMbEs5bzd5eHBMMFY2WE1ickVWOUlhblpQTnVxS05rRWFvVUgvZjRpN2Vmd3hYaGRTL2JFU1h4QXhtNWJFaE9iMnNxU1UwcGJxdXJOVHA5ZGFyTmxhaXRoWFNsWVQwYWx1ZStVcWYyVVdlcnV4ZG5SZDNXOW5jeGl4VGk2K2xhV1pkU0c1Q3BVNjVyTFB6L0pBdEs1VHBLdzFscm5uZmdTRzNocko5cFdXaExCOGFxNXA2OUFFOHBFMjMranlkM3pCMDNyZnZzK010VFJHTEx0MzZEVk9rTXNTajA0UjhvUm42aGg1aWZGNnYwcGVMdWdOc0xoS2hqdW82U252WVhHZVlCWEt6NmtOU281S3pTK1RFQjVXY0RwRnp0SHFqejZzc1ZWbGo1SGRGdXlQVU1kZVRrNDNaVjMvVDhZdDhUMGoyTjg1dGJsZTRxYlhUVjFwSzgxWmJGd3FVUWdTYUltTXQ2eHFVaS9KTmpSakVmR1VhcXV0Q3ViN0ZJYnV2bUFyQTRGSFdZSDVOblZvbFVpMWtMd214eHVaSXJWQnVXYW5TTDA5WloyTXBkVkJweTFkZHQ1UGw5YjdibGU5eGJzMWorYXhlNlVjb29RU0w0aS9yckd0cEM3a2JuUzNZbjIyZU9xYzNGS2pIOU5YNzZscnJsVlh5V1VPWjcrSnhYdldKYWkyTTdZVFNvckF5Y24yNndWTW5PZVY2WmJWZzhKVGo1aXNlaFF3cmxrdE5LaXRhUE1wVHg1MU1GTU5USWlVVWRWdzdTTWpwSmVPVUxGbXBXakxPNmEzMzB2VXpYWEpHK3FSTkR4bjZ0R1dGNFdpZjZEay8yVFVxclhRbzAxUFdXdHFuZzhjMXFvMTBNTkxheWZzcEtYTVJlVEJxR0pUbEhDZXk1SFNjWE5na05LT2FsRlYwZUVKc2lxZk8xK3FyOTJFUEJhYlVLV05UNWxwZDM2b2FYMVYxUTUyNjJwRmRVbnRjaXZJTEtCVmlYbVNMaEZTQ1BWaWU1UlRMcXFZcjFQVFI1TGdUc2l0RnRxZlQ0S3VxNlZRYTkwVWFaQjZjSUF4YTU2OXN1cWdnTmg5SHN4emV6VmZlNVBOWVBlV2RUZDI5SFhNN3V3S0J6c1ZsamUwamxEWjhsUzJkdnBxNlVVNjFyMVByMWpuWEtJK0taVlc4cXJZNEp4dStwN2pMeHkrbzdncndDMm9hNm5aYUVTRmNVRnNYbHJoVTBsaGMzOVVQZVhVN1BYRHVxbFZTcklwUlNYaVVoTkxTVkNRTWFubm56Z0JqSFdxdVJqV282ZVp1emxTYlFkZzRhKzZXeUdZVk5nazJEZGtDcWsyNXNFaU9ka3d4M0cyWnAwVlpuclgxN1oyTjljcmhZZ2xZU3Z6eUVQZU5ZU0hKTjZhTFN6cFRLTnJYV2h3eStvb1ZlNUZpTHlLN1RySHJzVEY0QXNma0tENnBzOUVIUDRVTlZjZWNuTGFpckRUcDZlN3RyYTN6dnVUY1YrL0ZWcHNKTk5TRm9yTGcrN1hwNDFHdVFrRWp6QldoanVZbXBSOHNXS2ZVMWFkWE50ZGoyNG9HVWFReUZJVVdvaUl0b0VTNVdrZlpqcWpVakxYQkFxcjFPNUFJZGRTSDZyT1VoOWJOcjFlM3N6WEV4dmxHWU5tcFRhMWZlVkJ1Zldlc2I0aDZObkVVb3RQUFZ5Z0tmV00xZFdSeElvbUgxZE1rNlUzb2ViTVBXYzJOSHN5MmhqWFhZS3VUTDQxMmtxVVZMbEhqYjFVUjdZeGtNbVZZY3JyUkhCMktHb2dHOGF0bzQwRGxTR3JUOWZYMTFIazFkWDZrQUo1dERSblJJMytmcVl4VXdPd2dxMUxwQzM3UFIxZVZvazhvelZSM3M2bStWZkFzU3FmVmx2VElEcG5USzV2Zy9LbStFUlpmZ2Foc1VIeUVNZExHYnJMcWxaR2JNTzl5ZW0xMzc5MisxZDQrVjA2MlQzazVLQnVUT1hkaVk3UDZ6aE1Ob1JsWk9kbUdFNjFtMWR6WmFUQ2Z2QUxObDhGOGxCV2pwd3h2RGNiQ1ViS25XenBuVzVTRGo0ZllJTVRaUXB3bFJJY1Fad3F4WG9oMVFxd1Y0Z3doMWdpeFdvaFZRcXdVWW9VUXk0VllKc1JTSVU0WFlyRVFpNFE0VFlpRlFpd1E0bFFoVGhGaXZoRHRRc3dUb2sySVZpRmFoR2dXWXE0UVRVSTBDakZIaU5sQ3pCSmlwaEF6aEdnUW9sNklPaUdtQ3pGTmlLQVF0VUxVQ0RGVmlHb2hwZ2d4V1loSlFrd1VZb0lRVlVLTUY2SlNpSEZDVkFoUkxrU1pFS1ZDbEFoUkxNUllJUUpDRkFreFJvalJRb3dTWXFRUUk0UW9GS0pBaU9GQ0RCTmlxQkQ1UXVRSk1VU0l3VUlNRWlKWGlJRkM1QWlSTFVTV0VBT0V5QlNpdnhBWlF2aUZTQmVpbnhBK0lkS0U4QXJoRWNJdFJLb1FMaUZTaEhBS2tTeEVraEFPSVJLRlNCQWlYZ2k3RUhGQ3hBcGhFOElxaEVXSUdDSE1RcGlFTUFvUkxVU1VFQVloOUVMb2hOQUtvUkZDRmtJU2dndkJJb0wzQ3RFanhCRWhEZ3R4U0lnZmhEZ294UGRDZkNmRXQwSjhJOFRYUW53bHhKZENmQ0hFQVNFK0YySy9FUHVFK0V5SVQ0WDRSSWlQaGRncnhFZENmQ2pFQjBLOEw4VGZoWGhQaUQxQ3ZDdkUzNFI0UjRpM2hmaXJFRzhKOGFZUWZ4SGl6MEs4SWNTZmhQaWpFSzhMOFFjaFhoUGk5MEs4S3NRclFyd3N4RXRDdkNqRUMwSThMOFJ6UWp3cnhETkNQQzNFVTBMc0Z1SjNRandweEJOQzdCTGljU0VlRStLM1Fqd3F4Q05DUEN6RVRpRzZoZGdoeEVOQ2JCZGlteEJiaFFnTDBTVkVTSWdIaFhoQWlQdUZ1RStJTFVMY0s4UnZoTGhIaUx1RnVFdUlPNFc0UTRqYmhiaE5pRnVGdUVXSW00VzRTWWdiaGJoQmlPdUZ1RTZJYTRXNFJvaXJoYmhLaUN1RnVFS0l6VUpjTHNSbFFtd1M0bEloTGhGaW94QVhDM0dSRUoxQ1hDakVCVUtjTDhSNVFwd3JoQWg3dUFoN3VBaDd1QWg3dUFoN3VBaDd1QWg3dUFoN3VBaDd1QWg3dUFoN3VBaDd1QWg3dUFoN3VBaDd1QWg3dUFoN3VBaDcrQkloUlB6RFJmekRSZnpEUmZ6RFJmekRSZnpEUmZ6RFJmekRSZnpEUmZ6RFJmekRSZnpEUmZ6RFJmekRSZnpEUmZ6RFJmekRSZnpEUmZ6RFJmekRSZnpEUmZ6RFJmekRSZnpEUmZ6RFJmekRSZnpEUmZ6RFJmekRSZnpEUmZ6RFJmekRSZGpEUmRqRFJkakRSYlREUmJURFJiVERSYlREUmJURFJiVERSYlREUmJURFJiVERTN1lxQWxGek9IV01HekZ6T0RVZWREYWx6Z3FuamdCMVVPcE1vdlhoVkJOb0hhWFdFcDFCdElab2RkZzFGclFxN0NvQnJTUmFRYlNjOHBaUmFpblJFaktlSG5ZVmd4WVRMU0k2allvc0pGcEFkR280cFF4MEN0RjhvbmFpZVVSdDRaUlNVQ3VsV29pYWllWVNOUkUxRXMwaG1rMzFabEZxSnRFTW9nYWllcUk2b3VsRTA0aUNSTFZFTlVSVGlhcUpwaEJOSnBwRU5KRm9BbEVWMGZpd3N4SlVTVFF1N0J3UHFpQXFEenVyUUdWaDV3UlFLVkVKVVRIbGphVjZBYUlpcWplR2FEVFJLQ281a21nRVZTOGtLaUFhVGpTTWFDZzFsaytVUjYwTUlScE1OSWdheXlVYVNQVnlpTEtKc29nR0VHVVM5U2ZLb0tiOVJPblVaajhpSDFFYU5lMGw4bEE5TjFFcWtZc29oY2hKbEJ4T25nUktJbktFa3llREVva1N5QmhQWkNkakhGRXNrWTN5ckVRV01zWVFtWWxNbEdja2lpYUtvandEa1o1SUYwNmFBdEtHazZwQkdpS1pqQktsT0JGVGlmY1M5YWhGK0JGS0hTWTZSUFFENVIyazFQZEUzeEY5Uy9STjJGRUwranJzcUFGOVJha3ZpYjRnT2tCNW4xTnFQOUUrb3M4bzcxT2lUOGo0TWRGZW9vK0lQcVFpSDFEcWZVcjluVkx2RWUwaGVwZnkva2IwRGhuZkp2b3IwVnRFYjFLUnYxRHF6MFJ2aEJPbmcvNFVUcHdHK2lQUjYyVDhBOUZyUkw4bmVwV0t2RUwwTWhsZklucVI2QVdpNTZuSWMwVFBrdkVab3FlSm5pTGFUZlE3S3Zra3BaNGcya1gwT09VOVJ2UmJNajVLOUFqUncwUTdpYnFwNUE1S1BVUzBuV2diMGRad1FoRW9IRTZZQWVvaUNoRTlTUFFBMGYxRTl4RnRJYm8zbkFCL3pYOURyZHhEZERmbDNVVjBKOUVkUkxjVDNVWjBLOUV0UkRkVFl6ZFJLemNTM1VCNTF4TmRSM1F0MFRWVTRXcEtYVVYwSmRFVmxMZVpXcm1jNkRMSzIwUjBLZEVsUkJ1SkxxYVNGMUdxaytoQ29ndUl6aWM2THh6ZkJEbzNIRDhYZEE3UmhuQjhHK2hzb3JQQzhVRlFSemdlenBpZkdZNGZCbHBQdEk2cXI2VjZaeEN0Q2NlM2dGWlQ5VlZFSzRsV0VDMG5Xa2EwbEpwZVF0VlBKMW9jam04R0xhTEdUcU9TQzRrV0VKMUtkQXJSZktyWFRqU1BldFpHMVZ1SldxaGtNOUZjb2lhaVJxSTVSTE5wMExPb1p6T0padENnRzZqcGVucFFIZEYwNnU0MGVsQ1FXcWtscWlHYVNsUWR0Z2RBVThKMjVRbVR3M1psZTA4SzJ6ZUFKb2J0T2FBSlZLU0thSHpZanJpQVYxSnFIRkVGR2N2RDl2V2dzckQ5ZkZCcDJING1xQ1JzN3dBVmgyUExRV09KQWtSRlJHUENzWGkvODlHVUdoVzIxWU5HRW8wSTI1U3RVVWhVRUxaVmdJYUhiWFdnWVdGYkEyZ281ZVVUNVlWdDJhQWhWSEp3MktZTWJGRFlwcHpOWEtLQlZEMkhucEJObEVXTkRTREtwTWI2RTJVUStZblN3elpsbHZvUithak5OR3JUUzQxNXFCVTNVU3JWY3hHbEVEbUprb21Td3RaWklFZllPaHVVR0xiT0FTVVF4UlBaaWVLSVlxbUNqU3BZeVdnaGlpRXlFNW1vcEpGS1JwTXhpc2hBcENmU1VVa3RsZFNRVVNhU2lEZ1JDL1JhNXJvVjlGaWEzVWNzTGU3RDBJZUFINENEc0gwUDIzZkF0OEEzd05ld2Z3VjhpYnd2a0Q0QWZBN3NCL2JCL2hud0tmSStRZnBqWUMvd0VmQmh6RHozQnpIdDd2ZUJ2d1B2QVh0Z2V4ZjhOK0FkNEcyay93cCtDM2dUK0F2d1ovT3A3amZNZzkxL0F2L1J2TUQ5dXRudi9nUHdHdlR2elZudVY0RlhnSmVSL3hKc0w1b1h1bCtBZmg3Nk9laG56YWU0bnpIUGR6OXRibmMvWlo3bjNvMjZ2ME43VHdKUEFJSGVYYmcvRGp3Ry9OWjB1dnRSMHhMM0k2YWw3b2ROeTl3N2dXNWdCK3dQQWR1UnR3MTVXMkVMQTExQUNIalF1TnI5Z0hHTiszN2pXdmQ5eG5YdUxjYjE3bnVCM3dEM0FIY0Rkd0YzR25QY2Q0QnZCMjVEblZ2QnR4aFBkZDhNZlJQMGpjQU4wTmVqcmV2UTFyVm82eHJZcmdhdUFxNEVyZ0EyQTVlajNtVm9iMVAwSlBlbDBaUGRsMFRQYzIrTXZ0TjljZlRkN25QbGRQYzVjb0Y3QXk5d254M3NDSjYxcFNONFpuQmRjUDJXZFVIak9tNWM1MXhYdGU2TWRWdld2YlV1TUZFWHZUYTRKbmpHbGpYQjFjR1Z3VlZiVmdaWGJGa2UxQ3kzTDErMlhQNTZPZCt5bkpjdTU0T1djNGt0dHk3M0xKZE55NEpMZ2t1M0xBbXlKVk9XZEN3SkxkR01EQzE1ZDRuRWx2RG83dDVkVzVjNFU4dkJnYlZMek5ieTA0T0xnb3UzTEFxZTFyWXdlQXE2TmI5Z1hyQjl5N3hnVzBGTHNIVkxTN0M1WUc2d3FhQXhPS2RnVm5EMmxsbkJtUVVOd1JsYkdvTDFCWFhCNlNnL3JhQTJHTnhTRzZ3cHFBNU8zVklkbkZ3d0tUZ0o5b2tGVmNFSlc2cUM0d3ZHQlN1M2pBdFdGSlFIeXpCa2xtSk44YVRJVnFVRGsxTFFFK2JreFlPY0FlZTd6Z05PRFhPR25MdWNjcXdsMlowc1pWcVNlTW5rSkw0bzZjeWtTNU5raStNVmh4UndaR2FYV3hKZlNmeGI0dWVKbXJoQVl1YkFjcFpnVGZBa3lQSEsyQkltMXBhclhGUktQSGlvT3RhSkNUNS91U1dlVytMZDhWS1pPNTR6Mjd1MkF6WTUvbkhySzFiSll1RVdTNjlGQ2xoUTNCTGpqcEdVVzIrTUhJZ1pQTHpjWW5hYkplWFdhNVlUQW1aWWxCWXpURk5xeXkxR3QxRUtGaGtuRzZXQXNhaWtQR0RNR1ZUT1pPN2hYUGs2ajRmTEJwVGR4dVBkNWZLalhQbXlrWlp4dnFtcnRpWXJxNnJid0taV2hReFRab1Q0QmFIMEd1VWVxRzRJNlM0SXNXRERqTG91emkrcDcrSlNTVzNJcnZ4RnJabytkK05HNWlxdUNybHE2c0x5TGJlNGl1dXJRaDJLRGdSVTNhdG9oaUwxV2JPWExsK2FsYlZzTm02emx5N0xVbitSNHN1VlZKWmlWSDZYTGtOYStWbXVwbG5XejE1VUREUm5LYTVsRWR1eW42LzAvLzNpLytrTy9QcXZMcVo4djJCc3IzUU9hNUUyQUdjRFp3RWR3Sm5BZW1BZHNCWTRBMWdEckFaV0FTdUJGY0J5WUJtd0ZEZ2RXQXdzQWs0REZnSUxnRk9CVTRENVFEc3dEMmdEV29FV29CbVlDelFCamNBY1lEWXdDNWdKekFBYWdIcWdEcGdPVEFPQ1FDMVFBMHdGcW9FcHdHUmdFakFSbUFCVUFlT0JTbUFjVUFHVUEyVkFLVkFDRkFOamdRQlFCSXdCUmdPamdKSEFDS0FRS0FDR0E4T0FvVUEra0FjTUFRWURnNEJjWUNDUUEyUURXY0FBSUJQb0QyUUFmaUFkNkFmNGdEVEFDM2dBTjVBS3VJQVV3QWtrQTBtQUEwZ0VFb0I0d0E3RUFiR0FEYkFDRmlBR01BTW13QWhFQTFHQUFkQURPa0FMYU1iMjRpNERFc0FCeGxvNGJMd0hPQUljQmc0QlB3QUhnZStCNzRCdmdXK0FyNEd2Z0MrQkw0QUR3T2ZBZm1BZjhCbndLZkFKOERHd0YvZ0krQkQ0QUhnZitEdndIckFIZUJmNEcvQU84RGJ3VitBdDRFM2dMOENmZ1RlQVB3Ri9CRjRIL2dDOEJ2d2VlQlY0QlhnWmVBbDRFWGdCZUI1NERuZ1dlQVo0R25nSzJBMzhEbmdTZUFMWUJUd09QQWI4Rm5nVWVBUjRHTmdKZEFNN2dJZUE3Y0EyWUNzUUJycUFFUEFnOEFCd1AzQWZzQVc0Ri9nTmNBOXdOM0FYY0Nkd0IzQTdjQnR3SzNBTGNETndFM0FqY0FOd1BYQWRjQzF3RFhBMWNCVndKWEFGc0JtNEhMZ00yQVJjQ2x3Q2JBUXVCaTRDT29FTGdRdUE4NEh6Z0hOWnk5Z09qdlBQY2Y0NXpqL0grZWM0L3h6bm4rUDhjNXgvanZQUGNmNDV6ai9IK2VjNC94em5uK1A4YzV4L2p2UFBjZjc1RWdBK2dNTUhjUGdBRGgvQTRRTTRmQUNIRCtEd0FSdytnTU1IY1BnQURoL0E0UU00ZkFDSEQrRHdBUncrZ01NSGNQZ0FEaC9BNFFNNGZBQ0hEK0R3QVJ3K2dNTUhjUGdBRGgvQTRRTTRmQUNIRCtEd0FSem5uK1A4YzV4L2pyUFBjZlk1emo3SDJlYzQreHhubitQc2M1eDlqclBQY2ZiLzAzNzRWMzdWLzZjNzhDdS9ISE5tTThhVWYvZmZzL200TDA5UFlhZXdwYXdEUCtleGpXd3plNXk5eGVheURWRFhzbHZZWGV3M0xNU2VZTSt4Ti82VjM5anVXYTFkeUV6eURxWmpjWXoxL3RDN3IrY3VvRnNiMDhleUdhazRqZWVZcGRmYXUvOEUyLzZlemIzV25tNWRMSXRXNjVxbDEyRDlpaC9wL1FFdldLUjdoeWxwNlh4b2kxcmpDLzFOUFEvMjNIM0NIRlN6QmphRHpXU3pXQ05yd3ZoYldEdWJqNWs1bFMxZ0M5bHBhdW8wNU0zRHZRMnBPZXEvWVdoUjliRlNpOWhpWUFsYnhwYXpGZmhaREwwMGtsTHlUbGZUeTlsSy9LeGlxOWthZGdaYnk5WkY3aXRWeTFya3JGSFRxNEQxN0V5c3pGbnNiRlVKSnNzR2RnNDdGNnQyUHJ1QVhmaXpxUXVQcWs1MkVic1k2M3dKdS9RbjljYmpVcHZ3Y3htN0hQdmhDbllsdTRwZGczMXhQYnZoQk92VnF2MDZkaE83R1h0R3lic1NscHRWcGVRK3lwNW0yOWtEN0VIMmtEcVh6WmcxbWhFeEwyM3FIQzdHSEt6RkNEZjA2VEhOMzhxanM3VWVZMWZHMWhrWjZTcll6KzVUWTBWa0hwV1NHMUNTV3FGMVVGcFpkOEpNYk1JWVNCOGJFYVd1Vk1kL3pOcDNWbjdPS3ViamhqNHpjNzJhVXRTSjFwL1NWN0ViY1FKdnhWMlpWVVhkQmszcVpsWDN0ZDkwdE93dGF2cDJkZ2U3RTJ0eHQ2b0VrK1V1Nkx2WlBUamI5N0l0N0Q3OEhOTjlGZkVEN0g1MTVVS3NpNFhaVnJZTksva1EyOEc2VmZ2UDVaM012alZpRHgrMTdHUVBzMGV3UXg1anUrQnBuc1NQc1B3V3RzY2oxdDJxamRKUHN0OGhyWlNpMU5Qc0dYaW81OWtMN0VYMkNuc0txWmZWKzdOSXZjcGVZMzlnYjNBejFPL1p4N2dmQWRUL0o2Vm5xZndhdkliTTlLeVFUV1NUMkl4SG1SbnY5d1EyZ20vZkhsOWFhc2pSUDRaM3Q4UThlUHNiOFBHOEpHRFJTT1lkeWNsRnZoMURkUnRsVzJVM3o5bFdwTitJdUxib3lEdEhYczQ5OHM2KzJNTGNmVHozN1QzdjdMRis4Ykt0TURkdnordDdCZy9pTnE5TmhUMUcwdXZ0T2wvYVFHbG9objlZWHQ2UU1kTFFmTDh2TFVaU2JmbkRobytSODRha1NySmRXTVpJU3ByTHJ4MXVrQ2NmMFVucmZVWFQ4clNweVJhN1dhZVZVaHl4T2FQU3JUVXowa2NOZE9sbHZVN1dHdlQ5aHhlblZTMG9TM3RUYjNQRko3aGlEWVpZVjBLOHk2WS84cFkyNW9jdnRUR0hTalFMRGwwaDYwYk9MT29uWHhOdGtEUTZYWGVxSTJuQVNHL2xORXVjVldPTXM5b1NEUHBZbTZsLzZjd2o1OFduS0cya3hNZFRXMGNtWWxydVkweHpLV1l3bHJuWnlvQ3J5TXZqSEZZK01jNXF3YzF1eGkzV2hKdkRpTnNqK1BEQ1dITHYzcTBva2R6ZGUyQ3JKY0ptbGIvZGFsSjU3MWFVVG40RUh6T2ltSU9id2pIVnptN3U3OUxXc3FKOVJaalhQZXJyN0hXaXdZTm1LZlBwODZiNWg5cnloK1Y1TVUzNi9JR1N6MmRUcGxWejZiUTdEOXpWc3o4eE16T1JwOSt6OThicTdmbUw3ajN2d2E2MTl5NHBsSzY3NTlDZFU5MFptck16M05OdjMzdnQvTzNuakQ5c0c5UHhoUEl2dis3ci9VR3V4Y2d5Mk13dWZWdzM5VG91MHV1NFNLL2pJcjJPaS9RNnJsdXliVGU3V0twTDM4MU5XK1Bpa25UZHZQL1d0T3FrSUNzcWl1eUwzTjIyUXVyOEVHd0t0Zk0ycGR2eEpNV2FpOUhJdFpwb3M3N0h6M2ZwemRFYVZRY01kayt5STgxdXlFeVV5bFhyN3JnVW02Rm5uTjdxakk5ejJxS09mS0EzNjdWYTNEUVBaTGl4V01xL2RWTkc5QXhHbE1JeTJhcXVmcnJJbUhTUk1la2lZOUpGeHFTTGpFbUhNUVVTYlM1bFhWM0t1cnFzSmpPZjRQSWd6Nlg4SlRlenBYZno2SzA2bmNuWHpZMWI0NnROZlFaTHkyUTlmcnkrRXdlcDZiTms4ak9CbGZldjJod1Y1MDFLOHRvTkE1SjUvSUNKOHhkT3lOdytjdnFzN0p1dm56U3Z2Sis4dWVtRzAwYjFERFNZbEZHYURKcDcrNmZwRTR0bXJwNCsrWlQ4bUNNSCsxYzBLMnRvd1lqZndJalRXT3NPUndCamM5aVk4a2R1VU95Zkh2N0QySWkyM2wzYmtXZlR4U3JMNllxTWNBalB6ZnBDSGRoVFdkYmRXVWNYOCtqZ3ZPTHNxdXY0aGliS2JPaTV3bUQzSmlsckIyVTJhTFc0eWVjWXpGR1JOVHgwMDlFeHpUWFlVdUxpNkxBcDYyZkZhTjdYK0ZrLzFwOVZiWGNrWnBqODVtNkpCNklTL1I3WWpQN29ibWxrd01yODZhNEJHZCtiVExHdTF0aDJiVHQ2V3FTc2hTMjJrQ2ZsT2w3Zll5c3NqQzFNdHI1Tll2Q2c5SVFFbmVxTU1qSzgraGpabCtiM0R4dk9WUStrU2RUN1pLLzhwbDYyK3IzZWRMdEJudDRUbUtxSmp1dVg0dkxGU0FZK1gyTnlaS1FtK1J5eFJvTzhUbnFRenh1VmtCeWprWFdtcUgyZlJwa01zalltSlY1K3loaWpsem1ja3NuUTBST3RyTXpDM2dQeUJzMGdOcFNORER0WVJyYzBKaEJ0U2ppVTZ5cHlTYTYwYmg0Yk1OcmFwTzg5Z3djTmxnWm5kL09oWGZyNThLK3Z6OXFuM3VBRVh0K05udHRqTkgzOG95WStNdk9LSjQyM3AwcktkbEw5NkFaRGNuN2xyT0VMd21lV1YzUnNYWkE3ZmZ6STVDaU5QbHB2OUJmTkNwUXZyYzdPbmJheWN2VDAwZjNOT29OV3ZzYmxUZmFteEZWYytOelpaNzE0eVhocmlqZlo1NDFOdGhuYy9WS0h6N3RxMXR5cld2SlNmYWs2VzRxeUxyY3lKaDlHUktyNHdMUkhXWnhVcUxnNXlSNklpbkljakdseEh0VE9FNTVMZlEyb0orRGs3a28rWE5uNTdNWkQ5bjc5N056VytjU0cwbEQvNFBrTEx0dlVkbDU5dHVTKytNWHp4cnE4OGgxZVY5azVqNitmZXZHOEVZZjNEMjY5V3BsUHBROHg2RU0yRzl5VmpPbTBCK3hSbmpoUEhJdEsvczd2MXlWOWIyN0orRjVIL2FDRCtWSmhZVzZ1ZGM4UWRSYVA5NS94UC9KSjZ2Nk4wZWlOdWlNZktaMlRZdlZHdlFacGZVOGpuNmZIMnNzRzZHdjUzVHJZU3pGVmV1b29IRkpzYkpMRjBQT2kzcG9jWjB1eTZudnUxRnVUbEI0alBqK0FIdnZZd0M2dFRlbHhiSXJSNkdRcFR1MUJteTFSYzhqVGt0aCt2Tk9rN25LOE9YL0dZU1pJQnl5V250VjhzYzZrOU5DazY5bGtnQ3R4d0pWZ1VnOWFMUEpiL1R3OTJ3eldwTGpZWkhTczFrQWpNY2pQZTExZVJqM0RXZld6WEpiZjVUTXBYM3BKN0dkVXZsck5FZ2UwOWxQMlp0U1BEbGVSNHVtR0hKdE9PdjU5dWtZYk1SNDJJZVUzRFBIb2w4ZXU3MG1GUDFkN0NOL2dTUExZRGZ3OXZkM2pTUExHR3hMRlJQT25lMFlJTFg5NWRQcWIrWTFDUi9yT3E5RDNlQmEzRTN2VHZpM2EycXIyRXB1dmI5ZEVKM2lWZUhTVThrQTgrdWdENWE5MHBIUzl2WENBUC9DWjJuTWxQMkkzeG5TUzN4WjVtcllWYTFqQUtyWmx4K2RrT0xwNWJ5QXF6WndiblpPVGxoK3RwR3dzYldoTFRvSlJkdmxiWE8zV3lKUVZ4U1lXMHBURkZvN09qUzBzeE16QkcvWHRvWTlIbkJJLy9tMmhkRHVSNXluK0tXOUlRcnkyVlIvblNVenl4T3Fsbm9zMHZ2NElVcUxrbm1zbGZhd25LY2tkcS9jN0ZyaXp2WTRvbnFuaFEweEozc3lVdHFSK3g4YTQ4dkE1SnBPc2k5TEphdzlmZU5UNlRKckhsTnpmZVNSZmVqWjFRTExSa3hiWkZRY3dzeU94WDcyeHlsZGlValNEdXZrTEFUTkxHZFpxSEpDbzdGZDVIbTJJd3VQMmcwNDU3aG5jNzgvdzJaVTk4S1BoeENVa0pPWU5sSTh0aTN3Z0xYbXAyMi90MmR0L2NnYm5FdGZiVWhJY0xtVTRhMjFPTzE3MVdjRk1pZVBTeGFZa09sdzJYWEdheCsyVmpGWFhUVWdiWHpVKzdjaGpmUWRqc0Rpc1BmMnFiNTNhUHhpYzFwOS9BeitzMGVDbWVLKzIzdjJhVXMwUWZKck9ZQm1QTTdzMEVndWNpbnMwUytLV3NLVU5MM1ZMbDNiK2oxeFluN2hVOVdCOWZLNm1kT3laajY1WnMrT01VY1VkajY1WnZuMXRJT3dkdjZxdWJuV1Z6MU1GWGpQQks2V2UvY3BsazByUGYvNjg5Uzl0bWxSNjN0T1gxbTFlTUNxd2FIUDFqS3NXaml4ZWZLWHFXVEhucDJCL3VWZ1d5Kzd5Nng2UjdNeUd6bzNDcE5zeXZ0RnFUZW5meHJlWTJ2dUdHeEVmY1pJb0l5RXhWZGJuK3pQOGZ1RnRUOGx2dnJUMUNuRUMvQTV1OXBWNlJzd0lwRzB0SGhPZm0zRDVUU01yQnlkSkg5U2NQU08zNTdLK0U2clRtL0ltdFk0Zk45ZW0xZllzZEErdlVrNURaZTkrNlRCbXNvcFY3V1RGVXV4MmY3NC9QOGFsZkVlUXhXQ3JtQU5SaFdNT3VrcTBXVzA0R3JhSFBIR0Q0cVE0bkJtek9yM3F5dzFiNThqcjZqaGlGZjk4Yks1MVlxN3B3OExSdE82RWQ1NU9Panl5N1pLYXZEa1RobHIxV2ttQ1l6UG1sRGVOeXBrdzNKMVYzakNyb1dKQS9zeTE0d1pNTFJrY28rWkg2YU15UjAvTnl3aGtPN0lyR21ZM1ZHVHpqUEhMSm1mSE9sT3NSbXU4MWU2eVI3bDhyb1RNa2Y3TTBibnBBL0xLbXNZRzVvL1B0Q1lrV1l3Mmh6VU9yOFZrVjNKOGVwNHJhOHpBalA1RFNtY3IvMmNEVm00TVZzN0QzRjFNQStlNU5jR2lzWGJ6bUszT2x1ajJTUEMwKzR1bmhEdi9pWkJwakNXbVowOVVyRGNwMlkyQWFZOTQwVWg3bGNXUTMwcjNIajc3NkxLc045andybkhhOUhxYkV6MjR1WGUvdkIvbk5ZdjFlNVI1cERqczZnVEp2ajNhMzJwdGRSN2Iwa1VuYnVsanJqcXltZnVjeXYyalQ3dHA3cHdiRm8zQWxuRWtlK01NdnJJNWhZV3pTNzJHT0kvRDVZN1Q4K3VXWFQyL0lLLzF5ak9seGNKN0hybWhxYlUwTGEyMHVVNWFKR3pZMnpONzk4bEY4dk1zandWWUtPQ3hGTHVMYzR0bFkxUml2Z25SWjc0VjRXYSs4ckVwMzJxeDhnbjUzZnk3QUQ2TVpGZ1lOekVsQ21jamxJQVZSVWNvZ2FvNXdrYmliVXFkRWQyU0lXQzNKVDdGOHEzNTBzaGQrWnpsOC96OGdXTUhkSE5ud1BKcUdrOUwwN2crR1RoKzlGOU5FelVzTnhJYnpGSWl4ZHhacDgrZUpUNXI3YzZhUFV0NSthb2hQSnowN0Zud2FUb2xUaHc2bEp5MHVtSjVReU51SUdMUnFKT21wNDJaa0RkazJIQzV5SnJpVEhiSGpMeXN1bUpwZGM2WVpmZk1YNXN3ZUZMaDZLYkt3U2FEQ1JHWnMzaGFXMzdUQmJYK096YVd0aFM3NjZlTVhUVGFZVExoczRXcG9hZzh2Ynh0N0lURjQ5UEw4NmNNZFdKVDRpVnVTWElsKzF4eDJjSDF0YnNUYzRveXkydUtTN0g2RFpoZGovd2M0c3NMdTFLVW1GOEo5Y0h2S2pQRmxCbkNUQ0h1cE04QUdaSFBBT0Q5eXBSbVJLWVMvSWxTQVFHVk1XRE9qZUV4U1IrNUE5SG1jVzZFQWRLMnVQSHlwNE9WenhQL3c5Nlh4N2RSM2Z1ZUdlMkxiUzIyYkhtUngvc2lXM0ljNzNGc09YRzhPNHV6T0lSc2lxMGtTcndvc2h3N0s4WUVHaWp0aFRSUUlHRUpTeEo0RkFpRVFpa3Q2WVZTU29GSFc2QjlGSEpkU29tYU5tM2F4MDBEaGVoK3o1bVJMRHRMMDlmN1B2ZWZ6cy9mbVRNelozNXpmdHZaWmp6U3hMVFFUcXJ5bUthVHhySDlERnR4emxYaXNPY1ZjZWlEdGswWlZXc2ljcFZpNENxbjlGVUZYcUd5MXJZdmQ3cnY5SlEzYkxuN0d2dWl4dklralpJM3hjVGwxUzZ0R2JrdXc3V3F0bnBadlYyUERxenNRYVBWR0dQTlNUTzVkand6Zk9OTDIyY1prak9UWXMxSnByejBqUHlNNTU3b3ZtRzVQZHVlcFRhbkVWRXZ5bDhwUnNrdzJmYk1oaldMTnRIL2tTcXBYRVJTbitjK2V5WXZiMDM4ZDduUGlCcTFsODZWdk1aK1pyQzV2bVpCRFYvUzRlcmdhenBxT3Bycmc2VzlMYzBRMWFWZDBVbFNaWmtkc1IxV0tybXNqVVVUYmZyTzFNODhZNlMxMTZwVlVvc0JmL3AxNmNsM01QU2c0dzJ1b2tJS3NFaklNUzJvSmcvazVrclZtZnpTU2txWW9rcExJanhSVktvc1FUN2EvRy90SzNhMFoyclFBMGhLajFjbGxqVFBxTnN4VDgyNkNXYTFMaXR1ZG5kVlNuWXgwMmhNVHZXaWFrbWpTK3NMbVVhcC9tZTFkelA5dS9xK1BqK2h5SndZUDJQOVBkNkNlUldaTWJLSzl0Ylo2MjkyZi9tQldrZGJVWjJhait0YzNaaTlmT21YWHcwZmtmK001OU1yV2dycU8wdGlqY25HdkhSYmRycG9pU3htaVFTRDFhUzM1cVF5bSszNTNtaTFTcFU2dDJpT2Y4a01oVW9YR3lQYVNmR1NZcERzSm1QSHlZaDNnWXdhcXFWeVFTeWFpODljdXBtelp5NEFqY1RucnFDdWFSbXA3MXJReFpmMHVucjVydDZ1M2pYZHA5cDJ0cXloWnRMNE8yY21uWW1kM1FJTHk1OHQ3cHg3UnQzRW1uSVlxVFRhVkd4SXhSb2Q0MHkwb1laWDBaU2l6dzF6UlpUTEp5U0l5bGZTdHRVU1pReTUxR2ZMekoxYUUxeTFCZmxLUzd6ajJ2SEZpM2QxMlQraE5ZSFI4RWxsVTJKMmFvSmFvVmJLVkxHcGVhVXB6VDB1MjBpY0NjTm8xWWkxZUU1Qi9oeUgxVmFpVWZBbWZVeE9WY1NPNGNpSWppUFljVUdDWFhZOHBkRStaN0RMNFZoMi9kTFZLbU95R1VNQjI1WTFHcTFHRVp0a3NtWEd4T2hVT2UxRDY3alBoV3cwYktxMjJ1N0tsTlRTcHNLcVJhV3hKbXUwSmNXWWlvK09QbGl5U2tuN0xXdFIreHhVOUpOY1VrMis2a3F2bjhYcFVxcHBqVjZ0UmUxU2JURFFGZXFiYWxyQlY5T1FJOFFwMWsxT3FVcHlTbFdTVTZybG5WS1Y1SHllMTdxMDVvd21YWFZlaWp5MmtMNHFtTlNHNWtIK1RHeW5vb08ycktqRldlOTZ5andacmJkWFJYclgwZFUyd21leWhZc0VIak5VcGV3Z09wN3hkT0t2K2U1cmUyN3R6aTlkZC91YUJUZTRWUEhwR0N5WU5JZm43bXFzWDE1cFRTaGIxcEF4MjlXVVoxV0xveTcxU09leXpodU9yUXQ4ZDAvenZMbThMandkOWVXOHhkMjE2M2E2R3NjOXMwMkZjMmRRYmEyQ3R1NUdTMmduWmVRSlY2R3pvcjVpc0VKbUZ1ajhta0FuMjh3WlJRYW9vSWhxcTRpcXNZaTFpYWh4UDN1MjBmNnduYmREU2M4aXA3MU1MbFh4Y3FrbVovczZ0aFViUlRuVlgwWkcwWS9HNUxmSitSTnk3bTA1SjVlbk9qL0liVXM2dlRiV0Y4dkhhazZuZGtyZE1kWWVidkdIRzhMU0QrMWlsWTdEMHRRak9pK1g5V3JzNTFVd2hhcGtkK2RadjN6YTF1UmI1T3B0ZGVyUkJaRHhNcFd1WXRrVzErQVJmMDN0bGdkNk50Mnh0dml3Yk52STdKVjFtVHpQNTJXMGp5NXpKQ1FucUdLdHBoaHpuRjVuVFRMWGJYOStlK0E3MTg5ckhEcXczRHkrMzlIaHFhUzErdDNvYlQyZzJFSkt5YmJqOVdWYzRlUjBaT2dza3pwcW5sS2F0K1RPdXhKdE91cU9PcXBSSGRXdGpxbFZSODlwaVF1bmlLM1FpazZiOHJuaXR1d21hd2R6TDliVnhuamNIald4TjhXM0xxN05JejBxcWVLWFBhQTIwY0dtU1oza2FDMnAyOW1vcGxXMVlGWmhLTWNPTjkvV3VtSkhSNGIxOGpXc29pbzhodm55dDJKOVREM3BSb3hTRnltY0dQMW1rRnVmcTg5YWtEV1lKYk5JL1NPTHBBTzJiMmJiQ1JwcEZpblNMSkxTTE4vbHQ1QlVraUJxS2tHNktrRTZteEJXYVFMVTlHMXR1Z3RYMHRlbmoxc05yVXcvNzUyeFM5NGlSWjU5cW5Ja1haaHByWWphRWhxeGNIWFRGV0F1bWxWanA0aW9RTFpISlFxczRrcHFDZ3VxQWNueWlyY1JPd3ZKYVZlS3lhQ1Q1dHB6RFRvOTE1R1hSTmUrTHE3cDRubHFTRGQxUHZ0MHhDOXNOZ3VTTmx1cGxycUhscnFIbGpMVk12ZkEwT1d6NXhhNmpGem53cm84aVcxVWYrbnN0UDRVVTJMZWQ3bnpjRTREcDN5NnZTMmJOa3N4RFcxMVRjVlZyY1VkRWJjU3V3NlIrZjNxZDBRUE0xWkxyc2E4akwyQ2VDVlh1NXp2SllpK2x5ZytXRWxRdkMyNm9Ga2RYOVRvcUI2aTNZT2t4QXl6eWxJMDExRWRpSGdrSFY1YjBneXFqbjlycmJxbXNjUlF2S2k5T2J0N2EydjZwRzltVlUvenpZdVB5UGFvZFJxWlRLTlRqeXhka094c3lKL1JXR2lHMDNhRVl4Y1dMQ1g3WFhHaUJlbEtDdVBwVnJyTVV3ZFlMY1dtbysyS0dNMjBjUkdEbThVMXpqOG5CYlNCOWQySzJ3cXQyYTFoMVp1cXFkckRhalpNMGZaVmhIWEMzd3ZyaUJLLzJmbDN3bnFLb3FDZ3RUU3FhVi8rSkRSRVp5aU91bExyQzdoOEUxZGc1SEpqdUZ3OWw2dm1jbFZjb1l3cjREbWIxSGphSklYWnBOYkFKclVHTmtsaE50b0kySnhhVGh0UG4yckVVM1hGMC9ZbW5qNnRpcWM2aTMrQjF4SVNPdkZjSE9uMHdVeFcrdDU5WEZzVyt2M0hGSjNTWk1ncVNXWGhqajVVRmw3KzNoU0o3R1ROMExmOGc0OE1WRlFQUFQ2RWJlVVRLWFdiRnJSNkd6TlM2amN0YU5uVUtIQy9IZmpPVGUxemRoLzNZOXVHN2M3VzhYWFZaV3ZHTzl2RzNkVmxxOGRSckVXaE0veGIwRTByZWN1bGQ3Ylh0eTlvdjY3OXlYWkZnK1FxRFpLck5FZ2FhYUJESWJPMGI1QzJPcnJsUG5DbFo1ZG1sK3BUcUFlbFVPZEpvUTZWUXIweGhXb281UVh1SEZXSlMwdUhuM29YanV2cHk4NjU0RmV2ZjFMUDZ4MGZWbXAvYjF4b1hHdjBHV1dWeGtxanBmWlhEU21LZ2paTFVOUVpZbHpxYnhyT0dKZ0M3VktZMjhYNWpraU5LZWxQZnRYVEhtL05YRDArdjZSN1hvbEZLOGN3VzJldlgxWlYyRmlha3VkYXVIU1JLNitnYTBkWGRrdE5RWUpLSnBPcHRFcE5aa1dyczlCVmtKRHY2bHE2MkpYSHhjN3JhOHVOUzdUR1o2ZWJrdzJxRkNIRmxGV1JrMXVXbjU1cHIxdFdXKzV1TGRLYkVnejZPSXVCVG1OYnJCWnpWa2xxWG5tK2tGbFl1NFNJdGxEMG84KytqK3g3aVZSeEh4QVBXUW1OTlJBZk4zRTh1OEM4NDBiYWQ2K0pzOGIxTjNnYXpIRng1Z2FQdlBONjBybWpKZjNNY0ZQVnlrMU43Yi92V3RpMXRzdlhKWE4wT2JxNlovNDRkMU5iZDdDcDg4YTRNOWFXbTlISll5Tk0rRjUwMTkxQUIreTA3dVNjNk1FbjRwRFRhYW9XZS9FbjZZU1k0ZGRSZzg5TGR0TDU2ZXBNdUtMNm93WmNseGtUS1BwNTlBTFQ4NTJXNWw2WGJVZWNpVDduMm01MW9OTSt0d1FEZHJXTVRqeGxscmRGMitES0ZpeGV1TG5PYWpkWkVrdFczckNrYStlU3drL29Vek5UM0ttS0ZrdE9hcnhLcVZiS3J6VmFqRHBkbkVhSlR2eDhQamE2RXoranFjRFZsaXJZTG1HOG1pdWJ2c1k5TDFlcFRHckpuVE80YU1ySVFidEtHamxRNjJlRS9zVDN5NzlGYXNqSzR3WEVtRlVzUlZ5eEZJbkZVaVFXUzNWVXNWUVhGZFBLVzU4WVUzd21xeVV0NWt4aXl3eHFacFZvNWpkcExUTlQ2c0cvK1VwcDFDT3ZhRHRleVJKOHY5b2dGRGdTbTNwZGFidEZPK3dLZHpOTzBaRVdORmpaakpGV3ZGcWhVY2l2VGNzMHhFN1YzM3NxNUpKcjlFaE1senNVb25MTC9xUnc4cm5jVWZwNkdaL0QwMStXWWZxUWZRQjlOSkRaVHpzYkRQVHpMSGFielI1SFkwQXZLN2MzdEJqc1oyYVZ0OFRUb1V0T3AwWWN1cndKeithY3BSL1NLUVA0cnhQRHoxSkwxRXhCeHQ4YmQ0WlZJVHRxczRTSC9CZWNVUUplWGh1eTUxS1N2L2htcENsS21KVFRsSlpodkt4U1JGbmxQNVgvRWdHL0ZyTEcwdjh4dHM5ZlRpVk5qNWtUa3dvaTVmWWxaSDVMUTh1c1dVSkxTUXZmc2p6V2ZxYTh4VVQveFN1bmMyV1V5UkhicGErc3FuYlNtY2xYbkRPalJ1Uk1IWlAybjZhS2k2TTdySXJKUjA3R1N6bUkvS2RxbzYyQUJtcTk3VUpEbEtZd1FJbXo1VjlhVjl4THRNT0V4bDNOUnV1bTJGTVZ6WWpDQkVRaDlhRU1RNnhXVWxlVUZvM3h4cGlZbU12cGtlUENUNzR2aEtZN211aG55c1hNejQ2TGZxYmd3MzZtYklEdU41Rk5UNmZYTFdEdXRhbDBVK3ltVmFzMnhjcFM1dFAvKzU0emcxQ2I1S1FzaHFKZGliMmRMUjExTFROYTdIYWhxcVNLcjFwQVVzN2t0TWlwRVJLazZsVXlRYjBZZjlRZldiM0tERUdkOG1yZE1FcjNHVmZoeUp3bjJsT042WmZ4MUVudDgzUFRFcEcySnRMSGlNNG9HMHphVk9hSWR1UXJSSGUwQVM0ZkNWRU1vUDM5ZEtaRDlpSjZyL3RjNmVpejZ2Sm83eUdQOWg3eTFIUkV3TVlNZVFZMk9PQSsremFoQXdlU0x0V0o2VktkaU8xNU5xaWlDVm9wcG9kSFdlbFMveVNkMmxWakxtN04weW1zclJoQ0tDYW5POFRIYlZMWDRaMm93Y0pGMHgzVDV2WXJLaWNuUGc2cVRHa0ppV2xHWmVlZHJKTktuNzlTQlNjNlcwcnFkc3hUeGFlaksydlNSUHF1STB2bjEyNjRlUjJmR2U2d2Z2bnBnalZ6YzVZdjVZZkRSMmpmdFpIL0llOVNwSkJpdEF2dFQ2c1NhdWhIVDBoV0ZpbDducnZHbFJhWGM0Y2dwQ1RjTGppNEVvZkx3VHNjMnBRNzhyZFVma01ia0ExSmN6bDBCZ0l0UFgyNjlldFhXSzFZTFQ0MXlienM5UHRrNHh3OSs4NWpqSkNSbkxPcXBxaTlJajIvdlcvdWtwajBtYms1dGNVMmRZd3BkbGJ2N01aVjFjazNkZVhQeWpXVkZoWFZaL08vMGV0MU1TVTVCWmFpK2tMSHZHSkxWa3BoYW93cHdaaVZhbzYzSmFWVmREckg5QmJCa3BlWG5VY2prY3FhcExDU0VwTC9kQkxKZVo2N3hSV25UZmhtV3VaZGNWdGtkeGZsSDFRRjZMUUtuUndQdjhEQk1jL1B2YmhUcHhUTHpBWVdmQkw2RXBrclp0MTBpNzE5WTEyOFBUODNVYWVVeVpSYWxVcWJYNS9SM05IZVptL0kxYWxVcUxIS1lrd3gycVNNTzcrMllLZzlXNmt6R3JXeHBsaGR2RWtyejBoYzYxNTdiVnFXeHBnRXU3U2dyTnVWUnBKTnlrbnAweHByK1hjNStpbnZZdTVtbDhHWTNtL1Z5UEtmc213cFBhQ1Bza08xK0t3b292NnJuVWVENXJkYk00eVdPS1hUWFR2bjJ1cGtvV0ZOL1l5dWZGVmNjbng4c2tHNU43ODVQN3NzUFU1dks4M05iblh3SCt0ajVFcU5zc0U1dzduQVc5czB0TUNlbThzNUZHcTVUQ1pYS3k0c2RqaUVzcmxaMlUzbEdmWnk2bVBOa0dVQVBwWkRIR1RPTVllY2ZoQXB4V2hNeVgyZTYzWWxraFR6L3RoWWplTjJnVTUwSlJYc0U3Wm83a2dLaE44RTJSSjVXZEVVL1g1RlpFN0xrakRGUkpNeld2eEFzdm5DN2FhQ09UTnk2MHN6dEZwMWJLWjlScVZ3eHgxNWJac2JtMUR2ZkVVK3J6R3JMTnZNeTBteU5XOTJvVVVYcHpjbnAxcGo5UnJGdmp1YXRzd3Z6RzlhWFdGc2FrL01MN05SLytuamY4THZWcHJoUDg1aithYnZjdDBrbGVpNDVhNDRrbXJNVDR3OVp0K1MyWjg0cEJnS1QwSlZUMzNjS3daNDdwV25udmpkS2tOS1BIcDVpc3FxckpZQ2hVRTBnVkxjS2h3clNtb1dsVnI0ajZGL09UVUNWOVhTN0N5K2NFZDRYNWFxb0U5TnNicnc0OEw4N0xyRjFkUUNidjRuSEsvNEc1dDN5bnFKV0xqMzZRZnFJWUNXcEhQSng2MEdIeXYxeWZEemMvTkY1YXVNbWc3NlQwVmNja0s0VkFuSmNZcVl6QkpIUnFhakpHT3lYSHdTdXJzOGo5VnpoVFpiUVdGNldJT3kwL0NFSnVJNlZwRkZ2MEhncURWU3owNGxUZkNGZUczc3NZWXR3ckhxTGJVVkJhVytncUdJTmlmblhweS9yc2JmRlZVNmZaK1dXWndFc0lTblYyU25VWFl6bGFHc1hKaWJyNHhMTmlla3hLbEt5elBuUkhSdXpjcEtMRjA5bzNWcFVzcE1wek9wWnY2TStNdnJmZm8rbjZUSE1tZW1vOUtabW12VlpjL3VxcEk4YUFma0x5TEZ4N0tOa3g0VVMxSmpuOHJia3AwbytNSWlpM01lMUgrdUxPeWtjTlI3ZGxDVG1DRkpSVlZXYzM1WVJtdFdoclYwN2N4WlhUT21lRTRyTGZIK2kwck15c3FUZVlqYVF5aXJHWEdiK1NLSjUyNm03MTJnL3RGb3JYZkdiY202U3hHNCtsY3VLdmhEQlF2OExRdDhyWmw1SGNQejJ3WmFjNzRXbHpQYlVUZzdQNTV1NXkrVi9YV3VyNnM0cjZPL2VlN2dvcUtDOXY3Vy9PWnlXMnBaYzFGaFUxbmFhdW85Yy9nZmNtZFFvbnJTOGgxU3pTMTVWaWdTaXZUVzU3bWxyalNpTDd4OVlzYlpHZnlNaW4zV2FrWE9GdTN0SjR4dkczbWpaWjhpRVAzdXdxcEx2YndRTll5c1FDc1YzcFZQcWU5dGN1NU1kdjAxNWNJc1I3cGVLVk9vNU5xMC9JcWM0cnJDdXRiNkFxRjZVYWx0Wmw2eVRvRXpDcVVsMjVsZWFyZlh0OVVYeWtic2M0cVRkSEZ4K3NTRUdMTmVZVERGWmVhbFppUW01cnZLODJydEZvMCtSb3N6UnIwaXhoQlRrR3pMU3JMazFGRjVzeUR2azRwRDZMc1VQVU95MHZOb2RXa3d4K25TQi9PK2FkVjkwenhvdjFzbDJ1Qk5PclorODVVLy8vRGRxRGJMS0FYc2xQR1haYkozelZ5R2UxS3B0ZGd5NHRZdW1hL1Q2ZlNkU3FuTy95cjJkRjhWQ3BOemxYS2xncGNaTEVrNmpGOVhydVp5azlKU2szWXAwSVdUWTdVcktUVXQ2Y0lmWjVUR3lYVW01dUUvNUhjcjRsSFRGeC9UWkltTlZocjFjR09XUmxiZ1MvUUpUMFdhckhwV1JjSjdMdEZnUmZXRUxGTTZRdnh1YTVZcE1VWlI0cGs1YTlFTWl4SzFaYnpWb0t5c3ptZ3BDRHQvcElVcVpjN01kZENLa2I1RWRlRzE1bFpuTWRjWDNtZS9zQ0Y3anMzSjY0aWV4Tk4zMHJZY1YycGsraFpTZi9KTlR2U1JxSmx3YmxGNDV2dkNrL0kzcFludUM4Y29IN25BdFN0dW5PUXp3dmowWG9aUGUxRjFWYUc5dXNwKzRWbEZUcVc5b0xJS2ZGNGhQS2NObmVNK1VLeEdYVjFBWW5FdXBkUFFCSFY5K0ZaVWpNbHlJN1AwMDE3QS81Nkt2Z0NmYWxJWk9YVkNWbXBLVm9JNlZtUE5UMDh2U05Kb2tnclMwL090R200NC9HeEM5b0xlcEZjbzlVYjkzNm96N0NrNlhZbzlJNlBZcXROWml5SFJtZEFaN2tuNUdsWVM0VVZpNFh1SlFCTDQ2bS9yRElVb2w1ZWdVSVpYcHNWL25xenNjbVc3UXhXWGttQkpNU2c1bzlLY25acVNhVlpwTkpic3ROVGNSSTBtTVRjMUxkdWk0Y3JwazFFWlZueEliOUFxRkdpVXZ4RFM4cEowdXFTOHRMUjhxMVpyelllZmZUUDBWMjZBVEVEYmljZm9xOUludmsxZmlkYkkwT09HdHUwL0VGOXJpNmg3d0ZsWDY2RG9iM1k2NWdIaWYxVWQrc2VJaTcwQ2pmeC9wN09YSTc3N242THZYWjVrV1ZIa2tlajdGNU5jRjBWZVJuZE1vd2N1UndxTFlqeUszcjQwS2ZPbTBMZWk2SThpcWVyK24rak01VW05YlpJMG5acE9iUkhvWlpGMHZmOURGS1NrZC85akZETW41dWZSRk91K0dvckxteVJEcmtRUFJ1aUo2V1RNTnI0SCttZzZtUTZaWi93VGRBVDBaWHhQL0k4b0pXUW5iSlFvYUttemZFV2lVT0kxaVUvL0Q5SjdpWi8vaS81Ri96d2wxVStocjAyaEovOUYvNkpvWWpQTWhLTy9lMWRNVGhBRm1VK1NTRy9vZHlSSnRqejBOc25sK05DUHNUYUVQc1k2T2ZRSHJHMmhqN0RleFk3Y0Z2cVE1Q0puUGVuR1ZYOGwzY2ovRHRhRzBMdFlKNGRPWW0wTC9RTHJYV3g5VzJnQzYzdlF3KzJXTGIvd045SUxTaVc5dUlwK3FzSVFvaCtjU0E3UlQxM1lRdlJqRThNaCtobU5yV3k5aXgyNUxVUS9lUEVzUFlMN2xuTThkdzk2bVFadytCUFd1N0MyNGNoL2Npdlk4V0cyM3NyV3QySDlPKzRlNUR5RnRZR3RrOWtSVytnMDFydENmOEQ2bnRCdlpNc2h5d2JaY3FUZm9WTm1mQ1lKLzU2YitBdDJNcWExV0xaSDB6eUpsY2xKK0ZjRVMyUW1LUzJQeXFPQVJ1ZElhV1hVY1JYNVhMWkNTcXRKb2V4dEthMGhnbnlKbE5ieUQwVHk2OGd5ZVVCSzYwbWgvSFVwSGNQZkpmOVVTc2VTUHRXdGtWOEZMRldkbDlJY1Vha0xwVFJQVkpydDRkLy9JMG1hNjZXMFBDcVBndWcxZDB0cFpkUnhGZG1sZVVSS3EwbUNaa0pLYTRoQm15bWx0ZHpDU0g0ZHNXdExwYlNlSkdoWFNla1lya1BybDlLeHBFTDNmZnFyaTNLTnBHY3hMZXBaVEl0NkZ0T2luc1cwUENxUHFHY3hyWXc2THVwWlRJdDZGdE9pbnNXMHFHY3hMZXBaVEl0NkZ0T2luc1cwcU9kSE1hZ3BKU1dnQ3FRNjJYLysrOGtnR1FMV2t3Q096V1ZmVEJDL20rREdFZnFya1FNWTRRcWtnZlNCQk5LRll4dklScHdiWW5zZWJEM0l2UlhyWHVTY2krdjZrR2Nkam5tUnc4dnl1WUYrOE9wbGVRZXdONFJqQSt5Y2VMMFhKUkFBTi9KNXdXRWI5a2FRQ3VCZUF2dE93enFrKzVCWFlHVWV4dFc5N0RzUUd4aVhRWWxyQURuNnBYdlNIQUprSEdUMzlMRHZQVkJaV3BtczYzSEV6YjVENEdkU0NHenJabExTKzRweTlPQk1FZVBjejQ3ME1ZNXU2RWc4SHI1TFAvajBNWTM1cEZJTzRFZy91NnZJazhvWmlDb0J2YU9QeVJMK1RvV29iYkhzOUU2RDBJREF2dEN3Z1duQnk3N0pRTDkxRVdCN1ZPSkF4QjZpenNTN0NLenNBNUpjZzB5MzYxak95UkpIUzBTMU5zcXVFNlhlakgwSDg0ZG9hK1l4YnYyTXd6YW1oMkhKOHRINnBoWVQ1ZmV3OGxQNVJidjRtVGZRclhoSGFtc0JQSHdSYWNReWJwRHlER0Z2dThROUFDbEVDMjJOV01uTmZNU05vLzFUNUFwN00vMWRVemU3ZjQ5MGZ3ZnoyQTNNVnZUTXhURlFjNUhVTlpHb0tTZkxKQy95U3Y1V0RvNzB6S1c5M2lQNXJ5aU5XeXIvQm5aV0xJOUgwaGd0WXkvelhGcXF6Y3htNFdzdWZYYjlQeFRCazk0aTJtWXA5cnlzRFBUK2k1bTNCNmJZMFNtVllEQktnaDRwN2dKTVNnL3o1UTRjNlNINXpNWUZ5TlBMK0RlelVvblhCa0ErYU5FSkdtSGtZREUrdGVRT3hyMGZlUUx3TFZyK0RVd0NIemhzdzFGcXdmVk1GaG81VTdtR2o2OW5YNHZ4TS84Tjg3dUdsVm4wMm0zTTI0WllDUU1zcm9aWVBTQmVMVEFaYUV4Nm1FZDUyVDFFRGExajE0YTFOdy82NjBDTktGN3Jqem9qeG5NdjA4bGtqSTVJWDFuWmVKbjdpdnMwYncrOGFKanBzRGZpODczc3ZJOTU3TFlvUC9jeFNRY2tUeGQ1ZWRpYVJ1NTB1ZWw1c1liSXgxVUZ6RHY3SVpjbkVyTVhsMnJnSXM1WHI2Tko3dUZhV3BEcVdkRjdlcWJVZHhmTFB1bXZVOHMxSzBvRFZCSlJGckhXRDN1OVA5S0M5TEk2ZElEVnBlN0xTaXJxMlQxRnB4N0orNmZIQU5VcTlieGhkbVV2cTQrb05KNElINXF6ajlWcFY3TFFmMWRjVE1hRWs1V0d4b0RZRWptWXJYeGs5RkdodEtTa1F1ajA5dmdIaHdiWEI0UzVnMzdmb044ZDhBNE9PSVNHdmo2aHk3dGhZMkJJNlBJTWVmeGJQYjJPdWU0Kzd6cS9WL0FPQ1c2aGY3RFg0eDhRaHR3RFF3TE9lOWNMNjkzOTNyNXR3b2czc0ZFWUdsNFg2UE1JL3NIaGdWN3Z3SVloWVJCWkE1NStYRG5RSy9RTStnYzgvaUdIMEJvUTFudmNnV0cvWjBqd2U5eDlnamVBZS9RTUZRbEQvVzZVb01mdFE1cGUwai9jRi9ENndISmd1Ti9qUjg0aFQ0QXhHQko4L2tHVW14WWIzUHY2QmtlRWpTaTQ0TzMzdVhzQ2duZEFDRkE1VURKY0l2UjVCM0N2d2ZYQ091OEd4bGk4VWNBekdzREYzczBlaHlDSm1UY2s5THNIdGdrOXd4QmVMSGRnSSs3dkdSSDhic2ppOTBKc1hPanVGNFo5OURiZ3VBRkhocnpia1Qwd0NJRzJVcEhjd29qYjN5L2VpNnE1WjZQYmo0SjUvSTR1ejRiaFByYy9Zb0dhOEsxcnFHbktsMEZGRUVvb2Q1U1VSS25lQS8zaU5tN3czK0NsNWZDZ1lINTNyNmZmN2Q4c0ROSXpVYnZyTDIxZ3BoWklzM1RBRzhEMWl3UHVnQ2lqRXd3RzJRMTZZTHVBMytzWmNuUU05K1M3aHdxRVhvL1E3Qi9FMlVEQVYrTjBqb3lNT1ByRHpCMDlnLzNPd0RiZjRBYS8yN2R4bTdNbnNINXdJREFrWmFYcDlXNElzSm5tdTJad0dLcmRKZ3dQZVZBSWlFUlBDMjVZMHVQdjl3Wm9nZFp0WThXYnQ3U2pBV2Y5YkFkMjdoMFdMVHF5MGR1ek1lcGFiTDBEUFgzRHZWUVhnMEt2ZDhqWGh4dFFuZnY4WG1Ub1FTN1BRTUFoaE84OU9BQ0h5UGNXQ0o3K2RmU2lTVllENGN5WExCSExUbDBhNmgrQ2VucEV2NHZjbmVsVjRqV0xGU0RmaTd2QTlhbnEvVFJBZWdkSEJ2b0czZEUzUlpuZFlrbWgrSWdGQm9jRHZ1RUExTDdWMitPaGVUWjYrbnpUQkxvYVd6QkxPSHM5NjkwSUlvZDd5RGNhR1ErU1VCSzVpVnhxNFpBREl3cGlKcXBRaU1SSnY4U3V4SWw4Yk5jU0VobWZYWHJCK0Z1djU1Q0gyM3UxK1dOaVdQN2cxZWFQaTZQNStaYXJ6Vzh3c1B4SHJ6YS8wVWp6eTlSWG05OXNSdjRrOW92emFvenBhSDQ2cWpaSnZ5c2ZRN3BKTW1yalBJNG5aWnlCMUhQSnBJMnprVzV1QlZuSHJTS0QzRERaeVcwbFgrRjJrWDNjYmVSZTdoNXlsSHVXSEplMWtaZkE5UTF3K2RrMC91OWRCZi9sNE44RC9sdkFmemY0M3d6Kys4SC9mdkIvRFB5ZkEvOS9COWUzR2I4cC9MbkhvL2pIRXZxMHZwY1VnbjhWK0RlQy8wTHdYd1ArbThGL08vanZCZjhENEg4RS9KOEIvNWZBLzAzdy96L2dTdTM2cDZuOCtRTlIvT1BBM3diK3hlQmZDLzR0NEw4RS9Pbm5QbjNnUHdiK1h3Zi9RK0QvTGZEL0R2ai9FUHgvRHY3L0FhNS9BcGR6VS9uTGJvL2lid0IvMmphWGdMOEwvRHZCZndYNGJ3Yi9VZkMvR2Z6dkF2OXZnZjhMNFA4ajhIOEgvSDhEL21kbHk1bHZxcWZ5bDk4ZHhUOE4vQjNnWHcvK0M4Ri9OZmozZ2Y5dThMOFYvTzhELzZmQS8wZmcveTc0L3diOC84dzl5M0d5Tmk0Ty9ETlliQ0hPMUVwT3JUNjdkdytXdldmVmNrNnRQRHMyaHIreE1iV0M3dXpkdS9jczIwSE84NEs0S0JXY1VuVldQYnAzNzZoU3ppa1ZQcHJkUi9PcjFQUW9QVTZ6K1BhZUh4c2JaY2ZIeHA1NmpaNVFjNXhhUGtiRzJLSlVjMHJ0dDM5OE14YVdYOHdpWFlyRlI3a3JmV01uU2d3VEtqbFJ5VjFuWFZoS1dBNmE0YTZOSWorMndHWllaR05YbGtoMU5SSXBSWWswQ2s0RGljSWkwUk5yYnpzYlBqSDJUOHVrbGhPMTNIV3hVQnFPMDBoQ1JhVFNxRGlONXZ5ZWNicnNPYzhLQU9XT1VZeHBsSnhHZlI3eWluc3FvbEZkTUVpTFNzR3BjR2ZJY05kR21sYjY5aUxQWHA5V3lXblZjcms4Y0N1dXV6V2dVbklxOWVpZVBWK01qZTFrcDVEcCtWZW9EdmVJcFpIRUcxTnBPSlgrR2ZMRzJONElzV3VsekdFK1dFWlZjbnE3c2JHMUJzT0VSazQwQ2tsVVZ3bkx4VExkR3RCeXZGWVJFWGFNNXpnZXNtdlZuRmI3eGZqMWJCbi9RcXZndEpQeWpyRVNVb0ZGaWJVcW9sVkhKRFpJWlpEdnBIS0o0ak9SZFVxT3ZxNTVTWm5adWJIL0RxSHBEVWRwT0tqUFhsNW9IY2Zyd2tKSFNhMVRjenJ0K1p0RXNjZHZPczlLOVFYTncxWmpPaFduRTUxQUZGeW5Jcm9vd1EzTTlabmt1QWZkVVZVMTBueU5WWG9WcDlmd1dHcWFxQWMxMWJDc1ZZMVVlT21zS0QxenNIRXR4MmtueFI5VDZ6aDE3SGRPdkNyc2lTSzFpbE5yd2hld25hcEdtbXlzWW5FR3BhODFxT0ZvTUozUzVUclBsT0NxWWhuRmE1cHE5Qnl2VjQ1ZHJBZTlsdFByTDVDWHgzNHdkaUpxK2NIWXkyTVhDQ3ZzQmJwL1FUeXNWM042N1JldnZ2enl5NjkrSVIwZ2VrMG9hWEpod2JQelZhVnk5NnV2dnJXVjdxbHIxN09zNjJ0ajFGeU1Wb1psMW9hWDZiSmhsa2FOM0xYclgzMFYvTmVKNTJuV0R6NTVXVnhpZUM1R2Z1SUVJWkdDYWZTY0p1NkRpVk1scjA0aHlrZ2J1WXp0MWE1bjZmVzFMR1pIWHpreE1acXF2M1ZVcCtCMHlyVnJ6NjhWbHlxV1Y3cHV3NndZbm85UlRxb0I5NldXVk5BaWlEMEZMY1pmcnhCWnp6Wi9ING5mNFBkc0ppVjk3c0FBcWNVWmJuSFhISUhFRS9yZkdlSTc4alRGSVUzN0MySmFodmFLNzFyUUtaQ2tKVjN0OURzdDRuRzV0RlZJV3lWUjlmaUdmS1JvTTRaanBKU3RxOWk2anEwYjJicU5yUmRHZWpIc3h5eXV1T2JaN0xPNGg1TFFUMnVpZGVOUWVnVkcyQXRaV2VWa0hHMTRDYitRWE1jL3pQK0NQQ0M3VjNZdmVZZHdqejVGeThtUHlqKy9GS2xLVkNYYWZmb0hKaW5tc0VqMHpIU0szV3VxaU5BSG9IT21jK2FWNXBXSit5Z2x2emlkVkNVcFQ5dGVTOThuVXNhZVNjcThqMUplMGlYcDYvYUh3K1I4dlhSbG1DclBpVlJ6NjhVMDYvQ3N3N1g3WjIrYXBMcHNrZWlaNlZUM2F2MGZ3dVNhZnhsYTducmQ5WHJEcDVTbW5wbWJleW1hZFhodWNKNXAzbkdSbXA2WXBPYkhLYlU4ZGtrNjIzbzZURzIvYkw4dlRCMUhSZXJjZWltYS8vejg1eGRxRisyTW92ZnBzZW5VSlYrb1hhanRrdE5ybGpnb0xkMFpKcEhUc3ZlWGZienNYSGRKOTZidWg3dFBMbnUvTzBocCt2MnVtWE1wb21WWXFMMW03elgzaWJUaTQwbWk5MXBaUWRkZGNvcFZ0NjQ1SGFaMUxUMlBoV205V3FRTkp6ZWMzQmdQekFIdDNIaDQ0d1RTaHpjZTl2TGVEdStkakg3cFBlYzl0NmxzMDJwUTc2YnhUYzhENDV1K3YrbUx6VFdVTm8xdjltMitCZlQ0NW05dmZuSHpKNXMvNlZQM2RZRjYrd2I2N3BMb2hmN3MvbjM5ei9kL01sQUNxaGxZTXJCMVlQL0FleElGQi80eVNBYnJRQjArbTIrLzd4d2wvL0doZFpRQ0pQQmc0QTJKM3FOVFE5aCt5dlkrSFM0Y0xneThNYngvYStaVzE5WjFvOEtvc08zNzI1ZjdqNHU1c2YxVXpMWDlqelRmOWk5MjFPem8yM0hmamxkMi9KSFN6dHFkNDR5TzczeC9WOUt1VEd5UDd5b0REZXc2dXV1SlhlL3NOb0VXN3I0SCtXcDNuOWg5WWxjWjFuK2hxZDBucnBOZlo3dXU0N3F0ak02T05URWFIWHR3VnhMV28yT3ZqWjBlZXcwNWJOZXJyM2RjWDNiOU9PaTE2eisvN2l6eXZpYWVHVThkZTIxOHpuakhEZXR2T0w5bjMwMExiMXB4VSs4dE5WOXJ2UDJ4ZllId2R2LzgvZlB2TXR6OTBkMmZIckFjRUE2c1BqQjI0SllEK3c4OGVPREZBMjhkT0h2Zzg0UHlnNmFEd3NHS2c2NkQ4dyt1UEhqNDRHc0hUOTZiZjIvRnZTMzM3cjczcm50L2R1OGY3aXU4Yi9sOSsrN1gzMTkzZitEK3grNS84ZjZQN3YvaWdib0hSaDk0NFZEWm9XV0h4ZzdkYytqeFErOGRDajVvZW5EbGczYytlUFloMDBObER6VTl0UEFoLzBNN0g3cnZvWW1IVFEvM1Byejc0YnNlL3RuREh6OWllYVRra2UyUEhIL2szR0hYNGUySEh6OTgrZ2c1VW5Ga3laRUhqMHdjelQwYU9QcjAwZUNqdDVDdkVWUG96OFFNeEFNSmdBVklCUEtBZktBQUtBVHNRQTFKSkxPQTl0RHZTQWZRQ2N3SEZnQUxnVVZBRjdBWVdBWmNBL1NHMWhJUHNCN1lHSm9nWG1BVHNCbm9BL3FCQVdBUThBRmJBRDh3RkRwQ0FxR255REN3RlJnQlJvSHRvVTFrQjdBVDJBWHNCbTRQL1pUc0E3NEI3QWZ1QU80RURnTkhnS1BBbzhCandIZlFHcndBdkk3MFQ0QTNnRGVCdDREL0Rid04vQlQ0R2ZCejRCM2dYZUNYd0c5Q0krUmo0TGZBS2VnakNQd09PQTM4SHZnRGNBYjRJL0FuNEN6d1orQXZvZnZJL3czOWtId0svQ2R3RHZncjhGbm9JUGtjK0J2d0JmQmw2Q0IzVCtnTjdnQndFTGdQdUI5NEFEZ0VQQWc4QkR3TVBBSWNCbzRBUjRGSGdjZUEvd1U4RG53TGVBSjRFbmdLT0FZOERUd0RIQWRPaE43bmZoVDZCZmNhOEdQZ2RlQW5vVi9JT2tNdnlwWVNqTytJWHJZaTFDNjdOdlMwYkRXMmE3RDFoLzRzK3o2cEpMOGs4dEJwb2dDVWdBcFFBeHBBQytnQVBSQUR4QUttVUJBZUZvU0hCZUZoUVhoWUVCNFdoSWNGNFdGQmVGZ1FIaGFFaHdYaFdSUHdyQWw0MWdROGF3S2VOUUhQbW9CblRjQ3pKdUJaRS9Dc0NYaldCRmtkK2dOWkE2d0YzTUE2b0FlNExuU09qQUhYQStQQTdmRENmY0EzZ1AzQUhjQ2R3R0hnQ0hBVWVCUjRESGc5ZEFwZWNRcGVjUXBlY1FwZWNRcGVjUXBlY1FwZWNRcGVjUXBlY1FwZWNRcGVjUXBlY1lwOEdEcFBUZ0wvQVV3QXZ3WStBbjZEY3g4RHZ3WCtFbm9mSHZBUlBPQWplTUJIOElDUDRBRWZrZk00OTFub1hYakJ1L0NDZCtFRjc4SUwzdVg0MEVsT0JzZ0JCYUFFVklBYTBBQmFRQWZFQUliUXg1d1JNQUZtSUI1SUFDeEFJcEFFV0lIazBFZWNMZlFyTGgwUWdBd2dFOGdDc29FY0lCZklBL0pESjdnQ29CQ3dBMFZBTWVBQW5FQUpNQU1vQldZQ1pVQTVVQUZVQWxWQU5WQUR6QUpxZ2RsQUhWQVB1SUFHWUE0d0YyZ0U1Z0ZOUURQUUFyUUNiVUE3MEFGMEF2T0JCY0JTeUxJTTZBYVdBOWNBdTFEdTNjQjF3Qmh3UFRBTzNBRHNBVzRFYmdLK0F1d0Z2b3ByN2drRkVXMUJSRnNRMFJaRXRBVVJiVUZFV3hEUkZrUzBCUkZ0UVVSYkVORVdSTFFGRVcxQlJGc1EwUlpFdEFVUmJVRkVXeERSRmtTMEJSRnRRVVJiRU5FV1JMUUZFVzFCUk52YjNBOWdxMzhIWHY0djN1NC91dTZDelBQNE4zUFhJemdnNDg0Y1o1bWRjYVpuM0JrS0k2Q0NWbEJSWnZ4QjFLbWpBb0lDTFk2VXp0ZzlIa1p0N1lCT0xEOUtJVmxxb0VwYm12NUtvUW1tSldsSVcyeHUwYVFoQ1UwYmtpYnBUZE0wOXpiY0gvbjIzcE9RbXhSYWV2ZDFhOTExWjF2UDRKNnpmM3hPaTV3bTkvazh6K2Y5UE4vbVhxUVdhcVUyLy90TDFFNGQxRm5ZTDJXandSSUpDeVVzbExCUXdrSUpDeVVzbExCUXdrSUpDeVVzbExBUVkvZGo3SDZNM1krYlNkeE00V1lLTjFPNG1jTE5WUEQ5UWk5MnRtRm5HM2EyWVdjYmRyWkpTeWd0b2JTRTBoSUdTd3I1NEg1NmdCNmtoMmdwUFV6TDZCRjZsQktGU2RNOWFib25UWGZjZEdkTmQ5WjBaMDEzMW5SblRYZmNkUGViN243VDNXKzYrMDEzditrS1RWZG91a0xURlpxdTBIU0ZwaXMwWGFIcENrMVhhTHBDMHhXYXJ0QjBoYVlyTkYyaDZRcE5WMmk2UXRNVm1xN1FkSVdtS3pSZG9la0tUVmRvdWtMVEZacXUwSFNGcGlzMFhhSHBDazFYYUxwQzB4V2FydEIwaGFZck5GMmhxUWhOUldncVFsTVJtb3JRVklTbUlqUVZvYWtJVFVWb0trSlRFWnFLMEZTRXBpSTBGYUdwQ0UxRmFDcENVeEdhaXRCVWhLWWlOQldocVFoTlJXZ3F3cEk5aFhGVEVKcUMwQlNFcGlBMEJXSGt4c0pSL0QyS3ZVZURUd1lYZWZyNmE0NWVRalBwVXJxTXJna3VzSkV2d00wc2JtWnhNNHViV2R6TTRtWVdON080bWNYTkxHNW1jVE1iL0tObm9tL1NYYlFjbDM1TWxmUTRQVUVyYUZOaEFCc0hzSEVBR3dld2NjRG0vR09iODQ4eE1vYVJNWXlNWVdRTUkyTVlHY1BJR0ViR01ES0drVEdNakdGa0RDTmp0bVhPdHN6WmxqbmJNbWRiNW16TG5HMlpzeTF6dG1YT3RzelpsamxUazFQOWRPUldHK2hyd1MyUjIvMTZSM0JMTUVzbTRqSVJsNG00VE1SbElpNFRjWm1JeTBSY0p1SXlFWmVKZUhDUkZGMWorMXhMeGZmWGZaUHVvbitSaGU4VUp1UmpRajRtNUdOQ1BpYmtZMFErRHNySFFmazRLQjhINWVPZ2ZHVGtJeU1mR2ZuSXlNZVlmSXpKeDVoOGpNbkhtSHlNeWNlWWZJekp4NWg4akFVN09MM3pkQVltZzFPMlVLRXdWUkpRU1dGS2RlM0ZkL3ZwYjA2RkVUM09xVEFTakttd1hJWGxLaXhYWWJrS3kxVllyc0p5RlphcnNGeUY1U29zVitFNnV6VnJ0MmJ0MXF6ZG1yVmJzM1pyOXV5elVxamtSdVZibXBYYkN3azdObUhISnV6WWhCMmJzR01UM01wd0pzK1pQR2Z5bk1selppTm5ObkptSTJjMmNtWWpaelp5WmlObk5uSm1JMmMyQm84VlRwcTdsOHpkUytidUpYUDNrcmw3eWR5OUZQekV2L3NwcmFMVjlCU3RvU3BhUyt0b1BXMmdqVlJObS95NXAra1oya3cxVk90L2Y1YnFhQXR0cGVlb25ocG9HelhTODlSRTIybEhZYk9PYlE1ZThQdWYweTVxcGlqdHBsL1FMNm1GV21rUHRkRkwxRTRkaFhhNWFKZUxkcmxvbDR0MnVXaVhpM2E1YUplTGRybG9sNHQydVdnUCt2eVpmaHJ3KzROK2pkRWdIYUloM2grbVlUcENJeFNuUkdFWWRZZFJkMWltUXBrS1pTcVVxVkNtUXBrS1pTcVVxVkNtUXBrS1pTcEU2QVJDSDBYb293aDlGS0dQSXZSUjAva1NRaWNST29uUVNZUk9JblRTeE80enNmdE03RDRUdTYvNHJsVDNTTHQ3cE4wOTB1NGVhWGVQdEx0SDJ0MGo3ZTZSZHZkSXUzdWsvUzNjSTJIeHZhM3VrYmg3Sk80ZWlidEg0dTZSdUhzazdoNkp1MGZpN3BHNGZSL2E5NkY5SDlyM29YMGZsc3dKL3JCa2J2Q2xranVEaTB1K0Vjd28rY2ZnWFNYL1RQZjUyaitnSDlLL1VSbjlpSmJRL2ZRQVBVZ1AwVko2eE5kNnJEQlVzcHgrVEpYME9EMUJLK2duaFNHSnZhcjRydHZUYVpYVXlMY0swV0NtQkU1aHl4UzJUR0hMRkxaTVljczB0a3hqeXpTMlRHUExOSzdFY1NXT0szRmNpZU5LWENkUDZ1UkpuVHlwTzIvb3pnbmRPYUU3SjNUbmhPNmMwSmszZGVaTm5YbFRaOTdVbVRmdGpJUkxJdU9TeUxna01pNkpqRXNpWTQ5TTJDTnhleVJ1ajhUdGtiZzlncFY4dW9CUDcrTFRCWHg2eDJucUlFNXdNZG9Nb2MwUTJneWh6UkRhREtITkVOb01vYzBRMmd5aHpSRGFES2sxS2ZYRit5Q1U4bERLUXlrUHBUeVU4bERLUXlrUHBUeVU4dUwrR3VWWXR1UkNqS3JIcUhxTXFzZW9lb3lxeDZoNmpOcUtVVnN4YWl0R2JjV29yZGhVaFUxVjJGU0ZUVlhZVklWTlZkaFVoVTFWMkZTRlRWWFlWSVZOV1d6S1lsTVdtN0xZbE1XbXJLZk1oS2ZNaEtmTWhLZk1oS2ZNaEtmTWhLZk1oS2ZNaEtmTWhLZk1oS2ZNaEc0ZDFxM0R1blZZdHc3cjFtRnNhc2FtWm14cXhxWm1iR3JHcG1hYzJZVXp1M0JtRjg3c3dwbGRkdUlmMllsL0pQdk5zdDhzKzgyeTN5ejd6YkxmTFB2TnN0OHMrODJ5M3l6N3piTGZMUFBOTWo0bTQyTXlQaWJqWXpJK0p1TmpKcVBEWkhTWWpBNFozeS9qKzJWOHY0enZsL0g5TXI1Znh2ZkwrSDRaM3kvaisyVjh2eWxxUGV0VDVxK2VNVnBOVXF0SmFqVkpyU2FwOVMwK1l3ekk5SUJNRDhqMGdFd1B5UFNBVEEvSTlJQk1EOGowZ0V3UHY0Vm5qS1FyTU9rS1RMb0NrNjdBcENzdzZRcE11Z0tUcnNDa0t6RHBDa3k2QXBPdXdLUXJNT2tLVExvQ2s2N0FwQ3N3NlFwTXVnS1Ryc0NrS3pEcENreTZBcE91d0tRck1Pa0tUTG9DazY3QXBDc3c2UXBNdWdLVHJzQ2tLekRwQ2t5NkFwT3V3S1FyTU9rS1RMb0NrNWd6akRuRG1ET01PY09ZTTF4eWUvQ244dlNYOG5TalBQMlpQUDBsN3Z5M2tyc0tSN0JuUnNsMy9mbzlXa2lMNlB1MG1PNmx0L3A4OHJBLzg0anYrYWhmeTZtQy9nYzlKbFhMNmNkVVNZL1RFN1NDZmtJclBkbXZvdFcwaHFwb0xhMmo5YlNCTmxJMWJhS242Um5hVERWVVM4L1N6NmlPdHRCV2VvN3FxWUcyVWFQWDhqdzEwWGJhUVR2cEJmbzU3YUptaXRMdXdpclVXb2RhNjFCckhXcXRRNjExaUZXTFdMV0lWWXRZdFloVjYvbm5jTkRoNHIwT09UTElrVUdPREhKa2tDT0RIQm5rNkVhT2J1VG9SbzV1NU9oMkNWL3NFcjRZUVhvUXBBZEJlaENrQjBGNkVLUUhRWG9RcEFkQmVoQ2tCMEY2c1B1ZnNQdWZzUHVmVUtNWE5YcFJveGMxZWxHakZ6VjZVYU1YTlhwUm94YzFlbEdqRitjWElNY3k1RmlHSE11UVl4bHlMTVA1VzNEK0ZweS9CZWR2d2ZsYlBNbGRITnhQRDlDRDlCQXRwWWRwR1QxQ2o5SnlWOENQcVpJZXB5ZG9CVzJpcCtrWjJrdzF0Q01vUloxUzFPbEVuVTdVNlVTZFR0VHBSSjFPMU9sRW5VN1U2VVNkVHRUcFJKMU8xT2xFbHczb3NnRmROcUJMQWwwUzZKSkFsd1M2Sk5BbGdTNEpkRW1nU3dKZEV1aVNRSmVIMFdVZnV1eERsMzNvc2c5ZDlpSExQY2h5RDdMY2d5ejNJTXM5a3AyVzdMUmtweVU3TGRscHlVNUxkbHF5MDVLZGx1eTBaS2NsT3kzWmFjbE9TM1phc3RPU25aYnN0R1NuSlRzdDJXbkpUa3QyV3JMVGtwMlc3TFJrcHlVN0xkbHB5VTVMZGxxeTA1S2RsdXkwWktjbE95M1phY2xPUzNaYXN0UC8zeEt5dTFCdjZyZWIrdTJtZnJ1cDMyN3F0NXY2NTB6OWM2YitPVlAvbktsL0xuSmo4QzZiK2RySTF3b0xiT2RySTNmNDlWN1BDZmNWbWlMTndUV1JST0Vua2FQQmxaSFI0QU9SWkhCWkpGM29qbVNDM3dzK1pvdW5iZkcwTFo2MnhkTzJlTm9XVDl2aWFWczhiWXVuYmZHMExaNjJ4Uk9lQTBZOUI0eWEvbmJUMzI3NjI4LzhyVUhHUkdkTWRNWkVaMHgweHNiZmJhcmJUWFc3cVc0MzFlMm11dDN0SDNQN3g5eitNYmQvekZXUWN4WGtYQVU1VjBIT1ZaQnpGZVJjQlRsWFFjNVZrSE1WNU56WW9Sczd0Sk5DTjJiT2pabHpZK2JjbURrWHpHVEp5NTZROWxJWDdUdjlwTFRmRlJIblROYnRkUUZuc3U2dkM0SldWYzlYOVh4VnoxZjFmRlhQVi9WOFZjOVg5WHhWejFmMWZGWFBWL1YzVlAwZFZYL245S2VtdmtsM1VmRmUrNzdiWnJHbmwzK2xlK2srK2dIOWtGUC9SbVgwSTFwU1dLN0M1U3BjcnNMbEtseXV3dVVxWEs3QzVTcGNyc0xsS214UllZdnRuclBkYzdaN3puYlAyZTQ1MnowWEpEem5IYVZSZWd0UHhjWFBlZG5XdmJaMXIyM2RhMXYzMnRhOXRuV3ZiZDFyVy9mYTFyMjJkVy94MDJDMjlRSGIrb0J0ZmNDMlBtQmJIN0N0RDlqV0IyenJBN2IxZ2VMbnhZcWZGck90KzIzcmZ0dTYzN2J1dDYzN2JldCsyN3JmdHU2M3JmdExMbkU5enFSTDZUTDZHM29mWFU1WDBKWDBmdm9BZlpDdW9xdnBRL1JobWtVZm9Xdm9Xdm9vZll3K1R0ZlJKK2lUZEQzOUxmMGRmWW8rVForaHo5SU5WRXFmbzg4SHhmLzY3QVVsZjArejZVYTEzRVEzMDFmcEZyclA2LzRCL1pEK2pjcm9SN1NFN3FjSDZFRjZpSmJTSS83TVk3YlZjdm94VmRMajlBU3RvSi9RU3Q5ckZhMm1OVlJGYTJrZHJhY050SkdxYVJNOVRjL1FacXFoV25xV2ZrWjF0SVcyMG5OVVR3MjBqZHJvSldxbkR1bzAvVGNXZnEvNEtiN0lyY0dGMG5CbDVIYS8zdUhYYnhVZXR6Vi9HWHpBMWl5MUNXZmFoRE5OZXJkSjd6YnAzV2Z5UFNuZmsvSTlLZCtUOGowWmZGK1dGaGRlTWYydm1QNVhUUDhycHY4VlcydW1yVFhUMXBwcGE4MjB0V2JhV2pOdHJabTIxa3hiYTZhdE5kTW11dDRtdWo0NDd2ZW5xQkRNTEFtb2hMNFlYRnZ5RC9RbCtqSjloZVlIczB2MkJIK09kdk1qTndVZlZjVjVLamd2OHExZ2FlU0h3WHNpWmNGZlJKWUU3d251L1MxL3MzSE03ajltOXgreis0L1ovY2ZzL05ET0QrMzgwTTRQN2Z6UXpnL3QvTkRPRCszODBNNFA3ZnpRVThPb3A0WlJUdzJqbmhwR1BUV01lbW9vMGpEa1ZzaXRrRnRIdVpYbFZwWmJXVzVsdVpVOTYzT2NXYkczajlqYlIrenRJL2IyRVh2N2lMMDlZb";

    private static String PT_PREF_PART2 = "StQMk5zajl2YUl2VDNDclFpM0l2YjJpTDA5WW0rUDJOc2o5dmFJdlQxaWI0L1kyeVAyOW9pOVBXSnZqOWpiSS9iMkNGWk1ZOFUwVmt4anhUUldUR1BGTkZaTVk4VTBWa3hqeFRSV1ROdlZSOC82OTdHdnErME5Pa0VuNmMzVFQrQ0g1UCtRL0IrUy8wUHlmMGorRDhuL0lmay9KUCtINVArUUxQWEpVcDhzOWNsU255ejF5VktmTFBYSlVwOHM5Y2xTbnl6MXlWS2YzZmVxM1Rkczl3M2JmY04yMzdEZE4yejNEZHQ5dzNiZnNOMDNiUGNWM3d2NWp1RFRpRDZPNk9PSVBvN280NGcranVqamlENk82T09JUG83bzQ0ZytqdWduRVgwYTBhZDFia0xuSm5SdVF1Y21kRzVjNThaMWJsem54blZ1SE9WZjA3MVE5MExkQzNVdjFMM2kwK3dwVkQrRjZxZFEvUlNxbjBMMVU2aCtDdFZQb2ZvcFZEL2xhaHB3TlEyNG1nYTR1NSs3QTl3ZDRPNEFkd2U0TzJDbnhUaThrOE03T2J5VHd6czV2Sk1UYVU3a09KSGpSSTRUT1U3a09KSGtSSklUU1U0a09aRzA1MElYd0lROUY3b0FKcVI2dXVROW5KbkRtVG1jbWNPWk9aeVp3NWs1bkpuREdjLzNkQ0c5a3k0cTNDUTdmYkxUSnp0OXN0TW5PMzJ5MHljN3JiTFRLanV0c3RNcU82MWNqSEl4S2tON1pHaVBETzJSb1QweXRFZUc5c2pRSGhuYUkwTjdaR2lQRE8yUm9Ta1ptcEtoS1JtYWtxRXBHWm82L2RuaWI5SmR0S1F3aTdPek9EdUxzN000TzR1enN6ZzdpN096T0R1THM3T0N4d29OTW5TYkROMG1RN2ZKMEcweWRKc00zUmI4eEwvN0thMmkxZlFVcmFFcVdrdnJhRDF0b0kxVVRadlUvelE5UTV1cGhtcjk3ODlTSFcyaHJmUWMxVk1EYmFOR2VwNmFhRHZ0S0R4dWp6OGV2T0QzUDZkZDFFeFIyazIvb0Y5U0M3WFNIbXFqbDZpZE92U2lrMTZtdmRSRisyZy9kZE1yMUVPOWRJRDYvSmwrR3ZEN2czNk4wU0Fkb2lHVGRKaUc2UWlOVUp5U2hVcE1xTVNFU2t5b3hJUktUS2pFaEVwTXFNU0VTa3lveElSS1UvdThxVzAwdFkybXR0SFVOcHJhUmxPN3pkUU9tOXBoVXp0c2FvZE43YkRyYk1oMU51UTZHM0tkRFJVL01lNyttT1ArbU9QK21PUCttT1ArbU9QK21PUCttT1ArbU9QK21PUCttRlA4WExuN1k3YjdZN2I3WTdiN1k3YjdZN2I3WTdiN1k3YjdZN2I3WTNieGsrZkZ6NTNqVHluK2xPSlBLZjZVNGs4cC9wVGlUeW4rbE9KUGFja1hDL05ML29HK1JGK21yOUNOL3Z4TmRETjlsVzZoMjRPLzhvVCtTVS9vUC9LRS9tVlA2SGQ2UXIvQkUzcVpKL1JQRkQvaFh2eDh1eWYwTWsvb1paN1F5enlobDNsQ0x5dCs0aDNqU2pHdUZPTktNYTRVNDBveHJoVGpTakd1Rk9OS01hNFU0MG85b1plNUdSWjRRaS96aEY3bUNiM01FM3BaOFRQemJvaDVib2g1Ym9oNWJvaDVib2g1Ym9oNWJvaDV4VS9UZTNJdTgrUmM1c201ekpOem1TZm5Nay9PWlo2Y3l6dzVsM2x5THZQa1hJWWVHOUdqQmoxcTBLTUdQV3JRbzhhMXZBbEJOaURJQmdUWmdDQWJFR1NEQy9vZUYvUTlMdWg3WE5EM3VCa3VqOXhVcUlqY1hGaGMvUHkrMitIZFoyNkhkNSs1SFo1QW1UbVI1c0ttU01LemhiczA4cXJuam5Ud3RlRFBrS2NIZVhxUXB3ZDVlcENuQjNsNmtLY0hlWHFRcHdkNWVwQ25CM2tPbzhrQW1neEkvN2owajB2L3VQU1BTLys0OUk5TC83ajBqMHYvdVBTUC84Ynp3TW5UUC9INnFrM3pOdDkxcmUrNjFuZGQ2N3V1OVYzWCtxNXJmZGUxdnF0cml5NmtkOUpGaFcvOWxsc2hpWGRKdkV2aW5hY211cWF3MVNzcy9rM2pLTjZONHQwbzNvM2kzU2plamVMZEtONk40dDBvM28zaTNTamVUZUxkSk41TjR0MGszazNpM1dUd0w4SDVLbDJrMGtVcVhhVFNSU3BkcE5KRktsMmswa1VxWGFUU1JXZCs2ckVHNTliZzNCcWNXNE56YTNCdXplLzRVNCtuY080cG5Ic0s1NTdDdWFkK3g1OTZ0T3BBNi8vRFR6MXFjYTRXNTJweHJoYm5hbkd1RnVkcWNhNFc1MnB4cmhibmFuR3U5amQrNmxGN2xwOTZ2SWh6TCtMY2l6ajNJczY5aUhNdjRsdy96dlhqWEQvTzllTmNQODcxNDF3L3p2WGpYRC9POWVOY3YwbnF3YTdYc2V0MTdIb2R1MTdIcmdyc3FzQ3VDdXlxd0s0SzdLckFyZ3JzcXNDdUN1eXF3SzRLN0hvU3U1N0VyaWV4NjBuc2VoSzduc1N1SjdIclNleDZFcnVleEs3TjJMVVN1MVppMTByc1dvbGRLN0ZySlhhdHhLNlYyTFVTdTFaaTEycnNXbzFkcTdGck5YYXR4cTdOMkxVWnV6WmoxMmJzMm94ZFYvd0d1ejZMWGQvRHJrOWhWeGQyZlJpN3VyQ3JDN3U2c0tzTHU3cXdxd3U3dXJDckJydHFzS3NHdTJxd3F3YTdhckNyQnJ0cXNLc0d1MnF3cXdhN2FyQ3JDN3MyWTFjWGRuVmhWeGQyZFdIWEp1emFoRjJic0dzVGRtM0NyazNZdFFtN05tRlhGM1oxWVZjWGRuVmhWeGQyZFdGWEYzWjFZVmNYZG5WaFZ4YytEZUhURUQ0TjRkTVFQZzNoVXgwKzFlRlRIVDdWU2YydCtQUTRQalZKL3dleDZXcGN1aHFYVnVEU0tpejZPUmJkVkhJK0tteEFoUTJvc0FFVk5xRENCbFRZZ0FvYlVNRnpGMTFJNzZTTENnLzhscjg5ektCQ0JoVXlxSkJCaFF3cVBJTUt6NkJDQmhVeXFKQkJoUXdxWkZBaGd3b1pWTWlnUWdZVk1xaVFPZWNWVkx3MWx4U1dvc0pTVkZpS0NrdFJZU2txTEVXRnBhaXdGQldXb3NKU1ZNaWhRZ01xTktCQ0F5bzBvRUlES2pTZ1FnNFZjcWlRUTRVY0t1UlFJWWNLT1ZUSW9VSU9GWEtva0VPRkhDcmtVS0VCRlJwUW9RRVZHbENoQVJWeXFKQkRoUndxNUZBaGh3bzVWTWloUWc0VmNxaVFRNFVjS3VSUW9majNOTStqd3ZPb2tFT0ZIQ3JrVUNHSENqbFV5S0ZDRGhWeXFKQkRoUndxNUZBaGh3bzVWTWloUWdNcU5LQkNBeW8wb0VJREtqU2dRZ01xTktCQ0F5bzBvRUlES2pTZ1FnNFZjcWpRZ0FvNVZNaWhRZzRWY3FnUVI0VTRLc1JSSVk0S2NWU0l2OFdmZitaY1AwblhUOUwxazNUOUpGMC9TYlRvUE1mUFA0OGl5RkVFT1lvZ1J4RmtCNExzUUpBZENMSURRWFlneUE0RTJZRWdPeEJrQjRMc1FKQWRDRktQSVBVSVVvOGc5UWhTanlEMUNGS1BJUFVJVW84ZzlRaXlIVUVhRWFRUlFSb1JwQkZCR2hHa0VVRWFFYVFSUVJvUnBCRkJPaENrQTBFNkVLUURRVG9RWkR1Q2JFZVE3UWl5SFVHMkk4Z2ZJTWhmSThoL1I1QXJFT1FLQkxrTVFWb1E1SzhScEFWQldoQ2tCVUZhRUtRRlFWb1FwT1YzSUVnTGdteEhrQllFYVVHUUZnUnBRWkFtQkdsQ2tDWUVhVUtRSmdScFFwQW1CR2xDa0JZRWFVR1FGZ1JwUVpBV0JHbEJrQllFYVVHUUZnUnBRWkFXMTAveFBWS2RLTktKSXAwbzBva2luYWh4Q1dvVS80dEVIMENNZHlMR085SGltV0NqMUxkSmZadlV0MGw5bTlTM1NYMmIxRWVsUGlyMVVhbVBTbjN4bWFkTjJ0dWt2VTNhMjZTOVRkcmJwTDFOMnR1a3ZVM2EyNlM5N1p6dk5senVodjR4VmRMajlBU3RvRTMwTkQxRG02bUcvdmRQQ3h1bG8xRTZHcVdqVVRvYXBhTlJPaHFsbzFFNkdxV2pVVG9hcGFOUktocWxJSlNDVUFwQ0tRaWxJSlNDMEpOcHV5ZlRkayttN1JLeFZ5TDJTc1JlaWRnckVYc2xZcTlFN0pXSXZSS3hWeUwyU3NSZWlXaVhpQjZKNkpHSUhvbm9rWWdlYVhoWkdsNldocGVsNFdWcGVGa2FDdEpRa0lhQ05CUk1icS9KUFdoeUQ1cmNneWIzb01rOWFISVBtdHlESnZlZ3lUMW9jZythM0dtVE8yMXlwMDN1dE1tZE5ybTlKcmZYNVBhYTNGNlQyMnY2WXFZdlp2cGlwaTltK21LbUwyYjZZcVl2WnZwaXBpOW0rbUttTDJieWVrdFdGa1pLVnRGcVdrTlZ0SmJXMFhyYVFCdXBtamJSMC9RTWJhWWFxcVZuNldkVVIxdG9LejFIOWRSQTI2ang5Ti9sdDd2RDk3bkQ5N25EOTduRDk3bkQ5NW5PQTZiemdPazhZRG9QbU00RHA1L2dmL1gwdnFYa0xudnJibnZyYm52cmJudnJibnZyYm52cmJudnJibnZyYm52cmJudnJibnZyYm52cml5WjRsd25lWllKM21lQmRKbmlYQ2Q1bGduZVo0RjBtZUpjSjNtV0NkOWxiajlsYmo1bmtacFBjYkpLYlRYS3pTVzQyeWMwbXVka2tONXZrWnBQY2JKS2JnK0xQL3UrZ09UU1g3cVJ2MEw5LzMrd1NUK0QzMHdQMElEMUVTK2xoV2thUDBLUDBtQ1F0TDN4YkNyNHRCZCtXZ205THdiZWw0TnQyV05RT2k5cGhVVHNzYW9kRjdiQ29IUmExdzZKMldOUU9pOXBoVVRzc2FvZEZKV2VoNUN5VW5JV1NzMUJ5RnRwaFVUc3Nhb2RGN2JDb0hSYTF3NkoyV05RT2k5cGhVVHNzYW9kRjdiQ29IUmExdzM1cWgvM1VEb3ZhWVZFN0xHcUhSZTJ3cUIwV3RjT2lkbGpVRG92YVlWRTdMR3FIUmUyd3FCMFdsZEtGVXJwUVNoZEs2VUlwWFNpbEM2VjBvWlF1bE5LRlVycFFTaGRLNlVJN0xHcUhSYVYxb1IwV3RjT2lkbGpVRG90S2I1WDBWa2x2bGZSV1NXK1Y5RlpKYjYvMDlrcHZyL1IyUzIrMzlIWkxiN2YwZGt0dnQvUjJTMiszOUhaTGI3ZjBka3Z2VHVtdGs5NDY2YTJUM2pycHJiUFAxa3B3dHdSM1MzQzNCSGRMY0xjRXZ5QzhMMGp3Q3hMOGduMjIyRDViYko4dHRzOFcyMmVMN2JQRjl0bGkrMnl4ZmJiWVBsdHNueTIyeitiYVozUHRzN24yMlZ6N2JLNTlOdGMrbTJ1ZnpiWFA1dHBuYzFIaFBsUllnQW9MVUdFQktpeEFoUVdvc0FBVkZxRENBbFJZZ0FvTFNpNHAvS0prSmwxS2w5SGYwUHZvY3JxQ3JxVDMwd2ZvZzNRVlhVMGZvZy9UTFBvSVhVUFgwa2ZwWS9SeHVvNCtRWitrNitsdjZlL29VL1JwK2d4OWxtNmdVdm9jZlo2K1FIOVBzK21MbnA3L2diNUVYNmF2MEkzcXU0bHVwcS9TTFhSYjRZaWRlMG5KSFlXZDl1NFY5dTRkOXU1Vjl1NlhTNHFmckw2cnNMNWtQa3I4czMvM1hiLy9IaTJrUmZSOVdrejMwbjJGZWVnM0QvM21vZDg4OUp1SGZ2UFFieDc2elVPL2VlZzNELzNtb2Q4OHUzYzlBdDVuOTY2M2U5ZmJ2ZXZ0M3ZWMmI3bmRXMjczbHR1OTVYWnZ1ZDFiYnZlVzI3M2xKVCsxcjFjaTV5cGFUV3VvaXRiU09scFBHMmdqVmRNbWVwcWVvYzFVUTdYMExQMk02bWdMYmFYbnFKNGFhQnMxZWozUFV4TnRweDIwazE2Z245TXVhcVlvN1RhVEwvTDlGL1JMYXFGVzJzTlhtVVRZS01KR0VUYUtzRkZQRWFzOVJhejJGTEhhVThScTk4Qi9jZzk4ejFQRVhXNkNQM1FUL0ltYjRFODhSU3hCNFVjaThjS2tKNG1PU0tvdzZtbmlmWkZNSVJ0VUkzTWVtZlBJbkVmbVBETG5rVG1Qekhsa3ppTnpIcG56eUp4SDVTUXFKMUU1aWNwSlZFNmljdkljbjJKSW9YRUtqVk5vbkVMakZCcW4wRGlGeGlrMFRxRnhDbzJMNzBWOVBmZ3VmWThXMGlKYVRQOUs5OUo5OUFOYVh1aEQyRDZFN1VQWVBvVHRROWcrdE94RHl6NjA3RVBMUHJUc2MyZGM3YzY0R3NINkVLd1B3Zm9RckEvQitoQ3NEOEg2RUt3UHdmb1FyQS9CK2hDc0Q3bjZFT2tFSXAxQXBCT0lsRUdrRENKbEVDbURTQmxFeWlCU0JwRXlpSlJCcEF3aVpSRHBDQ0tsRUNtRlNDbEVTaUZTQ28xU2FKUkNveFFhcGRBb2hUNVQ2RE9GUGxQb000VStVK2d6aFQ1VDZETjErcitvL3Z0MElmMUJZUko5SnRGbkVuMG0wV2NTZlNiUlp4SjlKdEZuRW4wbTBTZUxQaVBvTTRJK0krZ3pnajRqNkRPQ1BpUG9NNEkrSStneklzbFpTYzVLY3JhaytCNjFXK2oyNEYwU2ZLa0V6NWZnRDB2d3B5VDR2Wko3aVhRbXBETWhuUW5wVEVoblFqb1QwcG1Rem9SMEpxUXpJWjBKNlV4SVp0WkU5NWpvbUltT21laVlpWTZaNk9KbmJnWk44NkJwSGpUTmc2WjUwTFJlYmxvdk42azl3YWQxT3FIVENaMU82SFJDcHhNNm5kRHBWM1g2VloxK1ZhZGYxZWxYZGZwS25iNVNwMU02bmRMcGxFNm5kRHFsMHltZFR1bDBTcWRUT3AzUzZaUk9wM1E2WlNlZHNwTk8yVW1uN0tSVGR0SXBPNm40czQ1VkptQ1ZDVmhsQWdaTndLQUpHRFFCZ3laZzBBUU1tb0JCRXpCb0FnWk53S0FKR0RRQlcwekFGaE93eFFSc01RRmJUTUNXNHZ1NVRjRXlVN0RNRkN3ekJjdE13YkxJRnp6eDN4ajgvcGxQR24zUjFiUXo4dlhDMXlLM0ZWb2p0L3RuVEkzTThjOXovZk05aFRlRGExMGtLUmRKeWtXU2NwR2tYQ1FwRjBuS1JaSnlrYVJjSkNrWFNZcURLUTZtT0pqaVlJcURLUTZtT0pqaFlJYURHUTVtT0pqNW5YNFdOK1RQSGFaaE9rSWpGRCtkZ1F3SEpqa3d5WUZKRGt4eVlQTDBlOEJmZDFHOVFTZm9KTDFKLy81OTRiY0ZiNC9NcGVJblFLNytqNzdUVWdYSFZYQmNCY2RWY0Z3RngxVndYQVhIVlhCY0JjZFZjRndGeDFWd1hBWEhWVkJRUVVFRkJSVVVWRkJRUVVIdk8vVytVKzg3VlRONGptZm0vYXJwVkUybmFqcFYwNm1hVHRXRXFnbFZFNnFtK0E3UWhMNitxcWZGOTV1K0dpbmVtNWNIRlo1L2xoY082ODloL1Rtc1A0ZjE1N0QrSEQ1cmYzWUU3emJoNzFabFJwVVpWV1pVbVZGbFJwVVpWV1pVbVZGbFJwVVpWV1pVbVZGbEprZ0U3d2lPMGloTm1PempoWU5lK1Ftdi9JUlhmc0lyUCtHVm4vaU5UeHQ4UGZMMUlLSVBGNTc1MU1IWEkzUDg4OXpnd3VBUy9Vam9SMEkvRXZxUjBJK0VmaVQwSTZFZkNmMUk2RWNpcU5DOUhiNzd6dUlyOFBSMGxFYnBWNFE4MjZkazlucFZCNzJxZzE3VlFhL3FvRmQxa0o4cGZxYjRtZUpueXF2c09QT3BnUlJQa3p4TjhUUVp1Yk1RajN5akVBOXU4QXBYZUlVcnZNSVZYdUVLcjNDRlY3akNLMXpoRmE3d0NsZDRoU3U4d3AvcHdWRTlPS29IUi9YZ3FCNGMxWU9qZWhEcVFhZ0hvUjZFZWhEK3I3OGo3bEJaSjcxTWU2bUw5dEYrNnFaWHFJZDY2UUFOMExuZTVUcFJHT0xHTURlR3VUSE1qV0Z1REovKys5dlhUZWdiZElKTzBwdDBxakRCalFsdVRIQmpnaHVsM0xoQjM4N1h0MHYxN2UzNk5rUGZ6dGUzUy9XdG1LVVozQ25sVHFsTG9DV3lMN2dzK0MrcUwzNG04SlRxVDZuK2xPcFBxZjZVNm92c3krdFhYci95Wi83TzZHeHBIajdiM3huOU9yM0I3L25kZVg1MzN1bWZnZzdxeUtDT0RPcklvSTRNNnNpZ2pnenF5S0NPRE9ySW9OZDA3czhSL3VxOVV2LytFeVM3VlQvb083M05kM3FiS2hPUjR1Y3lTbjNIdU84WTl4M2p2bVBjZDR6N2puSGZNZTQ3eG4zSHVPOVlmTjlDTlFlcU9WRE5nV29PVkhPZ1d2K3I5YjlhLzZ2MXYxci9xMlh3QWhtOFFQK3I5YjlhLzZ2MXYxci9xL1cvV3YrcjliOWEvNnYxdjFyL3EvVy9XdityVlhWTVZjZFVkVXhWeDFSMVRGWEhiSllSbTJYRVpobXhXVVpzbGhHYlpjUm1HYkZaUm15V0VadGx4R1laMFlsUm5ZanBSRXduWWpvUjA0blltYzF5V0NjTzY4UmhuVGlzRTRmTnhOdk14RitaaVhkdzZPTm00bTFtNHEvTXhEdTQ5WEY4alFXZkQ1WUVmeDdjVHcvUWcvUVFMYVdIYVJrOVFvOVNSZkJKYm5WeHE0dGJYZHpxNGxZWHQ3ck9jWDM5ZWljUGNtdVFXNFBjR3VUV0lMY0d1VFhJclVGdURYSnJrRnVEM0JyazFxRDVxekYvTmVhdjVpMCtENjduMEJvT3JlSFFHZzZ0NGRBYTdpemx6bEx1TE9YT1V1NHN0WFBQdzVEcjdOdnY0c2psOXUxM3NPUTYrL2E3ZUhLNWZmdWR5TDJGWFpIN0NvMlIvY0Zsa2U3Z1BaR2VZS2FLbHBqUysra0JlcEFlb3FYME1DMmpSK2hScWloeVc4OTJuczcvdWE2TVBTcmRvOUk5WG4zU3E4OTQ5Um12UHVQVlo3ejZqUDd1K1E5dW1tNXBHRG56THNIelZMWDN6RHNGejFQUlh1a1lqUlRmNDFOTVI0VUtLbFJRb1lJS0ZWU29vRUlGRlNxb1VFR0ZDaXBVY0srZVovVThxK2RaUGMvcWVWYlBzMmZmVWhpK2t6cmt0Sk5lcHIzVVJmdG9QM1hUSzlSRHZYU0FCbWdJVXc3VE1CMmhFZks4d3FFY2gzSWN5bkZvZ2tQSE9YU2NROGM1ZEp4RFJUWU1jdWcxRHIzR29kYzQ5QnFIWHVOUWdrTUpEaVU0bE9CUThiOHYvNmVTY1JHSHJwS0s4NlhpSWc1ZEpSSG5jK2dqSFBvSVN2NUlPdnFMMnk2WUlSMHpwR09HZE15UWpoblNNVU02WmtqSERPbVlJUjB6cE9OREp2NjlKdjY5LzhmZlprekk3V3NvT2tsNW1xSnBPaDc4WjY5NDFDc2U5WXBIdmVKUnIzalVWSDR1Y3BOWGM2cytmcjB3ckg4amVqY2N1Vk51djBIZkN1NlAvREQ0ODBnWkxRbithL0J4dlJ6VnkxRzlITlhMVWIwYzFjdFJ2UnpWeTFHOUhOWExVWDBjMDhjeGZSelR4ekY5SE5QSE1YMGMwOGN4ZlJ6VHh6RjlITk8vTWYwYjA3OHgvUnZUdnpIOUc5Ty9NZjBiMDc4eC9SdlR2ekg5RzlPL3NkL3lON05wYnFTNWtUN3orYXV6dlNNcnpvazRKK0tjaUhNaXJuZVRlamVwZDVONk42bGY1NSsrb2U3d2EvR0d1azdseDFSK1RPWEhWSDVNNWNkVWZremx4MVIrVE9YSFZIN01GRStvUHFQNmpPb3pxcytvUHFQNmpPcnpxcytyUHEvNnZPcnpwdmk0S1Q3T2hUd1g4bHpJY3lIUGhUd1g4bHpJY3lIUGhUd1g4bHpJY3lIUGhmeHZ5Zmt4TGh6andqRXVqSE5oZ2dzVFhKamd3Z1FYSmt6eGtiTnUxRnNMR1pRNmJoWXk2SFRjWkU2Y3JyNVM5WldxcjFSOXBlb3JWVitwK2tyVlY2cStVdldWcW45QTlidFZ2MXYxdTFXL1cvVzdWYjliOWMycWIxWjlzK3FiVmQrcytsN1Y5NnErVmZXdHFtOVZmYXZxVzFYZnF2cFcxYmVxdmxYMXJhcHZWWDJyNmx0VmYxTDFKMVYvVXZVblZYOVM5Y1gzTkh3MmNwTkp2aGxidityM3R3WVg2K2ZOWnpiVGJCbThXRjl2UHJPWlpzdmhJM0w0aUJ4V3EvWlJGOHVuSXE4VTZpTzloVk9SQThHZHdidFVmMGoxaDFSL1NQV0hWSDlJOVlkVWYwajFoMVIvU1BXSFZELzQ2M2RYZUJVeDMzM0FWNC81NnJIVDJkbm1xMnp6VmJiNUt0dDhsVzIreWpaZlpadXZzczFYMmVhcmJQTlZmc0RESVI0TzhYQ0loME04SE9MaEVBOWpQSXp4TU1iREdBOWp2dU1MdnVNTHYrT2xlSUtISjNoNGdvY25lSGlDaDhYci9FNGV2c0xESGxWOGdvZHY1K0VOUEx5SWgzTjQrSFllM3NERGkzZzRSNVdQcWZJeEhsYndzSTJIcFR6czVkOHRwNThoNjFSZXAvSTZsZGVwdkU3bGRTcXZVM21keXV0VVhuZm1SajdYay9oWjN0dHBDKytrMysyL0lISFc2WkdMNHAxN3Mrb1BxTDVYOWFWbnFwK2wrdk5WZjgrWjZtZXAvbnpWMzZQNkoxWC81T24zQTcvL1AveSsvT1dGZXBYV3E3UmVwZlVxclZkcHZVcTNxSFNMU3Jlb2RJdEt0L3pHdTFpYlZOcWswaWFWTnFtMFNhVk5LbTFTYVpOS20xVGFwTkltbFRhcHRPbGMxNkNLL2tKRmIxUFJQTlg4aFdxS2QrMDhWVHdkWEsrS2NsV1VxNkpjRmVXcUtGZEZ1U3JLVlZHdWluSlZsT3ZaTjgvNXllSk5oVGFWdEtta1RTVnRLbW5UczMxNnRrOGxIU3JwVUVtSFNqcFUwcUdTRHBWMHFLUkRKUjBxNlZCSmgwbzZWTktoa2pkVThvWkszbERKR3lwNVF5VnYvRi8wdnRGbGRWT2hTLy82OU8vYU03ZnBGMVI3c1dxWG5ibE52NkRpaTFXOFRQK1c2ZDh5MC91dzZyZWEzdXROYjRmcHZUTDRJQ2NtT0RIQmlRbE9USEJpZ2hNVG5Kamd4QVFuSmpoUnBINC9GL3E1ME0rRmZpNzBjNkgvSFBmcWVmcDVIaGY2dWREUGhYNHU5SE9obnd2OVhPam5RajhYK3JuUXo0VitMdlJ6b1IvTnA5RjhHczJuMFh3YXphZlA5amNkS242L2FtZXA5UDJxbktXeWc4RVhTeTdoMFV5NmxDNmp2NkgzMGVWMEJWMUo3NmNQMEFmcEtycWFQa1FmcGxuMEVicUdycVdQMHNmbzQzUWRmWUkrU2RmVDM5TGYwYWZvMC9RWitpemRRS1gwT2ZvOGZZSCtubVpUOGYvRlpoV3RwalZVUld0cEhhMm5EYlNScW1rVFBVM1AwR2Fxb1ZwNmxuNUdkY1gvYjBiYVNzOVJQVFhRTmlyK2xQdEZ6N1cvb0Y5U0M3WFNIdit1clJBcmVZbmFxWU02TWZ4V1QzNjM0L3RYT0pqbFlKYURXUTVtT1pqbFlKYURXUTVtT1pqbFlKYURXUTVtT1pqbFlKYURXUTVtT1pqbFlKYURXUTVtT1pqbFlKYURXUTVtT1pqbFlKYURXUTVtT1pqbFlKYURXUTVtT1pqbFlKYURXUTVtT1pqbFlKYURXUTVtVC84L0E2MmkxYlNHcW1ndHJhUDF0SUUyVWpXNVNEaVk1MkNlZzNrTzVqbVk1MkNlZzNrTzVqbVk1MkNlZzNrTzVqbVk1MkNlZzFNY25PTGdGQWVuT0RqRndTa09qbkR3TlE2K3hzSFhPUGdhQjE4cmVkbVR3MTdxb24wMlpQSEpvZmg1NHE5ek5NZlJIRWR6SE0xeE5NZlJIRWR6SE0xeE5NZlJIRWR6SE0zOVQrYnVQRDdLOHQ3NytEMHpNZUtRYUZVRUtUNjFyYnZXdW1FWE4ycHJyWjRlcFdpMWVqekhwK2ZVNm9PUW9zWGRnZ3FJYTkyM3RyWnFWY1Fsb01hbFlSTUZoaTFBQ0FUR2tJRE1aRUlJbVF4Ykp1QjJuL2NNb3c5dDZUbVAvNXpYOC9MMWNVSnlYOHY5L1gxL3YrdTZrbGtvbXFOb2pxSTVpdVlvbXFOb2pxSTVpdVlvbXFOb2pxSTVpdVlvbXFOb2pxSTVpdVlvbXFOb2pxSTVpdVlvbXFOb2pxSTVpdVlvbXFOb2pxSTVpdVlpRDdpckIvRVFIc1lqZUJTUDRYSDhQdXloZUEvRmV5amVRL0VlaXZkUXZJZmlQUlR2b1hnUHhYc28za1B4SG9yM1VMeUg0ajBVNzZGNEQ4VjdLTjVEOFI2SzkxQzhoK0k5Rk8raGVNOS9vZmhLaXVjcG5xZDRudUo1aXVjcDNrcnhWb3EzVXJ5MXVMZTd0UENNck9JblNjVlFodDFRanQzUkMzc2dqdDZvUk9IenBrYmpGdHlLMnpBRzFybUlkUzVpbll0WTV5TFd1VWhobll0SDlnb09qUHdMaHFNS0kzQVZyc2F2Y1EwS3AvMjd6U05wSGtuelNKcEgwanlTNXBFMGo2UjVKTTBqYVI1SjgwaEd2aFF1aSt5TmZiQXYrbUEvOUVVLzdJLysrSEs0TVBLVmNGSGtRSHdWWDhQWGNSQU94aUU0RklmaC8vZDN4ZmxKT0RzeUJPZmhmUHdVRjdpL0MvRXpYSVNMTVNwc0ZLTkdNV29VbzBZeGFoU2pSakZxRktOR01Xb1VvMFl4YWhTanhzZzkyandRWnJnNnc5VVpyczV3ZFlhck0xeWQ0ZXBNWkdaUUdYazNLSSs4aDFtWWpUbVlLOEx6TUI4THNCQi9tOXNYT05OZUZONVEybnRYbGZiY1ZkYWcrY1hYSEQwZDlJdE5EVTZNdmV2MG1Rb09pcVdEODJPdHdUNnhUUERsMkRyL2JnLzZ4TlpiaFR0OGIwTndZbkIrOFJQTllpakRiaWpIN3VpRlBSQkhiMVNpOExsbmUyTWY3SXMrMkE5OTBRLzdvejhLbjR4VytGeTBBL0ZWZkExZngwRTRHSWZnVUJ5R0MxeDdJWDZHaTNBeENwK21OaHEzNEZiY2hqRVlpM0c0SGVOeEIrN0VQY1hYd2FiVmlyUmFrVllyMG1wRldxMUlxeFZwdFNLdFZxVFZpclJha1ZZcjBtcEZXcTFJcXhWcHRTS3RWcVRWaXJSYWtWWXIwbXBGV3ExSXF4VnB0U0t0VnFUVmlqVGxlMVArUjVUdlRma2ZVZjVkaXZlTnpRaStYdndFdXlacU5sR3ppWnBOMUd5aVpoTTFtNmpaUk0wbWFqWlJzK2tML0RVd1M4MGNOWFBVekZFelI4MGNOWFBVekZFelI4MGNOWE9SbndUN1JvYmdQSnlQbitJQzdTL0V6M0FSTHNZb0svSm8zSUpiY1J2R3dKNk13cHNwdkpuQ215bThtY0tiS1p6OW4zcUdVdXdDSHQ3eHFzRHpTNjhLUEQ4MlBEZzdpUHBPNFhuK2ZZTXlQKzlWUERkZFduek4zZG5Cbm1wamhkcFlvVFpXcUkwVmFtT0YybGloTmxhb2pSVnFZNFhhV0tIbFlWcWVwK1ZoV3A1WGJEbEF5d0ZhRHRCeWdKWUR0QnlnNVFBdEIyZzVRTXNCV3ZiUjhsZGE5dEh5VjhXV2xWcFdhbG1wWmFXV2xWcFdhbG1wWmFXV2xWcFdhbmxvY2RkNHFVZTd4aTgwMjBPTHYrUGEwWEpnVVlNakMzOFRDQzdrdFJaZWErRzFGbDVyNGJVV1htdmh0UlplYStHMUZsNXI0YlVXWHF2bnRYcGVxK2UxZWw2cjU3VjZYcXZudFhwZXErZTFlbDZieDJzemVHMEdyODNndFJtOE5vUFhadkRhREY2YndXc3plRzBHWDgzanEzbDhOWSt2NXZIVlBMNmF5bGRUK1dvcVgwM2xxNmw4TlpXdnB2TFZWTDZheWxkVCtXb3FYMDNscTNucVpWYTl6S3FYV2ZVeXExNW0xY3VzZXBsVkx3dSt5L05kbnUveWZKZm51enpmNWZrdXozZDV2c3Z6WFo3djhueVg1N3M4MytYNUxzOTNlYjdMODEyZTcvSjhsK2U3UE4vbCtTN1BkM20reS9OZFB2Sm0yQ2FiYTRLOTdRZDY3QWUyMlE5c3N4L1laait3elg1Z20vMUEwbjZnMjM2ZzIzNmcyMzZnMjM2Z1c1WE9xTklaVlRxalNtZFU2VlhCYm5JeExoZmpjakV1RitOeU1SNE1FYlVWb3JaQzFGYUkyZ3BSV3lGcUswUnRoYWl0RUxVVm9yWkMxRmFJV2tyVVVxS1dFcldVcUtWRUxTVnFLVkZMaVZwSzFGS2kxaXBxR1ZITGlGcEcxREtpbGhHMWpLaGxSQzBqYWhsUnkxajVXcTE4clZhK1ZpdGZxNVd2VlNSYlJiSlZKRnRGc2xVa1cwVnlqVWl1RWNrMUlybEdKTmVJNUJxUlhDT1NhMFJ5alVpdUVjazFJcmxHSkZ2L3UzY2Vzdkx0YStYcmJlWHJiZVhyYmVYcmJlWHJ2YXVWajRZL0tUNGo5bCtDQTNqK0VobHdBTjlmSWtLemkyZUZyUDFGMXY0aWEzK1J0Yi9JMmw5azdTK3k5aGRaKzR1cy9VWFcvaUpyZjVHMXY4amFYMlR0TDdMMkYxbjdpNno5UmRiK0ltdC9rYlcveU5wZlpPMHZzdllYV2Z1THJQMUYxdjRpYTMrUnRiL0kybDlrN1MreTloZForNHVzL1VYVy9pSnJmNUcxdjhqYVgyVHRMN0wyRjluaXA0WCtBVS9pVDNnS1QrTVovQm5QNGprOGp3bDRBUlB4SWw3Q3kzZ0YxWmlFeVhnVnIrRjExT0FOekZRemQ3MXo3ZGpWc3pNNHRZdFR1emkxaTFPNzdGd3pzY0tyaUMrZ2FJcWlLWXFtS0pxaWFJcWlLWXFtS0pxaWFJcWlLWXFtS0pxaWFJcWlLWXFtS0pxaWFJcWlLWXFtS0pxaWFJcWlLWXFtS0pxaWFJcWlLWXFtS0pxaWFJcWlLWXFtS0pxaWFJcWlLWXFtS0pxaWFJcWlLWXFtS0ZwNGQ4c1VSVk1VVFZFMFJkRVVSVk1VVFZFMFJkRVVSVk1VVFZFMFJkRVVSVk1VVFZFMFJkRVVSVk1VVFZFMFJkRVVSVk1VVFZFMFJkRVVSVk9xUUk2cXFWMCszeVhoKzd0NGppeFYyNmphUnRVMnFyWlJ0WjZxOWNIanNqMGwyMU95UFNYYlU3STlKZHRUc2owbDIxT3lQU1hiVTdJOTlRVjJWNFZYT25mSzlrN1ozaW5iTzJWN3AyenZsTzJkc3IxVHRuZks5czdJNGJMckNCeUpvL0FOSEkxdjRoZ2NpK053UEU3QVFKeUliK0hiK0E2K2k1TndNazdCcVRnTmcvQTluSTd2NHdjNEF6L0VtZmdSenNMWitDZjhHUCtNYzNBdUJtUFgxZWp2M3g5dGxKd2FqVnR3SzI3REdJekZPTnlPOGJnRGQyTEgrNkQxcUVZOXFsR1BhdFNqR3ZXb1JqMnFVWTlxMUJQNXZVcnpCenlKUCtFcFBJMW44R2M4aStmd1BDYmdCVXpFaTNnSkwrTVZWR01TSnVOVnZJYlhVWU0zTU5OYS9pN2V3eXpNeHB4ZHZ5dkMzem5wZ3ZEZlZjR0xTbi9yT3FYMGQ2NVRWTUdsUlhldDU2NzEzTFdldTlaejEzcnVXczlkNjdsclBYZXQ1NjcxM0xXZXV6cTVxNU83T3Jtcms3czZ1YXVUdXpxNXE1TzdPcm1ycy9UY3N5N3U2dUt1THU3cTRxNHU3dXJpcmk3dTZ1S3VMdTdxNHE1eTdpcm5ybkx1S3VldWN1NHE1NjV5N2lybnJuTHVLdWV1Y3U0cTU2NXk3aXJucm5MdUt1ZXVjdTRxNTY1eTdpcm5ybkx1S3VldWN1NHE1NjV5N2lybnJuTHVLdWV1Y3U0cTU2NXk3aXJucm5MdUt1ZXVjdTRxNTY1eTdpcm5ybkx1S3VldUx1N3E0cTR1N3VyaXJxNWRQbC91aTd1cjY3OS81NnFnakx2S3VLdU11OHE0cTR5N3lyaXJqTHZLdUt1TXU4cTRxNHk3eXJpcmpMdkt1S3VNdThxNHE0eTd5cmlyakx2S3VLdU11OHE0cTR5N3lyaXJqTHZLU3U3cXhWMjl1S3NYZC9YaXJsNy93RjA1N3NweFY0NjdjcVUxZHRRdTNOVVlYTVJkcTdockZYZXQ0cTVWM0xXS3UxWngxeXJ1V3NWZHE3aHJGWGV0NHE0RzdtcmdyZ2J1YXVDdUJ1NXE0SzRHN21yZ3JnYnVhaWk5Mm1JQmR5M2dyZ1hjdFlDN0ZuRFhBdTVhd0YwTHVHc0JkeTM0QjYrc21DOVM4MFZxdmtqTkY2bjVJalZmcE9hTDFIeVJtaTlTODBWcXZrak5MNzZ5NGdGbnFBZnhFQjdHSTNnVWorSHh3bTlHT2VVUGVCSi93bE40R3MvZ3ozZ1d6K0Y1VE1BTG1JZ1g4UkpleGl1b3hpUk14cXQ0RGErakJtL2d6Y0w3SVFRblVQZ0VDcjlYek4rMUZGNUw0YlVVWGt2aHRSUmVTK0cxRkY1TDRiVVVYa3ZodFJSdW8zQWJoZHNvM0ViaE5ncTNVYmlOd20wVWJxTndHNFhiS2J5T3d1c292STdDNnlpOGpzTHJLTHlPd3Vzb3ZJN0M2K1J2WFA3RzVXOWMvc2JsYjF6K3h1VnZYUDdHNVc5Yy9zYmxiMXoreHVWdlhQN0c1VzljL3NibGIxeit4dVZ2WFA3RzVXOWMvc2JsYjF6K3h1VnZYUDdHNVc5Yy9zYmxiMXoreHVWdlhQN0c1VzljL3NibGIxeit4dVZ2WFA3RzVXOWMvc2JsYjV2OGJaTy9iZkszVGY2MmNVVTdWN1J6UlR0WHRITkZPMWUwYzBVN1Y3UnpSVHRYdEhORk8xZTBjMFU3VjdSelJUdFh0SE5GTzFlMC8vZW5qdi9SMVdGWCtidXIzd2I5YmY3K1N2N2VXTXJmMDByNWUxcnh1YmZYZnNIM1JQeC8vUjFnQTNjMWNGY0RkelZ3VndOM05YQlhBM2MxY0ZjRGR6VndWNE1kWmQ2T01tOUhtYmVqek50UjV1MG84M2FVZVR2S3ZCMWwzbzR5YjBlWnQ2UE0yMUhtN1NqemRwUjVPOHE4SFdYZWpqSnZSNW0zbzh6YlVlYnRLUE4ybEhrN3lyd2RaZDZPTW05SG1iZWp6TnRSNXUwbzgzYVVlVHZLdkIxbDNvNHliMGVadDZQTTIxSG03U2p6ZHBSNU84bzhkK1c0SzhkZE9lN0tjVmVPdXhxNHE0RzdHcmlyZ2JzYXVHc1pkeTNqcm1YY3RZeTdsbkhYTXU1YXhsM0x1R3NaZHkzanJtWGN0WXk3R3Jpcmc3czZ1S3VEdXpxNHE0TzdPcmlyZzdzNjFJSzhYV2JYTHQvOWRLN3Z6OE44TE1EQzRqT0JDcWVmQTRMSzZFbGhPdm9EbkJYK2EvVHM4S3JvajhPcll1ZUVzMklYT0ROZFZIeEgxTUxuSEV3dWZjN0JaRjdJQnIyang0WWQwWUg0TmdiaHJIQ3AxZ3UxWGhnOUoxek9TYXUxYk5hcU9kalQxWjJ1N25SMTNuaHJ0T2cwNXByb0VJOFgrZDRsdnI0TVZiZ3JYQlc5TzF4bG5IVkJYTXR1TGJ1MXltalZyVlhHRld0ZHNkWTlITzBlam5ibHd1SVliYTVzTThaS1Y3YVpVWjBaTFRLalJkSGkvWVR6Q3UrZ1VIcGY2Y3JTKzBwWEJyMzBQVkcvRTgxaWtsbE1Nb3RKeHBocGpKbkZaOFB0bysraCtoNnE3N1MraCtxN1I5OWI5YjFWMzRVNUoxMmRqRjN3NmRiWVJaOTJsMzdiZEV6cHQwM0hsSjQ1Tkx2WTA4LzE5SE5qenRmVHo0MDdQM3BXOEwvMGNLNGV6aTI5ZHZPZTBuTWlqaXk5SThVSnBYZWtPS0gwamhRWEIxL1MwOVY2dXRxY3R1cHRudDZ1MXRzOFBWMnVwOHYxVktHbkIvVjBuWjcyMGN1QmVqaFFENlAxY0gyd3UxYXp0Wml0eFFvdFZyaGlIMWZzNDZjUEIzdFJZd3MxdGtTSGhpOUVyd3dmaVE3RGNGU0ZXLzdHSHpmengzUjYzc3dmMDdYZUttNURlT0lpRE9XTEszbGlHSWJqVjhhNUlQemdjMjlFWE45aExrUEU5aEt4dlF4VnpwQ1hGSDgvZGFpZk52RG9FTis5S0h4ZmIzVjZxOU5ibmQ3cTlQYnUzOFIxejFKYzl5ejJ2TVo5REFscnRFMXAyNlB0eDlwK3JPM0gycTdSdHBlMnZha2NMNzdPNEZLUGhkY2E3UGo3Y3FMWWVxeDV6VE92ZWRHaHdRSG1OaytyWDFCMk8yWHpXby9UdWwvcHQzSDlpbi9ESFI0K1h2anJkSEhlVnh0NzYrYzk3R2o5SGEyWGxGN1RmMmF4NVk1V0QydGxmWGYxSWxjdmN2VWlQOTNQVC9memswYzU5VnJ6dmk3Y0hyMmV2OGNGdTBWL0czWkZINVZEVDRUWjZPL2taZFJQUDR5T0MvTkJ4UCszaThQMXN1c0dkMzRqeG9XTjBmRjZ1VU12OTRidDBjZk04SW53SXkwLzh0MWY2MnNrcnRYRDlYdytLbHluLzlib0kyRXU2blREdTcrbStFaGM2N3MzdVBKRzNCTFdSMi9GYlJpRGNjRiswZkhoNU9qOXJuc2cvQ0Q2SUI3Q3czZ2lyRFZXYlZCbWxxdGR0Y3hWYTZPUGhadk1lMXk0Mlp4NnpQalg0VGFqYkRQQ2gwWW8zTTFhRHMxemFONFZlYjFzMDhzMk03N09MQXYzTjByYjBXWTVUaFVZNzN2M2kvWmpZUzc0c3I1bTZHdUdHYmU3Y3A0KzAvcE1SMi95NzFIaGVxMDJ1NE5PZDlEcERqcmRRYWZ4UGpIZURPUE5NTjZtNkgzaFhIZXkwcDJzZENjcjNjbEttdFRSUFdFdWplYlNHQXd3VXNaSUdTTnRNYS8zakpZelFwTVJOaGloeXdoZFJ1Z3lRcGQ1VnBqbjYwYnBORXBuMU82ZjB1M21YVzJrZGlPMUc2bmRTTzFHeWhwcGsvdVpiclExUmxzVDdHRzB2Tkh5eGJ2ZjBkdFd2VzNTMnlZOTVmVzBWWTIrUVp4dnhEaGpqS2ZzUGE2ODE1WEZLM3o5R0o3Z2k5K1ZvdHV1ejNhdE9yWHFOT3NPcys0dzZ3Nno3aWlPODltTTlmOTNNMzNDTmIrVHk0VlliaXoreHJKY241MzY3SFFYbmU0aTQ1cE8xM1FXWTlmOTEvUFQ0ejNGbWVmZGExNUZ1RTc3Y2ZvY2I5eDdmSDB2SDl6UFhZOEZjZFh2TXdXdUZhMVJHQjMwZC9YSFpyalJERGU2ZXB2NzNDaDZXN1g2cGhua3pXRHI1KzRwZUxEZ3NVMW1zRW5MdVovbnp0WWdWdnpaT0l5WEdYc1phNEd4RnJnNjYrcXNzUXJaMVdSbXV4bHZuZkhXRmJXOTM3OGZrUnVQY3RZVHZ2YzdLMWlzbUs4NzduQmJNREE2U1F6ZkRQcEdwNGRMb2pQd0xsZStaMjZ6d2x1anM4Tm5vbk13MTFxME1QeE5kSEU0THRyZ21rYVBLNUNqOGtaczBVZDNlSDgwSDA2SjltQzd2UHNrL0dNc1VIZkt3dmJZN2g1N29kTFhmY1B1V0Qvc2o4TnhCSTRPdDhhT3NaSWZGODZJSFk4VE1GRE4vWTVWOStUd2paaDE5dk5QT3JvdytGTHNaMEZGNlhWSXg2cjByNnF3eDZyMHI2cEo3VmF1NTl6YkpMeEgvVm5oY25leDFWMXNkUmZiby9NOExyU2VMMWJObHJ2TFJvOHIwTTdENjBXeFcvN2x1YjhISDRhYjNNRUhaditCMlg4UTY2L0NIaE9telhLTFdXNHh5eTB4dXprelRBWDdsMFp0b3Q4Mm82NDBhdGFvV2FOK2JOUldveWFOdXRLb1c0eTYwcWdyalpZMzJtYWpiVGJTWmlOdE50Sm1vMnczU3JkUnVvM1NiWVJzTU1JSWwwZGZDbTgyeXJib20rR3IwYitFbzZLMW1LNHV6TUM3ZUUvMW5CdE9pQzdrbmdiL1hpN2JrK0V2bysrSDA2Sk5XSVZtdEdCMWVFVjBqY2VVNjlMaDI5RldYMmV3RHUzQnRkSDE0VlBSRGw5dlFHZDRYVFRyc1FzNXU0U04yT1RyemRnU1hoamRxbExrM1dFUHRvZW4wUzRSL2NqUFBzWW40VnZSVHoyR0loaEJGTEZ3Tm1mOFBMYWJyOHZEKzJKeGU0TGV2cTZ3MzY4TUcyTjdoVmZIdm9TOXNRLzZoRi9qbklHY001QnpCb3JGME5pWHcydGlBL3pzQUJ3WVhCNzdtc2V2NDZEd3U3R0RjWWlmSCtyZmgrSHc4Q0JPT3loMnBLKy9nYVBEcjhlK0dYNmYwczlTK25wS1gwL3A2N251VERIOVEreGJydmsydmhQZUcvdXV4NU53Y2pnbGRvckhVM0dhUGNzZzgvaWVyMDhQTHlrOTYzR05OZlJwYStpVlZ1Q3ZXbjIvR2h0YTNIKzhHeHdsU210RmFhMG9yZVdQdFR2NW80VTMybmdqS1dKcmVTUEpHMGtxdDFHNWpicHRmTEthdWkzVWJhRnVocUpwZmxsTndUYWVXYzB6cTZtMm5rSWJLYlNSUWh2ZDhVWjN2TkdkYm5TWEtYZVpkcGRwZDVsMmx6bDMxdVdPV3R5RjJoaWNMSzlPbFUrcjVkTHE0REEreS9OWE8zKzFtM21qbVRlYStWSXpMK1JSWGNuTks4eTRzWlJISzh4NkJkOTh4Y3liekx6SnpCdk1kcjdadHBscGt4bTJpbVBHTEJ2TXNzRXNHOFN0dDNpMWlWZWJHU2ZOT0duR3kwdDU5cjRadjIvRzc1dng4a0tlbVcxRGNMZ1pMVEtqUldZMDM0emF6T2l0VWxhbnpXaVIyYVROSm0wMis1ck5CclBaWURacjZiaUdqbTEwYkN2VnFDUWRWNXJkQmpxdXBPTktEdHhXcWxOcHMweWJaZG9zSzh3dWJYWnBzNnMzdTlWbXQ5VHNscHJkVXJQN2dKN05aampYRE5OV3R1ZGtlV0ZtcytYcUhNeVQ1UXZOWUxGS3NOd0sxK2h4UmRncVM5dnRFQThzZk02cU5wdnB2bzN1ZWJybnRmbVFyNXI1S1dOZjlCemxYOElrZkhiRnJQRFhuOWZvZWZZWUM4TW5DL1ZGSEo4VHgrZnNONTZ6dzMySklwTTRaekszdldsT2YvSHZXc3lpd0d5S3pNRTg2aTIwT2kwTzM0a3VWZnQyeFBRZGMzeEgvY2lwRlIxcVFFNmU1c1J3cVpnMWlWbVQrYTAwdjVYQlFVYTYzMGkzR2luOVY5VnBsdFZzdG5Wc0R1YjUyY0l3Tk1yV1VoM2Nhb1N0UnBoa2hCb1ZwOTRvQ2FNa2pQTEx2NHJCUWVGSUk0NlUxUnZFSWlVV0tiRm9MV3BQYzFsWWVPN0ljV1p6SEMzZnBlVmNpclE3b1FUaEo2TDdpZWgrUXVsdlcrc0xXczV5aDdQZDhSeTBCK1hpdVVrOE40bm5wbUt0dmMzZFBPWk9acnFUaDl6SlExeFh6M1gxK3A0Ym5XM0ZuSU81NGJQdTZDT3VxM2RIYmU3azJuOVFhKzhvMWRwcGF1M012Nm0xdDduem1wMXE3YTA3MWRxUjNEdHlwMW83VksydDM2bldYcWpXTHQ2cDFrNy9CN1YyWktuV1BrdmRXMHUxdHFwVWE2dlUyaXExdGtxdHJhTDh5WlEvaS9KblVmNHN0WGE4V251OVdsdWwxbGJSOEdLMXRrcXRyUktWTTBYbFRGSDVWN1cyU3EydEVwMHpSZWRNdGJaS3JhMFNwUitvdGY4dWE2cXBmQnVWYjZQeWJTSjNqbHI3VzdXMlNxMnRra0ZqMU5vcXRiWktKdDJpMWxhcHRWVnE3YitKOEZscWJaVW9GOTRqOGNMU3MvU3JSZnQ2dFhaL3RYWi90WGFKV3Z0S2NJWG8vVkwwZmkxNnI0bmVTTkViS1hyTFJXOTVzWXE5SnhQbmhuTkVib1BJTFJlNWRTSjNqY2pWaVZ5ZHlOV0pYSjNJMVluYy9TSlhKMnJ2aUZxZHFOV0pXcDJvL1p1b3ZTbHFkYUpXSjJwUGlGcWRxTldKMmx1aTlwYW8xWWxhbmFoTkZyWDU2ayszaUwwallta1JxeE94T3RHcUU2MDYwYW9UclRyUldpcGFiNGxXbldndEVLMG5SS3RPdEthTDFqTFJxaFd0V3RHcUZhMWEwZnFoYU4wdld2ZUwxdjJpOVk1b3pSV3RXdEdxRmExTFJhdFd0R3BGNnhMUnVrUzBKb3RXcldqVml0WmRvbldYYU5XS1ZxMW8vVmEwNW9qV090RnFGcTFtMFdvV3JVZEZhNWxvMVlwV2JYRnY5bDJQSitFVW5JclQ3TnNHbWNQM2ZIMTYrTHhJRFJhcG1TSlZlUDNqdDBSblNYQ282THdvT3Erb0ZBMXEwaVpSV2l4S0w0clNpeUxUSlROZmtaa3JaT1lLRldPbUtHVlZqRGZWcGF4SXJWYzEzbFExM2hTeDUwUm5qcHhhTHhLTFJXQTJoWk55SXlVM1VsUmVKL2VYVXpFbi81ZkwvK1hVbkUyeGFrcFVVNkxhTEF1dldEaXZzR1lIaC9ESVVoNVp5aU4xL1BFWEk3Znl4MUt4L3I2WU5vdHBzMWd1Tk1vSFJxazNTcjE0dmgwdHZJb3hjSkl1bzlQdUhudWhRcDJxdEtyMXBWay83SThEZzIvUXZvZm1UVFJ2b3ZYN2RNN1FlUkdkRjlGNUVaMGJhZnUrREVqU2NwbjllcnZWcDdCT0Rndy90dmVmVk54ZkRxUFFFeFI2Z2tMdm1lZXRWTmh1WGhQTXE4YThhdHg5UjJtZk1NbWNKcG5USkNlQ1dlRnZ0Wnl0NVd4MzJLUGxmVnBOLzN6bnZtTkZYUDNacmtLTG5EcTlFVnYwMXgyKzVNcUVLeFB1ZTdXclgzSDFSUGU5Ull1SldreTBqMDY1dXRzdUpTOHVQZmpRRGlOd1orVmhoNnNXdVdxUkZUSWxGN3JEOTEyVmNWWEdWZTM2Syt5RDYxMjUxWlgxcnF5M2dpZjVZalU2a2VPSmpkamlGTElkSDZya24xaXR5OElWV2pXTDc0dmlXY3QxSzdtdXNEYTlYWHdtVmVGWlZBWDM3YTJIclhyWXFvZXRwZjNBR3VPdjBWdEhjYWNTY0UxWmNmeDF4bDlYZXFYYk1jNGJ4MXRYRzYycmpVVlZPdlRTb1pjUDlOS3NsMDY5ZE9ybEE3M2s5REpGTDRYN25hS1hLVlRKK2VsRzdOalBmVlRRcjdqbkw3TnVsZmI5b3B2MDA0SitPVmRzeEk1WmJ0NXAxOUxwbmxkb3VVckxqOXp6SnExWGFiMHFpTHJUd3UvbFRuQ1dURnJWT3ZYem9aN2o0Wlpnejg5WDhaUU02eXpHY3FsZVYrdDF0YXRXNmZFTnRXZHJTZnMzOVBoR3JQRDVzSVdXYzdUOGk1YWJ0WnlpWmR0bnA1WkNKZHZaTDFwTW9mOFF1di9DbWJUd0dybzl0TitrelNadE5taXpvWFN1eWhscHZYWTU3WExGTzU5cWxJUVI1dTJVYTh2ZGNiVVcrV0p1eFl1L3JicTlGTjMzalRLMCtQdTRRdXRYaTdXZzA2a3FwMDVzeEJaVmZYdlJHMWxqRlhSWXFmVlZwZDkxTGRINjR1TGZFZmJSZXJYV2I3dkRSZXI4Q3IyOFk4YVRkOHFsdDB1WjhVY2FGU3BLWWQvOVJ6UC9vMTdmMGVzOWVwdFVqUElxWTY4cTduMjc1ZGYyY0paV1dYTllwVVZXaXl6M0pDbVg0dmR1K1p1M1R2VGdRL1Vnc05MdVVHVzhLOGZ6MjMvdzIzK0laN2NaNWZYWkEydTgvVXAvdThOamNKeDRIWThUY0dLNFhaM29vL3IzTjlabmU3Umo2SDJjK3o4ZUorREU0bXYxMmd2dlNCREVYZDNsNm0ydTduSjFseXQ3WE5uanlwN1M2YlpEbGR3WWZDVVNEZWRHWWlqRGJpakg3dWlGUFJCSGIxUWF2OElKdGsvNHBPcTNRZlhib1BwdE1NSmtJMHhXQWJ0VXdQVXE0SG9WY0oyNjFxYlNiVEJLRnlXZnBlU3p4V2YvRnA3NVc4alovZFRScmJFK2NyQ3Z1K3FIL1hHNHZEa0NSK01ZUFIzSFNjZmpCQXowdlJPRHZkVFJ3dGs1by9lc1BMWnJDSTV6NXl2ZCtVbzF0WS9kWTMrN3lJUDQ5bUFjVXZ3dHhMYS9POS8vaTdsZEdtNmdWWVdyKzFLdUgvYkgvejBUZEJUSFBOblZnNmk2aDc1enhkOFhIR1NmY2pDTzg1UGpjUUpPTkViaGN5NTd1V0p6NmZjZVMveDBpWjh1S1kzNG9CRWZESGIzMDY2L2luQWhHcDlWb2NJb2kvWFJiWlNWUmxtNTh4NVdQNFg1WklLSTJYVUgrOFlxZmRVbmZNcjh1OHkveS95N3RIdEx1N2ZjZWZkT0VjbTZsdzN1bzB0RUNxK0pmRmxFWGhhUmZVU2s4THY0WkxDM25sNzdtOWpPME5NTVBXM1IwMVk5YmRYVE5qMXRLY1YydTU0VzZtbmhaejBWSTdEVy9OZHAvYjdXNzJ1ZEw1MzhkdloweG4xOHFJZHVpdlcxMisrSC9YRTRqc0RSenQwN1RnRWZGRFZwMFYrTC9scjBsOVBmY3YwdDA5OHkvUzNqaVk3aWMwd1BpdjN2NEtEZ3pPQ3k4SGZCTDNFNVJvWlBCemZSL1diOEJxTXdHdW53aWFBVkdXd3V2bWZiQThHSCtBZ2Y0NVB3Z2NqaFlYM2tDQnlKby9BTk9DdEd2b2xqY0N5T3cvRTRBUU54SXI2RmIrTTcrQzVPd3NrNEJhZmlOQXpDOTNBNnZvOGY0QXo4RUdmaVJ6Z0xaK09mOEdQOE04N0J1UmlNb1VHL3lNenduY2k3NGR1Ujl6QUxzekVIYzhQcGtYbVlqd1ZZcU1JY0hPd1RMZzcyQlpjRis2RXYrdUV3SEk0amNDU093by94enpnSDUySXdmb0loT0EvbjQwSmNqTXZDSnluK0pNV2ZwUGpvNEpyd2o4RzF1QTdYNHdiY1pDZHhNMzZEVVJpTlE0TUg3UWNld3NONEJJL2lNVXpBQzVpSUYvRVNGbUFoNnJBSWk3RUU5VmdLSjdaZ0daYWpFVW1rdzlmRitYVnhmcjMwanB2ekEvdjJvQnQ1OUdCN09GbnNKNHY5WkxHZkxQYVRneEZCV2JCM3NCdktzVHQ2WVEvRTBSc1ZxTVNlT0Nub0c1eU15OEtiNlhBekhXNm13N1YwdUpJT1Y5TGhTanBjU1ljcmd4djFjRk5ZUllzcVdsVFJvb29XVmNIWVlLOWdIRzdIZU55Qk8zRVg3c1k5dUJlMXdWZUNLVWlITjdtem05elpUZTdzRVhjMjBaMU5kR2NUM2RsRWR6WXgyR2JHMjhOUjdtNlV1eHZsN2thNXUxR1IzNGVOa1QvZ1Nmd0pUK0ZwUElNLzQxazhoK2N4QVM5Z0lsN0VTM2dacjZBYWt6QVpyK0kxdkk0YXZCRTJSbysxamgvblREM1E0eUNjRmQ0Y1Bkdko3Y2NZNHQ5RG5jbXZESWRIaDJGNE9MejBkK0JyU244SHZpWjJqZFBTdFU1UDljRnVzYVZCbjlpeTRHdXhSdnZORlNwMzJ1NjBWVDNOQklmSDJqeXVLN3lybk1jTjZsQTB0c1RWYVJFcGZGVjRSVW0vb0Z0RUswUzBRa1FyUkxSQ1JDdm9VeUVlRlNKYVVmeXZFbnRpbjdCSnBqVEpsQ2FaMGlSVG1tUktrMHhwa2lsTk1xVkpwalRKbENiUjMxZjA5LzFDNzExOVdYZ0ZwMXpCS1ZjRS84ZWVhaWl1eERBTVJ4VitoUkc0Q2xmajF4Z1pEdVdxcTdqcUtxNjZpcXV1NHFxck9Pb01qanFEbzg3Z3FETTQ2Z3lPaW5OVW5LUGlIQlhucURoSHhUa3F6bEZ4am9welZPRXpxSnZsWUxNY2JKYUR6WEt3V1E0Mnk4Rm1PZGdzQjV2bFlMTWNiT2ErL3R6WFh5NTJ5Y1V1dWRnbEY3dmtZcGRjN0pLTFhYS3hTeTUyeWNVdXVkZ2xGN3ZrWXVHemM2L20yS3M1OXVvditON1JqM1AzQk82ZXdOMFR1SHNDZDAvZzdCczUrMGJPdnBHemIrVHNHOVhzcEpxZFZMT1RhblpTelU2cTJVazFPNmxtSjlYc3BKcWRWTE9UYW5aU3pVNnEyVWsxTzZsbUo5WHNwSnFkVkxPVGFuWlN6VTZxMlVrMU82bG1KOVhzcEpxZFZMT1RhblpTelU2cTJVazFPNmxtSjlYc3BKcWRWTE9UYW5aU3pVNnEyVWsxTzZsbUp5TS9DZnBHaHVBOG5JK2Y0bi9xL1NCbmhqWFdpbW5XaW1uV2ltbldpbW5XaW1uV2locHJSWTIxb3NaYVVXT3RxSW5VQmZHSU0xMWtNWllVbmlOaGozc2NCcUx3Ykk1QkhuYzhvK01XR1gydWpENjNtTkdYT00xY2hxRXlmS2ZNamxZVlgrTjVxdXkrVW5hZktydXZ0Tys0UHpiU2lYMXErRjVzUnJCbjdGMFZZSW05eTFLN2lXVkJQNW5lSWROanNaWDJNanV5ZlRmWmZuRHgwL2M2ZkgrRGF2aGVVQmIrTk5nTjVkZ2R2YkFINHVpTkNsUmlUK3dWbml5RG0yVndzd3h1bHNITk1yZzVPSW1iVHNZWHl1RGdrdUNYdUJ3amcrOEUxOGlrYTNFZHJzY05oVG9mSEJYY2pOOWdGRVpqYlBqRFlCeHV4M2pjZ1R0eEYrN0dQYmdYOTRXbi9CZXZwZC9GWjFLR3p3WlRzTUQ1WnlIcXNBaUxzUVQxV0lvR0xNTnlOQ0tKZEhCZTBJb01OZ2ZIQjF2VXg2M29SaDQ5MkI0Y0VueUlqL0F4UGdrT2NYNVk3UHl3MlBsaHNmUERZdWVIeGM0UGk1MGZGanMvTEhaK1dPejhzTmo1WVhIa1MrRXprYjJ4RC9aRkgreUh2dWlIL2RFZlh3NmZqWHdsZkRGeUlMNktyK0hyT0FnSDR4QWNpc1B3azNCU1pBak93L240S1p3M0loZmlaM0R1aUZ5TVM0TnpJajhQem8vOGUzQkQ1RCtDSDBaK0Vad1N1U3o0V1dSVU9DVXlHcmZnVnR5R01SaUxjYmdkNDNFSDdzUTkrbm9nWEJwNUVBL2hZVHlDUi9FWUhuY0NQemE4SURvUUo0VnJvNE04L3NEaldjRkYwYk9EbzZJL3hwRHdJbG1TbGlYcDZORGd3dWlWd2VIUllSaU9LdDhyUFMvQTN2cjc5dGFueDZhRUUySXp3bk5pcVhDK2RheFByTlV1dnMxcG90MlpiSDB3SU5aaGZkd1E1aVA5ZzdKUHR3VzdvUnk3b3hmMlFCeTlVWUZLN0ltOVBtMnd4azJ6eGsyenhrMnp4azJ6eGsyenhrMlRJVFV5cEVhRzFNaVFHaGxTSTBOR3k1RFJNcVJHaHRUSWtCb1pVaU5EYW1SSWpReXBrU0UxTXFSR2h0VElrQm9ac3BjTTJVdUc3Q1VUS21WQ3BVeW9sQW1WTXFGU0psaWZjRHZHNHc3Y2lidHdOKzdCdmJqdjAxbkJBMkdEYkJnbUc0YkpobUd5WVpoc0dDWWJoZ1dQKzlrVCtBT2V4Qi94Snp5RnAvRU0vb3huOFJ5ZXh3UTdzUmN3RVMvaUpienMrNjlnRWlialZieUcxMUdETi9BbTNzTGIrQXRxdzdHeWJtd3cxZGZUTUIwejhBNW00ajNNd216TVFRSnpNUS96c2NDNEMxR0hSVmlNSmFqSFVqUmdHWmFqRVN1MFdZbWtyOS8zMklSVmFFWkwrSGF3R212d0FkWWloZTEyT2gvaUkzeU1UNEplTW5lWXpCMG1jNGZKM0dFeWQ1ak1IU1p6aDhuY1lUSjNtTXdkSm5PSHlkemhNbmU0ekIwdWM0ZkwzT0V5ZDdqTUhTNXpoOHZjNFRKM3VNd2RJWE5IeU53Uk1uZUV6QjBoYzBmSTNCRXlkNFRNSFNGelI4amNFVEozcE13ZEtYTkh5dHlSTW5la3pCMGhjMGZJM0JFeWQ0VE1IUkg1TjNPOU5EaXQ5SmtLMzVLOVI4bmVvMlR2OXlLWGg4c2lRem4vV28vWDRYcmNnQnR4RTM2RFVlWTFHcmZnVnR5R01SaUxjYmdkNDNFSDdzUmR4ZWRDam9qYzYvRzN1QS8zNDRGd3JLd2ZLK3ZIeXZxeHNuNnNyQjhyNjhmSytyR1JOMTN6RnQ3R1gxQ0xLWmlLYVppT0dYZ0hNOE9NZFRoakhjNVloelBXNFl4MU9CTkpxQ0M3ZnFYT3VzZ2lMTWFTY0owSzAxdUY2YTNDVEZGaGVxc3dVMVNZdmFKRFB1MVJXZTVXV2U1V1dlS3F5ZDJxeVlXcXlZV3F5VW1xeWFtcXliV3hhZUhVMkhUTStMUXpOak44MDdxN012WmVPQ2MySzd4UGxSbW53bXlMWlp6aDI3UnB0MGF2dDlaMmhIOVNaUXFmY0RrMkhDUnJCOG5hUWJKMmtLd2RKR3NIeWRwQnNuYVFyQjBrYXdmSjF1bXlkYnBzblM1YnA4dlc2YkoxdXN5cmxYbTFNcTlXNXRYS3ZGcFpORmNXelpVTjFiS2hXalpVeTRacTJWQXRHNnBsUTdWc3FKWU4xYktoV2paVXk0WnFXVkROOWExYzM4cjFyVnpmeXZXdFhOOGFXeHcrRjZ0WEk1ME1ZdzNoTDJMTHd0clljbmUzSWx4bFI5RmluUjc3YVhjd0RyZGpQTzdBbmJnTGQrTWUzSXNId29TN0dlSnVocmliSWU1bWlMc1o0bTZHcUQwSnRTZWg5aVRVbm9UYWsxQjdFbXBQUXUxSnFEMEp0U2VoOWlUVW5vVGFrNkRBdVJRNGx3TG5VdUJjQ3B5cjlpVFVub1RhazFCN0VtcFBRdTFKcUQwSnRTZWg5aVRVbm9UYWsxQjdFbXBQZ21xWFVlMHl0U2VoOWlUVW5vVGFrMUI3RW1wUFF1MUpxRDBKdFNlaDlpVFVub1RhazFCN0VtcFBndHFEcVQyWTJvT3BQWmphZzZrOW1OcURxVDJZMm9PcFBaamFnNms5V08xSnFEMEpxZzlXZXhKcVQwTHRTYWc5Q1ZFWUl3cGpSR0dNS0l3UmhUR2lNTWFlL3kxNy9yZnMrZCt5ajMvZVByN2FQcjdhUHI3YVByN2FQcjY2OUg2MnkrM2xsOXZMTDdlWFgyNHZ2eno0Tkh3cUNNT25JZ0VpNFZNaWVySDk0V0pSZlZ4VWI0NDFmUHFwcUQ0anFtZmJLNzRwc3RlTDdPK0RqVTU2L1ozMCtqdnA5YmR6NmEvbTlYZlM2MjlIMXQ5SnI3L3pYWC9yVDM4bnZmN1dwb0ZXd295Vk1HTWx6RmdKTTFiQ2pKVXdZeVYwdXNRUk9CSkg0YVJnZ05QZUFDdGhvNVd3MFVyWWFDVnN0QkkyV2drYnJZU05Wc0pHSzJHamxiRFJTdGpvdEhlNjA5N3BUbnVuTysyMU9PMjFPTzIxT08yMU9PMjFPTzIxT08yMU9PMjFPTzIxT08yMU9PMjFPTzJkN3JSM2hkUGVGVTU3VnpqdFhlRzBkMFhwVTJPUGNPSTd3b252Q0NlK0k1ejRqbkRpTzhDSjd3QW52Z09jK0E1dzRqdkFpZTgvcWZzT3NLaXVkZTFWTnIxS0V3RnhRSnJZaHFJaUlsaUlYVlNzc1NCZFVBUUMySWdhSmNZYW80bGRFeVgyRnNUZUMzYU4yRFVJZ2czQmdpSVdzT0RNZmZlYTBlTkp1U1k1NTl6ei84Nnp2c1Zldlh6bGZRZG1kQURqY3dEamN3RGpjd0RqY3lEZmtNYlE2RnZRNkZ2UTZGdlE2RnZRNkZ2UTZGdC84QzNYUG1COVB0Q1lQR2hNSGpRbUR4cVRCNDNKZzhia1FXUHlvREY1MEpnOGFFd2VOQ1lQR3BNSFRjbURCZ3lHQmd5R0Jnd0c2eXNDNnlzQzZ5c0M2eXNDNnlzQzZ5c0M2eXNDNnlzQzZ5c0M2eXNDNnl1Q3RuU0d0c1JDVzJLaExiSFFsbGhvU3l4NUNhYjlTdTBEYmZHQnR2aEFXM3lnTFQ2VUVWM0trU1FrSFNSZEpEMGtmU1FESkVNa0l5UVQrVE5XaUNvOWtFS1JlaUtCTllDRjVZS0Y1WUtGNVlLRjVZS0Y1WUtGNVlLRjVZS0Y1WUtGNVlLRjVZS0Y1WUtGNVlLRjVZS0Y1WUtGNVlLRjVZS0Y1WUtGNVlLRjVZS0Y1WUtGNVlLRjVZS0Y1WUtGNVlLRjVZS0Y1Y0w3ajRQM0h3ZnZQdzdlZnh5OC96aDQvM0h3L09udy9PbncvT253L09udy9PbS93OEpxZ1lYVkJndXJCZStmQnhaV0M5NC9EeXlzSTFoWWM3Q3c1cXdybUZrUG9rQWt5RU1reUFNVGl3Y1Q2d1ltMWcxTXJCdWlRaDVMSkp5dEp6VlpGbUZzRi9MZFNFZlVrZXlvZWdzN2huUkN2WkQ5ckxaaFJhUUpld0gyVmdsY1c0WDBWajJJV3hFejNsaTlpbnVwTjNKdkpCOGtQM1VXMzBlTXdlSWFJNXBzaHBXZTRPY1JOUzRTUFZqbU5yQTRIVmltQ3RIbE1wamM1MW9teDdYdjIzQkVtVkorSHhIbUFjb2ZxaCtCUzBtSUNqcEl1a2g2U1BwSUJraUdTRVpJeGtnbVNLWkladXE5d0tjRndLY0ZpRTdaaUU3WmlFN1ppRTdaaUU3WmlFN1ppRTdaaUU3WmlFN1ppRTdaWUZkSDNuMWozRi84ekZnQnNGQUJzRkFCc0ZBQnNGQUJzRkFCc0ZBQnNGQUJzRkFCc0ZBQmNGQUJjRkFCY0ZBQmNGQUJjRkFCY0ZBQmNGQUJjRkFCY0ZBQmNGQUJjRkFoY0ZBaGNGQWhjRkFoY0ZBaE1Fc2hNRXNoTUVzaE1Fc2hNRXNoTUVzaE1Fc2hNRXNoTUVzaE1Fc2hNRXNoTUVzaHNNazFZSk5yd0NiWGdFMnVBWnRjQXphNUJteHlEZGprR3ZCQ052QkNOckJDTnRqSExtQ0NmR0NDZkxDSzNyaVJNc1Q3YXNUNmJOeENHV0o5TjhUNmFsNnB1c2Vyd0VCZXF2WDRLMVVWZjYwcTRHL1V1cnhhVmNyZnFvTzRDdVZxZFMxSlIzVlAwbFcza2ZUVWVwSytxa295VUJWSWhtcGR5VWhWS2htcmd5UVRsSnVxd1UwUWc3UGhxUi93SzZRUmwvLy93UTd3WUV2Z3daYkFneTJCQjFzQ0Q3WUVscDBQeTg2SFplZkRzdk5oMmZuL3ozL0hsdnlabU9QaWZ4WW9nd1dYd1lMTFlNRmxzT0F5V09WQ1dPVkNXT0FsV09BbFdPQWx0bEo5VC94RmhPWjM1YmRnV2JkZ1VYbXduQ1g4SEttRCtIWVRWdk0xWWJpWmpiQ0w2YkM0UGFRNTMwc0c4WVBFbHg4aXRtaTdreDhHK3p0QzNQbFpFb0orSWZ3Q3JPY2lDZVNYaUFXL1RMd3h4ZzFZbmlQUjU3ZFJlb2Q0d3Q1Q1lHOXUvQjVwaDNFUGE5OHZyWStaY3RRYjBINk9tRE1iZFVOaGxYdUpLY3BPNGVtYzhKbS8rV1lJR2srQ2NMUG5pQmR1TlFBemRJSCtkTVI4bWhJdmFGY1ZTdHRBdS9aQ3V4NkliL0Y1U0NobXVVTWM4QlFvM3ArdGliYXVtRS8rMzA5TFNFTzBhSVNuY3lRSXU3RkNuU1AySlgvV3A1ODZsNmNSZjZ6MXNOUVNFWkNoNUNTZWZrYnIvZXBDWU5seVBCWGlLWUdZNE9rMW5rNFNDNkNCSUtDQklLQ0JJS0NCSUtDQklLQ0JJS0NCSUtDQklJd1VCRFFRQkRRUXhIc1RhOTRmV0NJTUtRRjcyZ3U4Y1FqNE9BZStRbCtNdTBmOUhLV0ZtTEdFSDhRSkg0SWw1YWgzQUVFL3hEclRzUDQ5R0dNL1dtRmxXS2NKc2FEblNWMTZnU2l4aXpDc3VTM3ZqMWFhNzRkb0tMNGZJa0dkSTM4V2hJOVUzK2J6U1ZPK2dEVERQSTl4QTY2dzBpeXBPZkdXL0lrU094dEFITkhERWZQNDR1VFRpQUl6UFpMbkZ6T1pZSWE3bU9FRUg0amVnOUErSEhrRThqVGMvSG4xTmVDbU1tQ21WK0plcnhKOTlFTE1SQSs1dFExYTJxQ2xBVm8rUm90eW5NZ2RXQ3I4QTZKM3RmeXBMSEgrNEZyQVltVzRJVE5ZOVFVeDNtV2M5aFgwd3BpeXA4ZVl2ZFgzNUw5elJBOVBhS2llYUgxRi9SeDQ2OE14QjBMdnc1R0dZKzFwaUNEbjFCV1l2UnpyZkl6YnQ4YllMOURyR01ZMXdyZ3ZJWHZqalBvaHBVSEx6K0xFejZIRmVhem1Bazc4SXRaK0JTTm9WbEVOL2UyTjBuN3FTdkY5L2ZKMzlhZWdKbzNVUWs4RHJFZ1hQU3ZSc3hvOVRUQ1hTdDQxZXI2UmY3OUMycE5pcEx0SXI0ZzdHTFE3R0xRN0dMUTdHTFE3UnY0VUl5djVRRmpoSURLWWh5T1BRRDRjdkNNRjZ4bXBYc0hINGw3bkV6L2Nad0JPN0R4bTlCZG5lMUg5ZzVqdHN2b3E5TnNLeVBXMTlvNjljUWE5c1laK09OT0JTSVBrYjlKSEhvRThqZGhoM2JKMTZXTzlobGhyS2YrRm1JdGIzNGNlaDlHakdEM3MwS01ZUGV6UXd3K3R6VEZuaWJqNWkrbzNtUGNsZWhhTFhwZkZkOWhyUHRXazBINnFTY0ZId0Z2Y0pzN3dCT1d3UjBONGpGcndHRFhnQi9aQjV6VG5uNDlXSENYbE9NZmUrS21mMEUzeFRVYzhGYmMrQ2o2dEJPdStoeG52cXg4TGZiaUpmc1hvWjRqUjlURXlRMDArMWgrdHJnQTZyZ0E2cmdEaXJVREw2bmZmc0lqV2VxTDFIZHh5Q1U2cUZHdTZEOHovQUtNOFZKZEJmMnVLV3o0dnZsKzNQOVlWaHBTS0ZZK0FuN3VOTTc2REd5Nkdkb3Fkd0IvZHcvN3ZxOC9JNzVtSnVkOWc3amVZKzQxMloyWHYvbVlhb3pDTTRvbjV6VEZLRlVaUllSVDVPOG4wTWNJdHJJRkJUNkpKRnhLREZJdVVUb0xKNTBoamtjWWhqU2ZCR05VTW96YlFmdTlvRCszM2pmYUF6YS9CU1czQlNlMkhucHlBbm5TRW5uVGg2OVhmWXQwL3d4dTZhV2FFUDVabnZLZk9oNDc0UTBmOHBaYkExZm9ZdVZKN2QweDdkMHljbDd6VCsvSW5HOUVpQzNPdjFyYXkwYmF5d2R5UDBkSmJNUDM3OHY4dXpSTlVSeERySHlHMjMwUXNmNFRZZlZQeVVOM0ZmU2VvSHFPMEhDWGxrb2M2RUtNbXFJcDRKYzdqRFhwWHc3N2VxczlLT3VvcXhQMlhrcEg2T1ZxZVJjdDJvbThPYWkrZzVBSktERVhmeC93MTVudURuYjFWWHdHR1VFbEE1K2lyUXFzcndBb3F0QXlDYlNlb1NqQ0xDaWprT1ZaV3hsOGhmNE5acTZFZG1wN1ZtRlVGOVBFY0t5NlQ5SkViWWhWR0tOZU1WSTBkdklCMkpBQzNWQkdLVWNveGlncWpxTG44RjJieTNMcUVvbmM1ZXF2UVc0MmU5N1JycUNlZmsybzIxbkFidmV1aWR3RjZWM0t3VkxINmF1amJXMmlHQ25GTnJYNkx0ZHpHYUhVeFdnRkdxNVFNMUpmRnJvelVlWkl4TVFjU2VvQ1IzMkpOUDhtUlJNMHc0a3VzbzVDckNFT3ZsNWk3VURMQnp4NXFKN21GNmh4YWxHSSsrYVR5MGFJVVk4cW5sSTh4d0hGL2ZWKzRmZTA5b2ZkSDdrZTBGZmVDdGgrNUQrenhYN3dIK0xTL2VQNnc5SC96dVdPUGYzRGVvdVozejVtWVNsYkVRTExHK215Sm9XU0gwZXpSeHdGeHN6Witka1JkSGRRNW84NEZ6NjZvYzBPZE8veUtKTmxnQm52VUtwQzc0azZNSlNzOFdhc2ZTelV4dngxbXNNZE04bGlPS0srRGNpZVV1NkRjRmVVWUI3Y2d0NVpudHRlMmtHZVN4N0xBdWhocTcwbzJLS21KWkVzY3NUNEx0THlMTVIyeFBvYjFNZlM2S3lsUTc0VGtqSElYdEhGRm1SdCtkc2ZlVFRGS0lkWXE3NUJKdGJCV082S2pIVVh1WFlqMXl6dGtVbDNVdWFCTzA1dGh2MVpJMXRBOUc2elpGdVBhWVMvMnVIMEh6RlZiM2hmcTY2QmVnWHBuMUx1Z3pCWDFicWgzeC82d0M5eU5OY2ExUVdsTkpGdjFWYXhCaGRPNUxUbmdMbXRqejQ1b1V3ZHRGS2gzUW5KR203cG80NEkyYm1qamp1Z2kzNU94T0ZkYllvVjF5Q2YyRXV1d3dqcU1zQTVqY2JiT2VIWVJKL2dTYTdEQ0dvemtXeUZjN04xT2U4NmExY3VueDhXK05UM0t0YXRteE96djZnU3M5akhPNzFkNkFXdHZURXorcW02Z2w1TG8vWkYrb05hVldQNjdkQVNqTmNDdS82YWVvTGNIcWZHdjZncEdhUzd2Nk4rakw3aUowK0llLzViT2lOaGc4bGYxUm5oMUQvRHErL0NrNGZBNER2QnFYY0dyeStIVlBnR3ZmZ0R2RXcydnBvQlg4d2V2dmcrUEdnNXY1QUN2MWhXOHVoeGU3UlB3Nmdmd1ROSHdhZ3A0TlgvSlNsV0pFMm1JRTZtSEU2a24yZUs1bHJvQlRzUVVxL0xDcWJqaFZGd2xSNVRYUVRzRjJqZ2hPZU81THRxNW9KMHIycm1oblR1MHhnRHN4Umk4SXdnczB3VHMwaEtJMHdwbzB3V293ZzlZNFJnUWx4bXNZQkJZVlVOQ2lEOTRreWRwalplU2RDWTlpUmZwUS9xaTlGUGdvUUFTUjc0bW5jZzNaQ05KSWxsa0ozN2FRdzZRaGVRUVhqK1FJK1FxV1VyeWdMQzNrRkpxVFE1UmUycFB5cWtqYlVpZTBDNjBLeVcwRysxRkdlMVBCMUY5T3BoR1UyTWFqNWNGVGFDSjFKS09vQXVwRFYyTWx6LzlIcThXZENsZUFYUWRYVTliMGtQMEhBMWlTdVpOdXpGZjFveUdNbi9tVC91d1FCWkUrN0syTEpoK3l0cXhkblFBNjhBNjA0R3NLK3RLdzFrUDFwTkdzRDZzSDQxbUE5Z0FPb1FOWm9OcEhJdG1NVFNlRFdGRDZEQVd6eEpwQWt0aEkya0tHODJtMEZGc0dwdEpwN0JaYkQ3OW1pMWtpK2hjdHBKdHB2UFpWbmFNcm1RbjJGVzZrK1d4Ty9RRXU4Y2Uwa3VzbkQyaHY3Q25ySXBlWTYvWUczcURxVG1odHpuam5CWnpQVzVDUzdnWnQ2Q1B1Qlczb2hYY2h0dlJwN3dPcjBPcnVCTjNwaSs1QzNlbHI3azdyMGVyZVFQZWtLcDVZOTZZVWU3RnZSbmp2cndwazdnL2I4SDBlRXNleUF4NEs5NktHZkUydkEwejVzRThtSm53cnJ3Yk0rVzllRDlXZy9mblVReDRodzluQ3A3Q1J6Rm5QcGFQWlI1OFBCL1A2dkg1ZkFIejVGazhpelhnMi9nMjFwRHY1RHRaSTc2YkgyR04rVm4rQy9QbnQvbERGc3dydVpxRlNEcVNLZXNuV1VrZUxGSnFLYlZrbzZBcEZNbVErdXVlSmp4cVRFb0NzUnFTRWpPTUpDWkVwQ1dTeGVEaXRHZG9Hd1g0RDFHcnhWL1g2Uko3NGdUMjdnbk44b1ZHdFNYZFNHK00wWkVNSUJGa2lMYWRDUmk5QTNFbWxxUStkSzhKYVFIVTNSMDZTS0YzQTBra05GQkNIMDFiVThTYzJxUXVzU0lOTUU5VDZPY25wQWUwbFVGekI1RW9Fbys0elVLN2RWV1FnRjZoblJWa3FPaG5DWDAzQU02M0pTN0VHanJmakxRa3JVZzdFa3JBZWNBRnU1QXdjQUJOVzNnNTdLUU9xVVZjaVExcFJIeUlId21FYmJTSFpYeUtsWGlRcm1RdzJNSXc3Y2p5WHhJcWlCMXhBNHRwVEpyRGx0cVFEcVFYQVZjZzlVZ0lDWWNWSlpEaFVkNnBVV3kya0l1RlhDbGtscEM3aER3Y0ZaR1F4czRJZVZuSTYwSVdDMWttNVBPb2lOUVlWaTFMem9UVUY5SlVTQ3NoN2FLaWhpZHpoWkJ1UWpZUTBsdElQeUVEb3hQaWgvQmdJVHNKMlQwNk1XazQ3eVBrUUNFamhZd1RNbEhJdE5pVWlDaWVMdVFrSWI4UmNxR1F5NFhjaU1FaStBNGg5d2w1T0NGeHhIQitVc2l6UWw0V01sL0ltMEtXSkNSRkpmQXlJWjhLK1ZxV0VrRmxpcVFycExHUUZrTGFDdWtvcEVzU01zbFRTS1dRVFlVTUVMS05rQjJTVXFJVHBSQWhld25aUDFrdUR4Y3lWc2dFSVZPRUhDM2srRlNjdVRSSnlPbENmaWZrUWlHWENyazZOVDR4VnRvbzVCWWhkd2w1UU1palFwNU9IUjZWTEowWHNraklNaUZmeTFKSFgwaWIxQkdScVRvdVFub0txUlN5cVpBQlFyWkpIWkdjcXROQnlCQWhld25aWDhod0lXUFRzSEtkQkNGVGhCd3Q1SGdoSndrNUhlYkVZWmMxWVJGLzV5Y21mekwyTCtRVXR2QnhLZjBKNmZBYmFmUlJ5ZUV6REdEVGYrY25DZy8yYTJuK0p5UVR1MmNZU1g2aVd0OHBTLzAvSWMzK2hMVDdqVFQ5RTdLR1dCY1hPZjFBeXV2OXNNejRvMUlIdnM4SzNsU2pFZi9hazQzMjZjL01TK0daUHk1TlBpS2Q0ZjFERUdQQzRKMFR5VWd5bmt3R3Nwa1BMTE1hS0djSEVNNXhjaGJZcG9pVWtNZWtrcWlvTGpVRlRuR2ticlFSYlVvRGFUc2FvcmxYYXE3TjdiUzVRcHMzaGZiTGVZRG1tU2swejJ5Mjl2bThKdWQybW5LdWJjKzdhOHZUdGZsOGJYNVdrd09WYW5KdHZiUmVtK2RyY2gxZlRhNjNVdHdxTmJpdWVUWnNxczFiYWVZeDdLUjlucTdOcXpXNXNZZkcxb3l2YTNJelhVMjVXWncyUDYzTkwydHo3YnhtbFpqUEVDbUt6aE1XRUVublF1cGpwMHRrRzZBdlpLNUtKUDRKYjg4NzhJNnlmVEFMWmdIbHMySTJvZ2ZhY2xPNUxUZUJqbExjRG9YUDBkZ09jRGt4cGsvb0V6eSt3RmlVdnFhdkNhTnFxaWFjNlRBZElqRWpaa1IwbURreko3ck1tbGtUUFdiSHdGS1lNM01tQnN5RGVSQkQzaEV6RzJHc210aGRocnhzYWthK29IYTBOcGxBUGFnSG1RU2tPcEI4QlhRNm5FeWxTVFNKVEtlZjBUUXlnMDZuMDhrc29OVkZaRFlRYVhmeUhVdGpJOGhXTmdyWWFEdExaK2xrQnh2SHhwT2RiQktiUkhhektXd0syY1Btc3Jsa0wxdkFGcEI5d0pPL2tQM2NCSHVzQUxyekpjK0E1WUxKYzZ5bUFiRmt5M2dYM28zSDhpRjhLQi9HVS9rSVBvcVA0ZVA0VkQ2TlQrY3orTmQ4SnY5QlBnVzJsQzJGaytyTU8rT2tRbmdJWVR5YXh4RE80M2c4MFFIMlN5RjZQSTJuRVgwK2tvOEVIeGpOUjJQblFJUEVDR2p3UzdDREpYd0pUcFlMMy9HUE0zYVViNEcxWVBMZDZERXY1b1c3YWNxZ042dzVhNDZhQUJhQXN3NW13VGpyVHF3VHpybzd6a0VYclcxeHZrcldoUG1oZDF2V2tYVmpMVmxubEJ2OCtWRllCc3ZBckhQWUhPZ0JJekluYzVUcVNBckpTWEtXNmtvdWtxdmtKdGc3NVh2QWFvaFl2ZTBIcTY4ak5DZEJiaUhKckZmVHd1R0RGb29QNnBqOE95YTBKcExNRnFua0lYa0l2WkRudFpLc0pSdXBwbVFyMVpMc0pIdkpRV2FGNytkbFFKRm1rb1ZrQ1l5c0srbEorcEtCWkNnWlNjYVNpV1FxbVVubVVnMjBrWERTWDJBSmNoOEdCQjBJTnRwYWFnMExZTUN0dG53MVg4czM4azM4S0QvR2ovTVQvQ1EveFUvem4va1puc3ZMK0NQK21KZnpKN3lDUCtYUCtIUCtRcnpIdFlxdndvaHIrQnFzWlFQZmdIc0huc2MrNURra0diMi9IMzBWV20xQTdSNitsKy9qKy9rQmZwQWY0am44TUQrQ2RuZDRNYi9MUzNncHY4ZnY4d2ZvSjQrK21xL0c2R3Y1V295K2tXL0U2SnY0Sm94K2xPZGk5REtzUVI2OUViams3NDM2Ty9zUVozWWIvWWkyMysvTS9BZDdsYzg2Vi9SekpxYTBOKzFMUDZYOWFCOGFSeSt5RVd3OG04cm04Y1Y4SGQ4aSt4emFuZmJDQlEraFE0Z09QVS9QUTVmU1dCcDBhUndiSjMrUEdlelFRTmloSVYvSUY4SUc1Qk0wNXB2NVprUUNSaXZKUy9JbG1VUytRZ3lZUXFhU2FXUTZtUUhXT3hNUllSYVpUYjRsMzVFNVpDNlpoL2l3QU14M0Viak9Fdkk5Mk85U3NveGtraC9KY3JLQ3JDU3JFRHZXa0xWa0hWbFBOb0F2LzRSSXNvbGtrODNneGx2Sk5ySWRjV1VuMlVWMmcwSHZKZnZJZmtTWmcyRFJPZVF3ZVBSUmNnd3g1d1E1U1U2UjArUm5jb2JrSWdLZEkrZkpCWEtSWENLWHlSWEVvMS9BdGErUmZGSkFycE5DUktjYjVDYTVSVzZUTzZRWUhMeUVsSko3NUQ1NVFCNlNNdklJa2F1Y1BDRVY1Q2w1QmkvekFuR3NDbnQ5UlY2VE42U2F2Q1Vxb3BZZE0vaHlLT3ZKZXJIZTRNeDlXVC8yS2VzUDNqeVFEV0poWU03aExJSkZzaWlaUGJOWXNPYzRjT2VoYkJoTFlNTlpJa3RpeWV3ejhPSnJMSjhWc091c2tCV3hHK3dtdThWdXN6dXNtTjFsSmF3VWpQaytlOEFlc2pKdXlCNnh4OXhJWnMrc0F1ejVHWHZPWHJCS1ZzVmVna1cvWm05WU5YdkxWREtYNWxUbTBsemlPbHdYZkZxZkcvQWVQSlQzQk44ZHlBZnhjQjdCaC9QUCtDVCtGWi9NcC9BNWZCSC9ubWZqWHJmd3JlQzR1OEJ0ei9Kei9EeS93Qy95Uy93eXY4S3Y4bCtrRmxJQXRNWmE0LytGSi85Q2VPWk0zZ2tlOVRJNGRRaTVDalk5Z09UeE1ENlk1QXMvY1owbjgyUlNDS3VlU0lyNGQvdzdjbHRvMHgzaFM0dUZiZDRWbWxVQ3ZWeEhTb1dGM2hNV2VwL3Y0RHZKQTJHblpWSnp5Ujgzd2VnQjNPRi9SdS8rV2V2K1V6cFgrRy9SdXQvcTNUdk4rMzNkKzRmMnlmcjNEdzNNRkRyNGY2T0ZpMlQ5b1l4YXcrdllBVE5ZQ1E5VVZ5QUhEeHBHWTBoOTRZMTg1SGU2aUM4ZEJpelJCRmdpbmZqUnNVQkh3WFFSL1lHRTBlMzBISWxpS2ZCUDQ5azB0b2pNRlpGOUZUZm1GbVMxL0s0UitZbmJjRStTeFJ2d3h1UUk5d0phT0NHMHJnRHh6QitSMXdJUjBKRzRBVC80WWsycjhKSWxZb0w0T1VzODdkYys3Y2RUSVY3eVg5blZwL1d4OWthMEVTN0NqL3BCRzl2VDl0aHFaOXFaU01BNEM0SE1OV2d1Q3krZ0FqcUl4bXBMZG54UThtc0U0U1FReEVBMlZDQ0lVQllLQyt2TCtpTDI5MmY5VVRPSURVTHNqMkV4aVAzRDJEREUvcy9ZWndKQk9NdmZ6ZnBQQ0tJSHRPSlRqQldOKzA2V3NlTmZ3Qkx5ekhwaVpuMHhzNEdZMlZETWJDUm1OaFl6eTc4N21rdmEwMHYwTXIxQ3I5SmZhQjY5UnZOcEFiMU9DMmtSdlVGdjBsdjBOcjFEaStsZFdrSkw2VDE2bno2Z0QybVp4Q1dKVi9JcS9wSy80cS81RzE3TjMzSVZWLzhyWlJJT1g1SjVveDIwaXdsMGFpNHpDM0FMRHU3aGlHb1pvK3BBMzdCTDZGdC9vb2Q3a1AvQ1h0WTBBNkRXQk1SREdiVWEwUkYwSkJEek9Eb09FWFFhblViTTZBejZOVEducytsc1lpRy80MG9zb1lIYm9iMkhhQTcwK1RnOVFXclNNL1FNcVNXd2k1Mkl3UTRpZ2lzRmdna1dDS1lkMXVlUEZmNk5NOVBhelg5eFo5QWNUNEVaK3NOcVBzWUF6OEFMNXNIakZjTzNQWUVmcThiYTljRURyYkJ1QlpoZ0Erb042d21rd2JRVDljUStQTENyaGlMdkQ1dVM4MEcwdWNqRHFML0lCOU1XSWc4SEs1VHpDTnBTNUpFMFVPUlJORWprMGUvejFpS1BwY0VpajZmdFJKNEFPNVh6SkZvUGxtZ0dhMlo0YWtEazk5a2JDZHRzREJsR2xaQ0RxUmRrT1BXR2pLQStrSkVVM2dKek5ZR01CazlsTklZMmc0eWxiU0RqYVZ2SVlmUVR5QVI0QllaWk9rQW1VL0FDY0tGT2tDbTBDK1JpY0dCR2w5Q3VrTitEVVNtSkgybEZPcER1cEI4SkozRWttWXdtRXhEWnZvR05MVWJFV28zb3RBWFI2QUFpejJtNkhqdFlqRlZ2RVBrZ3VsSGtZZlFua1ErbVdTSVBwNXRGSGtFM2lUeVNab3M4aW00UmVUVGRLdklZdWsza3NYU3BPSXRsNGhReXhTbjhLRTVodVRpRlZlSVVWb2hUV0NsT1liVTRoVFhpRk5hS1UxZ243MDM0T0ErUmh3QXJtQklQNGswQ3hIdERwdEFzRzNIV05jVVoyV3JiUzdUVys1L2k1Sk1VNzRBWjAvbmlySVNVbVFFMWgrNFRhbzM0UVlXT002RzVYUHd0aWpHdG9KWDBEWnlBTGpObU5aZ05zMmQxV1QzQmwvK2QvQmVlWEhDMGZ6QStmWmtGdmVkUS93c1BlcytnNURIQ1dNSjdueTlIQVBrOUdodFJpeWNwbkx4NzU0NW8zLzk2LzI2WWZUQnlLMDJ4ZllBeXc5NVAxOEJ6Y29mSlZTWlVqMlZtMk5kRGtTdWoxTXRJYWFDclU5K1VNenNkb296UU5heXZTeVdhMFl4UktiT25zb2V5d1FjbERzc2RKempna3VSWE54SkpVa2tTU1NBeEpBMHBVSDRwblQ0WVRMSXlDMTFNYldaNWh2a082R3krS2lnaTRNS29tWTZaR2RZM2xCbjhLRkxEVEE1bnhjemJINncxNzhiTTBIWnRxd3FHZHpEeFdxazBlYjlVcW9ORlRad2hGc2w3UzdxV3JIOXJMMnVscGZ5Z2IybmNOeVkxTFNZbFVkRTJJam5HeTBwcElSZnJXUm9GajBpSmpFZ2NHWitRRU9ObGh0RlFhbWlwMnlzdVlsUmFqRmR0cGIxY1lHUnBwU2xRdEkxSlNZdVBqWStLU0l0UFN2U3FvNnd0VjNOTEcyMTFyL2pobUNWaWVISjg0aEJGMjlaS3g1b21TaDh2YjZXdlV2enJYOVBFUzM3MDhmWnAwcnhKOC83S25oOHN0bmRQcjVwS2E4MzhwbjFpVXVKN3hnOUpiS0RvbUJqVnlLdStzcDVtSXVkM0ZXSXFSYzkzYy9XTVNSa1pIeFdUS2srYVFaMC9QQldxUTNnR05TTW9OMlFabEpKMXA3ZXNQSk9yMkdRNGJ0ckdLU09lYkF1cHVKRmpkbkJJeFA0VjBRNzVlMStkOXRrd1NUbXQzL2l2QzRZVk5sMXFkdkJDMmVpbm8xYVBUd280T0dlVHlaNjQ1d2x6VCs4UGJiaWhROHNYTzY0TUdtelBscjF1UE14eFpkV0t4YXZ0VHJKYlgzUUp2V01hWHRiS1lmeHVrNktnRTl0dVROay9PSDJvVnlPK2FLTGwydmFLczE2cEpuMGI1bzcyOVpsbnNjaGlkMUZjNC9VbGR3NVAvOXJ6eUF5bktiSDd2K3pYTjJuRXdZRDFibE1HblRhM0RsZzI2VUd2SE1QRW82cGpuUXAzNjlWWTREeTJJTkQ5Z3VQb3NtVmVweXBLbkdzVkhOM2F2dTFpdThHWmpyT0x3MTQ4SGxzeGJrTWtuZldpcTFIUmVlYythK2ZsWmswZG1mVjRqOG16NHE3WE10L0VaV1padGRnNkpXY3Y0MUQ4RlJNTGxCUHpsTDY2K3RCWUhSMDlDb05UdWlsZDNqMHI2V1RidUxTMFpQL0dqWk9pVXBNYmpjUzVwK0xjRzBVbERSZTZVOXVTVXJXa3I5UkZ4aWhSdHBiTDZraitTajlsMDB6ZlRPL0pTbTMzcUpTRWYrcmRXS01ySDZwSzI5YU4wRXBvYW0xWHlWaHArRzRWWEY5cEtoZWF5WE5Kc0FCZHJCRFBOU1JvNXNwYXlwcnY5SnRiR3ZmcTJScUs1dGZRcTJFVG4xOVpCWjg0a1hRYTl1cEJ2OFBCRGw3VHhpeXFQLzlneGtaNjFhRkxidmIwZm9rMzlPdXRDRHQ1ZW81bHFSUnFVdDdldlRIeHl5NCtOU2RrOFdYblNPdXFvR1pPM1pLOUpsVE04SnV5OWQ2OUJVUjFydmY4RUplTDY5eEQwck4yUnJSKzVubTI5TlMxc01LOTliOEszUDdEOW11Mytxb1BiRHMyL3NVNTQ2VlBGcWpxWDJvUmFtL3Y1MTRWMUFrMnJGWm1zRkt0SFp2Y3IvL2tjbDY5cWJiZU9nWmhpMGRPL2JVZC8wY3M0N2ZtcVBUNzBCejcvc2xKR3lzYmFpWjErOWlrY2wxTXlrZE5ja3Qzanc2RmwrTFNKOWtHeDQ0WU5QN29ybVZSYnVxV2JiOGZXOFBQM0xWMzZyVVI3dkZ2UTNZckJsNHlmSlZwNy9tb2R4K25pRHpIZ3VKOVBzTk9sQmV1YUJiempmMGM0eDA5SFFlT2pXMHlXR2Y2SjZxUklUZDZUbGcrVWZGRDF0U0J5L1dyN2lwZlBYWnUxcVdONGRrYngrc2N2ZHI3L3NTZzdhRXJHcXluNlUrWHI1L1pSTFdzWk5CUW5XVXRoOTA1T1ArUTZrejRxMWFsZXBuQkR5ZjJTRnpsK1hUSGRIT1BSN091NjJaTzdyNzQ4MDc2SnNyYXA4MlhEcXU2M3k5TFd0ZHEwUmFQZTdOc05nYmM2Wm5VK1ZLVEg3WW5SZGZlT3IvQjNwYWxZeDRPVDM5bFUrTDIwNmJ5UlQxM3Rtb3diOWVZOWFyTG9SdnFwWTF2VTliY2NmbFFtNUpQOTdyRTVaRUpiYzJuVEJpbU5jblR5b2tuL3FaSkdyODNTYVlrU2grTk1UWlFlaW85TXQweVhTWTcvNUV4cHFXbU5veUtFT1puSTh4UEh1Si9zVURkUTMvS0FuMS9iWUh5TFU4Wm5ad2ZFa29WQTI2T09aV2hQUHAyZDYzNSs3OGxSL2JuNWg1L2JwcW5mdFgxa0Ura3NzYXhGMm4ybDc4ckd2eTl3bkx6MkU4T2RNLzlzblJDelMvWHVNOFpZdG51emVsZEMxdnpNMHQ2RE5DWjhjWGFwR2YyM2UxZEdqMk5uNW5nWExYM3RNMjhSOFpwaCtKR1hYdTRLSEpLVHVyc2w5UFMwdXR1V0xIdzh3V2JxMmJWKzZ4cm94SDJIVnJuUDlsdW91aDFkVlRtZ295bytMY0c1NlkvR2JIWFlNbTFWelY2dXkyTzhENlF6ckkvbjN4ZytaRVp6ZzFHWDJneWN0OTNxUU5mN1M3cFltMVk5MHp4eGN1K2pUcTJzZzR3QzA5M09iNHF0bnordWVTSGdhWFBUY1pmdnpCMnhjalA0bk8rNzlaZTJjUnA4L0pOZHBFQjlhOTlzOTVUNy9NODI2MERQNy85dzZva1ZjQzBuNVFaa2dWY3dHdU5DekFqT1dSR1FNRFVHaGNDSzZQS2JyVDY4TVFrZUlEa2Q3WnRaT25jTmlsNVRFcjhrTGcwaFVkVVBZVlg4K2JORkYzam8xS1NVcE5pMHhSdGsxS1NHM2s1S2gwMGphMy91U1lwUlJPcm5aUjFOTmRrKzQvNjBLU2tORVhyRVdseFNTbnhhV05rOTlDOG1kTExTNmxzcG5VUDNrb3ZieDh2N2VOL1lVVWZEZVZzZjA1eVNZdW5JZllleXhhTURsTStXTDV1cHV2Z2w2cDVYVmJzVlAyd1hCRTR0c2Z5SmN0bmhYc1B1OUFtZXN6ampTTlA5Y3AvK3ZEN3lRNnpsazJLM1hwc1dIcGszYXUxQTRyTTZIZjM1aDg5MkRCMjhlSTR0MFhuL1JzY05ON2V6eTJuWGFsaG9OLzhCdXM4bXE4dDYvaGxtenVUelBZdVR1Z2RzVEZqN0kvaERVZDF1YjlvVzNTTHhkMGR2UFJkckphdEsvMjJ2bTFKeTRWUlZ1SDlkR0tXMVc0V09xVnFUZmxjZHR6KzBzSGVuMnlkTnVHZ2YxbXZ1U0ZaYjlla0QwOEwyV1I3WnI2Qmh4UHBPenM4dnRuZXpoWjZBWDNVQTk2c2pEWFVYMzF4WXArKzVUdGFoTmxNSENYbFZ4N0ltakJQbFozN3hkVTFkaWtEQTA3dmU2Sy93bG01VmZlclUxc1ZveXkvdXFIMUcydVZFMWNwSnk2WDdaSktFeGNySnk2WVlEN2dmSEo1Zk1yU3VqM0dXMjNwK28zNjV4OVQvdS92TCtNak9pNjh3cng3Um9kbVBsdGcyK1RSTHVxU042ckdzNEhoM3N1V0d2MGNxUFB0MUZtbi9FdWNuajdwTzZmQjlzejJKeVBMcTM4NTA2SkYvM1ZOZThXclhJWUhuVHF6dmtobmJLSFh6SmJMekpPSDdsVlpkTE9OUDFSOXZ1MmRHdjBWM1I1RWZyNXBmYTJUOVp1NU5qd1E4NlBGZEZlenFCVlZ2UnhlT1oyNmF2MHNkR05pVzIrOXR4azFYOTRka21EU28zSi9SZWlKL2FWSGxkVUtMNE9wdGVmVnMrdDZwVFpiVlRIaEp0ODI0UG5td3BOOUg4ZDBQQkhhYThjMjdtR2hubjMxaWY2czhic1dITnZRckVGeGV2SGFVWGRHWnBMelE0TnlMamFkZnJPMXhkb21RKzJIRmpTNWRkbEJLbDc3aVhTeXY0OWZZbGNIazhpZGhzdS92blNsVjFDN1hJZmVxNU1MTFB5bnpCbXhiTTNGVEhpRkl3QUhtN1RBWUtqUm9tNkhTTzBOTmZLUHNoOWozZmU4SXdtMS8xc3VRZGtVZU1IWHE1bXZyNWV2RE9EaDRyMmJ2bk1KRTFmL00yU3dWTmJRMEEzRHZoR3BjWUFDYVpqSFhJUVFrQTI5MEpqbzRVbUowZTlXWnZoSEsvdWpiWHBqMHQ5c3M2N1NTYk1OdXc5cm9tTUUrSkRSU0hkQkNoUy85U1Ftc2lmUkY1N2t5Qm5GekgwMzFJSGRINmNmdnV6aVdqbnlySk02MTdOUHlPbnZkMlpzYVRLbUlUbTZWdjlLMUttZHF5cnY1K1JjM2Z6MS9PVjZyODEyWklRdWZwaHhmTC81c2JXSEhnK2I5RTFQKzczZFgwZlRhVGsybHpQaVNLdlJ3UzhzL0VMZVJQVzQrYnJsN3J2Tk50K0kwcXZiNHJOV3Z1MmZEOHRxOThJOTFkSDU1emExSEh2c0NGMThhY1Y1eStPMWdqN1RIZjUwbmxQdzREYVBEcDFhRkszWWxlTmJ2VHk0NVBNdHRSdnZXbDMwL01jYlM1ek1WUDI4V3ZmMkc3K3BYMmx4MmFkalhEZFVlVGF1RWVRM09yRE5GMnZpaXNjN3g5VXM2ZlRkMGRIQm9lMS83RFpwMnB3bGg0Wjgvc0RneldRK3JuTFJad0gxMThRdVBIT2o0ZTM2ek03TXQwUE1pd0NMVFJWVEhHcTdoU2FkZ2U3eEZSblVFK2ZoOW5zNG5QLy80VjRzZEEyMEJOd2Evb1Z4VGlSQlVXdWJTamFTbGV2TCtwMEhuVXpwOWRQZHlrelBtalp2Y2w3MW5LaXM5YjZMRlpPTUhRMUpUeklDZEwwdGFhMDBFc0JIOEk1MlNyUDNBRXRIeVpGOVlKZkNqVVhkdWZsTVoxZjJBeU1qM3dzWlhvSFRJais1b3IvbVZVVE15VWI4dFYrSDF1ZTJQM1gvOHRLZFkzMTZydDFlNit5Wmtvck1WMzEyZEpqYjN1WHV1anJYMHk5WDJxUmJGRHliYlYrbVAyanJWN04zZjkxdnI4T1plWmZtemZWNS9tMlJldXFTc000ZHV6ZDM4MWZZOTJwV1BXNmc5WndqMXgyK2VSSVJHbkJYNzFGcytaaXlXV2Y3UnNYTXMrMlltWDRqWnVjTnR5elZTWXNkeDVlZk9UNTRSdkt6MHdVYk1oTDFyc2ZVMnIyMmN2SmhnellMSzl3MnhxZHZ6cW0vSmp1MnpxcE5VL1NITGJEY2xkMTBrYVBPQ2t1L0ZZYzJLZ1AzT1AyaVhIMDYwc0poVTkrWmR5dlNhK3dKQ3pCdVZqRW41N3VwSVZKL25ZRW56bDFkZCszV3VHOUh1Ny9abHJocWxxNVB2ODFobmpYTWxCazZQbkJsOWhvM1poalJidW5QNHUyV21OKzhRL0UvR3lwRkJxTHNzelF4TmpFRDlaYk1nVzBqSU5jVXhEVW9vWWsvb1BMTU9PUUpOb25PTms2eldCZXo2TlBCKy9jdXJKN2FlODFtcm56MzRkZzJ2ZGdQRzR1K3JsN1RrYlgxMWthbGF1NFRKNVo2VDR4VEVuNzU4NnZ5M0sxZjhzcld2WCszeE9iNGtRT1JNZmFyTnhjYnF5MUxha3lzWEpqMEphOWo2b1c4dThmblgxb1NLRmlXdUt1Z0szWGhOTEhPNWJHTkYxelNudDRPbStkdzZzK2RNaFU5RndPR3A5ZHFxNmNLWG8yUVhmekNuL3RreDUxRjE0Sm41cHhLUGpVemE5YWtPQjlmd1JmNmw2T2o0K0tERmhmckx0M2Q0c3JiSXlsYWRwcmoxcXhsQmFJdmZOOWsvbzNkbE4zL1ZqUFEzS0w3bUp1bjZKU0FHUnUrWkN5NWZvK3pNTDFrWG5tUGJHdjI5RmZQNDEzUFBIaFd5SHN4bVdGeXRlR01QdTR0d25zM1gzajM4YjdpdTVVSmllL01uVzBQUTVwRVRZeVRnQ0hTaDlGM1FSUUc3MjVtcnl3TlB1di9UdHBQa2sxdThaelY1NmY4eFZIeXJRU0pLck0wTGpSb25OZUF0UlJaV0xKa0lNby96TWFDTjZUajUyTGdaT0N3d0c2QlRac1ZVc2N2RjJZT3VPZFhrSjBKRXRVdktNcFBLVTB1S2RZSFpRQlErZ2VtZlNOd2g5QWZxU2ZxYk9Cb1lBL3ZpVEsxR1VQTkxTOHZ4Mlp1YWhHbWdTWFkrb1FXTjk5UHRaZ2RNME1rTmlRdjh6N1RpZWViZjE4KzVMdFdmM1Y5Q084dG8yMC9zcDd4L2xhVUtyZGJtbEcxWldwZFY4d241eVBOczFOck93SUNhNXBFdmpZWFgxKzBMK1lVVThFNXRSenhQVUVpU3pzUGJIK3k4TXpDMHJrVEMyMmxENFF4aEczOTBhSjJLODc0OXpYVnFyaFp0NWI5L3ZMSlVXcE5xTnRhanpzVExZUWpPRDAvZmpac2w5L0QwaGN0bE1yOGtqdnd3a0tlcnBsN2J4NWNjWUZEVkZWeDY3YndUcG1MMFcybVMwLzlYZFgrWnFXNS9YYm43TWNLSDEzMzFLMTcrVEYwMDBLUFBhbjdnazF1bm56QmxzekNWcEVYOE45ajkreFh6bEh0dDlkeU5YeU5QS3J6NUdsOXROZFRvOHAzU3EyVGVIUTNCMFFmTytRUUViSDYwdG5IK2dmUHZzbWRiMTVwMk1SeUdsaHNIbWRpWkRSbzNEcGtDa2VVQWg0eGpMMmc4WVdCQ0x4QzFXQTBaR2RtQlM4OEJWV3owS2puWkRia1FSNDVCem9kd2VNMjVETkFsaFUxVUVab1pERUU1dHRyQytmZjErZnhEdlFvWEpqR2U1dEw0cFc1M1IrRElpUXRQSVlwQmtrTExCck1HSHdaTWhtU0dZb1k4c0dENzJrTUpRd0tERzVBVmg2WUZjYVFDcFFyQnFvQmlTZ3dtRExvTVJnd0dDeFVhMURCbWJaTEtndnkwNHNTQ3pJcTBWdVRMRTJNRFBWUnQvd1MwdjhyeGUrNTVHVVc4L3RZdGZWajdWQzNOVVYyMVg3MU82L256VnJqZU1UWDdzY3QxbGxNSDdkRy9Nclk3T005K1pWUXpOeE9JVmN0OGVtU01kNTJvcjg2bzR2ZVNpeVBZdlBNMDVhWnVkaktkTXJKZVJPcjdpZldwMDdwZWZIQ3hGT3luK09Kd2F1cGxZczN5cW9uUDkwUXNyb25kbW9zcytTS0RMM3pWd0xteXRkRXVETDcxclFwaExOVzZCdXo2VTk1cUNtMi9ydW9oR1RlbC9KdG4zM2lmaS9nWWI3bSsrM3ZldGVYN1MzMXA3a0xqdlN5ZkdzNU5hT0tMeUt0Y2ttbGxNOHZtemYyL3hwZmhyMS9aRnZDMHFlMzFDYjVnL0tkSFJ2QzNKNFh2VTZRU0daaUNMc1pzcmovUUJOLzZkT0NueUZUTEFLTy85NzQ0cnJraCtjbEQ1b1N2b29zYkFJMmk1b1lmeU5pak0yd2lmRU5VT2dGS0htbjAyUlFFOHRRS2c4YkI4UUJUTUJTWmtHa2dRUnkydU5HVE8wd0FwTWVYSWJWa0I5VTN3TXJlQ1BRa0llaGVSU3cvRVZLZWtJc0FnRkdlN1dYS0hLVUthbjllS0NZWGxHREpRbE1Qams1T25XbHhQS3I3eU42Wi9Eb3NPK3Rmck5NK3IyOWRHdGc0cFlUbHZkblYxckxlay9jKzRncGMyZmo1emRMK09WbmJaVE5xZWRlTENkZ3h2bkxUenYwbjdORzdsazdsdTlyYzRvZUIvbUxWUlpPNW4xaitydzJZWXBibk9RQnpYVjl2NUlPVGJUY3ZMb2wxWGpadlcreE9adGZ6WjZqVnI1STgrd0xxVVpyeHFXdmk0SU1tTDlGeUo0NTUvVi9idExEN0l3bkpoZDM3YXExMGxWNkc1NVVaTHdwdi9pSnpNSUlnM2VQZ2xPUGRVKzNPUjNSTzJ2cGRla2phL29WVDF2TGJqUjJDRERJMy9UYzJuNnRkUEszK3VkUEorblYvL3hSY3RkSm12SDB2VWtwazY3dE9QWWh1bmxEWE1EaHlzYnFybDM1S1pPZk9tcGtMazEvc2k2MnRzRXQ3b3VXRmhORDFRSUFhMW8wTlEwS1pXNWtjM1J5WldGdERRcGxibVJ2WW1vTkNqRTJJREFnYjJKcURRbzhQQzlVZVhCbEwxaFNaV1l2VTJsNlpTQXhOaTlYV3lBeElEUWdNbDBnTDFKdmIzUWdNU0F3SUZJdlNXNW1ieUEzSURBZ1VpOUpSRnM4TVRBM09VSTNNMFV6TlVVeE1UTTBPVGhCTmpoRlFUWkJPRUkzUTBWQ05qTStQREV3TnpsQ056TkZNelZGTVRFek5EazRRVFk0UlVFMlFUaENOME5GUWpZelBsMGdMMFpwYkhSbGNpOUdiR0YwWlVSbFkyOWtaUzlNWlc1bmRHZ2dOekErUGcwS2MzUnlaV0Z0RFFwNG5HTmdBSUwvL3htQnBDQURBNGlxZ1ZCYndCVGpMRERGMUF5bW1MWEJGSXNVVUFTb2hKZUJDVUl4UXloR0NBV1ZZd0dxWkdVQ2EyRDlDcWJZN2pBd0FBRExVZ2J1RFFwbGJtUnpkSEpsWVcwTkNtVnVaRzlpYWcwS2VISmxaZzBLTUNBeE53MEtNREF3TURBd01EQXdPQ0EyTlRVek5TQm1EUW93TURBd01EQXdNREUzSURBd01EQXdJRzROQ2pBd01EQXdNREF4TWpRZ01EQXdNREFnYmcwS01EQXdNREF3TURFNE1DQXdNREF3TUNCdURRb3dNREF3TURBd05ERXdJREF3TURBd0lHNE5DakF3TURBd01EQTJORE1nTURBd01EQWdiZzBLTURBd01EQXdNRGd4TVNBd01EQXdNQ0J1RFFvd01EQXdNREF4TURVd0lEQXdNREF3SUc0TkNqQXdNREF3TURBd01Ea2dOalUxTXpVZ1pnMEtNREF3TURBd01EQXhNQ0EyTlRVek5TQm1EUW93TURBd01EQXdNREV4SURZMU5UTTFJR1lOQ2pBd01EQXdNREF3TVRJZ05qVTFNelVnWmcwS01EQXdNREF3TURBeE15QTJOVFV6TlNCbURRb3dNREF3TURBd01EQXdJRFkxTlRNMUlHWU5DakF3TURBd01ERTFNalVnTURBd01EQWdiZzBLTURBd01EQXdNVGMxTmlBd01EQXdNQ0J1RFFvd01EQXdNRFV5TnpnNUlEQXdNREF3SUc0TkNuUnlZV2xzWlhJTkNqdzhMMU5wZW1VZ01UY3ZVbTl2ZENBeElEQWdVaTlKYm1adklEY2dNQ0JTTDBsRVd6d3hNRGM1UWpjelJUTTFSVEV4TXpRNU9FRTJPRVZCTmtFNFFqZERSVUkyTXo0OE1UQTNPVUkzTTBVek5VVXhNVE0wT1RoQk5qaEZRVFpCT0VJM1EwVkNOak0rWFNBK1BnMEtjM1JoY25SNGNtVm1EUW8xTXpBMU9BMEtKU1ZGVDBZTkNuaHlaV1lOQ2pBZ01BMEtkSEpoYVd4bGNnMEtQRHd2VTJsNlpTQXhOeTlTYjI5MElERWdNQ0JTTDBsdVptOGdOeUF3SUZJdlNVUmJQREV3TnpsQ056TkZNelZGTVRFek5EazRRVFk0UlVFMlFUaENOME5GUWpZelBqd3hNRGM1UWpjelJUTTFSVEV4TXpRNU9FRTJPRVZCTmtFNFFqZERSVUkyTXo1ZElDOVFjbVYySURVek1EVTRMMWhTWldaVGRHMGdOVEkzT0RrK1BnMEtjM1JoY25SNGNtVm1EUW8xTXpVMU5BMEtKU1ZGVDBZPQ==</binaryDocument>"
            + "        </binaryDocumentPolicyCriterion>"
            + "    </binaryDocumentPolicyCriteria>"
            + "    <fineGrainedPolicyMetadata/>" + "</PatientPreferences>";

    static String BINARY_DOC_PART1 = "JVBERi0xLjUNCiW1tbW1DQoxIDAgb2JqDQo8PC9UeXBlL0NhdGFsb2cvUGFnZXMgMiAwIFIvTGFuZyhlbi1VUykgL1N0cnVjdFRyZWVSb290IDggMCBSL01hcmtJbmZvPDwvTWFya2VkIHRydWU+Pj4+DQplbmRvYmoNCjIgMCBvYmoNCjw8L1R5cGUvUGFnZXMvQ291bnQgMS9LaWRzWyAzIDAgUl0gPj4NCmVuZG9iag0KMyAwIG9iag0KPDwvVHlwZS9QYWdlL1BhcmVudCAyIDAgUi9SZXNvdXJjZXM8PC9Gb250PDwvRjEgNSAwIFI+Pi9Qcm9jU2V0Wy9QREYvVGV4dC9JbWFnZUIvSW1hZ2VDL0ltYWdlSV0gPj4vTWVkaWFCb3hbIDAgMCA2MTIgNzkyXSAvQ29udGVudHMgNCAwIFIvR3JvdXA8PC9UeXBlL0dyb3VwL1MvVHJhbnNwYXJlbmN5L0NTL0RldmljZVJHQj4+L1RhYnMvUy9TdHJ1Y3RQYXJlbnRzIDA+Pg0KZW5kb2JqDQo0IDAgb2JqDQo8PC9GaWx0ZXIvRmxhdGVEZWNvZGUvTGVuZ3RoIDE1OT4+DQpzdHJlYW0NCnicTY09C8IwGIT3QP7Dje87NE1qag2UDv0ShYJDtuIgWj+G6tD6/00HodxxHNzBg/iEPI+76lBDFwXKukLppYhbA2OUtvB3KQx0kEGWKJ1YZNqpNCyjFBqPJfZS9OSfrwnBF7CjeeBoQ9McKk5sqebIUosbG0sfjlK6fkfe0fBmk9Cs+Ax/lKIJ6AX/BxqXqa1bA3vC6oumq/ADztso9g0KZW5kc3RyZWFtDQplbmRvYmoNCjUgMCBvYmoNCjw8L1R5cGUvRm9udC9TdWJ0eXBlL1RydWVUeXBlL05hbWUvRjEvQmFzZUZvbnQvQUJDREVFK0NhbGlicmkvRW5jb2RpbmcvV2luQW5zaUVuY29kaW5nL0ZvbnREZXNjcmlwdG9yIDYgMCBSL0ZpcnN0Q2hhciAzMi9MYXN0Q2hhciAxMTcvV2lkdGhzIDE0IDAgUj4+DQplbmRvYmoNCjYgMCBvYmoNCjw8L1R5cGUvRm9udERlc2NyaXB0b3IvRm9udE5hbWUvQUJDREVFK0NhbGlicmkvRmxhZ3MgMzIvSXRhbGljQW5nbGUgMC9Bc2NlbnQgNzUw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";
    static String BINARY_DOC_PART2 = "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";

    static PatientPreferencesType oPtPref = null;
    static String sHomeCommunity = "";

    /**
     * Default constructor.
     */
    public CdaPdfCreatorTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        PatientPreferencesSerializer oSerializer = new PatientPreferencesSerializer();
        oPtPref = oSerializer.deserialize(PT_PREF_PART1 + PT_PREF_PART2);

        // get Home Community ID for later comparisons.
        // ----------------------------------------------
        sHomeCommunity = PropertyAccessor.getInstance().getProperty(NhincConstants.GATEWAY_PROPERTY_FILE,
                NhincConstants.HOME_COMMUNITY_ID_PROPERTY);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * This method verifies that the date and time are the same.
     *
     * @param sMessage The message to output with the assert statement.
     * @param sExpectedDate The expected date and time.
     * @param sActualDate The actual date and time.
     */
    private void assertHL7DateTime(String sMessage, String sExpectedDate, String sActualDate) {
        Date dtActualDate = null;
        Date dtExpectedDate = null;
        try {
            dtActualDate = oHL7DateTimeFormatter.parse(sActualDate);
        } catch (Exception e) {
            fail("Failed to parse actual date.  Value: " + sActualDate);
        }

        try {
            dtExpectedDate = oHL7DateTimeFormatter.parse(sExpectedDate);
        } catch (Exception e) {
            fail("Failed to parse expected date.  Value: " + sExpectedDate);
        }

        assertEquals(sMessage + "...  Expected Value: " + sExpectedDate + "  Actual Value: " + sActualDate + ". ",
                dtExpectedDate, dtActualDate);

    }

    /**
     * This method verifies that the date and time are the same.
     *
     * @param sMessage The message to output with the assert statement.
     * @param sExpectedDate The expected date and time.
     * @param sActualDate The actual date and time.
     */
    private void assertHL7DateOnly(String sFieldName, String sExpectedDate, String sActualDate) {
        // Dates will only be in the first 8 characters. Ignore everything after that.
        // -----------------------------------------------------------------------------
        if ((sExpectedDate == null) && (sActualDate == null)) {
            return; // We are good - they are the same.
        }

        if (((sExpectedDate == null) && (sActualDate != null)) || ((sExpectedDate != null) && (sActualDate == null))) {
            fail(sFieldName + " was incorrect. Expected: " + sExpectedDate + "  But was: " + sActualDate);
        }

        if (sExpectedDate.length() < 8) {
            fail(sFieldName + " was incorrect. Expected date should have at least 8 characters.  Expected: "
                    + sExpectedDate + "  But was: " + sActualDate);
        }

        if (sActualDate.length() < 8) {
            fail(sFieldName + " was incorrect. Actual date should have at least 8 characters.  Expected: "
                    + sExpectedDate + "  But was: " + sActualDate);
        }

        // Now see if the first 8 characters of the expected are the same as the first 8 characters of the actual.
        // --------------------------------------------------------------------------------------------------------
        if (!(sExpectedDate.substring(0, 8).equals(sActualDate.substring(0, 8)))) {
            fail(sFieldName + " (date portion) was incorrect. Expected: " + sExpectedDate + "  But was: " + sActualDate);
        }

    }

    /**
     * This method asserts the value of an II data type.
     *
     * @param oII The II that is being asserted.
     * @param sFieldName The field name for the message.
     * @param sExtension The extension value.
     * @param sRoot The root value.
     */
    private void assertII(String sFieldName, II oII, String sRoot, String sExtension) {
        assertNotNull(sFieldName + " should not have been null.  ", oII);
        assertEquals(sFieldName + ".extension not correct: ", sExtension, oII.getExtension());
        assertEquals(sFieldName + ".root not correct: ", sRoot, oII.getRoot());
    }

    /**
     * This method asserts the value of an address.
     *
     * @param sFieldName The name of the field for the output message.
     * @param oAddr The address to be asserted.
     * @param sSreet The value of the street tag.
     * @param sCity The value of the city tag.
     * @param sState The value of the state tag.
     * @param sZipcode The value of the zip code tag.
     * @param sCountry The value of the country tag.
     */
    private void assertAddr(String sFieldName, ADExplicit oAddr, String sStreet, String sCity, String sState,
            String sZipcode, String sCountry) {
        // Check for the null or empty case...
        // ------------------------------------
        if ((sStreet == null) && (sCity == null) && (sState == null) && (sZipcode == null) && (sCountry == null)
                && ((oAddr == null) || (oAddr.getContent() == null) || (oAddr.getContent().isEmpty()))) {
            return; // We expected null and we got null.
        }

        assertNotNull(sFieldName + " should not have been null.  ", oAddr);
        assertTrue(sFieldName + ".content.size should have been > 0 but was: " + oAddr.getContent().size(), oAddr
                .getContent().size() > 0);
        boolean bFoundStreet = false;
        boolean bFoundCity = false;
        boolean bFoundState = false;
        boolean bFoundZipcode = false;
        boolean bFoundCountry = false;

        // Loop through and make a note of what we find.
        // -----------------------------------------------
        for (Serializable oSerialElement : oAddr.getContent()) {
            if (oSerialElement instanceof JAXBElement) {
                JAXBElement oJAXBElement = (JAXBElement) oSerialElement;

                if (oJAXBElement.getValue() != null) {
                    if (oJAXBElement.getValue() instanceof AdxpExplicitStreetAddressLine) {
                        AdxpExplicitStreetAddressLine oStreetAddressLine = (AdxpExplicitStreetAddressLine) oJAXBElement
                                .getValue();
                        assertEquals(sFieldName + ".streetAddressLine not correct.  ", sStreet,
                                oStreetAddressLine.getContent());
                        bFoundStreet = true;
                    } else if (oJAXBElement.getValue() instanceof AdxpExplicitCity) {
                        AdxpExplicitCity oCity = (AdxpExplicitCity) oJAXBElement.getValue();
                        assertEquals(sFieldName + ".city not correct.  ", sCity, oCity.getContent());
                        bFoundCity = true;
                    } else if (oJAXBElement.getValue() instanceof AdxpExplicitState) {
                        AdxpExplicitState oState = (AdxpExplicitState) oJAXBElement.getValue();
                        assertEquals(sFieldName + ".state not correct.  ", sState, oState.getContent());
                        bFoundState = true;
                    } else if (oJAXBElement.getValue() instanceof AdxpExplicitPostalCode) {
                        AdxpExplicitPostalCode oPostalCode = (AdxpExplicitPostalCode) oJAXBElement.getValue();
                        assertEquals(sFieldName + ".postalCode not correct.  ", sZipcode, oPostalCode.getContent());
                        bFoundZipcode = true;
                    } else if (oJAXBElement.getValue() instanceof AdxpExplicitCountry) {
                        AdxpExplicitCountry oCountry = (AdxpExplicitCountry) oJAXBElement.getValue();
                        assertEquals(sFieldName + ".country not correct.  ", sCountry, oCountry.getContent());
                        bFoundCountry = true;
                    } else {
                        fail(sFieldName + ": found an invalid type: Class was: "
                                + oJAXBElement.getValue().getClass().getName());
                    }
                } // if (oJAXBElement.getValue() != null)
                else {
                    fail(sFieldName + ": JAXBElement did not have a value.");
                }
            } // if (oSerialElement instanceof JAXBElement)
            else {
                fail(sFieldName + ": Found a serial element that was not a JAXBElement.  Class name was: "
                        + oSerialElement.getClass().getName());
            }
        } // for (Serializable oSerialElement : oAddr.getContent())

        // Now for any of the items we have that were not found - make sure we expected them to be null.
        // ----------------------------------------------------------------------------------------------
        if ((!bFoundStreet) && (sStreet != null)) {
            fail(sFieldName + ".streetAddressLine incorrect.  Expcted: " + sStreet + " Actual: null");
        }

        if ((!bFoundCity) && (sCity != null)) {
            fail(sFieldName + ".city incorrect.  Expcted: " + sCity + " Actual: null");
        }

        if ((!bFoundState) && (sState != null)) {
            fail(sFieldName + ".state incorrect.  Expcted: " + sState + " Actual: null");
        }

        if ((!bFoundZipcode) && (sZipcode != null)) {
            fail(sFieldName + ".postalCode incorrect.  Expcted: " + sZipcode + " Actual: null");
        }

        if ((!bFoundCountry) && (sCountry != null)) {
            fail(sFieldName + ".country incorrect.  Expcted: " + sCountry + " Actual: null");
        }

        // If we got here - we are in good shape.
        // ----------------------------------------

    }

    /**
     * Assert the id tag wihtin the cda.RecordTarget.patientRole.
     *
     * @param olII The list of IDs to assert.
     *
     */
    private void assertRecordTargetPatientRoleId(List<II> olII) {
        assertNotNull("cda.recordTarget[0].patientRole.id should not have been null.  ", olII);
        assertEquals("cda.recordTarget[0].patientRole.id.size not correct: ", 1, olII.size());
        assertII("cda.recordTarget[0].patientRole.id", olII.get(0), "1.1", "1111");
    }

    /**
     * Assert the addr tag wihtin the cda.RecordTarget.patientRole.
     *
     * @param olAD The list of addresses to assert.
     *
     */
    private void assertRecordTargetPatientRoleAddr(List<ADExplicit> olAD) {
        assertNotNull("cda.recordTarget[0].patientRole.addr should not have been null.  ", olAD);
        assertEquals("cda.recordTarget[0].patientRole.addr.size not correct: ", 1, olAD.size());
        assertAddr("cda.recordTarget[0].patientRole.addr", olAD.get(0), "17 Daws Rd.", "Blue Bell", "MA", "02368",
                "USA");
    }

    /**
     * This validates the parts of the name field.
     *
     * @param sFieldName The name of the field for the messages.
     * @param oName The name field containing the data.
     * @param sPrefix The expected prefix of the name.
     * @param sGiven The expectd given name.
     * @param sFamily The expected family name.
     * @param sSuffix The expected suffix.
     */
    private void assertName(String sFieldName, PNExplicit oName, String sPrefix, String sGiven, String sFamily,
            String sSuffix) {
        // Check for the null or empty case...
        // ------------------------------------
        if ((sPrefix == null) && (sGiven == null) && (sFamily == null) && (sSuffix == null)
                && ((oName == null) || (oName.getContent() == null) || (oName.getContent().isEmpty()))) {
            return; // We expected null and we got null.
        }

        assertNotNull(sFieldName + " should not have been null.  ", oName);
        assertTrue(sFieldName + ".content.size should have been > 0 but was: " + oName.getContent().size(), oName
                .getContent().size() > 0);
        boolean bFoundPrefix = false;
        boolean bFoundGiven = false;
        boolean bFoundFamily = false;
        boolean bFoundSuffix = false;

        // Loop through and make a note of what we find.
        // -----------------------------------------------
        for (Serializable oSerialElement : oName.getContent()) {
            if (oSerialElement instanceof JAXBElement) {
                JAXBElement oJAXBElement = (JAXBElement) oSerialElement;

                if (oJAXBElement.getValue() != null) {
                    if (oJAXBElement.getValue() instanceof EnExplicitPrefix) {
                        EnExplicitPrefix oPrefix = (EnExplicitPrefix) oJAXBElement.getValue();
                        assertEquals(sFieldName + ".prefix not correct.  ", sPrefix, oPrefix.getContent());
                        bFoundPrefix = true;
                    } else if (oJAXBElement.getValue() instanceof EnExplicitGiven) {
                        EnExplicitGiven oGiven = (EnExplicitGiven) oJAXBElement.getValue();
                        assertEquals(sFieldName + ".given not correct.  ", sGiven, oGiven.getContent());
                        bFoundGiven = true;
                    } else if (oJAXBElement.getValue() instanceof EnExplicitFamily) {
                        EnExplicitFamily oFamily = (EnExplicitFamily) oJAXBElement.getValue();
                        assertEquals(sFieldName + ".family not correct.  ", sFamily, oFamily.getContent());
                        bFoundFamily = true;
                    } else if (oJAXBElement.getValue() instanceof EnExplicitSuffix) {
                        EnExplicitSuffix oSuffix = (EnExplicitSuffix) oJAXBElement.getValue();
                        assertEquals(sFieldName + ".suffix not correct.  ", sSuffix, oSuffix.getContent());
                        bFoundSuffix = true;
                    } else {
                        fail(sFieldName + ": found an invalid type: Class was: "
                                + oJAXBElement.getValue().getClass().getName());
                    }
                } // if (oJAXBElement.getValue() != null)
                else {
                    fail(sFieldName + ": JAXBElement did not have a value.");
                }
            } // if (oSerialElement instanceof JAXBElement)
            else {
                fail(sFieldName + ": Found a serial element that was not a JAXBElement.  Class name was: "
                        + oSerialElement.getClass().getName());
            }
        } // for (Serializable oSerialElement : oAddr.getContent())

        // Now for any of the items we have that were not found - make sure we expected them to be null.
        // ----------------------------------------------------------------------------------------------
        if ((!bFoundPrefix) && (sPrefix != null)) {
            fail(sFieldName + ".prefix incorrect.  Expcted: " + sPrefix + " Actual: null");
        }

        if ((!bFoundGiven) && (sGiven != null)) {
            fail(sFieldName + ".given incorrect.  Expcted: " + sGiven + " Actual: null");
        }

        if ((!bFoundFamily) && (sFamily != null)) {
            fail(sFieldName + ".family incorrect.  Expcted: " + sFamily + " Actual: null");
        }

        if ((!bFoundSuffix) && (sSuffix != null)) {
            fail(sFieldName + ".suffix incorrect.  Expcted: " + sSuffix + " Actual: null");
        }

        // If we got here - we are in good shape.
        // ----------------------------------------

    }

    /**
     * Assert the given code has the correct values.
     *
     * @param sFieldName The name of the field being checked.
     * @param oCE The code to be checked.
     * @param sCode The expected code.
     * @param sDisplayName The expected display name.
     * @param sCodeSystem The expected code system.
     * @param sCodeSystemName The expected code system name.
     */
    private void assertCode(String sFieldName, CE oCE, String sCode, String sDisplayName, String sCodeSystem,
            String sCodeSystemName) {
        assertNotNull(sFieldName + " should not have been null. ", oCE);
        assertEquals(sFieldName + ".code incorrect: ", sCode, oCE.getCode());
        assertEquals(sFieldName + ".displayName incorect: ", sDisplayName, oCE.getDisplayName());
        assertEquals(sFieldName + ".codeSystem incorect: ", sCodeSystem, oCE.getCodeSystem());
        assertEquals(sFieldName + ".codeSystemName incorect: ", sCodeSystemName, oCE.getCodeSystemName());

    }

    /**
     * Assert the given CS (basically a code) has the correct values.
     *
     * @param sFieldName The name of the field being checked.
     * @param oCS The code to be checked.
     * @param sCode The expected code.
     * @param sDisplayName The expected display name.
     * @param sCodeSystem The expected code system.
     * @param sCodeSystemName The expected code system name.
     */
    private void assertCS(String sFieldName, CS oCS, String sCode, String sDisplayName, String sCodeSystem,
            String sCodeSystemName) {
        assertNotNull(sFieldName + " should not have been null. ", oCS);
        assertEquals(sFieldName + ".code incorrect: ", sCode, oCS.getCode());
        assertEquals(sFieldName + ".displayName incorect: ", sDisplayName, oCS.getDisplayName());
        assertEquals(sFieldName + ".codeSystem incorect: ", sCodeSystem, oCS.getCodeSystem());
        assertEquals(sFieldName + ".codeSystemName incorect: ", sCodeSystemName, oCS.getCodeSystemName());
    }

    /**
     * Assert the patient tag wihtin the cda.RecordTarget.patientRole.
     *
     * @param oPatient The patient information to be checked.
     *
     */
    private void assertRecordTargetPatientRolePatient(POCDMT000040Patient oPatient) {
        assertNotNull("cda.recordTarget[0].patientRole.patient should not have been null.  ", oPatient);
        assertNotNull("cda.recordTarget[0].patientRole.patient.name should not have been null.  ", oPatient.getName());
        assertEquals("cda.recordTarget[0].patientRole.patient.name.size incorrect.  ", 1, oPatient.getName().size());
        assertName("cda.recordTarget[0].patientRole.patient.name", oPatient.getName().get(0), "Mrs.", "Ellen", "Ross",
                null);

        assertCode("cda.recordTarget[0].patientRole.patient.administrativeGenderCode",
                oPatient.getAdministrativeGenderCode(), "F", null, "2.16.840.1.113883.5.1", null);

        assertNotNull("cda.recordTarget[0].patientRole.patient.birthTime should not have been null",
                oPatient.getBirthTime());
        assertNotNull("cda.recordTarget[0].patientRole.patient.birthTime.value should not have been null", oPatient
                .getBirthTime().getValue());
        assertHL7DateOnly("cda.recordTarget[0].patientRole.patient.birthTime", "19600127000000-0700", oPatient
                .getBirthTime().getValue());

    }

    /**
     * Assert the patient role tag within the Record target tag.
     *
     */
    private void assertRecordTargetPatientRole(POCDMT000040PatientRole oPatientRole) {
        assertNotNull("cda.recordTarget[0].patientRole should not have been null.  ", oPatientRole);

        assertRecordTargetPatientRoleId(oPatientRole.getId());
        assertRecordTargetPatientRoleAddr(oPatientRole.getAddr());
        assertRecordTargetPatientRolePatient(oPatientRole.getPatient());
    }

    /**
     * This is a helper class that will create the list of clinical documents by calling the CdaPdfCreator methods.
     *
     * @param oPtPref The patient preferences object to be converted.
     * @return The CDA document that came out of the transformation.
     */
    private POCDMT000040ClinicalDocument createTheCDADocs(PatientPreferencesType oPtPref) {
        CdaPdfCreator oCreator = new CdaPdfCreator();

        // Test Policy 1
        // --------------
        List<POCDMT000040ClinicalDocument> olCda = null;
        try {
            olCda = oCreator.createCDA(oPtPref);
        } catch (Exception e) {
            fail("Received unexpected exception while creating Cda.  Error: " + e.getMessage());
        }

        assertNotNull("List<Cda> was null.", olCda);
        assertEquals("Size of CDA should have been 0", 1, olCda.size());
        assertNotNull(olCda.get(0));

        return olCda.get(0);
    }

    /**
     * This checkes the ONExplicit object for single string containing the specified value.
     *
     * @param sFieldName The name of the field.
     * @param oHL7On The HL7 type being checked.
     * @param sExpectedName The expcted value.
     */
    private void assertONSimple(String sFieldName, ONExplicit oHL7On, String sExpectedName) {
        assertNotNull(sFieldName + " should not have been null.  ", oHL7On);
        assertNotNull(sFieldName + ".content should not have been null.  ", oHL7On.getContent());
        assertEquals(sFieldName + ".content.size incorrect.  ", 1, oHL7On.getContent().size());
        assertNotNull(sFieldName + ".content[0] should not have been null.  ", oHL7On.getContent().get(0));
        assertTrue(sFieldName + ".content[0] is incorrect class type - it should have been String but was: "
                + oHL7On.getClass().getName(), (oHL7On.getContent().get(0) instanceof String));
        String sValue = (String) oHL7On.getContent().get(0);
        assertEquals(sFieldName + ".content[0] incorrect.  ", sExpectedName, sValue);
    }

    /**
     * This checkes the SCExplicit object for single string containing the specified value.
     *
     * @param sFieldName The name of the field.
     * @param oHL7Sc The HL7 type being checked.
     * @param sExpectedName The expcted value.
     */
    private void assertSCSimple(String sFieldName, SCExplicit oHL7Sc, String sExpectedName) {
        assertNotNull(sFieldName + " should not have been null.  ", oHL7Sc);
        assertNotNull(sFieldName + ".content should not have been null.  ", oHL7Sc.getContent());
        assertEquals(sFieldName + ".content.size incorrect.  ", 1, oHL7Sc.getContent().size());
        assertNotNull(sFieldName + ".content[0] should not have been null.  ", oHL7Sc.getContent().get(0));
        assertTrue(sFieldName + ".content[0] is incorrect class type - it should have been String but was: "
                + oHL7Sc.getClass().getName(), (oHL7Sc.getContent().get(0) instanceof String));
        String sValue = (String) oHL7Sc.getContent().get(0);
        assertEquals(sFieldName + ".content[0] incorrect.  ", sExpectedName, sValue);
    }

    /**
     * Assert that the author (scanner) contained the correct values.
     *
     * @param oAuthor The object containing the author information.
     */
    private void assertAuthorScanner(POCDMT000040Author oAuthor) {
        assertNotNull("cda.author[].time should not have been null.  ", oAuthor.getTime());
        assertNotNull("cda.author[].time.value should not have been null.  ", oAuthor.getTime().getValue());
        assertHL7DateTime("cda.author[].time", "20050329204411-0700", oAuthor.getTime().getValue());

        assertNotNull("cda.author[].assignedAuthor should not have been null.  ", oAuthor.getAssignedAuthor());

        assertNotNull("cda.author[].assignedAuthor.id should not have been null.  ", oAuthor.getAssignedAuthor()
                .getId());
        assertEquals("cda.author[].assignedAuthor.id.size was incorrect.  ", 1, oAuthor.getAssignedAuthor().getId()
                .size());
        assertNotNull("cda.author[].assignedAuthor.id[0] should not have been null.  ", oAuthor.getAssignedAuthor()
                .getId().get(0));
        assertII("cda.author[].assignedAuthor.id[0]", oAuthor.getAssignedAuthor().getId().get(0),
                "1.3.6.4.1.4.1.2835.2.1234", null);

        assertNotNull("cda.author[].assignedAuthor.assignedAuthoringDevice should not have been null.  ", oAuthor
                .getAssignedAuthor().getAssignedAuthoringDevice());
        assertNotNull("cda.author[].assignedAuthor.assignedAuthoringDevice.code should not have been null.  ", oAuthor
                .getAssignedAuthor().getAssignedAuthoringDevice().getCode());
        assertCode("cda.author[].assignedAuthor.assignedAuthoringDevice.code", oAuthor.getAssignedAuthor()
                .getAssignedAuthoringDevice().getCode(), "CAPTURE", "Image Capture", "1.2.840.10008.2.16.4", null);

        assertNotNull(
                "cda.author[].assignedAuthor.assignedAuthoringDevice.manufacturerModelName should not have been null.  ",
                oAuthor.getAssignedAuthor().getAssignedAuthoringDevice().getManufacturerModelName());
        assertSCSimple("cda.author[].assignedAuthor.assignedAuthoringDevice.manufacturerModelName", oAuthor
                .getAssignedAuthor().getAssignedAuthoringDevice().getManufacturerModelName(),
                "SOME SCANNER NAME AND MODEL ");

        assertNotNull("cda.author[].assignedAuthor.assignedAuthoringDevice.softwareName should not have been null.  ",
                oAuthor.getAssignedAuthor().getAssignedAuthoringDevice().getSoftwareName());
        assertSCSimple("cda.author[].assignedAuthor.assignedAuthoringDevice.manufacturerModelName", oAuthor
                .getAssignedAuthor().getAssignedAuthoringDevice().getSoftwareName(), "SCAN SOFTWARE NAME v0.0");

        assertNotNull("cda.author[].assignedAuthor.representedOrganization should not have been null.  ", oAuthor
                .getAssignedAuthor().getRepresentedOrganization());
        assertNotNull("cda.author[].assignedAuthor.representedOrganization.id should not have been null.  ", oAuthor
                .getAssignedAuthor().getRepresentedOrganization().getId());
        assertEquals("cda.author[].assignedAuthor.representedOrganization.id.size incorrect.  ", 1, oAuthor
                .getAssignedAuthor().getRepresentedOrganization().getId().size());
        assertNotNull("cda.author[].assignedAuthor.representedOrganization.id[0] should not have been null.  ", oAuthor
                .getAssignedAuthor().getRepresentedOrganization().getId().get(0));
        assertII("cda.author[].assignedAuthor.representedOrganization.id[0]", oAuthor.getAssignedAuthor()
                .getRepresentedOrganization().getId().get(0), "1.3.6.4.1.4.1.2835.2", null);

        assertNotNull("cda.author[].assignedAuthor.representedOrganization.name should not have been null.  ", oAuthor
                .getAssignedAuthor().getRepresentedOrganization().getName());
        assertEquals("cda.author[].assignedAuthor.representedOrganization.name.size incorrect.  ", 1, oAuthor
                .getAssignedAuthor().getRepresentedOrganization().getName().size());
        assertNotNull("cda.author[].assignedAuthor.representedOrganization.name[0] incorrect.  ", oAuthor
                .getAssignedAuthor().getRepresentedOrganization().getName().get(0));
        assertONSimple("cda.author[].assignedAuthor.representedOrganization.name", oAuthor.getAssignedAuthor()
                .getRepresentedOrganization().getName().get(0), "SOME Scanning Facility");

        assertNotNull("cda.author[].assignedAuthor.representedOrganization.addr should not have been null.  ", oAuthor
                .getAssignedAuthor().getRepresentedOrganization().getAddr());
        assertEquals("cda.author[].assignedAuthor.representedOrganization.addr.size incorrect.  ", 1, oAuthor
                .getAssignedAuthor().getRepresentedOrganization().getAddr().size());
        assertNotNull("cda.author[].assignedAuthor.representedOrganization.addr[0] should not have been null.  ",
                oAuthor.getAssignedAuthor().getRepresentedOrganization().getAddr().get(0));
        assertAddr("", oAuthor.getAssignedAuthor().getRepresentedOrganization().getAddr().get(0), "21 North Ave",
                "Burlington", "MA", "01803", "USA");
    }

    /**
     * Assert that the author (original) contained the correct values.
     *
     * @param oAuthor The object containing the author information.
     */
    private void assertAuthorOriginal(POCDMT000040Author oAuthor) {
        assertNotNull("cda.author[].time should not have been null.  ", oAuthor.getTime());
        assertNotNull("cda.author[].time.value should not have been null.  ", oAuthor.getTime().getValue());
        assertHL7DateOnly("cda.author[].time", "19990522", oAuthor.getTime().getValue());

        assertNotNull("cda.author[].assignedAuthor should not have been null.  ", oAuthor.getAssignedAuthor());

        assertNotNull("cda.author[].assignedAuthor.id should not have been null.  ", oAuthor.getAssignedAuthor()
                .getId());
        assertEquals("cda.author[].assignedAuthor.id.size was incorrect.  ", 1, oAuthor.getAssignedAuthor().getId()
                .size());
        assertNotNull("cda.author[].assignedAuthor.id[0] should not have been null.  ", oAuthor.getAssignedAuthor()
                .getId().get(0));
        assertII("cda.author[].assignedAuthor.id[0]", oAuthor.getAssignedAuthor().getId().get(0), "1.3.5.35.1.4436.7",
                "11111111");

        assertNotNull("cda.author[].assignedAuthor.assignedPerson should not have been null.  ", oAuthor
                .getAssignedAuthor().getAssignedPerson());
        assertNotNull("cda.author[].assignedAuthor.assignedPerson.name should not have been null.  ", oAuthor
                .getAssignedAuthor().getAssignedPerson().getName());
        assertEquals("cda.author[].assignedAuthor.assignedPerson.name.size incorrect.  ", 1, oAuthor
                .getAssignedAuthor().getAssignedPerson().getName().size());
        assertNotNull("cda.author[].assignedAuthor.assignedPerson.name[0] should not have been null.  ", oAuthor
                .getAssignedAuthor().getAssignedPerson().getName().get(0));
        assertName("cda.author[].assignedAuthor.assignedPerson.name[0]", oAuthor.getAssignedAuthor()
                .getAssignedPerson().getName().get(0), "Dr.", "Bernard", "Wiseman", "Sr.");

        assertNotNull("cda.author[].assignedAuthor.representedOrganization should not have been null.  ", oAuthor
                .getAssignedAuthor().getRepresentedOrganization());
        assertNotNull("cda.author[].assignedAuthor.representedOrganization.id should not have been null.  ", oAuthor
                .getAssignedAuthor().getRepresentedOrganization().getId());
        assertEquals("cda.author[].assignedAuthor.representedOrganization.id.size incorrect.  ", 1, oAuthor
                .getAssignedAuthor().getRepresentedOrganization().getId().size());
        assertNotNull("cda.author[].assignedAuthor.representedOrganization.id[0] should not have been null.  ", oAuthor
                .getAssignedAuthor().getRepresentedOrganization().getId().get(0));
        assertII("cda.author[].assignedAuthor.representedOrganization.id[0]", oAuthor.getAssignedAuthor()
                .getRepresentedOrganization().getId().get(0), "1.3.5.35.1.4436.7", "aaaaabbbbb");

        assertNotNull("cda.author[].assignedAuthor.representedOrganization.name should not have been null.  ", oAuthor
                .getAssignedAuthor().getRepresentedOrganization().getName());
        assertEquals("cda.author[].assignedAuthor.representedOrganization.name.size incorrect.  ", 1, oAuthor
                .getAssignedAuthor().getRepresentedOrganization().getName().size());
        assertNotNull("cda.author[].assignedAuthor.representedOrganization.name[0] incorrect.  ", oAuthor
                .getAssignedAuthor().getRepresentedOrganization().getName().get(0));
        assertONSimple("cda.author[].assignedAuthor.representedOrganization.name", oAuthor.getAssignedAuthor()
                .getRepresentedOrganization().getName().get(0), "Dr. Wiseman\"s Clinic");
    }

    /**
     * Assert that the time interval is correct with precision only to the date.
     *
     * @param sFieldName The name of the field.
     * @param oTimeInterval The time interval that is being checked.
     * @param sExpectedLowValue The low date.
     * @param sExpectedHighValue The high date.
     */
    private void assertDateOnlyInterval(String sFieldName, IVLTSExplicit oTimeInterval, String sExpectedLowValue,
            String sExpectedHighValue) {
        assertNotNull(sFieldName + " should not have been null.", oTimeInterval);
        assertNotNull(sFieldName + ".content should not have been null.", oTimeInterval.getContent());
        assertEquals(sFieldName + ".content.size incorrect.", 2, oTimeInterval.getContent().size());

        for (JAXBElement oJaxbElement : oTimeInterval.getContent()) {
            // Start Time
            // ------------
            if ((oJaxbElement.getName() != null) && (oJaxbElement.getName().getLocalPart() != null)
                    && (oJaxbElement.getName().getLocalPart().equals("low")) && (oJaxbElement.getValue() != null)
                    && (oJaxbElement.getValue() instanceof IVXBTSExplicit)) {
                IVXBTSExplicit oHL7LowTime = (IVXBTSExplicit) oJaxbElement.getValue();
                assertHL7DateOnly(sFieldName + ".content[].low", sExpectedLowValue, oHL7LowTime.getValue());
            } // if ((oJaxbElement.getName() != null) &&
              // End Time
              // ------------
            else if ((oJaxbElement.getName() != null) && (oJaxbElement.getName().getLocalPart() != null)
                    && (oJaxbElement.getName().getLocalPart().equals("high")) && (oJaxbElement.getValue() != null)
                    && (oJaxbElement.getValue() instanceof IVXBTSExplicit)) {
                IVXBTSExplicit oHL7HighTime = (IVXBTSExplicit) oJaxbElement.getValue();
                assertHL7DateOnly(sFieldName + ".content[].high", sExpectedHighValue,
                        oHL7HighTime.getValue());
            } // else if ((oJaxbElement.getName() != null) &&
            else {
                fail(sFieldName + ".content[] contained an unknown object type.");
            }
        } // for (JAXBElement oJaxbElement :
          // oCda.getDocumentationOf().get(0).getServiceEvent().getEffectiveTime().getContent())
    }

    /**
     * Test out the Cda attribute fields: Class Code and Mood Code.
     *
     */
    @Test
    public void testCreateCdaAttributes() {
        POCDMT000040ClinicalDocument oCda = createTheCDADocs(oPtPref);

        assertEquals("Class Code incorrect: ", oCda.getClassCode(), ActClassClinicalDocument.DOCCLIN);

        assertNotNull("Mood code should not have been null.", oCda.getMoodCode());
        assertEquals("cda.moodCode - there should be only one of these: ", 1, oCda.getMoodCode().size());
        assertEquals("cda.moodCode incorrect: ", "EVN", oCda.getMoodCode().get(0));
    }

    /**
     * Tests the type ID tag to make sure that it has what was expected.
     *
     */
    @Test
    public void testTypeIdTag() {
        POCDMT000040ClinicalDocument oCda = createTheCDADocs(oPtPref);

        assertNotNull("cda.TypeId should not have been null.", oCda.getTypeId());
        assertEquals("cda.typeId.extension incorrect: ", "POCD_HD000040", oCda.getTypeId().getExtension());
        assertEquals("cda.typeId.root", "2.16.840.1.113883.1.3", oCda.getTypeId().getRoot());
    }

    /**
     * Tests the templateId tag to make sure that it has what was expected.
     *
     */
    @Test
    public void testTemplateIdTag() {
        POCDMT000040ClinicalDocument oCda = createTheCDADocs(oPtPref);

        assertNotNull("cda.templateId should not have been null. ", oCda.getTemplateId());
        assertEquals("cda.templateId - there should exactly one in the array. ", 1, oCda.getTemplateId().size());
        assertNotNull("cda.templateid[0] shouod not be null: ", oCda.getTemplateId().get(0));
        assertNull("cda.templateId.extension should have been null: ", oCda.getTemplateId().get(0).getExtension());
        assertEquals("cda.templateId.root", "1.3.6.1.4.1.19376.1.2.20", oCda.getTemplateId().get(0).getRoot());
    }

    /**
     * Tests the Id tag to make sure that it has what was expected.
     *
     */
    @Test
    public void testIdTag() {
        POCDMT000040ClinicalDocument oCda = createTheCDADocs(oPtPref);

        assertNotNull("cda.Id should not have been null. ", oCda.getId());
        assertEquals("cda.templateId.root", sHomeCommunity, oCda.getId().getRoot());
        assertEquals("cda.templateId.extension should have been null: ", "20.200.1.1", oCda.getId().getExtension());
    }

    /**
     * Tests the Code tag to make sure that it has what was expected.
     *
     */
    @Test
    public void testCodeTag() {
        POCDMT000040ClinicalDocument oCda = createTheCDADocs(oPtPref);

        assertNotNull("cda.code should not have been null. ", oCda.getCode());
        assertEquals("cda.code.code incorrect: ", "34133-9", oCda.getCode().getCode());
        assertEquals("cda.code.codeSystem incorect: ", "2.16.840.1.113883.6.1", oCda.getCode().getCodeSystem());
        assertEquals("cda.code.codeSystemName incorect: ", "LOINC", oCda.getCode().getCodeSystemName());
        assertEquals("cda.code.displayName incorect: ", "SUMMARIZATION OF EPISODE NOTE", oCda.getCode()
                .getDisplayName());
    }

    /**
     * Tests the title tag to make sure that it has what was expected.
     *
     */
    @Test
    public void testTitleTag() {
        POCDMT000040ClinicalDocument oCda = createTheCDADocs(oPtPref);

        assertNotNull("cda.title should not have been null. ", oCda.getTitle());
        assertNotNull("cda.title.content should not have been null.  ", oCda.getTitle().getContent());
        assertEquals("cda.title.content,size incorrect: ", 1, oCda.getTitle().getContent().size());
        assertEquals("cda.title incorrect: ", "Good Health Clinic Care Record Summary", oCda.getTitle()
                .getContent().get(0));
    }

    /**
     * Tests the effectiveTime tag.
     */
    @Test
    public void testEffectiveTimeTag() {
        POCDMT000040ClinicalDocument oCda = createTheCDADocs(oPtPref);

        assertNotNull("cda.effectiveTime should not have been null. ", oCda.getEffectiveTime());
        assertNotNull("cda.effectiveTime.value should not have been null.  ", oCda.getEffectiveTime().getValue());
        assertHL7DateTime("cda.effectiveTime.value incorrect", "20050329204411-0700", oCda.getEffectiveTime()
                .getValue());
    }

    /**
     * Tests the Confidentiality Code tag to make sure that it has what was expected.
     *
     */
    @Test
    public void testConfidentialityCodeTag() {
        POCDMT000040ClinicalDocument oCda = createTheCDADocs(oPtPref);

        assertNotNull("cda.confidentialityCode should not have been null. ", oCda.getConfidentialityCode());
        assertEquals("cda.confidentialityCode.code incorrect: ", "N", oCda.getConfidentialityCode().getCode());
        assertEquals("cda.confidentialityCode.codeSystem incorect: ", "2.16.840.1.113883.5.25", oCda
                .getConfidentialityCode().getCodeSystem());
        assertNull("cda.confidentialityCode.codeSystemName incorect: ", oCda.getConfidentialityCode()
                .getCodeSystemName());
        assertNull("cda.confidentialityCode.displayName incorect: ", oCda.getConfidentialityCode().getDisplayName());
    }

    /**
     * Tests the language code tag to make sure that it has what was expected.
     *
     */
    @Test
    public void testLanguageCodeTag() {
        POCDMT000040ClinicalDocument oCda = createTheCDADocs(oPtPref);

        assertNotNull("cda.languageCode should not have been null. ", oCda.getLanguageCode());
        assertEquals("cda.languageCode.code incorrect: ", "en-US", oCda.getLanguageCode().getCode());
        assertNull("cda.languageCode.codeSystem incorect: ", oCda.getLanguageCode().getCodeSystem());
        assertNull("cda.languageCode.codeSystemName incorect: ", oCda.getLanguageCode().getCodeSystemName());
        assertNull("cda.languageCode.displayName incorect: ", oCda.getLanguageCode().getDisplayName());
    }

    /**
     * Tests the record target tag to make sure that it has what was expected.
     *
     */
    @Test
    public void testRecordTargetTag() {
        POCDMT000040ClinicalDocument oCda = createTheCDADocs(oPtPref);

        assertNotNull("cda.recordTarget should not have been null. ", oCda.getRecordTarget());
        assertEquals("cda.recordTarget array size incorrect.  ", 1, oCda.getRecordTarget().size());
        assertNotNull("cda.recordTarget.get(0) should not have been null. ", oCda.getRecordTarget().get(0));
        assertRecordTargetPatientRole(oCda.getRecordTarget().get(0).getPatientRole());
    }

    /**
     * Tests the author tag to make sure that it has what was expected.
     *
     */
    @Test
    public void testAuthorTag() {
        POCDMT000040ClinicalDocument oCda = createTheCDADocs(oPtPref);

        assertNotNull("cda.author should not have been null. ", oCda.getAuthor());
        assertEquals("cda.author.size was incorrect. ", 2, oCda.getAuthor().size());

        // Check the first element in the list to see what type of author we have.
        // ------------------------------------------------------------------------

        int i = 0;
        for (POCDMT000040Author oHL7Author : oCda.getAuthor()) {
            assertNotNull("cda.author[" + i + "] should not have been null.  ", oHL7Author);
            assertNotNull("cda.author[" + i + "].templateId should not have been null.  ", oHL7Author.getTemplateId());
            assertEquals("cda.author[" + i + "].templateId.size incorrect.  ", 1, oHL7Author.getTemplateId().size());
            assertNotNull("cda.author[" + i + "].templateId[0] should not have been null.  ", oHL7Author
                    .getTemplateId().get(0));
            assertNotNull("cda.author[" + i + "].templateId[0].root should not have been null.  ", oHL7Author
                    .getTemplateId().get(0).getRoot());

            if ((oHL7Author.getTemplateId().get(0).getRoot().equals("1.3.6.1.4.1.19376.1.2.20.1"))) {
                assertAuthorOriginal(oHL7Author);
            } else if ((oHL7Author.getTemplateId().get(0).getRoot().equals("1.3.6.1.4.1.19376.1.2.20.2"))) {
                assertAuthorScanner(oHL7Author);
            } else {
                fail("cda.author[" + i + "].templateId[0].root contained an unepected value: "
                        + oHL7Author.getTemplateId().get(0).getRoot());
            }
            i++;

        } // for (POCDMT000040Author oHL7Author : oCda.getAuthor())
    }

    /**
     * Tests the data enterer tag to make sure that it has what was expected.
     *
     */
    @Test
    public void testDataEntererTag() {
        POCDMT000040ClinicalDocument oCda = createTheCDADocs(oPtPref);

        assertNotNull("cda.dataEnterer should not have been null. ", oCda.getDataEnterer());
        assertNotNull("cda.dataEnterer.templateId should not have been null. ", oCda.getDataEnterer().getTemplateId());
        assertEquals("cda.dataEnterer.templateId.size incorrect. ", 1, oCda.getDataEnterer().getTemplateId().size());
        assertNotNull("cda.dataEnterer.templateId[0] should not have been null. ", oCda.getDataEnterer()
                .getTemplateId().get(0));
        assertII("cda.dataEnterer.templateId[0]", oCda.getDataEnterer().getTemplateId().get(0),
                "1.3.6.1.4.1.19376.1.2.20.3", null);

        assertNotNull("cda.dataEnterer.time should not have been null. ", oCda.getDataEnterer().getTime());
        assertNotNull("cda.dataEnterer.time.value should not have been null. ", oCda.getDataEnterer().getTime()
                .getValue());
        assertHL7DateTime("cda.dataEnterer.time.value", "20050329204411-0700", oCda.getDataEnterer().getTime()
                .getValue());

        assertNotNull("cda.dataEnterer.assignedEntity should not have been null. ", oCda.getDataEnterer()
                .getAssignedEntity());
        assertNotNull("cda.dataEnterer.assignedEntity.id should not have been null. ", oCda.getDataEnterer()
                .getAssignedEntity().getId());
        assertEquals("cda.dataEnterer.assignedEntity.id.size incorrect. ", 1, oCda.getDataEnterer().getAssignedEntity()
                .getId().size());
        assertNotNull("cda.dataEnterer.assignedEntity.id[0] should not have been null. ", oCda.getDataEnterer()
                .getAssignedEntity().getId().get(0));
        assertII("cda.dataEnterer.assignedEntity.id[0]", oCda.getDataEnterer().getAssignedEntity().getId().get(0),
                "1.3.6.4.1.4.1.2835.2", "22222222");

        assertNotNull("cda.dataEnterer.assignedEntity.assignedPerson should not have been null. ", oCda
                .getDataEnterer().getAssignedEntity().getAssignedPerson());
        assertNotNull("cda.dataEnterer.assignedEntity.assignedPerson.name should not have been null. ", oCda
                .getDataEnterer().getAssignedEntity().getAssignedPerson().getName());
        assertEquals("cda.dataEnterer.assignedEntity.assignedPerson.name.size incorrect. ", 1, oCda.getDataEnterer()
                .getAssignedEntity().getAssignedPerson().getName().size());
        assertNotNull("cda.dataEnterer.assignedEntity.assignedPerson.name[0] should not have been null. ", oCda
                .getDataEnterer().getAssignedEntity().getAssignedPerson().getName().get(0));
        assertName("cda.dataEnterer.assignedEntity.assignedPerson.name[0]", oCda.getDataEnterer().getAssignedEntity()
                .getAssignedPerson().getName().get(0), "Mrs.", "Bernice", "Smith", null);

    }

    /**
     * Tests the Custodian tag to make sure that it has what was expected.
     *
     */
    @Test
    public void testCustodianTag() {
        POCDMT000040ClinicalDocument oCda = createTheCDADocs(oPtPref);

        assertNotNull("cda.custodian should not have been null. ", oCda.getCustodian());
        assertNotNull("cda.custodian.assignedCustodian should not have been null. ", oCda.getCustodian()
                .getAssignedCustodian());
        assertNotNull("cda.custodian.assignedCustodian.representedCustodianOrganization should not have been null. ",
                oCda.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization());

        assertNotNull(
                "cda.custodian.assignedCustodian.representedCustodianOrganization.id should not have been null. ", oCda
                        .getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getId());
        assertEquals("cda.custodian.assignedCustodian.representedCustodianOrganization.id.size incorrect. ", 1, oCda
                .getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getId().size());
        assertNotNull(
                "cda.custodian.assignedCustodian.representedCustodianOrganization.id[0] should not have been null. ",
                oCda.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getId().get(0));
        assertII("cda.custodian.assignedCustodian.representedCustodianOrganization.id[0]", oCda.getCustodian()
                .getAssignedCustodian().getRepresentedCustodianOrganization().getId().get(0), "1.3.6.4.1.4.1.2835.2",
                null);

        assertNotNull(
                "cda.custodian.assignedCustodian.representedCustodianOrganization.name should not have been null. ",
                oCda.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getName());
        assertONSimple("cda.custodian.assignedCustodian.representedCustodianOrganization.name", oCda.getCustodian()
                .getAssignedCustodian().getRepresentedCustodianOrganization().getName(), "SOME Scanning Facility");

        assertNotNull(
                "cda.custodian.assignedCustodian.representedCustodianOrganization.addr should not have been null. ",
                oCda.getCustodian().getAssignedCustodian().getRepresentedCustodianOrganization().getAddr());
        assertAddr("cda.custodian.assignedCustodian.representedCustodianOrganization.addr", oCda.getCustodian()
                .getAssignedCustodian().getRepresentedCustodianOrganization().getAddr(), "21 North Ave", "Burlington",
                "MA", "01803", "USA");

    }

    /**
     * Tests the Legal Authenticator tag to make sure that it has what was expected.
     *
     */
    @Test
    public void testLegalAuthenticatorTag() {
        POCDMT000040ClinicalDocument oCda = createTheCDADocs(oPtPref);

        assertNotNull("cda.legalAuthenticator should not have been null. ", oCda.getLegalAuthenticator());
        assertNotNull("cda.legalAuthenticator.time should not have been null. ", oCda.getLegalAuthenticator().getTime());
        assertNotNull("cda.legalAuthenticator.time.value should not have been null. ", oCda.getLegalAuthenticator()
                .getTime().getValue());
        assertHL7DateOnly("cda.legalAuthenticator.time.value", "19990522000000-0600", oCda.getLegalAuthenticator()
                .getTime().getValue());

        assertNotNull("cda.legalAuthenticator.signatureCode should not have been null. ", oCda.getLegalAuthenticator()
                .getSignatureCode());
        assertCS("", oCda.getLegalAuthenticator().getSignatureCode(), "S", null, null, null);

        assertNotNull("cda.legalAuthenticator.assignedEntity should not have been null. ", oCda.getLegalAuthenticator()
                .getAssignedEntity());
        assertNotNull("cda.legalAuthenticator.assignedEntity.id should not have been null. ", oCda
                .getLegalAuthenticator().getAssignedEntity().getId());
        assertEquals("cda.legalAuthenticator.assignedEntity.id.size incorrect. ", 1, oCda.getLegalAuthenticator()
                .getAssignedEntity().getId().size());
        assertNotNull("cda.legalAuthenticator.assignedEntity.id[0] should not have been null. ", oCda
                .getLegalAuthenticator().getAssignedEntity().getId().get(0));
        assertII("cda.legalAuthenticator.assignedEntity.id[0]", oCda.getLegalAuthenticator().getAssignedEntity()
                .getId().get(0), "1.3.5.35.1.4436.7", "11111111");

        assertNotNull("cda.legalAuthenticator.assignedEntity.assignedPerson should not have been null. ", oCda
                .getLegalAuthenticator().getAssignedEntity().getAssignedPerson());
        assertNotNull("cda.legalAuthenticator.assignedEntity.assignedPerson.name should not have been null. ", oCda
                .getLegalAuthenticator().getAssignedEntity().getAssignedPerson().getName());
        assertEquals("cda.legalAuthenticator.assignedEntity.assignedPerson.name.size incorrect. ", 1, oCda
                .getLegalAuthenticator().getAssignedEntity().getAssignedPerson().getName().size());
        assertNotNull("cda.legalAuthenticator.assignedEntity.assignedPerson.name[0] should not have been null. ", oCda
                .getLegalAuthenticator().getAssignedEntity().getAssignedPerson().getName().get(0));
        assertName("cda.legalAuthenticator.assignedEntity.assignedPerson.name[0]", oCda.getLegalAuthenticator()
                .getAssignedEntity().getAssignedPerson().getName().get(0), "Dr.", "Bernard", "Wiseman", "Sr.");

    }

    /**
     * Tests the Documentation Of tag to make sure that it has what was expected.
     *
     */
    @Test
    public void testDocumentationOfTag() {
        POCDMT000040ClinicalDocument oCda = createTheCDADocs(oPtPref);

        assertNotNull("cda.documentationOf should not have been null. ", oCda.getDocumentationOf());
        assertEquals("cda.documentationOf.size incorrect. ", 1, oCda.getDocumentationOf().size());
        assertNotNull("cda.documentationOf[0] should not have been null. ", oCda.getDocumentationOf().get(0));

        assertNotNull("cda.documentationOf[0].typeCode should not have been null. ", oCda.getDocumentationOf().get(0)
                .getTypeCode());
        assertEquals("cda.documentationOf[0].typeCode.size incorrect. ", 1, oCda.getDocumentationOf().get(0)
                .getTypeCode().size());
        assertEquals("cda.documentationOf[0].typeCode[0] incorrect. ", "DOC", oCda.getDocumentationOf().get(0)
                .getTypeCode().get(0));

        assertNotNull("cda.documentationOf[0].serviceEvent should not have been null. ",
                oCda.getDocumentationOf().get(0).getServiceEvent());

        assertNotNull("cda.documentationOf[0].serviceEvent.moodCode should not have been null. ", oCda
                .getDocumentationOf().get(0).getServiceEvent().getMoodCode());
        assertEquals("cda.documentationOf[0].serviceEvent.moodCode.size incorrect. ", 1,
                oCda.getDocumentationOf().get(0).getServiceEvent().getMoodCode().size());
        assertNotNull("cda.documentationOf[0].serviceEvent.moodCode[0] should not have been null. ", oCda
                .getDocumentationOf().get(0).getServiceEvent().getMoodCode().get(0));
        assertEquals("cda.documentationOf[0].serviceEvent.moodCode[0] incorrect. ", "EVN", oCda.getDocumentationOf()
                .get(0).getServiceEvent().getMoodCode().get(0));

        assertNotNull("cda.documentationOf[0].serviceEvent.classCode should not have been null. ", oCda
                .getDocumentationOf().get(0).getServiceEvent().getClassCode());
        assertEquals("cda.documentationOf[0].serviceEvent.classCode.size should not have been null. ", 1, oCda
                .getDocumentationOf().get(0).getServiceEvent().getClassCode().size());
        assertNotNull("cda.documentationOf[0].serviceEvent.classCode[0] should not have been null. ", oCda
                .getDocumentationOf().get(0).getServiceEvent().getClassCode().get(0));
        assertEquals("cda.documentationOf[0].serviceEvent.classCode[0] incorrect. ", "ACT", oCda.getDocumentationOf()
                .get(0).getServiceEvent().getClassCode().get(0));

        assertNotNull("cda.documentationOf[0].serviceEvent.templateId should not have been null. ", oCda
                .getDocumentationOf().get(0).getServiceEvent().getTemplateId());
        assertEquals("cda.documentationOf[0].serviceEvent.templateId.size incorrect. ", 1, oCda.getDocumentationOf()
                .get(0).getServiceEvent().getTemplateId().size());
        assertNotNull("cda.documentationOf[0].serviceEvent.templateId[0] should not have been null. ", oCda
                .getDocumentationOf().get(0).getServiceEvent().getTemplateId().get(0));
        assertII("cda.documentationOf[0].serviceEvent.templateId[0]", oCda.getDocumentationOf().get(0)
                .getServiceEvent().getTemplateId().get(0), "1.3.6.1.4.1.19376.1.5.3.1.2.6", null);

        assertNotNull("cda.documentationOf[0].serviceEvent.code should not have been null. ", oCda.getDocumentationOf()
                .get(0).getServiceEvent().getCode());
        assertCode("cda.documentationOf[0].serviceEvent.code", oCda.getDocumentationOf().get(0).getServiceEvent()
                .getCode(), "417370002", "Consent Given for Upload to National Shared Electronic Record",
                "2.16.840.1.113883.6.96", "SNOMED CT");

        assertNotNull("cda.documentationOf[0].serviceEvent.effectiveTime should not have been null. ", oCda
                .getDocumentationOf().get(0).getServiceEvent().getEffectiveTime());
        assertDateOnlyInterval("cda.documentationOf[0].serviceEvent.effectiveTime", oCda.getDocumentationOf().get(0)
                .getServiceEvent().getEffectiveTime(), "19800127", "19990522");
    }

    /**
     * Tests the Component tag to make sure that it has what was expected.
     *
     */
    @Test
    public void testComponentTag() {
        POCDMT000040ClinicalDocument oCda = createTheCDADocs(oPtPref);

        assertNotNull("cda.component should not have been null. ", oCda.getComponent());
        assertNotNull("cda.component.nonXMLBody should not have been null. ", oCda.getComponent().getNonXMLBody());
        assertNotNull("cda.component.nonXMLBody.text should not have been null. ", oCda.getComponent().getNonXMLBody()
                .getText());
        assertEquals("cda.component.nonXMLBody.text.mediaType incorrect. ", "application/pdf", oCda.getComponent()
                .getNonXMLBody().getText().getMediaType());
        assertNotNull("cda.component.nonXMLBody.text.representation should not have been null. ", oCda.getComponent()
                .getNonXMLBody().getText().getRepresentation());
        assertEquals("cda.component.nonXMLBody.text.representation incorrect. ", "B64", oCda.getComponent()
                .getNonXMLBody().getText().getRepresentation().value());
        assertNotNull("cda.component.nonXMLBody.text.content should not have been null. ", oCda.getComponent()
                .getNonXMLBody().getText().getContent());
        assertEquals("cda.component.nonXMLBody.text.content.size incorrect. ", 1, oCda.getComponent().getNonXMLBody()
                .getText().getContent().size());
        assertNotNull("cda.component.nonXMLBody.text.content[0] should not have been null. ", oCda.getComponent()
                .getNonXMLBody().getText().getContent().get(0));
        assertTrue("cda.component.nonXMLBody.text.content[0] instanceof String failed.  It was:  "
                + oCda.getComponent().getNonXMLBody().getText().getContent().get(0).getClass().getName(), (oCda
                .getComponent().getNonXMLBody().getText().getContent().get(0) instanceof String));
        String sText = (String) oCda.getComponent().getNonXMLBody().getText().getContent().get(0);
        assertEquals("The Binary Document was corrupted in the transformation: ", BINARY_DOC_PART1 + BINARY_DOC_PART2,
                sText);

    }

}
