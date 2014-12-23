package at.sbc.firework.service.alt;

import at.sbc.firework.service.INotification;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Implementation vo nam IRemoteEventListener.
 */
public class RemoteEventListener extends UnicastRemoteObject implements IRemoteEventListener {

    public RemoteEventListener(INotification listener) throws RemoteException {
        this.listener = listener;
    }

    private INotification listener;

    @Override
    public void invoke() {
        listener.dataChanged();
    }
}
