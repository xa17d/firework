package at.sbc.firework.service;

import at.sbc.firework.daos.*;
import at.sbc.firework.service.IService;
import org.mozartspaces.capi3.*;
import org.mozartspaces.core.*;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.net.URI;
import java.util.ArrayList;

/**
 * Created by daniel on 14.11.2014.
 */
public class ServiceXvsm implements IService {

    private Capi capi;


    private URI spaceUri = URI.create("xvsm://localhost:9876");
    public static final long DEFAULT_TIMEOUT = 5000;
    // Lager
    private static final String CONTAINER_NAME_STOCK = "stock";
    private static final String CONTAINER_NAME_OPENSTOCK = "openStock";
    private static final String CONTAINER_NAME_QUALITYCHECKQUEUE= "qualityCheckQueue";
    private static final String CONTAINER_NAME_PACKINGQUEUE= "packingQueue";
    private static final String CONTAINER_NAME_GARBAGE= "garbage";
    private static final String CONTAINER_NAME_DISTRIBUTIONSTOCK= "distributionStock";
    private ContainerReference stockContainer;
    private ContainerReference openStockContainer;
    private ContainerReference qualityCheckQueueContainer;
    private ContainerReference packingQueueContainer;
    private ContainerReference garbageContainer;
    private ContainerReference distributionStockContainer;

    @Override
    public void start() throws ServiceException {
        System.out.println("Starting XVSM...");

        // Initialisierung
        MzsCore core;
        core = DefaultMzsCore.newInstance(0);
        System.out.println("Space URI: " + this.spaceUri);

        capi = new Capi(core);

        // Container erstella
        try {
            stockContainer = FindOrCreateStockContainer(CONTAINER_NAME_STOCK);
            openStockContainer = FindOrCreateStockContainer(CONTAINER_NAME_OPENSTOCK);
            qualityCheckQueueContainer = FindOrCreateQueueContainer(CONTAINER_NAME_QUALITYCHECKQUEUE);
            packingQueueContainer = FindOrCreateQueueContainer(CONTAINER_NAME_PACKINGQUEUE);
            garbageContainer = FindOrCreateQueueContainer(CONTAINER_NAME_GARBAGE);
            distributionStockContainer = FindOrCreateQueueContainer(CONTAINER_NAME_DISTRIBUTIONSTOCK);
        } catch (MzsCoreException e) {
            throw new XvsmException(e);
        }
    }

    private ContainerReference FindOrCreateStockContainer(String name) throws MzsCoreException {
        ContainerReference container;

        try {
            container = capi.lookupContainer(name, spaceUri, MzsConstants.RequestTimeout.TRY_ONCE, null);
        } catch (MzsCoreException e) {
            System.out.println(name + " not found and will be created.");
            ArrayList<Coordinator> obligatoryCoords = new ArrayList<Coordinator>();
            obligatoryCoords.add(new TypeCoordinator());
            obligatoryCoords.add(new FifoCoordinator());

            ArrayList<Coordinator> optionalCoords = new ArrayList<Coordinator>();

            container = capi.createContainer(name, spaceUri, MzsConstants.Container.UNBOUNDED, obligatoryCoords, optionalCoords, null);
        }

        return container;
    }

    private ContainerReference FindOrCreateQueueContainer(String name) throws MzsCoreException {
        ContainerReference container;

        try {
            container = capi.lookupContainer(name, spaceUri, MzsConstants.RequestTimeout.TRY_ONCE, null);
        } catch (MzsCoreException e) {
            System.out.println(name + " not found and will be created.");
            ArrayList<Coordinator> obligatoryCoords = new ArrayList<Coordinator>();
            obligatoryCoords.add(new FifoCoordinator());

            ArrayList<Coordinator> optionalCoords = new ArrayList<Coordinator>();

            container = capi.createContainer(name, spaceUri, MzsConstants.Container.UNBOUNDED, obligatoryCoords, optionalCoords, null);
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

    public Capi getCapi() { return capi; }

    public URI getSpaceUri() { return spaceUri; }

    public ContainerReference getStockContainer() { return stockContainer; }
    public ContainerReference getOpenStockContainer() { return openStockContainer; }
    public ContainerReference getQualityCheckQueueContainer() { return qualityCheckQueueContainer; }
    public ContainerReference getPackingQueueContainer() { return packingQueueContainer; }
    public ContainerReference getGarbageContainer() { return garbageContainer; }
    public ContainerReference getDistributionStockContainer() { return distributionStockContainer; }

    @Override
    public IServiceTransaction startTransaction() throws ServiceException {
        try {
            return new ServiceTransactionXvsm(this);
        } catch (MzsCoreException e) {
            throw new XvsmException(e);
        }
    }

    @Override
    public ArrayList<Part> listStock() throws ServiceException {

        ArrayList<Part> result = new ArrayList<Part>();
        result.addAll(this.<Part>listContainer(getStockContainer()));
        result.addAll(this.<Part>listContainer(getOpenStockContainer()));

        return result;
    }

    @Override
    public ArrayList<Rocket> listQualityCheckQueue() throws ServiceException {
        return listContainer(getQualityCheckQueueContainer());
    }

    @Override
    public ArrayList<Rocket> listPackingQueue() throws ServiceException {
        return listContainer(getPackingQueueContainer());
    }

    @Override
    public ArrayList<Rocket> listGarbage() throws ServiceException {
        return listContainer(getGarbageContainer());
    }

    @Override
    public ArrayList<RocketPackage5> listDistributionStock() throws ServiceException {
        return listContainer(getDistributionStockContainer());
    }

    public <T extends Serializable> ArrayList<T> listContainer(ContainerReference container) throws ServiceException {
        ArrayList<T> result;

        try {
            ArrayList<Selector> selectors = new ArrayList<Selector>();
            selectors.add(FifoCoordinator.newSelector(MzsConstants.Selecting.COUNT_ALL));
            result = capi.read(container, selectors, ServiceXvsm.DEFAULT_TIMEOUT, null);
        } catch (MzsCoreException e) {
            throw new XvsmException(e);
        }

        return result;
    }
}
