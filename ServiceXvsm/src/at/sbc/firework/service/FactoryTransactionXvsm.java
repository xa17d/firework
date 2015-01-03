package at.sbc.firework.service;

import at.sbc.firework.entities.*;
import org.mozartspaces.capi3.*;
import org.mozartspaces.core.*;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by daniel on 14.11.2014.
 */
public class FactoryTransactionXvsm implements IFactoryTransaction {

    private Capi capi;
    private TransactionReference transaction;
    private FactoryService service;

    public FactoryTransactionXvsm(FactoryService service) throws MzsCoreException {
        this.service = service;
        this.capi = service.getCapi();
        this.transaction = capi.createTransaction(MzsConstants.RequestTimeout.INFINITE, service.getSpaceUri());
        //this.transaction = capi.createTransaction(Service.DEFAULT_TIMEOUT, service.getSpaceUri());
    }


    private ArrayList<Part> internalTakeFromStock(ContainerReference container, Class<?> type, int count) throws ServiceException
    {
        ArrayList<Selector> selectors = new ArrayList<Selector>();
        selectors.add(TypeCoordinator.newSelector(type, count));

        ArrayList<Part> entries = null;
        try {
            entries = capi.take(container, selectors, FactoryService.DEFAULT_TIMEOUT, transaction);
        } catch (CountNotMetException e) {
            throw new XvsmException(e);
        }
        catch (MzsCoreException e) {
            throw new XvsmException(e);
        }

        return entries;
    }

    private ArrayList<Serializable> internalTakeFromQueue(ContainerReference container, int count) throws ServiceException
    {
        ArrayList<Selector> selectors = new ArrayList<Selector>();
        selectors.add(FifoCoordinator.newSelector(count));
        ArrayList<Serializable> entries = null;
        try {
            entries = capi.take(container, selectors, FactoryService.DEFAULT_TIMEOUT, transaction);
        } catch (MzsCoreException e) {
            throw new XvsmException(e);
        }

        return entries;
    }

    private <T> T take1FromQueue(ContainerReference container) throws ServiceException
    {
        ArrayList<Serializable> items = internalTakeFromQueue(container, 1);
        if (items.isEmpty()) {
            return null;
        }
        else {
            return (T)items.get(0);
        }
    }

    private void internalAddToContainer(ContainerReference container, Serializable item) throws ServiceException
    {
        Entry entry = new Entry(item);

        try {
            capi.write(entry, container, FactoryService.DEFAULT_TIMEOUT, transaction);

        } catch (MzsCoreException e) {
            throw new XvsmException(e);
        }
    }

    @Override
    public void addToStock(Part part) throws ServiceException
    {
        internalAddToContainer(service.getStockContainer(), part);
    }

    @Override
    public ArrayList<Part> takeFromStock(Class<?> type, int count) throws ServiceException {
        return internalTakeFromStock(service.getStockContainer(), type, count);
    }

    @Override
    public PropellingChargePackage takePropellingChargePackageFromStock() throws ServiceException {
        ArrayList<Selector> selectors = new ArrayList<Selector>();

        Query query = new Query().filter(Property.forClass(PropellingChargePackage.class, "content").exists()).sortup(Property.forClass(PropellingChargePackage.class, "content")).cnt(1);

        selectors.add(QueryCoordinator.newSelector(query, 1));

        ArrayList<PropellingChargePackage> entries = null;
        try {
            entries = capi.take(service.getStockContainer(), selectors, FactoryService.DEFAULT_TIMEOUT, transaction);
        }
        catch (MzsCoreException e) {
            throw new XvsmException(e);
        }

        return entries.get(0);
    }

    @Override
    public void addToQualityCheckQueue(Rocket rocket) throws ServiceException {
        internalAddToContainer(service.getQualityCheckQueueContainer(), rocket);
    }

    @Override
    public Rocket takeFromQualityCheckQueue() throws ServiceException {
        return take1FromQueue(service.getQualityCheckQueueContainer());
    }

    @Override
    public void addToPackingQueue(Rocket rocket) throws ServiceException {
        internalAddToContainer(service.getPackingQueueContainer(), rocket);
    }

    @Override
    public Rocket takeFromPackingQueue() throws ServiceException {
        return take1FromQueue(service.getPackingQueueContainer());
    }

    @Override
    public void addToGarbage(Rocket rocket) throws ServiceException {
        internalAddToContainer(service.getGarbageContainer(), rocket);
    }

    @Override
    public void addToDistributionStock(RocketPackage5 rocketPackage) throws ServiceException {
        internalAddToContainer(service.getDistributionStockContainer(), rocketPackage);
    }

    @Override
    public void addOrder(Order order) throws ServiceException {
        internalAddToContainer(service.getOrdersContainer(), order);
    }

    @Override
    public void addOrderPosition(OrderPosition orderPosition) throws ServiceException {
        internalAddToContainer(service.getOrderPositionsContainer(), orderPosition);
    }

    @Override
    public OrderPosition takeOrderPosition(ArrayList<Long> excludeIds) throws ServiceException {

        ArrayList<Selector> selectors = new ArrayList<Selector>();

        Query query = new Query();

        // Exclude Ids
        for (Long id : excludeIds) {
            query = query.filter(Property.forName("orderId").notEqualTo(id));
        }

        // Count = 1
        query = query.cnt(1);


        selectors.add(QueryCoordinator.newSelector(query, 1));

        ArrayList<OrderPosition> entries = null;
        try {
            entries = capi.take(service.getOrderPositionsContainer(), selectors, FactoryService.DEFAULT_TIMEOUT, transaction);
        }
        catch (MzsCoreException e) {
            throw new XvsmException(e);
        }

        return entries.get(0);
    }

    @Override
    public EffectCharge takeEffectChargeFromStock(Color color) throws ServiceException {

        ArrayList<Selector> selectors = new ArrayList<Selector>();

        Query query = new Query().filter(Property.forClass(EffectCharge.class, "color").exists()).filter(Property.forName("color").equalTo(color)).cnt(1);

        selectors.add(QueryCoordinator.newSelector(query, 1));

        ArrayList<EffectCharge> entries = null;
        try {
            entries = capi.take(service.getStockContainer(), selectors, FactoryService.DEFAULT_TIMEOUT, transaction);
        }
        catch (MzsCoreException e) {
            throw new XvsmException(e);
        }

        return entries.get(0);
    }

    @Override
    public void addRocketToOrder(Rocket rocket) throws ServiceException{
        internalAddToContainer(service.getOrderStockContainer(), rocket);
    }

    @Override
    public void commit() throws ServiceException {
        try {
            capi.commitTransaction(transaction);
        } catch (MzsCoreException e) {
            throw new XvsmException(e);
        }
    }

    @Override
    public void rollback() throws ServiceException {
        try {
            capi.rollbackTransaction(transaction);
        } catch (MzsCoreException e) {
            throw new XvsmException(e);
        }
    }
}