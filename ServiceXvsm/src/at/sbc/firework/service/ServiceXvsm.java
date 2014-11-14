package at.sbc.firework.service;

import at.sbc.firework.daos.Part;
import at.sbc.firework.daos.Stick;
import at.sbc.firework.service.IService;
import org.mozartspaces.capi3.*;
import org.mozartspaces.core.*;

import java.net.URI;
import java.util.ArrayList;

/**
 * Created by daniel on 14.11.2014.
 */
public class ServiceXvsm implements IService {

    private Capi capi;


    private URI spaceUri = URI.create("xvsm://localhost:9876");
    private final long DEFAULT_TIMEOUT = 5000;
    // Lager
    private static final String CONTAINER_NAME_STOCK = "stock";
    private ContainerReference stockContainer;

    @Override
    public void start() throws ServiceException {
        System.out.println("Starting XVSM...");

        MzsCore core;
        core = DefaultMzsCore.newInstance(0);
        System.out.println("Space URI: " + this.spaceUri);

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

        try {
            container = capi.lookupContainer(name, spaceUri, MzsConstants.RequestTimeout.TRY_ONCE, null);
        } catch (MzsCoreException e) {
            System.out.println(name + " not found and will be created.");
            ArrayList<Coordinator> obligatoryCoords = new ArrayList<Coordinator>();
            obligatoryCoords.add(new FifoCoordinator());
            container = capi.createContainer(name, spaceUri, MzsConstants.Container.UNBOUNDED, obligatoryCoords, new ArrayList<Coordinator>(), null);
        }

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
            capi.write(stockContainer, entry);

        } catch (MzsCoreException e) {
            throw new XvsmException(e);
        }
    }

    @Override
    public ArrayList<Part> getStock() throws ServiceException {
        ArrayList<Part> result;

        try {
            ArrayList<Selector> selectors = new ArrayList<Selector>();
            selectors.add(FifoCoordinator.newSelector(MzsConstants.Selecting.COUNT_ALL));
            result = capi.read(stockContainer, selectors, DEFAULT_TIMEOUT, null);
        } catch (MzsCoreException e) {
            throw new XvsmException(e);
        }

        return result;
    }
}
