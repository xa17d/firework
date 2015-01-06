package at.sbc.firework.service.alt;

import at.sbc.firework.entities.Rocket;
import at.sbc.firework.service.ICustomerTransaction;
import at.sbc.firework.service.ServiceException;
import at.sbc.firework.service.alt.containers.Container;
import at.sbc.firework.service.alt.transactions.TransactionManager;

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
        System.out.println(address+": "+msg);
    }

    private Container stockContainer = new Container("stock");
    public Container getStockContainer() { return stockContainer; }

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
}
