package at.sbc.firework.service.alt;

import at.sbc.firework.entities.*;
import at.sbc.firework.service.ServiceException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Transaction Interfce fürs RMI
 */
public interface IFactoryTransactionRmi extends Remote {

    void addToStock(Part part) throws ServiceException, RemoteException;
    ArrayList<Part> takeFromStock(Class<?> type, int count) throws ServiceException, RemoteException;
    PropellingChargePackage takePropellingChargePackageFromStock() throws ServiceException, RemoteException;

    void addToQualityCheckQueue(Rocket rocket) throws ServiceException, RemoteException;
    Rocket takeFromQualityCheckQueue() throws ServiceException, RemoteException;

    void addToPackingQueue(Rocket rocket) throws ServiceException, RemoteException;
    Rocket takeFromPackingQueue() throws ServiceException, RemoteException;

    void addToGarbage(Rocket rocket) throws ServiceException, RemoteException;

    void addToDistributionStock(RocketPackage5 rocket) throws ServiceException, RemoteException;

    void addOrder(Order order) throws ServiceException, RemoteException;
    Order takeOrder(long orderId) throws ServiceException, RemoteException;
    void addOrderPosition(OrderPosition orderPosition) throws ServiceException, RemoteException;
    OrderPosition takeOrderPosition(ArrayList<Long> excludeIds) throws ServiceException, RemoteException;
    EffectCharge takeEffectChargeFromStock(Color color) throws ServiceException, RemoteException;

    void addRocketToOrder(Rocket rocket) throws ServiceException, RemoteException;

    void commit() throws ServiceException, RemoteException;
    void rollback() throws ServiceException, RemoteException;
}
