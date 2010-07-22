/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.hhs.fha.nhinc.admindistribution.adapter.proxy;
import oasis.names.tc.emergency.edxl.de._1.EDXLDistribution;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import gov.hhs.fha.nhinc.admindistribution.adapter.AdapterAdminDistImpl;
/**
 *
 * @author dunnek
 */
public class AdapterAdminDistJavaImpl implements AdapterAdminDistProxy{
     private Log log = null;

    public AdapterAdminDistJavaImpl()
    {
        log = createLogger();
    }
    protected Log createLogger()
    {
        return LogFactory.getLog(getClass());
    }
    public void sendAlertMessage(EDXLDistribution body)
    {
        log.debug("Begin sendAlertMessage");
        getAdapterImplementation().sendAlertMessage(body);
    }
    protected AdapterAdminDistImpl getAdapterImplementation()
    {
        return new gov.hhs.fha.nhinc.admindistribution.adapter.AdapterAdminDistImpl();
    }
}
