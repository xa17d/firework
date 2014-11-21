package at.sbc.firework.serveralt;

import at.sbc.firework.serveralt.containers.Container;
import at.sbc.firework.service.IDataChangedListener;
import at.sbc.firework.service.ServiceException;

import java.util.ArrayList;

/**
 * Created by daniel on 21.11.2014.
 */
public class Server implements IDataChangedListener {

    public Server() {
        stockContainer.setDataChangedListener(this);
        qualityCheckQueueContainer.setDataChangedListener(this);
        packingQueueContainer.setDataChangedListener(this);
        garbageContainer.setDataChangedListener(this);
        distributionStockContainer.setDataChangedListener(this);
    }

    @Override
    public void dataChanged() {
        synchronized (clients) {
            for (ClientService client : clients) {
                client.dataChanged();
            }
        }
    }

    public void addClient(ClientService client) {
        synchronized (clients) {
            clients.add(client);
        }
    }

    public void removeClient(ClientService client) {
        synchronized (clients) {
            try {
                client.cancel();
            } catch (ServiceException e) {
                e.printStackTrace();
            }
            clients.remove(client);
        }
    }

    private Container stockContainer = new Container();
    private Container qualityCheckQueueContainer = new Container();
    private Container packingQueueContainer = new Container();
    private Container garbageContainer = new Container();
    private Container distributionStockContainer = new Container();

    public Container getStockContainer() { return stockContainer; }
    public Container getQualityCheckQueueContainer() { return qualityCheckQueueContainer; }
    public Container getPackingQueueContainer() { return packingQueueContainer; }
    public Container getGarbageContainer() { return garbageContainer; }
    public Container getDistributionStockContainer() { return distributionStockContainer; }

    private long idCounter = 0;
    public synchronized long getNewId() {
        return idCounter++;
    }

    private ArrayList<ClientService> clients = new ArrayList<ClientService>();

}
