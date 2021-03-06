package at.sbc.firework.service;

import at.sbc.firework.entities.Order;
import at.sbc.firework.entities.Part;
import at.sbc.firework.entities.Rocket;
import at.sbc.firework.entities.RocketPackage5;
import at.sbc.firework.service.alt.*;
import at.sbc.firework.utils.NotificationMode;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Wrapper fürn RMI Service
 */
public class FactoryService implements IFactoryService {

    private IFactoryServerRmi server;
    private IFactoryServiceRmi remoteService;
    private Pinger pinger;
    private String address;

    public static String getDefaultFactoryAddress() {
        return "rmi://" + RmiRegistry.HOST + ":" + RmiRegistry.PORT + "/" + FactoryServer.SERVER_NAME;
    }

    @Override
    public void start() throws ServiceException {
        Console.println("--- Alternative Service ---");

        System.setProperty("java.security.policy","file:./firework.policy");

        try {
            if(System.getSecurityManager() == null)
                System.setSecurityManager(new SecurityManager());

            Console.println("Lookup RMI...");
            server = (IFactoryServerRmi) Naming.lookup(address);

            Console.println("Get Remote Service...");
            remoteService = server.getService();

            Console.println("connected to server: " + address);

            pinger = new Pinger(remoteService);
            pinger.start();

        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public void stop() throws ServiceException {
        if (pinger != null) {
            pinger.interrupt();
        }

        if(remoteService != null) {
            try {
                remoteService.cancel();
            } catch (RemoteException e) {
                throw new ServiceException(e);
            }
        }
    }

    @Override
    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public IFactoryTransaction startTransaction() throws ServiceException {
        try {
            return new FactoryTransactionAlt(remoteService.startTransaction());
        } catch (RemoteException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public ArrayList<Part> listStock() throws ServiceException {
        try {
            return remoteService.listStock();
        } catch (RemoteException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public ArrayList<Rocket> listQualityCheckQueue() throws ServiceException {
        try {
            return remoteService.listQualityCheckQueue();
        } catch (RemoteException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public ArrayList<Rocket> listPackingQueue() throws ServiceException {
        try {
            return remoteService.listPackingQueue();
        } catch (RemoteException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public ArrayList<Rocket> listGarbage() throws ServiceException {
        try {
            return remoteService.listGarbage();
        } catch (RemoteException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public ArrayList<RocketPackage5> listDistributionStock() throws ServiceException {
        try {
            return remoteService.listDistributionStock();
        } catch (RemoteException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public ArrayList<Order> listOrders() throws ServiceException {
        try {
            return remoteService.listOrders();
        } catch (RemoteException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public ArrayList<Rocket> listOrderRockets(long orderId) throws ServiceException {
        try {
            return remoteService.listOrderRockets(orderId);
        } catch (RemoteException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public int getOrderRocketCount(long orderId) throws ServiceException {
        try {
            return remoteService.getOrderRocketCount(orderId);
        } catch (RemoteException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public Order getOrder(long orderId) throws ServiceException {
        try {
            return remoteService.getOrder(orderId);
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

    @Override
    public long getNewId() throws ServiceException {
        try {
            return remoteService.getNewId();
        } catch (RemoteException e) {
            throw new ServiceException(e);
        }
    }

}
