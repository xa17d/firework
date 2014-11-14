package at.sbc.firework.service;

import at.sbc.firework.daos.Part;
import at.sbc.firework.service.IService;
import org.mozartspaces.core.*;

import java.net.URI;

/**
 * Created by daniel on 14.11.2014.
 */
public class ServiceXvsm implements IService {

    private Capi capi;


    private static URI SPACE_URI = URI.create("xvsm://localhost:9876");
    private final long DEFAULT_TIMEOUT = 2000;
    // Lager
    private static final String CONTAINER_NAME_STOCK = "stock";
    private ContainerReference stockContainer;

    @Override
    public void start() throws ServiceException {
        System.out.println("Starting XVSM...");

        MzsCore core = DefaultMzsCore.newInstanceWithoutSpace();
        capi = new Capi(core);

        // Lager erstellen
        try {
            stockContainer = FindOrCreateContainer(CONTAINER_NAME_STOCK);
        } catch (MzsCoreException e) {
            e.printStackTrace();
            throw new XvsmException(e);
        }
    }

    private ContainerReference FindOrCreateContainer(String name) throws MzsCoreException {
        ContainerReference container;
        container = capi.lookupContainer(name, SPACE_URI, DEFAULT_TIMEOUT, null);

        return container;
    }

    @Override
    public void stop() throws ServiceException {
        if (capi != null)
        {
            /*
            try {
                capi.shutdown(SPACE_URI);
            } catch (MzsCoreException e) {
                throw new XvsmException(e);
            }
            */
            capi = null;
        }
    }

    @Override
    public void addToStock(Part part) throws ServiceException
    {
        Entry entry = new Entry(part);

        try {
            capi.write(stockContainer, DEFAULT_TIMEOUT, null, entry);
        } catch (MzsCoreException e) {
            throw new XvsmException(e);
        }
    }
}
