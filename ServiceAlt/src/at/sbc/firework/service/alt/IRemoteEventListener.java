package at.sbc.firework.service.alt;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by daniel on 28.11.2014.
 */
public interface IRemoteEventListener extends Remote {
    void invoke() throws RemoteException;
}
