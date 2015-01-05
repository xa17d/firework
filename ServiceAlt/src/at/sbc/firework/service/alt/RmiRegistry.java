package at.sbc.firework.service.alt;

import at.sbc.firework.service.ServiceException;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Created by daniel on 05.01.2015.
 */
public class RmiRegistry {
    public RmiRegistry() throws ServiceException {
        System.setProperty("java.security.policy","file:./firework.policy");

        if(System.getSecurityManager() == null)
            System.setSecurityManager(new SecurityManager());

        this.port = PORT;

        try {
            this.registry = LocateRegistry.getRegistry(port);
        } catch (RemoteException e) {
            throw new ServiceException(e);
        }
    }

    public static final int PORT = 9876;
    public static final String HOST = "localhost";

    private int port;
    private Registry registry;

    public void bind(String name, Remote obj) throws ServiceException {

        Exception exception = null;

        boolean bound = false;
        for (int i = 0; !bound && i < 2; i++) {
            try {
                registry.rebind(name, obj);
                bound = true;
                System.out.println("Server bound to Registry on Port: " + port+" name: "+name);

            } catch (RemoteException e) {
                exception = e;
                try {
                    registry = LocateRegistry.createRegistry(port);
                    System.out.println("Registry started on Port: " + port);
                } catch (RemoteException e1) {
                    throw new ServiceException(e1);
                }
            }
        }

        if (!bound) { throw new ServiceException(exception); }
    }

    public void unbind(String name) throws ServiceException {
        try {
            registry.unbind(name);
        } catch (RemoteException e) {
            throw new ServiceException(e);
        } catch (NotBoundException e) {
            throw new ServiceException(e);
        }
    }
}
