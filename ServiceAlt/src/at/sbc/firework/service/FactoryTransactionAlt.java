package at.sbc.firework.service;

import at.sbc.firework.entities.*;
import at.sbc.firework.service.alt.IFactoryTransactionRmi;

import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Wrapper fürd RMI Transaction
 */
public class FactoryTransactionAlt implements IFactoryTransaction {

    public FactoryTransactionAlt(IFactoryTransactionRmi remoteTransaction) {
        this.remoteTransaction = remoteTransaction;
    }

    private IFactoryTransactionRmi remoteTransaction;

    @Override
    public void addToStock(Part part) throws ServiceException {
        try {
            remoteTransaction.addToStock(part);
        } catch (RemoteException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public ArrayList<Part> takeFromStock(Class<?> type, int count) throws ServiceException {
        try {
            return remoteTransaction.takeFromStock(type, count);
        } catch (RemoteException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public PropellingChargePackage takePropellingChargePackageFromStock() throws ServiceException {
        try {
            return remoteTransaction.takePropellingChargePackageFromStock();
        } catch (RemoteException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public void addToQualityCheckQueue(Rocket rocket) throws ServiceException {
        try {
            remoteTransaction.addToQualityCheckQueue(rocket);
        } catch (RemoteException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public Rocket takeFromQualityCheckQueue() throws ServiceException {
        try {
            return remoteTransaction.takeFromQualityCheckQueue();
        } catch (RemoteException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public void addToPackingQueue(Rocket rocket) throws ServiceException {
        try {
            remoteTransaction.addToPackingQueue(rocket);
        } catch (RemoteException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public Rocket takeFromPackingQueue() throws ServiceException {
        try {
            return remoteTransaction.takeFromPackingQueue();
        } catch (RemoteException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public void addToGarbage(Rocket rocket) throws ServiceException {
        try {
            remoteTransaction.addToGarbage(rocket);
        } catch (RemoteException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public void addToDistributionStock(RocketPackage5 rocketPackage) throws ServiceException {
        try {
            remoteTransaction.addToDistributionStock(rocketPackage);
        } catch (RemoteException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public void addOrder(Order order) throws ServiceException {
        try {
            remoteTransaction.addOrder(order);
        } catch (RemoteException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public void addOrderPosition(OrderPosition orderPosition) throws ServiceException {
        try {
            remoteTransaction.addOrderPosition(orderPosition);
        } catch (RemoteException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public OrderPosition takeOrderPosition(ArrayList<Long> excludeIds) throws ServiceException {
        try {
            return remoteTransaction.takeOrderPosition(excludeIds);
        } catch (RemoteException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public EffectCharge takeEffectChargeFromStock(Color color) throws ServiceException {
        try {
            return remoteTransaction.takeEffectChargeFromStock(color);
        } catch (RemoteException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public void addRocketToOrder(Rocket rocket) {
        // TODO: implement
    }

    @Override
    public void commit() throws ServiceException {
        try {
            remoteTransaction.commit();
        } catch (RemoteException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public void rollback() throws ServiceException {
        try {
            remoteTransaction.rollback();
        } catch (RemoteException e) {
            throw new ServiceException(e);
        }
    }
}
