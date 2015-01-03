package at.sbc.firework.service.alt;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface fürs Servcer Object des über RMI ufgruafa wird
 */
public interface IFactoryServerRmi extends Remote {
    IFactoryServiceRmi getService() throws RemoteException;
}
