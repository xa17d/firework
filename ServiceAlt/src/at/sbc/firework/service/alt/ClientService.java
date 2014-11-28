package at.sbc.firework.service.alt;

import at.sbc.firework.entities.Part;
import at.sbc.firework.entities.Rocket;
import at.sbc.firework.entities.RocketPackage5;
import at.sbc.firework.service.alt.transactions.Transaction;
import at.sbc.firework.service.IDataChangedListener;
import at.sbc.firework.service.IService;
import at.sbc.firework.service.IServiceTransaction;
import at.sbc.firework.service.ServiceException;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import at.sbc.firework.service.alt.Server;

/**
 * Created by daniel on 21.11.2014.
 */
public class ClientService extends UnicastRemoteObject implements IServiceRmi {

    public ClientService(Server server) throws RemoteException
    {
        Log("Client connected");
        this.server = server;
    }

    public void Log(String msg) {
        System.out.println("C#"+hashCode()+": "+msg);
    }

    private Server server;

    public Server getServer() { return server; }

    private ArrayList<Transaction> transactions = new ArrayList<Transaction>();

    public void cancel() throws ServiceException{
        Log("Client disconnected");

        synchronized (transactions)
        {
            for (Transaction t: transactions) {
                t.rollback();
            }
        }
    }

    @Override
    public void start() throws ServiceException {
        Log("Client Start");
        //TODO
    }

    @Override
    public void stop() throws ServiceException {
        Log("Client Stop");
        //TODO
    }

    @Override
    public IServiceTransactionRmi startTransaction() throws ServiceException {
        synchronized (transactions) {
            Transaction t = null;
            try {
                t = new Transaction(this);
            } catch (RemoteException e) {
                throw new ServiceException(e);
            }
            transactions.add(t);
            return t;
        }
    }

    public void endTransaction(Transaction t) {
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

    private IRemoteEventListener listener;
    @Override
    public void setChangeListener(IRemoteEventListener listener) {
        this.listener = listener;
    }

    public void dataChanged() throws ServiceException{
        if (listener != null) {
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
