package at.sbc.firework.service;

import at.sbc.firework.daos.Part;
import at.sbc.firework.daos.Rocket;
import at.sbc.firework.daos.RocketPackage5;
import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.capi3.Selector;
import org.mozartspaces.capi3.TypeCoordinator;
import org.mozartspaces.core.*;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by daniel on 14.11.2014.
 */
public class ServiceTransactionXvsm implements IServiceTransaction {

    private Capi capi;
    private TransactionReference transaction;
    private Service service;

    public ServiceTransactionXvsm(Service service) throws MzsCoreException {
        this.service = service;
        this.capi = service.getCapi();
        this.transaction = capi.createTransaction(MzsConstants.RequestTimeout.INFINITE, service.getSpaceUri());
    }


    private ArrayList<Part> internalTakeFromStock(ContainerReference container, Class<?> type, int count) throws ServiceException
    {
        ArrayList<Selector> selectors = new ArrayList<Selector>();
        selectors.add(TypeCoordinator.newSelector(type, count));
        ArrayList<Part> entries = null;
        try {
            entries = capi.take(container, selectors, MzsConstants.RequestTimeout.ZERO, transaction);
        } catch (MzsCoreException e) {
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
            entries = capi.take(container, selectors, MzsConstants.RequestTimeout.ZERO, transaction);
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
            capi.write(entry, container, Service.DEFAULT_TIMEOUT, transaction);

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
    public void addToOpenStock(Part part) throws ServiceException {
        internalAddToContainer(service.getOpenStockContainer(), part);
    }

    @Override
    public ArrayList<Part> takeFromOpenStock(Class<?> type, int count) throws ServiceException {
        return internalTakeFromStock(service.getOpenStockContainer(), type, count);
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
