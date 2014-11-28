package at.sbc.firework.service.alt;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by daniel on 21.11.2014.
 */
public interface IServerRmi extends Remote {
    IServiceRmi getService() throws RemoteException;
}
