package at.sbc.firework.service.alt;

import at.sbc.firework.service.alt.containers.Container;
import at.sbc.firework.service.IDataChangedListener;
import at.sbc.firework.service.ServiceException;

import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Created by daniel on 21.11.2014.
 */
public class Server implements IDataChangedListener, IServerRmi {

    public static void main(String[] args)
    {
        System.out.println("--- ServerAlt ---");

        Server server = new Server();
    }

    public Server() {
        stockContainer.setDataChangedListener(this);
        qualityCheckQueueContainer.setDataChangedListener(this);
        packingQueueContainer.setDataChangedListener(this);
        garbageContainer.setDataChangedListener(this);
        distributionStockContainer.setDataChangedListener(this);
    }


    @Override
    public IServiceRmi getService() throws RemoteException {
        ClientService clientService = new ClientService(this);
        addClient(clientService);
        return clientService;
    }

    @Override
    public void dataChanged() {
        synchronized (clients) {
            for (ClientService client : clients) {
                client.dataChanged();
            }
        }
    }

    private void addClient(ClientService client) {
        synchronized (clients) {
            clients.add(client);
        }
    }

    public void disconnectClient(ClientService client) {
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
