package at.sbc.firework.service.alt;

import at.sbc.firework.service.Console;
import at.sbc.firework.service.alt.containers.Container;
import at.sbc.firework.service.INotification;
import at.sbc.firework.service.ServiceException;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by daniel on 21.11.2014.
 */
public class FactoryServer extends UnicastRemoteObject implements IFactoryServerRmi {

    public static final String SERVER_NAME = "FireworkFactory";

    /**
     * sörfr starta und denn in dr retschistri beindn
     * (falls d retschistri noch ned existiert, macha mr a neue)
     * @param args args[0]: port (obsolete)
     * @throws RemoteException
     */
    public static void main(String[] args) throws RemoteException, ServiceException {
        Console.println("--- Alternative Server ---");

        RmiRegistry registry = new RmiRegistry();

        FactoryServer server = new FactoryServer();

        registry.bind(SERVER_NAME, server);

        Console.println("Server is ready");

        while (true) {

            // Disconnect dead clients
            // Wenn sich an client sit 5sec numm mittels ping gmolda hat, würd er
            // automatisch disconnected.

            server.findDeadClients();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static long getTimeDifferenceInMsec(Date a, Date b) {
        return b.getTime() - a.getTime();
    }

    public FactoryServer() throws RemoteException {

    }

    private void findDeadClients() {
        ArrayList<FactoryServiceClient> deadClients = new ArrayList<FactoryServiceClient>();

        synchronized (clients) {

            Date now = new Date();

            for (FactoryServiceClient client : clients) {
                if (getTimeDifferenceInMsec(client.getLastPing(), now) > 5000) {
                    deadClients.add(client);
                }
            }
        }

        for (FactoryServiceClient client : deadClients) {
            Console.println("! dead client C#"+client.hashCode());
            disconnectClient(client);
        }
    }

    @Override
    public IFactoryServiceRmi getService() throws RemoteException {
        FactoryServiceClient clientService = new FactoryServiceClient(this);
        addClient(clientService);
        return clientService;
    }

    private void addClient(FactoryServiceClient client) {
        synchronized (clients) {
            clients.add(client);
        }
    }

    public void disconnectClient(IFactoryServiceRmi client) {
        synchronized (clients) {

            if (clients.remove(client)) {
                try {
                    client.cancel();
                } catch (ServiceException e) {
                    e.printStackTrace();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Container stockContainer = new Container("stock");
    private Container qualityCheckQueueContainer = new Container("qualityCheckQueue");
    private Container packingQueueContainer = new Container("packingQueue");
    private Container garbageContainer = new Container("garbageQueue");
    private Container distributionStockContainer = new Container("distributionStock");
    private Container ordersContainer = new Container("ordersContainer");
    private Container orderPositionsContainer = new Container("orderPositionsContainer");
    private Container orderStockContainer = new Container("orderStockContainer");

    public Container getStockContainer() { return stockContainer; }
    public Container getQualityCheckQueueContainer() { return qualityCheckQueueContainer; }
    public Container getPackingQueueContainer() { return packingQueueContainer; }
    public Container getGarbageContainer() { return garbageContainer; }
    public Container getDistributionStockContainer() { return distributionStockContainer; }
    public Container getOrdersContainer() { return ordersContainer; }
    public Container getOrderPositionsContainer() { return orderPositionsContainer; }
    public Container getOrderStockContainer() { return orderStockContainer; }

    public Container[] getContainers() {
        return new Container[] {
            stockContainer,
            qualityCheckQueueContainer,
            packingQueueContainer,
            garbageContainer,
            distributionStockContainer,
            ordersContainer,
            orderPositionsContainer,
            orderStockContainer
        };
    }

    private long idCounter = 0;
    public synchronized long getNewId() {
        return idCounter++;
    }

    private ArrayList<FactoryServiceClient> clients = new ArrayList<FactoryServiceClient>();

}
