package at.sbc.firework.service.alt;

import at.sbc.firework.entities.*;
import at.sbc.firework.service.alt.containers.*;
import at.sbc.firework.service.IFactoryTransaction;
import at.sbc.firework.service.ServiceException;
import at.sbc.firework.service.alt.transactions.*;

import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Created by daniel on 21.11.2014.
 */
public class FactoryTransaction extends Transaction implements IFactoryTransaction, IFactoryTransactionRmi {

    public FactoryTransaction(FactoryServiceClient clientService, TransactionManager transactionManager) throws RemoteException {
        super(transactionManager);
        this.service = clientService;

        Log("transaction start");
    }

    private void Log(String msg) {
        service.Log("T#"+hashCode()+": "+msg);
    }

    private FactoryServiceClient service;

    @Override
    public void addToStock(Part part) throws ServiceException {
        Log("addToStock "+part);
        containerAdd(service.getServer().getStockContainer(), part);
    }

    @Override
    public ArrayList<Part> takeFromStock(Class<?> type, int count) throws ServiceException {
        Log("takeFromStock "+type.toString()+" count: "+count);
        ArrayList<Part> result = new ArrayList<Part>();

        for (int i = 0; i<count; i++) {
            Object item = containerTake(service.getServer().getStockContainer(), new ItemSelectorType(type));
            result.add((Part)item);
        }

        return result;
    }

    @Override
    public PropellingChargePackage takePropellingChargePackageFromStock() throws ServiceException {
        Log("takePropellingChargePackageFromStock");

        return (PropellingChargePackage)containerTake(
                service.getServer().getStockContainer(),
                new ItemSelectorPropellantChargePackage()
        );
    }

    @Override
    public void addToQualityCheckQueue(Rocket rocket) throws ServiceException {
        Log("addToQualityCheckQueue "+rocket);
        containerAdd(service.getServer().getQualityCheckQueueContainer(), rocket);
    }

    @Override
    public Rocket takeFromQualityCheckQueue() throws ServiceException {
        Log("takeFromQualityCheckQueue");

        return (Rocket)containerTake(
                service.getServer().getQualityCheckQueueContainer(),
                new ItemSelectorFirst());
    }

    @Override
    public void addToPackingQueue(Rocket rocket) throws ServiceException {
        Log("addToPackingQueue "+rocket);

        containerAdd(service.getServer().getPackingQueueContainer(), rocket);
    }

    @Override
    public ArrayList<Rocket> takeFromPackingQueue(int count, Quality quality, OrderMode orderMode) throws ServiceException {
        Log("takeFromPackingQueue count: "+count+"; quality: "+quality+"; orderMode: "+orderMode);

        ArrayList<Rocket> result = new ArrayList<Rocket>();

        for (int i = 0; i<count; i++) {
            Object item = containerTake(service.getServer().getPackingQueueContainer(), new ItemSelectorQualityOrderMode(quality, orderMode));
            result.add((Rocket)item);
        }

        return result;
    }

    @Override
    public void addToGarbage(Rocket rocket) throws ServiceException {
        Log("addToGarbage "+rocket);

        containerAdd(service.getServer().getGarbageContainer(), rocket);
    }

    @Override
    public void addToDistributionStock(RocketPackage5 rocketPackage) throws ServiceException {
        Log("addToDistributionStock "+rocketPackage);

        containerAdd(service.getServer().getDistributionStockContainer(), rocketPackage);
    }

    @Override
    public void addOrder(Order order) throws ServiceException {
        Log("addOrder "+order);

        containerAdd(service.getServer().getOrdersContainer(), order);
    }

    @Override
    public Order takeOrder(long orderId) throws ServiceException {
        Log("takeOrder "+orderId);

        return (Order)containerTake(
                service.getServer().getOrdersContainer(),
                new ItemSelectorOrderId(orderId));
    }

    @Override
    public void addOrderPosition(OrderPosition orderPosition) throws ServiceException {
        Log("addOrderPosition "+orderPosition);

        containerAdd(service.getServer().getOrderPositionsContainer(), orderPosition);
    }

    @Override
    public OrderPosition takeOrderPosition(ArrayList<Long> excludeIds) throws ServiceException {
        Log("takeOrderPosition "+excludeIds.toString());

        return (OrderPosition)containerTake(
                service.getServer().getStockContainer(),
                new ItemSelectorColorOrderPosition(excludeIds));
    }

    @Override
    public EffectCharge takeEffectChargeFromStock(Color color) throws ServiceException {
        Log("takeEffectChargeFromStock "+color);

        return (EffectCharge)containerTake(
                service.getServer().getStockContainer(),
                new ItemSelectorColor(color));
    }

    @Override
    public void addRocketToOrder(Rocket rocket) throws ServiceException {
        Log("addRocketToOrder "+rocket);

        containerAdd(service.getServer().getOrderPositionsContainer(), rocket);
    }

    @Override
    public void commit() throws ServiceException {
        Log("commit");
        super.commit();
    }

    @Override
    public void rollback() throws ServiceException {
        Log("rollback");
        super.rollback();
    }
}
