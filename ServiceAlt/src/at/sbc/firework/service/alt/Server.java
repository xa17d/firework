package at.sbc.firework.service.alt;

import at.sbc.firework.service.alt.containers.Container;
import at.sbc.firework.service.IDataChangedListener;
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
public class Server extends UnicastRemoteObject implements IDataChangedListener, IServerRmi {

    public static final String SERVER_NAME = "ServerObjectRmiName";

    /**
     * sörfr starta und denn in dr retschistri beindn
     * (falls d retschistri noch ned existiert, macha mr a neue)
     * @param args args[0]: port
     * @throws RemoteException
     */
    public static void main(String[] args) throws RemoteException {
        System.out.println("--- Alternative Server ---");

        System.setProperty("java.security.policy","file:./firework.policy");

        int port = (args.length > 0) ? Integer.parseInt(args[0]) : 9876;
        Server server = new Server();

        if(System.getSecurityManager() == null)
            System.setSecurityManager(new SecurityManager());

        Registry registry = LocateRegistry.getRegistry(port);
        boolean bound = false;
        for(int i = 0; !bound && i < 2; i++) {
            try {
                registry.rebind(SERVER_NAME, server);
                bound = true;
                System.out.println("Server bound to Registry on Port: " + port);

            } catch (RemoteException e) {
                registry = LocateRegistry.createRegistry(port);
                System.out.println("Registry started on Port: " + port);
            }
        }
        System.out.println("Server is ready");

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

    public Server() throws RemoteException {
        stockContainer.setDataChangedListener(this);
        qualityCheckQueueContainer.setDataChangedListener(this);
        packingQueueContainer.setDataChangedListener(this);
        garbageContainer.setDataChangedListener(this);
        distributionStockContainer.setDataChangedListener(this);
    }

    private void findDeadClients() {
        ArrayList<ClientService> deadClients = new ArrayList<ClientService>();

        synchronized (clients) {

            Date now = new Date();

            for (ClientService client : clients) {
                if (getTimeDifferenceInMsec(client.getLastPing(), now) > 5000) {
                    deadClients.add(client);
                }
            }
        }

        for (ClientService client : deadClients) {
            System.out.println("! dead client C#"+client.hashCode());
            disconnectClient(client);
        }
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
                try {
                    client.dataChanged();
                } catch (ServiceException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void addClient(ClientService client) {
        synchronized (clients) {
            clients.add(client);
        }
    }

    public void disconnectClient(IServiceRmi client) {
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
