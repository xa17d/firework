package at.sbc.firework.service.alt;

import at.sbc.firework.entities.Rocket;
import at.sbc.firework.service.ContainerOperation;
import at.sbc.firework.service.ICustomerTransaction;
import at.sbc.firework.service.ServiceException;
import at.sbc.firework.service.alt.containers.Container;
import at.sbc.firework.utils.NotificationMode;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Created by daniel on 05.01.2015.
 */
public interface ICustomerServerRmi extends Remote {
    void stop() throws ServiceException, RemoteException;

    String getAddress() throws ServiceException, RemoteException;

    ArrayList<Rocket> listRockets() throws ServiceException, RemoteException;

    ICustomerTransactionRmi startTransaction() throws ServiceException, RemoteException;

    void registerNotification(IRemoteEventListener notification, String containerId, ContainerOperation operation, NotificationMode mode) throws ServiceException, RemoteException;
}
