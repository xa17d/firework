package at.sbc.firework.service;

import at.sbc.firework.entities.Rocket;
import at.sbc.firework.service.alt.*;

import java.rmi.Naming;
import java.util.ArrayList;

/**
 * Quasi an RMI-Proxy zöm CustomerServer Objekt
 */
public class CustomerService implements ICustomerService {

    private ICustomerServerRmi remoteService;

    @Override
    public void startCreate(long customerId) throws ServiceException {

        remoteService = new CustomerServer(customerId);
    }

    @Override
    public void startGet(String address) throws ServiceException {
        System.setProperty("java.security.policy","file:./firework.policy");

        try {
            if(System.getSecurityManager() == null)
                System.setSecurityManager(new SecurityManager());

            System.out.println("Lookup RMI...");
            remoteService = (ICustomerServerRmi) Naming.lookup("rmi://" + RmiRegistry.HOST + ":" + RmiRegistry.PORT + "/" + FactoryServer.SERVER_NAME);
            System.out.println("connected to server: " + "rmi://" + RmiRegistry.HOST + ":" + RmiRegistry.PORT + "/" + FactoryServer.SERVER_NAME);

        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public void stop() throws ServiceException {

    }

    @Override
    public String getAddress() throws ServiceException {
        return null;
    }

    @Override
    public ArrayList<Rocket> listRockets() throws ServiceException {
        return null;
    }

    @Override
    public ICustomerTransaction startTransaction() throws ServiceException {
        return null;
    }
}
