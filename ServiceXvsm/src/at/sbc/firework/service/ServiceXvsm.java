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


    private URI spaceUri = null; // URI.create("xvsm://localhost:9876/");
    private final long DEFAULT_TIMEOUT = 5000;
    // Lager
    private static final String CONTAINER_NAME_STOCK = "stock";
    private ContainerReference stockContainer;

    @Override
    public void start() throws ServiceException {
        System.out.println("Starting XVSM...");

        MzsCore core;
        if (this.spaceUri == null) {
            // use embedded space
            core = DefaultMzsCore.newInstance(0);
            System.out.println("Space URI: " + core.getConfig().getSpaceUri());
        } else {
            core = DefaultMzsCore.newInstanceWithoutSpace();
            System.out.println("Space URI: " + this.spaceUri);
        }

        capi = new Capi(core);

        // Lager erstellen
        try {
            stockContainer = FindOrCreateContainer(CONTAINER_NAME_STOCK);
        } catch (MzsCoreException e) {
            throw new XvsmException(e);
        }
    }

    private ContainerReference FindOrCreateContainer(String name) throws MzsCoreException {
        ContainerReference container;
        container = CapiUtil.lookupOrCreateContainer(name, spaceUri, null, null, capi);
        return container;
    }

    @Override
    public void stop() throws ServiceException {
        if (capi != null)
        {
            try {
                if (spaceUri == null) {
                    capi.shutdown(null);
                }
            } catch (MzsCoreException e) {
                throw new XvsmException(e);
            }
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
