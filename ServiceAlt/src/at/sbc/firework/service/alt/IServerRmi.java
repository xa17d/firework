package at.sbc.firework.service.alt;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by daniel on 21.11.2014.
 */
public interface IServerRmi extends Remote, Serializable {
    IServiceRmi getService() throws RemoteException;
}
