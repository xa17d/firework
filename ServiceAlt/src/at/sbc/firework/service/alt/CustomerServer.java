package at.sbc.firework.service.alt;

import at.sbc.firework.entities.Rocket;
import at.sbc.firework.service.Console;
import at.sbc.firework.service.ContainerOperation;
import at.sbc.firework.service.ICustomerTransaction;
import at.sbc.firework.service.ServiceException;
import at.sbc.firework.service.alt.containers.Container;
import at.sbc.firework.service.alt.transactions.TransactionManager;
import at.sbc.firework.utils.NotificationMode;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

/**
 * Created by daniel on 05.01.2015.
 */
public class CustomerServer extends UnicastRemoteObject implements ICustomerServerRmi {

    private static final String SERVER_NAME_PREFIX = "FireworkCustomer";
    private RmiRegistry registry;
    private String address;
    private TransactionManager transactionManager = new TransactionManager();

    public CustomerServer(long customerId) throws ServiceException, RemoteException {
        address = SERVER_NAME_PREFIX + customerId;

        registry = new RmiRegistry();
        registry.bind(address, this);
    }

    public void Log(String msg) {
        Console.println(address + ": " + msg);
    }

    private Container stockContainer = new Container("stock");
    public Container getStockContainer() { return stockContainer; }

    public Container[] getContainers() {
        return new Container[] {
                stockContainer
        };
    }

    @Override
    public void stop() throws ServiceException {
        registry.unbind(address);
    }

    @Override
    public String getAddress() throws ServiceException {
        return address;
    }

    @Override
    public ArrayList<Rocket> listRockets() throws ServiceException {
        return getStockContainer().list();
    }

    @Override
    public ICustomerTransactionRmi startTransaction() throws ServiceException, RemoteException {
        return new CustomerTransaction(this, transactionManager);
    }

    @Override
    public void registerNotification(IRemoteEventListener notification, String containerId, ContainerOperation operation, NotificationMode mode) throws ServiceException, RemoteException {
        boolean all = ("*".equals(containerId));

        for (Container container : getContainers()) {
            if (all || (container.getId().equals(containerId))) {
                AltNotification listener = new AltNotification(notification, operation, mode);
                container.registerNotification(listener);
                Log("registerNotification "+operation+"@"+container.getId()+"#"+mode);
            }
        }
    }
}
