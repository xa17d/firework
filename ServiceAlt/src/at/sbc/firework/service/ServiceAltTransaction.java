package at.sbc.firework.service;

import at.sbc.firework.entities.Part;
import at.sbc.firework.entities.PropellingChargePackage;
import at.sbc.firework.entities.Rocket;
import at.sbc.firework.entities.RocketPackage5;
import at.sbc.firework.service.alt.IServiceTransactionRmi;

import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Created by daniel on 28.11.2014.
 */
public class ServiceAltTransaction implements IServiceTransaction {

    public ServiceAltTransaction(IServiceTransactionRmi remoteTransaction) {
        this.remoteTransaction = remoteTransaction;
    }

    private IServiceTransactionRmi remoteTransaction;

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
