package at.sbc.firework.service;

import at.sbc.firework.entities.Rocket;
import at.sbc.firework.service.alt.*;
import at.sbc.firework.utils.NotificationMode;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Quasi an RMI-Proxy zöm CustomerServer Objekt
 */
public class CustomerService implements ICustomerService {

    private ICustomerServerRmi remoteService;

    @Override
    public void startCreate(long customerId) throws ServiceException {

        try {
            remoteService = new CustomerServer(customerId);
        } catch (RemoteException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public void startGet(String address) throws ServiceException {
        System.setProperty("java.security.policy","file:./firework.policy");

        try {
            if(System.getSecurityManager() == null)
                System.setSecurityManager(new SecurityManager());

            Console.println("Lookup RMI...");
            remoteService = (ICustomerServerRmi) Naming.lookup("rmi://" + RmiRegistry.HOST + ":" + RmiRegistry.PORT + "/" + FactoryServer.SERVER_NAME);
            Console.println("connected to server: " + "rmi://" + RmiRegistry.HOST + ":" + RmiRegistry.PORT + "/" + FactoryServer.SERVER_NAME);

        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public void stop() throws ServiceException {
        try {
            remoteService.stop();
        } catch (RemoteException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public String getAddress() throws ServiceException {
        try {
            return remoteService.getAddress();
        } catch (RemoteException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public ArrayList<Rocket> listRockets() throws ServiceException {
        try {
            return remoteService.listRockets();
        } catch (RemoteException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public ICustomerTransaction startTransaction() throws ServiceException {
        try {
            return new CustomerTransactionAlt(remoteService.startTransaction());
        } catch (RemoteException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public void registerNotification(INotification notification, String containerId, ContainerOperation operation, NotificationMode mode) throws ServiceException {
        try {
            RemoteEventListener remoteListener = new RemoteEventListener(notification);
            remoteService.registerNotification(remoteListener, containerId, operation, mode);
        } catch (RemoteException e) {
            throw new ServiceException(e);
        }
    }
}
