package at.sbc.firework.service.alt.transactions;

import at.sbc.firework.service.alt.containers.Container;
import at.sbc.firework.entities.Part;
import at.sbc.firework.entities.PropellingChargePackage;
import at.sbc.firework.entities.Rocket;
import at.sbc.firework.entities.RocketPackage5;
import at.sbc.firework.service.alt.ClientService;
import at.sbc.firework.service.alt.ServiceAltException;
import at.sbc.firework.service.alt.containers.*;
import at.sbc.firework.service.IServiceTransaction;
import at.sbc.firework.service.ServiceException;
import at.sbc.firework.service.alt.IServiceTransactionRmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

/**
 * Created by daniel on 21.11.2014.
 */
public class Transaction extends UnicastRemoteObject implements IServiceTransaction, IServiceTransactionRmi {

    public Transaction(ClientService clientService) throws RemoteException {
        this.service = clientService;
        this.active = true;
    }

    private void Log(String msg) {
        service.Log("T#"+hashCode()+": "+msg);
    }

    private ClientService service;

    private boolean active;
    public boolean isActive() { return active; }

    private ArrayList<TransactionOperation> operations = new ArrayList<TransactionOperation>();

    private void addOperation(TransactionOperation operation) throws ServiceException{

        synchronized (operations) {
            // druf schaua, dass koa neue Operationa dazua kommt, wenn grad
            // a commit odr an rollback durchgführt wörn, odr scho durchgführt
            // wora sind. Damit nix vrgeassa wird.
            if (!isActive()) {
                throw new ServiceAltException("Transaction is not active anymore");
            }

            operations.add(operation);
            operation.instant();
        }
    }

    private Object containerTake(Container container, ItemSelector selector) throws ServiceException {
        TransactionOperation operation =
                new TransactionOperationTake(
                        this,
                        container,
                        selector
                );
        addOperation(operation);
        return operation.getItem();
    }

    private void containerAdd(Container container, Object item) throws ServiceException {
        addOperation(
                new TransactionOperationAdd(
                        this,
                        container,
                        item
                )
        );
    }

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
    public Rocket takeFromPackingQueue() throws ServiceException {
        Log("takeFromPackingQueue");

        return (Rocket)containerTake(
                service.getServer().getPackingQueueContainer(),
                new ItemSelectorFirst());
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
    public void commit() throws ServiceException {
        Log("commit");
        active = false;

        synchronized (operations) {
            for (TransactionOperation o : operations) {
                o.commit();
            }
        }

        service.endTransaction(this);
    }

    @Override
    public void rollback() throws ServiceException {
        Log("rollback");
        active = false;

        synchronized (operations) {
            for (TransactionOperation o : operations) {
                o.rollback();
            }
        }

        service.endTransaction(this);
    }
}
