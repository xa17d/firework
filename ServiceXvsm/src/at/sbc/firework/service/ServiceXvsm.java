package at.sbc.firework.service;

import at.sbc.firework.service.IService;
import org.mozartspaces.core.Capi;
import org.mozartspaces.core.DefaultMzsCore;
import org.mozartspaces.core.MzsCore;
import org.mozartspaces.core.MzsCoreException;

/**
 * Created by daniel on 14.11.2014.
 */
public class ServiceXvsm implements IService {

    private Capi capi;

    @Override
    public void Start() throws ServiceException {
        System.out.println("Starting XVSM...");

        MzsCore core = DefaultMzsCore.newInstance();
        capi = new Capi(core);
    }

    @Override
    public void Stop() throws ServiceException {
        if (capi != null)
        {
            try {
                capi.shutdown(null);
            } catch (MzsCoreException e) {
                throw new XvsmException(e);
            }
            capi = null;
        }
    }
}
