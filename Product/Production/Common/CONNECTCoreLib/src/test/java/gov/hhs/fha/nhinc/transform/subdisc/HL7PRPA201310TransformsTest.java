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
package gov.hhs.fha.nhinc.transform.subdisc;

import javax.xml.bind.JAXBElement;
import org.hl7.v3.II;
import org.hl7.v3.PRPAIN201310UV02;
import org.hl7.v3.PRPAMT201307UV02ParameterList;
import org.hl7.v3.PRPAMT201307UV02PatientIdentifier;
import org.hl7.v3.PRPAMT201307UV02QueryByParameter;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * @author achidambaram
 *
 */
public class HL7PRPA201310TransformsTest {
    @Test
    public void createPRPA201310() {
        String patientId = "D123401";
        String assigningAuthorityId = "1.1";
        String localDeviceId = "1.1";
        String senderOID = "1.1";
        String receiverOID = "2.2";
        JAXBElement<PRPAMT201307UV02QueryByParameter> queryParam = createQueryByParameter();
        PRPAIN201310UV02 result = HL7PRPA201310Transforms.createPRPA201310(patientId, assigningAuthorityId,
            localDeviceId, senderOID, receiverOID, queryParam);
        assertEquals(result.getId().getRoot(), "1.1");
        assertEquals(result.getSender().getDevice().getId().get(0).getRoot(), "1.1");
        assertEquals(result.getReceiver().get(0).getDevice().getId().get(0).getRoot(), "2.2");
        assertEquals(result.getControlActProcess().getQueryByParameter().getValue().getParameterList()
            .getPatientIdentifier().get(0).getValue().get(0).getExtension(), "D123401");
        assertEquals(result.getControlActProcess().getSubject().get(0).getRegistrationEvent().getSubject1()
            .getPatient().getId().get(0).getRoot(), "1.1");
    }

    @Test
    public void createPRPA201310WhenPatIdandAAIDNull() {
        String patientId = null;
        String assigningAuthorityId = null;
        String localDeviceId = "1.1";
        String senderOID = "1.1";
        String receiverOID = "2.2";
        JAXBElement<PRPAMT201307UV02QueryByParameter> queryParam = createQueryByParameter();
        PRPAIN201310UV02 result = HL7PRPA201310Transforms.createPRPA201310(patientId, assigningAuthorityId,
            localDeviceId, senderOID, receiverOID, queryParam);
        assertEquals(result.getId().getRoot(), "1.1");
        assertEquals(result.getSender().getDevice().getId().get(0).getRoot(), "1.1");
        assertEquals(result.getReceiver().get(0).getDevice().getId().get(0).getRoot(), "2.2");
        assertEquals(result.getControlActProcess().getQueryByParameter().getValue().getParameterList()
            .getPatientIdentifier().get(0).getValue().get(0).getExtension(), "D123401");
        assertNull(result.getControlActProcess().getSubject().get(0).getRegistrationEvent().getSubject1().getPatient()
            .getId().get(0).getRoot());
    }

    @Test
    public void createFaultPRPA201310() {
        PRPAIN201310UV02 result = HL7PRPA201310Transforms.createFaultPRPA201310();
        assertNull(result.getSender().getDevice().getId().get(0).getRoot());
        assertNull(result.getReceiver().get(0).getDevice().getId().get(0).getRoot());
        assertTrue(result.getControlActProcess().getQueryByParameter().getValue().getParameterList()
                .getPatientIdentifier().isEmpty());
        assertNull(result.getControlActProcess().getSubject().get(0).getRegistrationEvent().getSubject1().getPatient()
                .getId().get(0).getRoot());
    }

    @Test
    public void createFaultPRPA201310WithSenderAndReceiver() {
        String senderOID = "1.1";
        String receiverOID = "2.2";
        PRPAIN201310UV02 result = HL7PRPA201310Transforms.createFaultPRPA201310(senderOID, receiverOID);
        assertEquals(result.getSender().getDevice().getId().get(0).getRoot(), "1.1");
        assertEquals(result.getReceiver().get(0).getDevice().getId().get(0).getRoot(), "2.2");
        assertTrue(result.getControlActProcess().getQueryByParameter().getValue().getParameterList()
                .getPatientIdentifier().isEmpty());
        assertNull(result.getControlActProcess().getSubject().get(0).getRegistrationEvent().getSubject1().getPatient()
                .getId().get(0).getRoot());
    }

    private JAXBElement<PRPAMT201307UV02QueryByParameter> createQueryByParameter() {
        PRPAMT201307UV02QueryByParameter parameter = new PRPAMT201307UV02QueryByParameter();
        parameter.setQueryId(createII());
        parameter.setParameterList(createPRPAMT201307UV02ParameterList());
        javax.xml.namespace.QName xmlqname = new javax.xml.namespace.QName("urn:hl7-org:v3", "parameter");
        JAXBElement<PRPAMT201307UV02QueryByParameter> queryByParameter = new JAXBElement<>(
            xmlqname, PRPAMT201307UV02QueryByParameter.class, parameter);
        return queryByParameter;
    }

    /**
     * @return
     */
    private II createII() {
        II ii = new II();
        ii.setAssigningAuthorityName("CONNECT");
        ii.setExtension("D123401");
        ii.setRoot("1.1");
        return ii;
    }

    private PRPAMT201307UV02ParameterList createPRPAMT201307UV02ParameterList() {
        PRPAMT201307UV02ParameterList parameterList = new PRPAMT201307UV02ParameterList();
        parameterList.setId(createII());
        parameterList.getPatientIdentifier().add(createPRPAMT201307UV02PatientIdentifier());
        return parameterList;
    }

    private PRPAMT201307UV02PatientIdentifier createPRPAMT201307UV02PatientIdentifier() {
        PRPAMT201307UV02PatientIdentifier identifier = new PRPAMT201307UV02PatientIdentifier();
        identifier.getValue().add(createII());
        return identifier;
    }
}
