package at.sbc.firework.service.alt;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Bruchts damit mr Listener uf da jeweils andra Sita ufruafa können.
 */
public interface IRemoteEventListener extends Remote {
    void invoke() throws RemoteException;
}
