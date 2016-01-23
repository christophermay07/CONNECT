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
package gov.hhs.fha.nhinc.patientcorrelation.nhinc.parsers.helpers;

import org.hl7.v3.II;
import org.hl7.v3.MCCIMT000100UV01Receiver;
import org.hl7.v3.MCCIMT000100UV01Sender;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;

/**
 * @author achidambaram
 *
 */
public class SenderReceiverHelperTestMCCIMT000100UV01 {

    @Test
    public void testCreateSender() {
        MCCIMT000100UV01Sender senderDevice = SenderReceiverHelperMCCIMT000100UV01.CreateSender(createSenderDeviceId());
        assertEquals(senderDevice.getDevice().getId().get(0).getAssigningAuthorityName(), "CONNECTSender");
        assertEquals(senderDevice.getDevice().getId().get(0).getExtension(),"D123401");
        assertEquals(senderDevice.getDevice().getId().get(0).getRoot(), "1.1");
    }

    @Test
    public void testCreateSenderWhenDeviceIdNull() {
        MCCIMT000100UV01Sender senderDevice = SenderReceiverHelperMCCIMT000100UV01.CreateSender();
        assertNull(senderDevice.getDevice().getId().get(0));
    }

    @Test
    public void testCreateReceiver() {
        MCCIMT000100UV01Receiver receiverDevice
            = SenderReceiverHelperMCCIMT000100UV01.CreateReceiver(createReceiverDeviceId());
        assertEquals(receiverDevice.getDevice().getId().get(0).getAssigningAuthorityName(), "CONNECTReceiver");
        assertEquals(receiverDevice.getDevice().getId().get(0).getExtension(), "D123401");
    }

    @Test
    public void CreateReceiver() {
        MCCIMT000100UV01Receiver receiverDevice = SenderReceiverHelperMCCIMT000100UV01.CreateReceiver();
        assertNull(receiverDevice.getDevice().getId().get(0));
    }

    private II createSenderDeviceId() {
        II senderDeviceId = new II();
        senderDeviceId.setAssigningAuthorityName("CONNECTSender");
        senderDeviceId.setExtension("D123401");
        senderDeviceId.setRoot("1.1");
        return senderDeviceId;
    }

    private II createReceiverDeviceId() {
        II receiverDeviceId = new II();
        receiverDeviceId.setAssigningAuthorityName("CONNECTReceiver");
        receiverDeviceId.setExtension("D123401");
        receiverDeviceId.setRoot("1.1");
        return receiverDeviceId;
    }
}
