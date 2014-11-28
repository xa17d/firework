package at.sbc.firework.service.alt;

import at.sbc.firework.service.IDataChangedListener;

import java.rmi.server.RemoteObject;

/**
 * Created by daniel on 28.11.2014.
 */
public class RemoteEventListener implements IRemoteEventListener {

    public RemoteEventListener(IDataChangedListener listener) {
        this.listener = listener;
    }

    private IDataChangedListener listener;

    @Override
    public void invoke() {
        listener.dataChanged();
    }
}
