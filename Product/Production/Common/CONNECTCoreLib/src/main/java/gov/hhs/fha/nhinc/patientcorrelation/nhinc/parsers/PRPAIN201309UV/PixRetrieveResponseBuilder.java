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
package gov.hhs.fha.nhinc.patientcorrelation.nhinc.parsers.PRPAIN201309UV;

import gov.hhs.fha.nhinc.patientcorrelation.nhinc.parsers.helpers.CDHelper;
import gov.hhs.fha.nhinc.patientcorrelation.nhinc.parsers.helpers.CSHelper;
import gov.hhs.fha.nhinc.patientcorrelation.nhinc.parsers.helpers.Configuration;
import gov.hhs.fha.nhinc.patientcorrelation.nhinc.parsers.helpers.Constants;
import gov.hhs.fha.nhinc.patientcorrelation.nhinc.parsers.helpers.CreationTimeHelper;
import gov.hhs.fha.nhinc.patientcorrelation.nhinc.parsers.helpers.IIHelper;
import gov.hhs.fha.nhinc.patientcorrelation.nhinc.parsers.helpers.InteractionIdHelper;
import gov.hhs.fha.nhinc.patientcorrelation.nhinc.parsers.helpers.SenderReceiverHelperMCCIMT000300UV01;
import gov.hhs.fha.nhinc.patientcorrelation.nhinc.parsers.helpers.UniqueIdHelper;
import java.util.List;
import javax.xml.bind.JAXBElement;
import org.hl7.v3.*;

/**
 *
 * @author rayj
 */
public class PixRetrieveResponseBuilder {

    private static final String CODE = "CA";
    private static final String CONTROL_ACT_PROCESS_CODE = "PRPA_TE201310UV";
    private static final String SUBJECT_TYPE_CODE = "SUBJ";
    private static final String STATUS_CODE_VALUE = "active";
    private static final String PATIENT_CLASS_CODE = "PAT";
    private static final String PATIENTPERSON_CLASSCODE = "PSN";
    private static final String QUERY_RESPONSE = "OK";
    private static final String ACCEPT_ACK_CODE_VALUE = "AL";
    private static final String INTERACTION_ID_EXTENSION = "PRPA_IN201310";
    private static final String PROCESSING_CODE_VALUE = "P";
    private static final String PROCESSING_MODE_CODE = "T";
    private static final String ITS_VERSION = "XML_1.0";

    private PixRetrieveResponseBuilder() {
    }

    public static PRPAIN201310UV02 createPixRetrieveResponse(PRPAIN201309UV02 retrievePatientCorrelationsRequest,
            List<II> resultListII) {
        PRPAIN201310UV02 message = createTransmissionWrapper(
                IIHelper.IIFactory(Configuration.getMyCommunityId(), null), IIHelper.IIFactoryCreateNull());
        message.getAcknowledgement().add(createAck());
        message.setControlActProcess(createControlActProcess(resultListII, retrievePatientCorrelationsRequest));
        return message;
    }

    private static MFMIMT700711UV01QueryAck createQueryAck(PRPAIN201310UV02MFMIMT700711UV01ControlActProcess controlAct) {

        MFMIMT700711UV01QueryAck queryAck = new MFMIMT700711UV01QueryAck();
        queryAck.setQueryId(controlAct.getQueryByParameter().getValue().getQueryId());
        queryAck.setQueryResponseCode(CSHelper.buildCS(QUERY_RESPONSE));

        return queryAck;
    }

    private static PRPAIN201310UV02MFMIMT700711UV01RegistrationEvent createRegistrationEvent(List<II> patientIds,
            PRPAIN201309UV02 originalRetrievePatientCorrelationsRequest) {

        PRPAIN201310UV02MFMIMT700711UV01RegistrationEvent registrationEvent = new PRPAIN201310UV02MFMIMT700711UV01RegistrationEvent();
        registrationEvent.getId().add(IIHelper.IIFactoryCreateNull());
        registrationEvent.setStatusCode(CSHelper.buildCS(STATUS_CODE_VALUE));
        PRPAIN201310UV02MFMIMT700711UV01Subject2 subject1 = createSubject1(patientIds,
                originalRetrievePatientCorrelationsRequest);

        registrationEvent.setSubject1(subject1);

        registrationEvent.setCustodian(createCustodian());
        return registrationEvent;
    }

    private static PRPAIN201310UV02MFMIMT700711UV01Subject1 createSubject(List<II> patientIds,
            PRPAIN201309UV02 originalRetrievePatientCorrelationsRequest) {

        PRPAIN201310UV02MFMIMT700711UV01Subject1 subject = new PRPAIN201310UV02MFMIMT700711UV01Subject1();
        subject.getTypeCode().add(SUBJECT_TYPE_CODE);
        PRPAIN201310UV02MFMIMT700711UV01RegistrationEvent registrationEvent = createRegistrationEvent(patientIds,
                originalRetrievePatientCorrelationsRequest);

        subject.setRegistrationEvent(registrationEvent);

        return subject;
    }

    private static PRPAIN201310UV02MFMIMT700711UV01Subject2 createSubject1(List<II> patientIds,
            PRPAIN201309UV02 originalRetrievePatientCorrelationsRequest) {

        PRPAIN201310UV02MFMIMT700711UV01Subject2 subject1 = new PRPAIN201310UV02MFMIMT700711UV01Subject2();
        PRPAMT201304UV02Patient patient = createPatient(patientIds);
        subject1.setPatient(patient);

        // TODO: add provider organization

        return subject1;
    }

    private static MCCIMT000300UV01Acknowledgement createAck() {
        MCCIMT000300UV01Acknowledgement ack = new MCCIMT000300UV01Acknowledgement();
        ack.setTypeCode(CSHelper.buildCS(CODE));
        return ack;
    }

    private static PRPAIN201310UV02MFMIMT700711UV01ControlActProcess createControlActProcess(List<II> patientIds,
            PRPAIN201309UV02 originalRetrievePatientCorrelationsRequest) {
        PRPAIN201310UV02MFMIMT700711UV01ControlActProcess controlActProcess = new PRPAIN201310UV02MFMIMT700711UV01ControlActProcess();
        controlActProcess.setMoodCode(XActMoodIntentEvent.EVN);
        controlActProcess.setCode(CDHelper.CDFactory(CONTROL_ACT_PROCESS_CODE, Constants.HL7_OID));

        PRPAIN201310UV02MFMIMT700711UV01Subject1 subject = createSubject(patientIds,
                originalRetrievePatientCorrelationsRequest);
        controlActProcess.getSubject().add(subject);

        JAXBElement<PRPAMT201307UV02QueryByParameter> queryByParameter = PRPAIN201309UVParser
                .extractQueryId(originalRetrievePatientCorrelationsRequest);
        controlActProcess.setQueryByParameter(queryByParameter);

        controlActProcess.setQueryAck(createQueryAck(controlActProcess));

        return controlActProcess;
    }

    private static MFMIMT700711UV01Custodian createCustodian() {

        MFMIMT700711UV01Custodian custodian = new MFMIMT700711UV01Custodian();
        COCTMT090003UV01AssignedEntity assignedEntity = new COCTMT090003UV01AssignedEntity();
        assignedEntity.getId().add(IIHelper.IIFactoryCreateNull());
        custodian.setAssignedEntity(assignedEntity);
        return custodian;
    }

    private static PRPAMT201304UV02Patient createPatient(List<II> patientIds) {
        PRPAMT201304UV02Patient patient = new PRPAMT201304UV02Patient();
        patient.getClassCode().add(PATIENT_CLASS_CODE);
        for (II patientId : patientIds) {
            patient.getId().add(patientId);
        }

        patient.setStatusCode(CSHelper.buildCS(STATUS_CODE_VALUE));

        PRPAMT201304UV02Person patientPerson = new PRPAMT201304UV02Person();
        // create patient person element
        javax.xml.namespace.QName xmlqname = new javax.xml.namespace.QName("urn:hl7-org:v3", "patientPerson");
        JAXBElement<PRPAMT201304UV02Person> patientPersonElement = new JAXBElement<>(xmlqname,
                PRPAMT201304UV02Person.class, patientPerson);
        patient.setPatientPerson(patientPersonElement);
        patientPerson.getClassCode().add(PATIENTPERSON_CLASSCODE);

        patientPerson.setDeterminerCode("INSTANCE");

        PNExplicit patientName = new PNExplicit();
        patientName.getNullFlavor().add("NA");
        patientPerson.getName().add(patientName);
        return patient;
    }

    private static PRPAIN201310UV02 createTransmissionWrapper(II senderId, II receiverId) {
        PRPAIN201310UV02 message = new PRPAIN201310UV02();

        message.setITSVersion(ITS_VERSION);
        message.setId(UniqueIdHelper.createUniqueId());
        message.setCreationTime(CreationTimeHelper.getCreationTime());
        message.setInteractionId(InteractionIdHelper.createInteractionId(INTERACTION_ID_EXTENSION));

        message.setProcessingCode(CSHelper.buildCS(PROCESSING_CODE_VALUE));
        message.setProcessingModeCode(CSHelper.buildCS(PROCESSING_MODE_CODE));
        message.setAcceptAckCode(CSHelper.buildCS(ACCEPT_ACK_CODE_VALUE));

        message.getReceiver().add(SenderReceiverHelperMCCIMT000300UV01.CreateReceiver(receiverId));
        message.setSender(SenderReceiverHelperMCCIMT000300UV01.CreateSender(senderId));

        return message;
    }
}
