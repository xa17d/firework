package at.sbc.firework.service;

import at.sbc.firework.entities.Order;
import at.sbc.firework.entities.Part;
import at.sbc.firework.entities.Rocket;
import at.sbc.firework.entities.RocketPackage5;
import at.sbc.firework.utils.NotificationMode;
import org.mozartspaces.capi3.*;
import org.mozartspaces.core.*;
import org.mozartspaces.notifications.Notification;
import org.mozartspaces.notifications.NotificationListener;
import org.mozartspaces.notifications.NotificationManager;
import org.mozartspaces.notifications.Operation;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * XVSM Service bütat Zugriff ufn Space und ma kann Transaktiona erstella
 */
public class FactoryService implements IFactoryService {

    private Capi capi;

    private URI spaceUri = URI.create("xvsm://localhost:9876");
    public static final long DEFAULT_TIMEOUT = 5000;
    // Lager
    private static final String CONTAINER_NAME_STOCK = "stock";
    private static final String CONTAINER_NAME_QUALITYCHECKQUEUE= "qualityCheckQueue";
    private static final String CONTAINER_NAME_PACKINGQUEUE= "packingQueue";
    private static final String CONTAINER_NAME_GARBAGE= "garbage";
    private static final String CONTAINER_NAME_DISTRIBUTIONSTOCK = "distributionStock";
    private static final String CONTAINER_NAME_ORDERS = "orders";
    private static final String CONTAINER_NAME_ORDERSTOCK = "orderStock";
    private static final String CONTAINER_NAME_IDCOUNTER = "idCounter";
    private static final String IDCOUNTER_KEY = "idCounter";
    private ContainerReference stockContainer;
    private ContainerReference qualityCheckQueueContainer;
    private ContainerReference packingQueueContainer;
    private ContainerReference garbageContainer;
    private ContainerReference distributionStockContainer;
    private ContainerReference ordersContainer;
    private ContainerReference orderStockContainer;
    private ContainerReference idCounterContainer;
    private ContainerReference[] allContainers;

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
            stockContainer = findOrCreateQueryTypeContainer(CONTAINER_NAME_STOCK);
            qualityCheckQueueContainer = findOrCreateFifoContainer(CONTAINER_NAME_QUALITYCHECKQUEUE);
            packingQueueContainer = findOrCreateFifoContainer(CONTAINER_NAME_PACKINGQUEUE);
            garbageContainer = findOrCreateFifoContainer(CONTAINER_NAME_GARBAGE);
            distributionStockContainer = findOrCreateFifoContainer(CONTAINER_NAME_DISTRIBUTIONSTOCK);
            ordersContainer = findOrCreateQueryTypeContainer(CONTAINER_NAME_ORDERS);
            orderStockContainer = findOrCreateQueryTypeContainer(CONTAINER_NAME_ORDERSTOCK);

            allContainers = new ContainerReference[] {
                    stockContainer,
                    qualityCheckQueueContainer,
                    packingQueueContainer,
                    garbageContainer,
                    distributionStockContainer,
                    ordersContainer,
                    orderStockContainer
            };

            idCounterContainer = findOrCreateIdCounterContainer(CONTAINER_NAME_IDCOUNTER);

        } catch (MzsCoreException e) {
            throw new XvsmException(e);
        }
    }

    private ContainerReference findOrCreateQueryTypeContainer(String name) throws MzsCoreException {
        ContainerReference container;

        try {
            container = capi.lookupContainer(name, spaceUri, MzsConstants.RequestTimeout.TRY_ONCE, null);
        } catch (MzsCoreException e) {
            System.out.println(name + " not found and will be created.");
            ArrayList<Coordinator> obligatoryCoords = new ArrayList<Coordinator>();
            obligatoryCoords.add(new TypeCoordinator());
            obligatoryCoords.add(new QueryCoordinator());

            ArrayList<Coordinator> optionalCoords = new ArrayList<Coordinator>();

            container = capi.createContainer(name, spaceUri, MzsConstants.Container.UNBOUNDED, obligatoryCoords, optionalCoords, null);
        }

        return container;
    }

    private ContainerReference findOrCreateFifoContainer(String name) throws MzsCoreException {
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

    private ContainerReference findOrCreateIdCounterContainer(String name) throws MzsCoreException {
        ContainerReference container;

        try {
            container = capi.lookupContainer(name, spaceUri, MzsConstants.RequestTimeout.TRY_ONCE, null);
        } catch (MzsCoreException e) {
            System.out.println(name + " not found and will be created.");
            ArrayList<Coordinator> obligatoryCoords = new ArrayList<Coordinator>();
            obligatoryCoords.add(new KeyCoordinator());

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
    public ContainerReference getQualityCheckQueueContainer() { return qualityCheckQueueContainer; }
    public ContainerReference getPackingQueueContainer() { return packingQueueContainer; }
    public ContainerReference getGarbageContainer() { return garbageContainer; }
    public ContainerReference getDistributionStockContainer() { return distributionStockContainer; }

    @Override
    public IFactoryTransaction startTransaction() throws ServiceException {
        try {
            return new FactoryTransactionXvsm(this);
        } catch (MzsCoreException e) {
            throw new XvsmException(e);
        }
    }

    @Override
    public ArrayList<Part> listStock() throws ServiceException {
        return listStockContainer(getStockContainer());
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

    @Override
    public long getNewId() throws ServiceException {

        long id;

        List<Selector> selectors = new ArrayList<Selector>();
        selectors.add(KeyCoordinator.newSelector(IDCOUNTER_KEY));
        TransactionReference transaction = null;

        try {
            transaction = capi.createTransaction(DEFAULT_TIMEOUT, spaceUri);

            // vom Container
            ArrayList<Long> items;
            try {
                items = capi.take(idCounterContainer, selectors, MzsConstants.RequestTimeout.TRY_ONCE, transaction);
            }
            catch (CountNotMetException e) {
                items = null;
            }

            Long counter;
            if (items == null)
            { counter = new Long(0L); }
            else
            { counter = items.get(0); }

            // Increment
            id = counter + 1;

            // In n container
            counter = new Long(id);

            Entry entry = new Entry(counter, KeyCoordinator.newCoordinationData(IDCOUNTER_KEY));
            capi.write(entry, idCounterContainer, DEFAULT_TIMEOUT, transaction);

            capi.commitTransaction(transaction);

        } catch (MzsCoreException e) {

            try {
                if (transaction != null) {
                    capi.rollbackTransaction(transaction);
                }
            }
            catch (MzsCoreException e2) {}


            throw new XvsmException(e);
        }

        return id;
    }

    public <T extends Serializable> ArrayList<T> listContainer(ContainerReference container) throws ServiceException {
        ArrayList<T> result;

        try {
            ArrayList<Selector> selectors = new ArrayList<Selector>();
            selectors.add(FifoCoordinator.newSelector(MzsConstants.Selecting.COUNT_ALL));
            result = capi.read(container, selectors, FactoryService.DEFAULT_TIMEOUT, null);
        } catch (MzsCoreException e) {
            throw new XvsmException(e);
        }

        return result;
    }

    public <T extends Serializable> ArrayList<T> listStockContainer(ContainerReference container) throws ServiceException {
        ArrayList<T> result;

        try {
            ArrayList<Selector> selectors = new ArrayList<Selector>();

            Query query = new Query().sortup(Property.forName("id"));
            selectors.add(QueryCoordinator.newSelector(query, MzsConstants.Selecting.COUNT_ALL));
            result = capi.read(container, selectors, FactoryService.DEFAULT_TIMEOUT, null);
        } catch (MzsCoreException e) {
            throw new XvsmException(e);
        }

        return result;
    }

    @Override
    public ArrayList<Order> listOrders() throws ServiceException {
        // TODO: implement
        return null;
    }

    @Override
    public ArrayList<Rocket> listOrderRockets(long orderId) throws ServiceException {
        // TODO: implement
        return null;
    }

    @Override
    public int getOrderRocketCount(long orderId) throws ServiceException {
        // TODO: implement
        return 0;
    }

    @Override
    public void registerNotification(INotification notification, String containerId, ContainerOperation operation, NotificationMode mode) throws ServiceException {

        boolean all = (containerId == "*");

        for (ContainerReference container : allContainers) {
            if (all || (container.getId() == containerId)) {
                try {

                    XvsmNotificationListener listener = new XvsmNotificationListener(getCapi().getCore(), notification, container, operation, mode);

                } catch (InterruptedException e) {
                    throw new ServiceException(e);
                } catch (MzsCoreException e) {
                    throw new ServiceException(e);
                }
            }
        }
    }
}