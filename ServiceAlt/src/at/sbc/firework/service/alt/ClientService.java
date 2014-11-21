package at.sbc.firework.service.alt;

import at.sbc.firework.entities.Part;
import at.sbc.firework.entities.Rocket;
import at.sbc.firework.entities.RocketPackage5;
import at.sbc.firework.service.alt.transactions.Transaction;
import at.sbc.firework.service.IDataChangedListener;
import at.sbc.firework.service.IService;
import at.sbc.firework.service.IServiceTransaction;
import at.sbc.firework.service.ServiceException;

import java.util.ArrayList;

/**
 * Created by daniel on 21.11.2014.
 */
public class ClientService implements IService, IServiceRmi {

    public ClientService(Server server)
    {
        this.server = server;
    }

    private Server server;

    public Server getServer() { return server; }

    private ArrayList<Transaction> transactions = new ArrayList<Transaction>();

    public void cancel() throws ServiceException{
        synchronized (transactions)
        {
            for (Transaction t: transactions) {
                t.rollback();
            }
        }
    }

    @Override
    public void start() throws ServiceException {

    }

    @Override
    public void stop() throws ServiceException {

    }

    @Override
    public IServiceTransaction startTransaction() throws ServiceException {
        synchronized (transactions) {
            Transaction t = new Transaction(this);
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

    private IDataChangedListener dataChangedListener;
    @Override
    public void setChangeListener(IDataChangedListener listener) {
        this.dataChangedListener = listener;
    }

    public void dataChanged() {
        if (dataChangedListener != null) {
            dataChangedListener.dataChanged();
        }
    }

    @Override
    public long getNewId() throws ServiceException {
        return server.getNewId();
    }
}
