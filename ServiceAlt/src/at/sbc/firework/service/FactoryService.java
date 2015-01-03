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

    private static final String HOST = "localhost";
    private static final int PORT = 9876;

    private IFactoryServerRmi server;
    private IFactoryServiceRmi remoteService;
    private Pinger pinger;

    @Override
    public void start() throws ServiceException {
        System.out.println("--- Alternative Service ---");

        System.setProperty("java.security.policy","file:./firework.policy");

        try {
            if(System.getSecurityManager() == null)
                System.setSecurityManager(new SecurityManager());

            System.out.println("Lookup RMI...");
            server = (IFactoryServerRmi) Naming.lookup("rmi://" + HOST + ":" + PORT + "/" + FactoryServer.SERVER_NAME);

            System.out.println("Get Remote Service...");
            remoteService = server.getService();

            System.out.println("connected to server: " + "rmi://" + HOST + ":" + PORT + "/" + FactoryServer.SERVER_NAME);

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
    public IFactoryTransaction startTransaction() throws ServiceException {
        try {
            return new ServiceAltTransaction(remoteService.startTransaction());
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
    public void registerNotification(INotification notification, String containerId, ContainerOperation operation, NotificationMode mode) throws ServiceException {
        // TODO: implement correctly
        try {
            RemoteEventListener remoteListener = new RemoteEventListener(notification);
            // TODO: register notification
            // remoteService.registerNotification(...);
        } catch (RemoteException e) {
            e.printStackTrace();
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