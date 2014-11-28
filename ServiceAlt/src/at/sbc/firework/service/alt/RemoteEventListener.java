package at.sbc.firework.service.alt;

import at.sbc.firework.service.IDataChangedListener;

import java.rmi.RemoteException;
import java.rmi.server.RemoteObject;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by daniel on 28.11.2014.
 */
public class RemoteEventListener extends UnicastRemoteObject implements IRemoteEventListener {

    public RemoteEventListener(IDataChangedListener listener) throws RemoteException {
        this.listener = listener;
    }

    private IDataChangedListener listener;

    @Override
    public void invoke() {
        listener.dataChanged();
    }
}
