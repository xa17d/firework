package at.sbc.firework.service.alt;

import at.sbc.firework.entities.Rocket;
import at.sbc.firework.service.ServiceException;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by daniel on 05.01.2015.
 */
public interface ICustomerTransactionRmi extends Remote {
    void addRocket(Rocket rocket) throws ServiceException, RemoteException;

    void commit() throws ServiceException, RemoteException;
    void rollback() throws ServiceException, RemoteException;
}
