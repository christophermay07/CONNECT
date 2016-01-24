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

import gov.hhs.fha.nhinc.common.nhinccommon.AddressType;
import gov.hhs.fha.nhinc.common.nhinccommon.AddressesType;
import gov.hhs.fha.nhinc.common.nhinccommon.CeType;
import gov.hhs.fha.nhinc.common.nhinccommon.PersonNameType;
import gov.hhs.fha.nhinc.common.nhinccommonadapter.BinaryDocumentPolicyCriterionType;
import gov.hhs.fha.nhinc.common.nhinccommonadapter.BinaryDocumentStoreActionType;
import gov.hhs.fha.nhinc.common.nhinccommonadapter.PolicyCustodianInfoType;
import gov.hhs.fha.nhinc.common.nhinccommonadapter.PolicyDataEntererInfoType;
import gov.hhs.fha.nhinc.common.nhinccommonadapter.PolicyLegalAuthenticatorType;
import gov.hhs.fha.nhinc.common.nhinccommonadapter.PolicyOriginalAuthorInfoType;
import gov.hhs.fha.nhinc.common.nhinccommonadapter.PolicyPatientInfoType;
import gov.hhs.fha.nhinc.common.nhinccommonadapter.PolicyScannerAuthorInfoType;
import java.io.Serializable;
import java.util.List;
import javax.xml.bind.JAXBElement;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hl7.v3.ADExplicit;
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
import org.hl7.v3.IVXBTSExplicit;
import org.hl7.v3.ONExplicit;
import org.hl7.v3.PNExplicit;
import org.hl7.v3.POCDMT000040Author;
import org.hl7.v3.POCDMT000040ClinicalDocument;
import org.hl7.v3.POCDMT000040Custodian;
import org.hl7.v3.POCDMT000040DataEnterer;
import org.hl7.v3.POCDMT000040LegalAuthenticator;
import org.hl7.v3.POCDMT000040PatientRole;
import org.hl7.v3.SCExplicit;

/**
 * This class is used to extract information from a CDA document containing a PDF.
 *
 * @author Les Westberg
 */
public class CdaPdfExtractor {

    /**
     * Create a CE from the given data.
     *
     * @param sCode The code.
     * @param sDisplayName The display text.
     * @param sCodeSystem The code system.
     * @param sCodeSystemName The name of the code system.
     * @return
     */
    private CeType createCe(String sCode, String sDisplayName, String sCodeSystem, String sCodeSystemName) {
        CeType oCE = new CeType();
        boolean bHaveData = false;

        if (sCode != null) {
            oCE.setCode(sCode);
            bHaveData = true;
        }

        if (sDisplayName != null) {
            oCE.setDisplayName(sDisplayName);
            bHaveData = true;
        }

        if (sCodeSystem != null) {
            oCE.setCodeSystem(sCodeSystem);
            bHaveData = true;
        }

        if (sCodeSystemName != null) {
            oCE.setCodeSystemName(sCodeSystemName);
            bHaveData = true;
        }

        if (bHaveData) {
            return oCE;
        } else {
            return null;
        }
    }

    /**
     * Create a CeType from an HL7 CE data type.
     *
     * @param oHl7Ce The HL7 CE data type.
     * @return The Ce that is returned.
     */
    private CeType createCe(CE oHl7Ce) {
        CeType oCe = null;

        if (oHl7Ce != null) {
            oCe = createCe(oHl7Ce.getCode(), oHl7Ce.getDisplayName(), oHl7Ce.getCodeSystem(),
                oHl7Ce.getCodeSystemName());
        }

        return oCe;
    }

    /**
     * Create a CeType from an HL7 CS data type.
     *
     * @param oHl7Cs The HL7 CS data type.
     * @return The Ce that is returned.
     */
    private CeType createCe(CS oHl7Cs) {
        CeType oCe = null;

        if (oHl7Cs != null) {
            oCe = createCe(oHl7Cs.getCode(), oHl7Cs.getDisplayName(), oHl7Cs.getCodeSystem(),
                oHl7Cs.getCodeSystemName());
        }

        return oCe;
    }

    /**
     * Create an Address from the given HL7 address.
     *
     * @param oHL7Addr The address in an HL7 format.
     * @return The address that is returned.
     */
    private AddressType createAddr(ADExplicit oHL7Addr) {
        AddressType oAddr = new AddressType();
        boolean bHaveData = false;

        if (oHL7Addr == null) {
            // Get out of here there is nothing to do.
            return null;
        }

        // Addresses are set up in HL7 in a somewhat strange way. They can
        // be either text or tags or both.... Since we are the ones creating
        // these strings. We will be creating them with individual tags. So
        // we should only see individual tags. We will ignore anything that
        // is not a tag part of the address.
        // ---------------------------------------------------------------------------
        if (CollectionUtils.isNotEmpty(oHL7Addr.getContent())) {
            for (Serializable oSerialElement : oHL7Addr.getContent()) {
                if (oSerialElement instanceof JAXBElement) {
                    JAXBElement oJAXBElement = (JAXBElement) oSerialElement;

                    if (oJAXBElement.getValue() != null) {
                        if (oJAXBElement.getValue() instanceof AdxpExplicitStreetAddressLine) {
                            AdxpExplicitStreetAddressLine oStreetAddressLine
                                = (AdxpExplicitStreetAddressLine) oJAXBElement.getValue();
                            if (oStreetAddressLine != null && oStreetAddressLine.getContent() != null) {
                                oAddr.setStreetAddress(oStreetAddressLine.getContent());
                                bHaveData = true;
                            }
                        } else if (oJAXBElement.getValue() instanceof AdxpExplicitCity) {
                            AdxpExplicitCity oCity = (AdxpExplicitCity) oJAXBElement.getValue();
                            if (oCity != null && oCity.getContent() != null) {
                                oAddr.setCity(oCity.getContent());
                                bHaveData = true;
                            }
                        } else if (oJAXBElement.getValue() instanceof AdxpExplicitState) {
                            AdxpExplicitState oState = (AdxpExplicitState) oJAXBElement.getValue();
                            if (oState != null && oState.getContent() != null) {
                                oAddr.setState(oState.getContent());
                                bHaveData = true;
                            }
                        } else if (oJAXBElement.getValue() instanceof AdxpExplicitPostalCode) {
                            AdxpExplicitPostalCode oPostalCode = (AdxpExplicitPostalCode) oJAXBElement.getValue();
                            if (oPostalCode != null && oPostalCode.getContent() != null) {
                                oAddr.setZipCode(oPostalCode.getContent());
                                bHaveData = true;
                            }
                        } else if (oJAXBElement.getValue() instanceof AdxpExplicitCountry) {
                            AdxpExplicitCountry oCountry = (AdxpExplicitCountry) oJAXBElement.getValue();
                            if (oCountry != null && oCountry.getContent() != null) {
                                oAddr.setCountry(oCountry.getContent());
                                bHaveData = true;
                            }
                        }
                    }
                }
            }
        }

        if (bHaveData) {
            return oAddr;
        } else {
            return null;
        }
    }

    /**
     * Create a Person Name from the given HL7 name.
     *
     * @param oHL7Pn The person name in an HL7 format.
     * @return The person name that is returned.
     */
    private PersonNameType createPersonName(PNExplicit oHL7Pn) {
        PersonNameType oPersonName = new PersonNameType();
        boolean bHaveData = false;

        if (oHL7Pn == null) {
            // Get out of here, there is nothing to do.
            return null;
        }

        // Addresses are set up in HL7 in a somewhat strange way. They can
        // be either text or tags or both.... Since we are the ones creating
        // these strings. We will be creating them with individual tags. So
        // we should only see individual tags. We will ignore anything that
        // is not a tag part of the address.
        // ---------------------------------------------------------------------------
        if (CollectionUtils.isNotEmpty(oHL7Pn.getContent())) {
            for (Serializable oSerialElement : oHL7Pn.getContent()) {
                if (oSerialElement instanceof JAXBElement) {
                    JAXBElement oJAXBElement = (JAXBElement) oSerialElement;

                    if (oJAXBElement.getValue() != null) {
                        if (oJAXBElement.getValue() instanceof EnExplicitPrefix) {
                            EnExplicitPrefix oPrefix = (EnExplicitPrefix) oJAXBElement.getValue();
                            if (oPrefix != null && oPrefix.getContent() != null) {
                                oPersonName.setPrefix(oPrefix.getContent());
                                bHaveData = true;
                            }
                        } else if (oJAXBElement.getValue() instanceof EnExplicitGiven) {
                            EnExplicitGiven oGiven = (EnExplicitGiven) oJAXBElement.getValue();
                            if (oGiven != null && oGiven.getContent() != null) {
                                oPersonName.setGivenName(oGiven.getContent());
                                bHaveData = true;
                            }
                        } else if (oJAXBElement.getValue() instanceof EnExplicitFamily) {
                            EnExplicitFamily oFamily = (EnExplicitFamily) oJAXBElement.getValue();
                            if (oFamily != null && oFamily.getContent() != null) {
                                oPersonName.setFamilyName(oFamily.getContent());
                                bHaveData = true;
                            }
                        } else if (oJAXBElement.getValue() instanceof EnExplicitSuffix) {
                            EnExplicitSuffix oSuffix = (EnExplicitSuffix) oJAXBElement.getValue();
                            if (oSuffix != null && oSuffix.getContent() != null) {
                                oPersonName.setSuffix(oSuffix.getContent());
                                bHaveData = true;
                            }
                        }
                    }
                }
            }
        }

        if (bHaveData) {
            return oPersonName;
        } else {
            return null;
        }
    }

    /**
     * Create Addresses from the given HL7 addresses.
     *
     * @param oHL7Addrs The set of addresses to get the data from.
     * @return The internal addresses representation.
     */
    private AddressesType createAddrs(List<ADExplicit> oHL7Addrs) {
        AddressesType oAddrs = new AddressesType();

        if (CollectionUtils.isNotEmpty(oHL7Addrs)) {
            for (ADExplicit oHL7Addr : oHL7Addrs) {
                if (oHL7Addr != null) {
                    AddressType oAddr = createAddr(oHL7Addr);
                    if (oAddr != null) {
                        oAddrs.getAddress().add(oAddr);
                    }
                }
            }
        }

        if (!oAddrs.getAddress().isEmpty()) {
            return oAddrs;
        } else {
            return null;
        }
    }

    /**
     * This method creates a patient info object from the information in the PatientInfo object.
     *
     * @param oHL7PatientRole The HL7 patient role information.
     * @return The Patient Info object to be reutnred.
     * @throws AdapterPIPException This exception is thrown if there is an exception thrown in the conversion.
     */
    private PolicyPatientInfoType createPatientInfo(POCDMT000040PatientRole oHL7PatientRole)
        throws AdapterPIPException {

        PolicyPatientInfoType oPatientInfo = new PolicyPatientInfoType();
        boolean bHaveData = false;

        // Patient Address
        // ----------------
        if (oHL7PatientRole.getAddr() != null) {
            AddressesType oAddrs = createAddrs(oHL7PatientRole.getAddr());
            if (oAddrs != null) {
                oPatientInfo.setAddr(oAddrs);
                bHaveData = true;
            }
        }

        // Patient Name
        // --------------
        if (oHL7PatientRole.getPatient() != null
            && CollectionUtils.isNotEmpty(oHL7PatientRole.getPatient().getName())) {

            // If there is more than one name, this will only collect the first instance of the name.
            // ----------------------------------------------------------------------------------------
            PersonNameType oPersonName = createPersonName(oHL7PatientRole.getPatient().getName().get(0));
            if (oPersonName != null) {
                oPatientInfo.setName(oPersonName);
                bHaveData = true;
            }
        }

        // Patient Gender
        // ---------------
        if (oHL7PatientRole.getPatient() != null
            && oHL7PatientRole.getPatient().getAdministrativeGenderCode() != null) {

            CeType oGender = createCe(oHL7PatientRole.getPatient().getAdministrativeGenderCode());
            if (oGender != null) {
                oPatientInfo.setGender(oGender);
                bHaveData = true;
            }
        }

        // Patient Birth Time
        // --------------------
        if (oHL7PatientRole.getPatient() != null && oHL7PatientRole.getPatient().getBirthTime() != null
            && oHL7PatientRole.getPatient().getBirthTime().getValue() != null) {

            oPatientInfo.setBirthTime(oHL7PatientRole.getPatient().getBirthTime().getValue());
            bHaveData = true;
        }

        if (bHaveData) {
            return oPatientInfo;
        } else {
            return null;
        }
    }

    /**
     * This method looks through the authors looking for the specific author specified by the given template ID.
     *
     * @param oHL7Authors The array of HL7 authors.
     * @param sTemplateId The template ID that we are looking for.
     * @return The Author with that tempate ID.
     */
    private POCDMT000040Author findAuthor(List<POCDMT000040Author> oHL7Authors, String sTemplateId) {
        if (CollectionUtils.isEmpty(oHL7Authors)) {
            return null;
        }

        for (POCDMT000040Author oHL7Author : oHL7Authors) {
            if (CollectionUtils.isNotEmpty(oHL7Author.getTemplateId())) {
                for (II oTemplateId : oHL7Author.getTemplateId()) {
                    if (oTemplateId.getRoot() != null && oTemplateId.getRoot().equals(sTemplateId)) {
                        return oHL7Author;
                    }
                }
            }
        }

        // If we got here - we never found the author we were looking for....
        // -------------------------------------------------------------------
        return null;
    }

    /**
     * This message locates the original author information in the array of authors and converts the data to a
     * PolicyOriginalAuthorInfoType object.
     *
     * @param oHL7Authors The set of authors in the CDA document.
     * @return The original author information extracted from the HL7 authors.
     * @throws AdapterPIPException This exception is thrown if there is an error in the conversion.
     */
    private PolicyOriginalAuthorInfoType createOriginalAuthor(List<POCDMT000040Author> oHL7Authors)
        throws AdapterPIPException {
        PolicyOriginalAuthorInfoType oOriginalAuthor = new PolicyOriginalAuthorInfoType();
        boolean bHaveData = false;

        // Original author information will have a specific template ID - locate that template ID and then extract the
        // data from there.
        // -----------------------------------------------------------------------------------------------------------
        POCDMT000040Author oHL7Author = findAuthor(oHL7Authors, CDAConstants.TEMPLATE_ID_ROOT_AUTHOR_ORIGINAL);
        if (oHL7Author != null) {
            // Author time
            // -------------
            if (oHL7Author.getTime() != null && StringUtils.isNotEmpty(oHL7Author.getTime().getValue())) {
                oOriginalAuthor.setAuthorTime(oHL7Author.getTime().getValue());
                bHaveData = true;
            }

            // There should only be one - if more, then we will use the first.
            if (oHL7Author.getAssignedAuthor() != null
                && CollectionUtils.isNotEmpty(oHL7Author.getAssignedAuthor().getId())
                && oHL7Author.getAssignedAuthor().getId().get(0) != null) {

                // Assigned Person Assigning Authority
                // -------------------------------------
                if (StringUtils.isNotEmpty(oHL7Author.getAssignedAuthor().getId().get(0).getRoot())) {
                    oOriginalAuthor.setAuthorIdAssigningAuthority(oHL7Author.getAssignedAuthor().getId().get(0)
                        .getRoot());
                    bHaveData = true;
                }

                // Assigned Person Id
                // -------------------
                if (StringUtils.isNotEmpty(oHL7Author.getAssignedAuthor().getId().get(0).getExtension())) {
                    oOriginalAuthor.setAuthorId(oHL7Author.getAssignedAuthor().getId().get(0).getExtension());
                    bHaveData = true;
                }
            }

            // Author name
            // ------------
            if (oHL7Author.getAssignedAuthor() != null
                && oHL7Author.getAssignedAuthor().getAssignedPerson() != null
                && CollectionUtils.isNotEmpty(oHL7Author.getAssignedAuthor().getAssignedPerson().getName())
                && oHL7Author.getAssignedAuthor().getAssignedPerson().getName().get(0) != null) {

                // Should only be one name - if more, take the first.
                PersonNameType oPersonName = createPersonName(oHL7Author.getAssignedAuthor().getAssignedPerson()
                    .getName().get(0));
                if (oPersonName != null) {
                    oOriginalAuthor.setName(oPersonName);
                    bHaveData = true;
                }
            }

            // Should only be one - if more, take the first.
            if (oHL7Author.getAssignedAuthor() != null
                && oHL7Author.getAssignedAuthor().getRepresentedOrganization() != null) {

                if (CollectionUtils.isNotEmpty(oHL7Author.getAssignedAuthor().getRepresentedOrganization().getId())
                    && oHL7Author.getAssignedAuthor().getRepresentedOrganization().getId().get(0) != null) {

                    // Represented organization ID assigning authority
                    // -------------------------------------------------
                    if (StringUtils.isNotEmpty(oHL7Author.getAssignedAuthor().getRepresentedOrganization().getId()
                        .get(0).getRoot())) {

                        oOriginalAuthor.setRepresentedOrganizationIdAssigningAuthority(oHL7Author.getAssignedAuthor()
                            .getRepresentedOrganization().getId().get(0).getRoot());
                        bHaveData = true;
                    }

                    // Represented organization ID
                    // ----------------------------
                    if (StringUtils.isNotEmpty(oHL7Author.getAssignedAuthor().getRepresentedOrganization().getId()
                        .get(0).getExtension())) {

                        oOriginalAuthor.setRepresentedOrganizationId(oHL7Author.getAssignedAuthor()
                            .getRepresentedOrganization().getId().get(0).getExtension());
                        bHaveData = true;
                    }
                }

                // Represented Organization Name
                // ------------------------------
                // Should only be one - if more then take the first.
                if (CollectionUtils.isNotEmpty(oHL7Author.getAssignedAuthor().getRepresentedOrganization().getName())
                    && (oHL7Author.getAssignedAuthor().getRepresentedOrganization().getName().get(0) != null)
                    && CollectionUtils.isNotEmpty(oHL7Author.getAssignedAuthor().getRepresentedOrganization().getName()
                        .get(0).getContent())) {

                    // In this case we should only have serializable objects that are "Strings". Gather all of the
                    // strings up and concatenate them together.
                    // -----------------------------------------------------------------------------------------------
                    StringBuilder sbName = new StringBuilder();
                    for (Serializable oSerialElement : oHL7Author.getAssignedAuthor().getRepresentedOrganization()
                        .getName().get(0).getContent()) {

                        if (oSerialElement instanceof String) {
                            String sName = (String) oSerialElement;
                            if (!sName.isEmpty()) {
                                sbName.append(sName);
                            }
                        }
                    }

                    if (sbName.length() > 0) {
                        oOriginalAuthor.setRepresentedOrganizationName(sbName.toString());
                        bHaveData = true;
                    }
                }
            }
        }

        if (bHaveData) {
            return oOriginalAuthor;
        } else {
            return null;
        }
    }

    /**
     * Several of the HL7 types end up down with a List of serializable objects. In some cases all we want is the string
     * values from it. So this method concatenates the string values from it and returns the concatenated string.
     *
     * @param olHL7Serializable The list of Serializable objects.
     * @return The concatenated set of strings.
     */
    private String extractStringFromSerializableArray(List<Serializable> olHL7Serializable) {
        String sText = null;

        if (CollectionUtils.isNotEmpty(olHL7Serializable)) {
            StringBuilder sbText = new StringBuilder();
            for (Serializable oSerialElement : olHL7Serializable) {
                if (oSerialElement instanceof String) {
                    String sElement = (String) oSerialElement;
                    if (!sElement.isEmpty()) {
                        sbText.append(sElement);
                    }
                }
            }

            if (sbText.length() > 0) {
                sText = sbText.toString();
            }
        }

        return sText;
    }

    /**
     * This is a helper method that extracts strings from an SC data type. This may contain multiple strings. if it
     * does, they will be concatenated together.
     *
     * @param oHL7Sc The HL7 node to extract the strings from.
     * @return The string that was obtained from the HL7 node.
     */
    private String extractStringFromSC(SCExplicit oHL7Sc) {
        String sText = null;

        if (oHL7Sc != null && CollectionUtils.isNotEmpty(oHL7Sc.getContent())) {
            sText = extractStringFromSerializableArray(oHL7Sc.getContent());
        }

        return sText;
    }

    /**
     * This is a helper method that extracts strings from an ON data type. This may contain multiple strings. if it
     * does, they will be concatenated together.
     *
     * @param oHL7On The HL7 node to extract the strings from.
     * @return The string that was obtained from the HL7 node.
     */
    private String extractStringFromON(ONExplicit oHL7On) {
        String sText = null;

        if (oHL7On != null && CollectionUtils.isNotEmpty(oHL7On.getContent())) {
            sText = extractStringFromSerializableArray(oHL7On.getContent());
        }

        return sText;
    }

    /**
     * This message locates the scanner author information in the array of authors and converts the data to a
     * PolicyScannerAuthorInfoType object.
     *
     * @param oHL7Authors The set of authors in the CDA document.
     * @return The scanner author information extracted from the HL7 authors.
     * @throws AdapterPIPException This exception is thrown if there is an error in the conversion.
     */
    private PolicyScannerAuthorInfoType createScannerAuthor(List<POCDMT000040Author> oHL7Authors)
        throws AdapterPIPException {

        PolicyScannerAuthorInfoType oScannerAuthor = new PolicyScannerAuthorInfoType();
        boolean bHaveData = false;

        // Scanner author information will have a specific template ID - locate that template ID and then extract the
        // data from there.
        // ------------------------------------------------------------------------------------------------------------
        POCDMT000040Author oHL7Author = findAuthor(oHL7Authors, CDAConstants.TEMPLATE_ID_ROOT_AUTHOR_SCANNER);
        if (oHL7Author != null) {
            // Author time
            // -------------
            if (oHL7Author.getTime() != null && StringUtils.isNotEmpty(oHL7Author.getTime().getValue())) {
                oScannerAuthor.setAuthorTime(oHL7Author.getTime().getValue());
                bHaveData = true;
            }

            // There should only be one - if more, then we will use the first.
            if (oHL7Author.getAssignedAuthor() != null
                && CollectionUtils.isNotEmpty(oHL7Author.getAssignedAuthor().getId())
                && oHL7Author.getAssignedAuthor().getId().get(0) != null) {

                // Assigned Person Assigning Authority
                // -------------------------------------
                if (StringUtils.isNotEmpty(oHL7Author.getAssignedAuthor().getId().get(0).getRoot())) {
                    oScannerAuthor.setAuthorIdAssigningAuthority(oHL7Author.getAssignedAuthor().getId().get(0)
                        .getRoot());
                    bHaveData = true;
                }

                // Assigned Person Id
                // -------------------
                if (StringUtils.isNotEmpty(oHL7Author.getAssignedAuthor().getId().get(0).getExtension())) {
                    oScannerAuthor.setAuthorId(oHL7Author.getAssignedAuthor().getId().get(0).getExtension());
                    bHaveData = true;
                }
            }

            if ((oHL7Author.getAssignedAuthor() != null)
                && (oHL7Author.getAssignedAuthor().getAssignedAuthoringDevice() != null)) {
                // Author Device Code
                // --------------------
                if (oHL7Author.getAssignedAuthor().getAssignedAuthoringDevice().getCode() != null) {
                    CeType oAuthorDeviceCode = createCe(oHL7Author.getAssignedAuthor().getAssignedAuthoringDevice()
                        .getCode());
                    if (oAuthorDeviceCode != null) {
                        oScannerAuthor.setAuthoringDevice(oAuthorDeviceCode);
                        bHaveData = true;
                    }
                }

                // Manufacture Model Name
                // ------------------------
                if (oHL7Author.getAssignedAuthor().getAssignedAuthoringDevice().getManufacturerModelName() != null) {
                    String sManufacturer = extractStringFromSC(oHL7Author.getAssignedAuthor()
                        .getAssignedAuthoringDevice().getManufacturerModelName());
                    if (sManufacturer != null) {
                        oScannerAuthor.setDeviceManufactureModelName(sManufacturer);
                        bHaveData = true;
                    }
                }

                // Software Name
                // --------------
                if (oHL7Author.getAssignedAuthor().getAssignedAuthoringDevice().getSoftwareName() != null) {
                    String sSoftware = extractStringFromSC(oHL7Author.getAssignedAuthor().getAssignedAuthoringDevice()
                        .getSoftwareName());
                    if (sSoftware != null) {
                        oScannerAuthor.setDeviceSoftwareName(sSoftware);
                        bHaveData = true;
                    }
                }
            }

            if (oHL7Author.getAssignedAuthor() != null
                && oHL7Author.getAssignedAuthor().getRepresentedOrganization() != null) {
                // Represented Organization Assigning Authority
                // ----------------------------------------------
                // Should only be one - if more take the first.
                if (CollectionUtils.isNotEmpty(oHL7Author.getAssignedAuthor().getRepresentedOrganization().getId())
                    && oHL7Author.getAssignedAuthor().getRepresentedOrganization().getId().get(0) != null
                    && StringUtils.isNotEmpty(oHL7Author.getAssignedAuthor().getRepresentedOrganization().getId().get(0)
                        .getRoot())) {

                    oScannerAuthor.setRepresentedOrganizationIdAssigningAuthority(oHL7Author.getAssignedAuthor()
                        .getRepresentedOrganization().getId().get(0).getRoot());
                    bHaveData = true;
                }

                // Represented Organization Name
                // ------------------------------
                if (CollectionUtils.isNotEmpty(oHL7Author.getAssignedAuthor().getRepresentedOrganization().getName())
                    && oHL7Author.getAssignedAuthor().getRepresentedOrganization().getName().get(0) != null) {

                    String sName = extractStringFromON(oHL7Author.getAssignedAuthor().getRepresentedOrganization()
                        .getName().get(0));
                    if (sName != null) {
                        oScannerAuthor.setRepresentedOrganizationName(sName);
                        bHaveData = true;
                    }
                }

                // Represented Organization address
                // ---------------------------------
                if (CollectionUtils.isNotEmpty(oHL7Author.getAssignedAuthor().getRepresentedOrganization().getAddr())
                    && oHL7Author.getAssignedAuthor().getRepresentedOrganization().getAddr().get(0) != null) {

                    // Should only be one. If there is more than one, use the first.
                    AddressType oAddr = createAddr(oHL7Author.getAssignedAuthor().getRepresentedOrganization()
                        .getAddr().get(0));
                    if (oAddr != null) {
                        oScannerAuthor.setRepresentedOrganizationAddress(oAddr);
                        bHaveData = true;
                    }
                }
            }
        }

        if (bHaveData) {
            return oScannerAuthor;
        } else {
            return null;
        }
    }

    /**
     * This method extracts the data enterer information from the HL7 message and creates a Data Enterer object with
     * that information.
     *
     * @param oHL7DataEnterer The HL7 version of the data enterer info.
     * @return The AdapterPIP data enterer information.
     */
    private PolicyDataEntererInfoType createDataEnterer(POCDMT000040DataEnterer oHL7DataEnterer)
        throws AdapterPIPException {

        PolicyDataEntererInfoType oDataEnterer = new PolicyDataEntererInfoType();
        boolean bHaveData = false;

        if (oHL7DataEnterer != null) {
            // Data enterer time
            // ------------------
            if (oHL7DataEnterer.getTime() != null
                && StringUtils.isNotEmpty(oHL7DataEnterer.getTime().getValue())) {

                oDataEnterer.setDataEntererTime(oHL7DataEnterer.getTime().getValue());
                bHaveData = true;
            }

            if (oHL7DataEnterer.getAssignedEntity() != null
                && CollectionUtils.isNotEmpty(oHL7DataEnterer.getAssignedEntity().getId())
                && oHL7DataEnterer.getAssignedEntity().getId().get(0) != null) {

                // There should only be one - if more, then we will use the first.
                // Assigned Person Assigning Authority
                // -------------------------------------
                if (StringUtils.isNotEmpty(oHL7DataEnterer.getAssignedEntity().getId().get(0).getRoot())) {
                    oDataEnterer.setDataEntererIdAssigningAuthority(oHL7DataEnterer.getAssignedEntity().getId().get(0)
                        .getRoot());
                    bHaveData = true;
                }

                // Assigned Person Id
                // -------------------
                if (StringUtils.isNotEmpty(oHL7DataEnterer.getAssignedEntity().getId().get(0).getExtension())) {
                    oDataEnterer.setDataEntererId(oHL7DataEnterer.getAssignedEntity().getId().get(0).getExtension());
                    bHaveData = true;
                }
            }

            // Data Enterer Name
            // -------------------
            if (oHL7DataEnterer.getAssignedEntity() != null
                && oHL7DataEnterer.getAssignedEntity().getAssignedPerson() != null
                && CollectionUtils.isNotEmpty(oHL7DataEnterer.getAssignedEntity().getAssignedPerson().getName())
                && oHL7DataEnterer.getAssignedEntity().getAssignedPerson().getName().get(0) != null) {

                // Should only be one - if there are more, use the first.
                PersonNameType oName = createPersonName(oHL7DataEnterer.getAssignedEntity().getAssignedPerson()
                    .getName().get(0));
                if (oName != null) {
                    oDataEnterer.setName(oName);
                    bHaveData = true;
                }
            }
        }

        if (bHaveData) {
            return oDataEnterer;
        } else {
            return null;
        }
    }

    /**
     * This creates a PolicyCustodianInfo based on the information in the HL7 object of the same type.
     *
     * @param oHL7Custodian The HL7 object for custodian.
     * @return The internal representation of the custodian information.
     */
    private PolicyCustodianInfoType createCustodian(POCDMT000040Custodian oHL7Custodian) {
        PolicyCustodianInfoType oCustodian = new PolicyCustodianInfoType();
        boolean bHaveData = false;

        if (oHL7Custodian != null) {
            if (oHL7Custodian.getAssignedCustodian() != null
                && oHL7Custodian.getAssignedCustodian().getRepresentedCustodianOrganization() != null) {
                // Represented Organization Assigning Authority
                // ----------------------------------------------
                // Should only be one - if more take the first.
                if (CollectionUtils
                    .isNotEmpty(oHL7Custodian.getAssignedCustodian().getRepresentedCustodianOrganization().getId())
                    && oHL7Custodian.getAssignedCustodian().getRepresentedCustodianOrganization().getId().get(0) != null
                    && StringUtils.isNotEmpty(oHL7Custodian.getAssignedCustodian().getRepresentedCustodianOrganization()
                        .getId().get(0).getRoot())) {

                    oCustodian.setOrganizationIdAssigningAuthority(oHL7Custodian.getAssignedCustodian()
                        .getRepresentedCustodianOrganization().getId().get(0).getRoot());
                    bHaveData = true;
                }

                // Represented Organization Name
                // ------------------------------
                if (oHL7Custodian.getAssignedCustodian().getRepresentedCustodianOrganization().getName() != null) {
                    String sName = extractStringFromON(oHL7Custodian.getAssignedCustodian()
                        .getRepresentedCustodianOrganization().getName());
                    if (sName != null) {
                        oCustodian.setOrganizationName(sName);
                        bHaveData = true;
                    }
                }

                // Represented Organization address
                // ---------------------------------
                if (oHL7Custodian.getAssignedCustodian().getRepresentedCustodianOrganization().getAddr() != null) {
                    AddressType oAddr = createAddr(oHL7Custodian.getAssignedCustodian()
                        .getRepresentedCustodianOrganization().getAddr());
                    if (oAddr != null) {
                        oCustodian.setOrganizationAddress(oAddr);
                        bHaveData = true;
                    }
                }
            }
        }

        if (bHaveData) {
            return oCustodian;
        } else {
            return null;
        }
    }

    /**
     * This method extracts the legal authenticator information from the HL7 node and places it into the internal
     * representation of this information.
     *
     * @param oHL7Authenticator The HL7 authenticator information.
     * @return The AdapterPIP representation of this data.
     */
    private PolicyLegalAuthenticatorType createAuthenticator(POCDMT000040LegalAuthenticator oHL7Authenticator)
        throws AdapterPIPException {

        PolicyLegalAuthenticatorType oAuthenticator = new PolicyLegalAuthenticatorType();
        boolean bHaveData = false;

        if (oHL7Authenticator != null) {
            // Authentication time
            // --------------------------
            if (oHL7Authenticator.getTime() != null && StringUtils.isNotEmpty(oHL7Authenticator.getTime().getValue())) {
                oAuthenticator.setAuthenticationTime(oHL7Authenticator.getTime().getValue());
                bHaveData = true;
            }

            // Signature Code
            // ---------------
            if (oHL7Authenticator.getSignatureCode() != null) {
                CeType oCode = createCe(oHL7Authenticator.getSignatureCode());
                if (oCode != null) {
                    oAuthenticator.setSignatureCode(oCode);
                    bHaveData = true;
                }
            }

            if (oHL7Authenticator.getAssignedEntity() != null) {
                if (CollectionUtils.isNotEmpty(oHL7Authenticator.getAssignedEntity().getId())
                    && oHL7Authenticator.getAssignedEntity().getId().get(0) != null) {

                    // There should only be one - if more, then we will use the first.
                    // Assigned Entity Assigning Authority
                    // -------------------------------------
                    if (StringUtils.isNotEmpty(oHL7Authenticator.getAssignedEntity().getId().get(0).getRoot())) {
                        oAuthenticator.setAuthenticatorIdAssigningAuthority(oHL7Authenticator.getAssignedEntity()
                            .getId().get(0).getRoot());
                        bHaveData = true;
                    }

                    // Assigned Entity Id
                    // -------------------
                    if (StringUtils.isNotEmpty(oHL7Authenticator.getAssignedEntity().getId().get(0).getExtension())) {
                        oAuthenticator.setAuthenticatorId(oHL7Authenticator.getAssignedEntity().getId().get(0)
                            .getExtension());
                        bHaveData = true;
                    }
                }

                // Name
                // -----
                if (oHL7Authenticator.getAssignedEntity().getAssignedPerson() != null
                    && CollectionUtils.isNotEmpty(oHL7Authenticator.getAssignedEntity().getAssignedPerson().getName())
                    && oHL7Authenticator.getAssignedEntity().getAssignedPerson().getName().get(0) != null) {

                    // should be only one - if more than one, use the first.
                    PersonNameType oName = createPersonName(oHL7Authenticator.getAssignedEntity().getAssignedPerson()
                        .getName().get(0));

                    if (oName != null) {
                        oAuthenticator.setAuthenticatorPersonName(oName);
                        bHaveData = true;
                    }
                }
            }
        }

        if (bHaveData) {
            return oAuthenticator;
        } else {
            return null;
        }
    }

    /**
     * This method extracts the binary document policy criterion from the CDA document and returns it.
     *
     * @param oCda The CDA document
     * @return The binary document policy criteria that contains the data extracted from the CDA document.
     * @throws AdapterPIPException This exception is thrown if there is any error condition.
     */
    public BinaryDocumentPolicyCriterionType extractBinaryDocumentPolicyCriterion(POCDMT000040ClinicalDocument oCda)
        throws AdapterPIPException {

        BinaryDocumentPolicyCriterionType oCriterion = new BinaryDocumentPolicyCriterionType();
        boolean bHaveData = false;

        if (oCda == null) {
            // there is nothing to convert so get out of here.
            return null;
        }

        // Set required lement storeAction to none by default
        // ---------------------------------------------------
        oCriterion.setStoreAction(BinaryDocumentStoreActionType.NONE);

        // Document Unique ID
        // --------------------
        if (oCda.getId() != null && StringUtils.isNotEmpty(oCda.getId().getExtension())) {
            oCriterion.setDocumentUniqueId(oCda.getId().getExtension());
            bHaveData = true;
        }

        // Document type
        // --------------
        CeType oDocType = createCe(oCda.getCode());
        if (oDocType != null) {
            oCriterion.setDocumentTypeCode(oDocType);
            bHaveData = true;
        }

        // Title
        // -------
        if (oCda.getTitle() != null && CollectionUtils.isNotEmpty(oCda.getTitle().getContent())
            && oCda.getTitle().getContent().get(0) != null
            && oCda.getTitle().getContent().get(0) instanceof String) {

            oCriterion.setDocumentTitle((String) oCda.getTitle().getContent().get(0));
            bHaveData = true;
        }

        // Effective Time
        // ----------------
        if ((oCda.getEffectiveTime() != null) && (oCda.getEffectiveTime().getValue() != null)
            && (oCda.getEffectiveTime().getValue().length() > 0)) {
            oCriterion.setEffectiveTime(oCda.getEffectiveTime().getValue());
            bHaveData = true;
        }

        // Confidentiality Code
        // ----------------------
        if (oCda.getConfidentialityCode() != null) {
            CeType oConfidentialityCode = createCe(oCda.getConfidentialityCode());
            if (oConfidentialityCode != null) {
                oCriterion.setConfidentialityCode(oConfidentialityCode);
                bHaveData = true;
            }
        }

        // Patient Information
        // --------------------
        if (CollectionUtils.isNotEmpty(oCda.getRecordTarget()) && oCda.getRecordTarget().get(0) != null
            && oCda.getRecordTarget().get(0).getPatientRole() != null) {

            PolicyPatientInfoType oPatientInfo = createPatientInfo(oCda.getRecordTarget().get(0).getPatientRole());
            if (oPatientInfo != null) {
                oCriterion.setPatientInfo(oPatientInfo);
                bHaveData = true;
            }
        }

        // Original Author Information
        // -----------------------------
        if (CollectionUtils.isNotEmpty(oCda.getAuthor())) {
            PolicyOriginalAuthorInfoType oOriginalAuthor = createOriginalAuthor(oCda.getAuthor());
            if (oOriginalAuthor != null) {
                oCriterion.setAuthorOriginal(oOriginalAuthor);
                bHaveData = true;
            }
        }

        // Scanner Author Information
        // -----------------------------
        if (CollectionUtils.isNotEmpty(oCda.getAuthor())) {
            PolicyScannerAuthorInfoType oScannerAuthor = createScannerAuthor(oCda.getAuthor());
            if (oScannerAuthor != null) {
                oCriterion.setAuthorScanner(oScannerAuthor);
                bHaveData = true;
            }
        }

        // DataEnterer Information
        // -------------------------
        if (oCda.getDataEnterer() != null) {
            PolicyDataEntererInfoType oDataEnterer = createDataEnterer(oCda.getDataEnterer());
            if (oDataEnterer != null) {
                oCriterion.setDataEnterer(oDataEnterer);
                bHaveData = true;
            }
        }

        // Custodian Information
        // -------------------------
        if (oCda.getCustodian() != null) {
            PolicyCustodianInfoType oCustodian = createCustodian(oCda.getCustodian());
            if (oCustodian != null) {
                oCriterion.setCustodian(oCustodian);
                bHaveData = true;
            }
        }

        // Legal Authenticator
        // --------------------
        if (oCda.getLegalAuthenticator() != null) {
            PolicyLegalAuthenticatorType oAuthenticator = createAuthenticator(oCda.getLegalAuthenticator());
            if (oAuthenticator != null) {
                oCriterion.setLegalAuthenticator(oAuthenticator);
                bHaveData = true;
            }
        }

        // Should only be one - if there is more than one use the first.
        if (CollectionUtils.isNotEmpty(oCda.getDocumentationOf())
            && oCda.getDocumentationOf().get(0) != null
            && oCda.getDocumentationOf().get(0).getServiceEvent() != null
            && oCda.getDocumentationOf().get(0).getServiceEvent().getEffectiveTime() != null
            && CollectionUtils.isNotEmpty(
                oCda.getDocumentationOf().get(0).getServiceEvent().getEffectiveTime().getContent())) {

            for (JAXBElement oJaxbElement : oCda.getDocumentationOf().get(0).getServiceEvent().getEffectiveTime()
                .getContent()) {

                if (oJaxbElement.getName() != null && oJaxbElement.getName().getLocalPart() != null
                    && oJaxbElement.getName().getLocalPart().equals("low") && oJaxbElement.getValue() != null
                    && oJaxbElement.getValue() instanceof IVXBTSExplicit) {

                    // Start Time
                    IVXBTSExplicit oHL7LowTime = (IVXBTSExplicit) oJaxbElement.getValue();
                    if ((oHL7LowTime.getValue() != null) && (oHL7LowTime.getValue().length() > 0)) {
                        oCriterion.setStartDate(oHL7LowTime.getValue());
                        bHaveData = true;
                    }
                } else if (oJaxbElement.getName() != null && oJaxbElement.getName().getLocalPart() != null
                    && oJaxbElement.getName().getLocalPart().equals("high") && oJaxbElement.getValue() != null
                    && oJaxbElement.getValue() instanceof IVXBTSExplicit) {

                    // End Time
                    IVXBTSExplicit oHL7HighTime = (IVXBTSExplicit) oJaxbElement.getValue();
                    if ((oHL7HighTime.getValue() != null) && (oHL7HighTime.getValue().length() > 0)) {
                        oCriterion.setEndDate(oHL7HighTime.getValue());
                        bHaveData = true;
                    }
                }
            }
        }

        // Mime type
        // -----------
        if (oCda.getComponent() != null && oCda.getComponent().getNonXMLBody() != null
            && oCda.getComponent().getNonXMLBody().getText() != null) {

            // Mime type
            // ----------
            if (StringUtils.isNotEmpty(oCda.getComponent().getNonXMLBody().getText().getMediaType())) {
                oCriterion.setMimeType(oCda.getComponent().getNonXMLBody().getText().getMediaType());
                bHaveData = true;
            }

            // Binary document
            // ----------------
            if (CollectionUtils.isNotEmpty(oCda.getComponent().getNonXMLBody().getText().getContent())) {
                // We really should only have one of these elements. If for some reason we have more than one, we will
                // take only the first.
                // -----------------------------------------------------------------------------------------------------
                Serializable oSerialElement = oCda.getComponent().getNonXMLBody().getText().getContent().get(0);
                if (oSerialElement instanceof String) {
                    String sBinaryDocument = (String) oSerialElement;
                    oCriterion.setBinaryDocument(sBinaryDocument.getBytes());
                    bHaveData = true;
                }
            }
        }

        if (bHaveData) {
            return oCriterion;
        } else {
            return null;
        }
    }
}
