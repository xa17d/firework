package at.sbc.firework.service.alt;

import at.sbc.firework.entities.Order;
import at.sbc.firework.entities.Part;
import at.sbc.firework.entities.Rocket;
import at.sbc.firework.entities.RocketPackage5;
import at.sbc.firework.service.ContainerOperation;
import at.sbc.firework.service.INotification;
import at.sbc.firework.service.ServiceException;
import at.sbc.firework.utils.NotificationMode;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Service Interface fürs RMI
 */
public interface IFactoryServiceRmi extends Remote {
    void cancel() throws ServiceException, RemoteException;

    void start() throws ServiceException, RemoteException;
    void stop() throws ServiceException, RemoteException;

    IFactoryTransactionRmi startTransaction() throws ServiceException, RemoteException;

    ArrayList<Part> listStock() throws ServiceException, RemoteException;
    ArrayList<Rocket> listQualityCheckQueue() throws ServiceException, RemoteException;
    ArrayList<Rocket> listPackingQueue() throws ServiceException, RemoteException;
    ArrayList<Rocket> listGarbage() throws ServiceException, RemoteException;
    ArrayList<RocketPackage5> listDistributionStock() throws ServiceException, RemoteException;

    ArrayList<Order> listOrders() throws ServiceException, RemoteException;
    ArrayList<Rocket> listOrderRockets(long orderId) throws ServiceException, RemoteException;
    int getOrderRocketCount(long orderId) throws ServiceException, RemoteException;

    void registerNotification(IRemoteEventListener notification, String containerId, ContainerOperation operation, NotificationMode mode) throws ServiceException, RemoteException;

    long getNewId() throws ServiceException, RemoteException;

    void ping() throws RemoteException;
}
