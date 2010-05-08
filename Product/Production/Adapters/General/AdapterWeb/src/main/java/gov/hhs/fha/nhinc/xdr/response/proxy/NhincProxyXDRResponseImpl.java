package gov.hhs.fha.nhinc.xdr.response.proxy;

import gov.hhs.fha.nhinc.async.AsyncMessageIdExtractor;
import gov.hhs.fha.nhinc.common.nhinccommonproxy.RespondingGatewayProvideAndRegisterDocumentSetResponseRequestType;
import gov.hhs.fha.nhinc.passthru.xdr.async.response.proxy.PassthruXDRAsyncRespProxy;
import gov.hhs.fha.nhinc.passthru.xdr.async.response.proxy.PassthruXDRAsyncRespProxyObjectFactory;
import ihe.iti.xdr._2007.AcknowledgementType;
import javax.xml.ws.WebServiceContext;

/**
 *
 * @author Neil Webb
 */
public class NhincProxyXDRResponseImpl
{

    public AcknowledgementType provideAndRegisterDocumentSetBResponse(RespondingGatewayProvideAndRegisterDocumentSetResponseRequestType request, WebServiceContext context)
    {
        AcknowledgementType response = null;

        // Extract the message id value from the WS-Addressing Header and place it in the Assertion Class
        if (request != null &&
                request.getAssertion() != null) {
            AsyncMessageIdExtractor msgIdExtractor = new AsyncMessageIdExtractor();
            request.getAssertion().setAsyncMessageId(msgIdExtractor.GetAsyncRelatesTo(context));
        }

        PassthruXDRAsyncRespProxyObjectFactory passthruXDRAsyncReqFactory = new PassthruXDRAsyncRespProxyObjectFactory();

        PassthruXDRAsyncRespProxy proxy = passthruXDRAsyncReqFactory.getPassthruXDRAsyncRespProxy();

        response = proxy.provideAndRegisterDocumentSetBAsyncResponse(request);

        return response;
    }
}
