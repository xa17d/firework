package at.sbc.firework.service.alt;

import at.sbc.firework.entities.*;
import at.sbc.firework.service.Console;
import at.sbc.firework.service.ContainerOperation;
import at.sbc.firework.service.alt.containers.Container;
import at.sbc.firework.service.ServiceException;
import at.sbc.firework.service.alt.transactions.TransactionManager;
import at.sbc.firework.utils.NotificationMode;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.function.Predicate;

/**
 * Isch der Service für an Client. Jeder Client kriagt oa Instanz vo dera Class.
 * Dia Instanz loft ufm Server, da Client kriagt zugriff ufd Methoda mittels RMI
 */
public class FactoryServiceClient extends UnicastRemoteObject implements IFactoryServiceRmi {

    public FactoryServiceClient(FactoryServer server) throws RemoteException
    {
        Log("Client connected");
        this.server = server;
        this.lastPing = new Date();
    }

    public void Log(String msg) {
        Console.println("C#" + hashCode() + ": " + msg);
    }

    private FactoryServer server;

    public FactoryServer getServer() { return server; }

    private Date lastPing;
    private TransactionManager transactionManager = new TransactionManager();

    public Date getLastPing() { return lastPing; }

    @Override
    public void ping() throws RemoteException {
        lastPing = new Date();
    }

    public void cancel() throws ServiceException{
        Log("Client disconnected");

        transactionManager.cancel();

        server.disconnectClient(this);
    }

    @Override
    public void start() throws ServiceException {
        Log("Client Start");
    }

    @Override
    public void stop() throws ServiceException {
        Log("Client Stop");
    }

    @Override
    public IFactoryTransactionRmi startTransaction() throws ServiceException {
        FactoryTransaction t;
        try {
            t = new FactoryTransaction(this, transactionManager);
        } catch (RemoteException e) {
            throw new ServiceException(e);
        }
        return t;
    }

    @Override
    public ArrayList<Part> listStock() throws ServiceException {
        return getServer().getStockContainer().list();
    }

    @Override
    public ArrayList<Rocket> listQualityCheckQueue() throws ServiceException {
        return getServer().getQualityCheckQueueContainer().list();
    }

    @Override
    public ArrayList<Rocket> listPackingQueue() throws ServiceException {
        return getServer().getPackingQueueContainer().list();
    }

    @Override
    public ArrayList<Rocket> listGarbage() throws ServiceException {
        return getServer().getGarbageContainer().list();
    }

    @Override
    public ArrayList<RocketPackage5> listDistributionStock() throws ServiceException {
        return getServer().getDistributionStockContainer().list();
    }

    @Override
    public ArrayList<Order> listOrders() throws ServiceException, RemoteException {
        return getServer().getOrdersContainer().list();
    }

    @Override
    public ArrayList<Rocket> listOrderRockets(final long orderId) throws ServiceException, RemoteException {
        final ArrayList<Rocket> result = getServer().getOrderStockContainer().list();
        result.removeIf(
                new Predicate<Rocket>() {
                    @Override
                    public boolean test(Rocket rocket) {
                        OrderPosition p = rocket.getOrderPosition();
                        return (p != null) && (p.getOrderId() == orderId);
                    }
                }
        );
        return result;
    }

    @Override
    public int getOrderRocketCount(long orderId) throws ServiceException, RemoteException {
        final ArrayList<Rocket> list = getServer().getOrderStockContainer().list();
        int count = 0;
        for (Rocket rocket : list) {
            OrderPosition p = rocket.getOrderPosition();
            if ((p != null) && (p.getOrderId() == orderId)) {
                count++;
            }
        }
        return count;
    }

    @Override
    public Order getOrder(long orderId) throws ServiceException, RemoteException {
        for (Order o : listOrders()) {
            if (o.getId() == orderId) {
                return o;
            }
        }

        return null;
    }

    @Override
    public void registerNotification(IRemoteEventListener notification, String containerId, ContainerOperation operation, NotificationMode mode) throws ServiceException, RemoteException {
        boolean all = ("*".equals(containerId));

        for (Container container : getServer().getContainers()) {
            if (all || (container.getId().equals(containerId))) {
                AltNotification listener = new AltNotification(notification, operation, mode);
                container.registerNotification(listener);
                Log("registerNotification "+operation+"@"+container.getId()+"#"+mode);
            }
        }
    }

    @Override
    public long getNewId() throws ServiceException {
        return server.getNewId();
    }
}
