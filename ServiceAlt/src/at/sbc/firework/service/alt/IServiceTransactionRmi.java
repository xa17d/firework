package at.sbc.firework.service.alt;

import at.sbc.firework.entities.Part;
import at.sbc.firework.entities.PropellingChargePackage;
import at.sbc.firework.entities.Rocket;
import at.sbc.firework.entities.RocketPackage5;
import at.sbc.firework.service.IServiceTransaction;
import at.sbc.firework.service.ServiceException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Transaction Interfce fürs RMI
 */
public interface IServiceTransactionRmi extends Remote {

    void addToStock(Part part) throws ServiceException, RemoteException;
    ArrayList<Part> takeFromStock(Class<?> type, int count) throws ServiceException, RemoteException;
    PropellingChargePackage takePropellingChargePackageFromStock() throws ServiceException, RemoteException;

    void addToQualityCheckQueue(Rocket rocket) throws ServiceException, RemoteException;
    Rocket takeFromQualityCheckQueue() throws ServiceException, RemoteException;

    void addToPackingQueue(Rocket rocket) throws ServiceException, RemoteException;
    Rocket takeFromPackingQueue() throws ServiceException, RemoteException;

    void addToGarbage(Rocket rocket) throws ServiceException, RemoteException;

    void addToDistributionStock(RocketPackage5 rocket) throws ServiceException, RemoteException;

    void commit() throws ServiceException, RemoteException;
    void rollback() throws ServiceException, RemoteException;

}
