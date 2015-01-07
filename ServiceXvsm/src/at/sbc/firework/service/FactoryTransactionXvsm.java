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

    private XvsmUtils utils;
    private TransactionReference transaction;
    private FactoryService service;

    public FactoryTransactionXvsm(FactoryService service) throws MzsCoreException {
        this.service = service;
        this.utils = service.getXvsmUtils();
        this.transaction = utils.createTransaction();
    }

    @Override
    public void addToStock(Part part) throws ServiceException
    {
        utils.addToContainer(transaction, service.getStockContainer(), part);
    }

    @Override
    public ArrayList<Part> takeFromStock(Class<?> type, int count) throws ServiceException {
        return utils.takeFromStock(transaction, service.getStockContainer(), type, count);
    }

    @Override
    public PropellingChargePackage takePropellingChargePackageFromStock() throws ServiceException {
        ArrayList<Selector> selectors = new ArrayList<Selector>();

        Query query = new Query().filter(Property.forClass(PropellingChargePackage.class, "content").exists()).sortup(Property.forClass(PropellingChargePackage.class, "content")).cnt(1);

        selectors.add(QueryCoordinator.newSelector(query, 1));

        ArrayList<PropellingChargePackage> entries = null;
        try {
            entries = utils.getCapi().take(service.getStockContainer(), selectors, MzsConstants.RequestTimeout.TRY_ONCE, transaction);
        } catch (CountNotMetException e) {
            throw new NotAvailableException(service.getStockContainer().getId(), e);
        }
        catch (MzsCoreException e) {
            throw new XvsmException(e);
        }

        return entries.get(0);
    }

    @Override
    public void addToQualityCheckQueue(Rocket rocket) throws ServiceException {
        utils.addToContainer(transaction, service.getQualityCheckQueueContainer(), rocket);
    }

    @Override
    public Rocket takeFromQualityCheckQueue() throws ServiceException {
        return utils.take1FromQueue(transaction, service.getQualityCheckQueueContainer());
    }

    @Override
    public void addToPackingQueue(Rocket rocket) throws ServiceException {
        utils.addToContainer(transaction, service.getPackingQueueContainer(), rocket);
    }

    @Override
    public ArrayList<Rocket> takeFromPackingQueue(int count, Quality quality, OrderMode orderMode) throws ServiceException {

        ArrayList<Selector> selectors = new ArrayList<Selector>();

        Query query = null;

        switch (orderMode) {

            case Indifferent:
                query = new Query().filter(Property.forName("quality").equalTo(quality)).cnt(count);
                break;
            case MustBeOrdered:
                query = new Query().filter(
                        Matchmakers.and(
                                Property.forName("quality").equalTo(quality),
                                Property.forName("orderId").notEqualTo(-1L)
                        )
                ).cnt(count);
                break;
            case MustNotBeOrdered:
                query = new Query().filter(
                        Matchmakers.and(
                                Property.forName("quality").equalTo(quality),
                                Property.forName("orderId").equalTo(-1L)
                        )
                ).cnt(count);
                break;
        }

        selectors.add(QueryCoordinator.newSelector(query, count));

        ArrayList<Rocket> entries = null;
        try {
            entries = utils.getCapi().take(service.getPackingQueueContainer(), selectors, MzsConstants.RequestTimeout.TRY_ONCE, transaction);
        } catch (CountNotMetException e) {
            throw new NotAvailableException(service.getPackingQueueContainer().getId(), e);
        } catch (MzsCoreException e) {
            throw new XvsmException(e);
        }

        return entries;
    }

    @Override
    public void addToGarbage(Rocket rocket) throws ServiceException {
        utils.addToContainer(transaction, service.getGarbageContainer(), rocket);
    }

    @Override
    public void addToDistributionStock(RocketPackage5 rocketPackage) throws ServiceException {
        utils.addToContainer(transaction, service.getDistributionStockContainer(), rocketPackage);
    }

    @Override
    public void addOrder(Order order) throws ServiceException {
        utils.addToContainer(transaction, service.getOrdersContainer(), order);
    }

    @Override
    public Order takeOrder(long orderId) throws ServiceException {
        return (Order)utils.takeById(transaction, service.getOrdersContainer(), orderId);
    }

    @Override
    public void addOrderPosition(OrderPosition orderPosition) throws ServiceException {
        utils.addToContainer(transaction, service.getOrderPositionsContainer(), orderPosition);
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
            entries = utils.getCapi().take(service.getOrderPositionsContainer(), selectors, MzsConstants.RequestTimeout.TRY_ONCE, transaction);
        } catch (CountNotMetException e) {
            throw new NotAvailableException(service.getOrderPositionsContainer().getId(), e);
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
            entries = utils.getCapi().take(service.getStockContainer(), selectors, MzsConstants.RequestTimeout.TRY_ONCE, transaction);
        } catch (CountNotMetException e) {
            throw new NotAvailableException(service.getStockContainer().getId(), e);
        } catch (MzsCoreException e) {
            throw new XvsmException(e);
        }

        return entries.get(0);
    }

    @Override
    public void addRocketToOrder(Rocket rocket) throws ServiceException{
        utils.addToContainer(transaction, service.getOrderStockContainer(), rocket);
    }

    @Override
    public void commit() throws ServiceException {
        try {
            utils.getCapi().commitTransaction(transaction);
        } catch (MzsCoreException e) {
            throw new XvsmException(e);
        }
    }

    @Override
    public void rollback() throws ServiceException {
        try {
            utils.getCapi().rollbackTransaction(transaction);
        } catch (MzsCoreException e) {
            throw new XvsmException(e);
        }
    }
}