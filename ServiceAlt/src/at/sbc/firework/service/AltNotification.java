package at.sbc.firework.service;

import at.sbc.firework.service.alt.IRemoteEventListener;
import at.sbc.firework.service.alt.containers.Container;
import at.sbc.firework.utils.Notification;
import at.sbc.firework.utils.NotificationMode;

import java.rmi.RemoteException;

/**
 * Created by daniel on 03.01.2015.
 */
public class AltNotification {
    public AltNotification(IRemoteEventListener notification, ContainerOperation operation, NotificationMode mode) {
        this.notification = notification;
        this.operation = operation;
        this.mode = mode;
    }

    private NotificationMode mode;
    private ContainerOperation operation;
    private boolean done = false;
    private IRemoteEventListener notification;

    public void dataChanged(ContainerOperation performedOperation) {
        if (operation == ContainerOperation.All || operation == performedOperation) {

            try {
                notification.invoke();
            } catch (RemoteException e) {
                System.out.println("Could not invoke remote Notification");
                e.printStackTrace();
            }

            if (mode == NotificationMode.Once) {
                done = true;
            }
        }
    }

    public boolean isDone() { return done; }
}
