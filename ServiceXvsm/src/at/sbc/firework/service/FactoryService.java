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
    private XvsmUtils utils;

    private URI spaceUri = URI.create("xvsm://localhost:9876");
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
        Console.println("Starting XVSM...");

        // Initialisierung
        MzsCore core;
        core = DefaultMzsCore.newInstance(0);
        Console.println("Space URI: " + this.spaceUri);

        capi = new Capi(core);
        utils = new XvsmUtils(capi, spaceUri);

        // Container erstella
        try {
            stockContainer = utils.findOrCreateContainer(CONTAINER_NAME_STOCK, false, true, true, false);
            qualityCheckQueueContainer = utils.findOrCreateContainer(CONTAINER_NAME_QUALITYCHECKQUEUE, true, false, false, false);
            packingQueueContainer = utils.findOrCreateContainer(CONTAINER_NAME_PACKINGQUEUE, true, false, true, false);
            garbageContainer = utils.findOrCreateContainer(CONTAINER_NAME_GARBAGE, true, false, false, false);
            distributionStockContainer = utils.findOrCreateContainer(CONTAINER_NAME_DISTRIBUTIONSTOCK, true, false, false, false);
            ordersContainer = utils.findOrCreateContainer(CONTAINER_NAME_ORDERS, false, false, true, false);
            orderStockContainer = utils.findOrCreateContainer(CONTAINER_NAME_ORDERSTOCK, false, false, true, false);
            orderPositionsContainer = utils.findOrCreateContainer(CONTAINER_NAME_ORDERPOSITIONS, false, false, true, false);

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

            idCounterContainer = utils.findOrCreateContainer(CONTAINER_NAME_IDCOUNTER, false, false, false, true);
            initIdContainer();

        } catch (MzsCoreException e) {
            throw new XvsmException(e);
        }
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

    public XvsmUtils getXvsmUtils() { return utils; }

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
        return utils.listQueryContainer(getStockContainer());
    }

    @Override
    public ArrayList<Rocket> listQualityCheckQueue() throws ServiceException {
        return utils.listFifoContainer(getQualityCheckQueueContainer());
    }

    @Override
    public ArrayList<Rocket> listPackingQueue() throws ServiceException {
        return utils.listFifoContainer(getPackingQueueContainer());
    }

    @Override
    public ArrayList<Rocket> listGarbage() throws ServiceException {
        return utils.listFifoContainer(getGarbageContainer());
    }

    @Override
    public ArrayList<RocketPackage5> listDistributionStock() throws ServiceException {
        return utils.listFifoContainer(getDistributionStockContainer());
    }

    private void initIdContainer() throws ServiceException {
        List<Selector> selectors = new ArrayList<Selector>();
        selectors.add(KeyCoordinator.newSelector(IDCOUNTER_KEY));

        try {

            // check if available

            try {
                capi.read(idCounterContainer, selectors, MzsConstants.RequestTimeout.TRY_ONCE, null);
            }
            catch (CountNotMetException e) {

                // if not, add it

                Entry entry = new Entry(new Long(0L), KeyCoordinator.newCoordinationData(IDCOUNTER_KEY));
                capi.write(entry, idCounterContainer, XvsmUtils.DEFAULT_TIMEOUT, null);

            }

        } catch (MzsCoreException e) {

            throw new ServiceException("Could not initialize ID-Container "+e.getMessage());
        }

    }

    @Override
    public long getNewId() throws ServiceException {

        long id;

        List<Selector> selectors = new ArrayList<Selector>();
        selectors.add(KeyCoordinator.newSelector(IDCOUNTER_KEY));
        TransactionReference transaction = null;

        try {
            transaction = capi.createTransaction(XvsmUtils.DEFAULT_TIMEOUT, spaceUri);

            // vom Container
            ArrayList<Long> items;
            try {
                items = capi.take(idCounterContainer, selectors, XvsmUtils.DEFAULT_TIMEOUT, transaction);
            }
            catch (CountNotMetException e) {
                throw new NotAvailableException("ID_CONTAINER", e);
            }

            Long counter;
            counter = items.get(0);

            // Increment
            id = counter + 1;

            // In n container
            counter = new Long(id);

            Entry entry = new Entry(counter, KeyCoordinator.newCoordinationData(IDCOUNTER_KEY));
            capi.write(entry, idCounterContainer, XvsmUtils.DEFAULT_TIMEOUT, transaction);

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

    @Override
    public ArrayList<Order> listOrders() throws ServiceException {
        return utils.listQueryContainer(getOrdersContainer());
    }

    @Override
    public ArrayList<Rocket> listOrderRockets(long orderId) throws ServiceException {
        ArrayList<Rocket> result;

        try {
            ArrayList<Selector> selectors = new ArrayList<Selector>();

            Query query = new Query().filter(Property.forName("orderId").equalTo(orderId)).sortup(Property.forName("id"));
            selectors.add(QueryCoordinator.newSelector(query, MzsConstants.Selecting.COUNT_ALL));
            result = capi.read(getOrderStockContainer(), selectors, XvsmUtils.DEFAULT_TIMEOUT, null);
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
    public Order getOrder(long orderId) throws ServiceException {
        return (Order)utils.takeById(
                null,
                getOrdersContainer(),
                orderId
        );
    }

    @Override
    public void registerNotification(INotification notification, String containerId, ContainerOperation operation, NotificationMode mode) throws ServiceException {

        boolean all = ("*".equals(containerId));

        for (ContainerReference container : allContainers) {
            if (all || (container.getId().equals(containerId))) {
                try {

                    XvsmNotificationListener listener = new XvsmNotificationListener(capi.getCore(), notification, container, operation, mode);

                } catch (InterruptedException e) {
                    throw new ServiceException(e);
                } catch (MzsCoreException e) {
                    throw new ServiceException(e);
                }
            }
        }
    }

}
