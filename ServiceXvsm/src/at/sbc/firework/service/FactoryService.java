package at.sbc.firework.service;

import at.sbc.firework.entities.Order;
import at.sbc.firework.entities.Part;
import at.sbc.firework.entities.Rocket;
import at.sbc.firework.entities.RocketPackage5;
import at.sbc.firework.utils.NotificationMode;
import org.mozartspaces.capi3.*;
import org.mozartspaces.core.*;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

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
    private static final String CONTAINER_NAME_ORDERPOSITIONS = "orderPositions";
    private static final String CONTAINER_NAME_IDCOUNTER = "idCounter";
    private static final String IDCOUNTER_KEY = "idCounter";
    private ContainerReference stockContainer;
    private ContainerReference qualityCheckQueueContainer;
    private ContainerReference packingQueueContainer;
    private ContainerReference garbageContainer;
    private ContainerReference distributionStockContainer;
    private ContainerReference ordersContainer;
    private ContainerReference orderStockContainer;
    private ContainerReference orderPositionsContainer;
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
            stockContainer = findOrCreateContainer(CONTAINER_NAME_STOCK, false, true, true, false);
            qualityCheckQueueContainer = findOrCreateContainer(CONTAINER_NAME_QUALITYCHECKQUEUE, true, false, false, false);
            packingQueueContainer = findOrCreateContainer(CONTAINER_NAME_PACKINGQUEUE, true, false, false, false);
            garbageContainer = findOrCreateContainer(CONTAINER_NAME_GARBAGE, true, false, false, false);
            distributionStockContainer = findOrCreateContainer(CONTAINER_NAME_DISTRIBUTIONSTOCK, true, false, false, false);
            ordersContainer = findOrCreateContainer(CONTAINER_NAME_ORDERS, false, false, true, false);
            orderStockContainer = findOrCreateContainer(CONTAINER_NAME_ORDERSTOCK, false, false, true, false);
            orderPositionsContainer = findOrCreateContainer(CONTAINER_NAME_ORDERPOSITIONS, false, false, true, false);

            allContainers = new ContainerReference[] {
                    stockContainer,
                    qualityCheckQueueContainer,
                    packingQueueContainer,
                    garbageContainer,
                    distributionStockContainer,
                    ordersContainer,
                    orderStockContainer,
                    orderPositionsContainer
            };

            idCounterContainer = findOrCreateContainer(CONTAINER_NAME_IDCOUNTER, false, false, false, true);

        } catch (MzsCoreException e) {
            throw new XvsmException(e);
        }
    }

    private ContainerReference findOrCreateContainer(String name, boolean fifo, boolean type, boolean query, boolean key) throws MzsCoreException {
        ContainerReference container;

        try {
            container = capi.lookupContainer(name, spaceUri, MzsConstants.RequestTimeout.TRY_ONCE, null);
        } catch (MzsCoreException e) {
            System.out.println(name + " not found and will be created.");
            ArrayList<Coordinator> obligatoryCoords = new ArrayList<Coordinator>();
            if (fifo) { obligatoryCoords.add(new FifoCoordinator()); }
            if (type) { obligatoryCoords.add(new TypeCoordinator()); }
            if (query) { obligatoryCoords.add(new QueryCoordinator()); }
            if (key) { obligatoryCoords.add(new KeyCoordinator()); }

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
    public ContainerReference getOrdersContainer() { return ordersContainer; }
    public ContainerReference getOrderPositionsContainer() { return orderPositionsContainer; }
    public ContainerReference getOrderStockContainer() { return orderStockContainer; }

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
        return listQueryContainer(getStockContainer());
    }

    @Override
    public ArrayList<Rocket> listQualityCheckQueue() throws ServiceException {
        return listFifoContainer(getQualityCheckQueueContainer());
    }

    @Override
    public ArrayList<Rocket> listPackingQueue() throws ServiceException {
        return listFifoContainer(getPackingQueueContainer());
    }

    @Override
    public ArrayList<Rocket> listGarbage() throws ServiceException {
        return listFifoContainer(getGarbageContainer());
    }

    @Override
    public ArrayList<RocketPackage5> listDistributionStock() throws ServiceException {
        return listFifoContainer(getDistributionStockContainer());
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

    public <T extends Serializable> ArrayList<T> listFifoContainer(ContainerReference container) throws ServiceException {
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

    public <T extends Serializable> ArrayList<T> listQueryContainer(ContainerReference container) throws ServiceException {
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
        return listQueryContainer(getOrdersContainer());
    }

    @Override
    public ArrayList<Rocket> listOrderRockets(long orderId) throws ServiceException {
        ArrayList<Rocket> result;

        try {
            ArrayList<Selector> selectors = new ArrayList<Selector>();

            Query query = new Query().filter(Property.forName("orderId").equalTo(orderId)).sortup(Property.forName("id"));
            selectors.add(QueryCoordinator.newSelector(query, MzsConstants.Selecting.COUNT_ALL));
            result = capi.read(getOrderStockContainer(), selectors, FactoryService.DEFAULT_TIMEOUT, null);
        } catch (MzsCoreException e) {
            throw new XvsmException(e);
        }

        return result;
    }

    @Override
    public int getOrderRocketCount(long orderId) throws ServiceException {
        // TODO: make more efficient
        return listOrderRockets(orderId).size();
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
