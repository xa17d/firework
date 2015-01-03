package at.sbc.firework.service.alt;

import at.sbc.firework.entities.Order;
import at.sbc.firework.entities.Part;
import at.sbc.firework.entities.Rocket;
import at.sbc.firework.entities.RocketPackage5;
import at.sbc.firework.service.ContainerOperation;
import at.sbc.firework.service.INotification;
import at.sbc.firework.service.alt.transactions.FactoryTransaction;
import at.sbc.firework.service.ServiceException;
import at.sbc.firework.utils.NotificationMode;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Date;

/**
 * Isch der Service für an Client. Jeder Client kriagt oa Instanz vo dera Class.
 */
public class FactoryServiceClient extends UnicastRemoteObject implements IFactoryServiceRmi {

    public FactoryServiceClient(FactoryServer server) throws RemoteException
    {
        Log("Client connected");
        this.server = server;
        this.lastPing = new Date();
    }

    public void Log(String msg) {
        System.out.println("C#"+hashCode()+": "+msg);
    }

    private FactoryServer server;

    public FactoryServer getServer() { return server; }

    private ArrayList<FactoryTransaction> transactions = new ArrayList<FactoryTransaction>();

    private Date lastPing;

    public Date getLastPing() { return lastPing; }

    @Override
    public void ping() throws RemoteException {
        lastPing = new Date();
    }

    public void cancel() throws ServiceException{
        Log("Client disconnected");

        synchronized (transactions)
        {
            while (!transactions.isEmpty()) {
                transactions.get(0).rollback();
            }
        }

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
        synchronized (transactions) {
            FactoryTransaction t = null;
            try {
                t = new FactoryTransaction(this);
            } catch (RemoteException e) {
                throw new ServiceException(e);
            }
            transactions.add(t);
            return t;
        }
    }

    public void endTransaction(FactoryTransaction t) {
        synchronized (transactions)
        {
            transactions.remove(t);
        }
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
        // TODO: implement
        return null;
    }

    @Override
    public ArrayList<Rocket> listOrderRockets(long orderId) throws ServiceException, RemoteException {
        // TODO: implement
        return null;
    }

    @Override
    public int getOrderRocketCount(long orderId) throws ServiceException, RemoteException {
        // TODO: implement
        return 0;
    }

    @Override
    public void registerNotification(INotification notification, String containerId, ContainerOperation operation, NotificationMode mode) throws ServiceException, RemoteException {
        // TODO: implement correctly
        this.remoteEventListeners.add(new RemoteEventListener(notification));
    }

    private ArrayList<IRemoteEventListener> remoteEventListeners = new ArrayList<IRemoteEventListener>();

    public void dataChanged() throws ServiceException{
        for(IRemoteEventListener listener : remoteEventListeners) {
            try {
                listener.invoke();
            } catch (RemoteException e) {
                throw new ServiceException(e);
            }
        }
    }

    @Override
    public long getNewId() throws ServiceException {
        return server.getNewId();
    }
}
