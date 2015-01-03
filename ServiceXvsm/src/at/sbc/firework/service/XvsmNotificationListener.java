package at.sbc.firework.service;

import at.sbc.firework.utils.NotificationMode;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.MzsCore;
import org.mozartspaces.core.MzsCoreException;
import org.mozartspaces.notifications.Notification;
import org.mozartspaces.notifications.NotificationManager;
import org.mozartspaces.notifications.Operation;
import org.mozartspaces.notifications.NotificationListener;

import java.io.Serializable;
import java.util.List;

/**
 * Created by daniel on 03.01.2015.
 */
public class XvsmNotificationListener implements NotificationListener {

    public XvsmNotificationListener(MzsCore core, INotification notification, ContainerReference container, ContainerOperation operation, NotificationMode mode) throws InterruptedException, MzsCoreException {
        this.notificationManager = new NotificationManager(core);
        this.notification = notification;
        this.mode = mode;

        switch (operation) {
            case All:
                notificationManager.createNotification(container, this, Operation.WRITE, Operation.TAKE);
                break;
            case Take:
                notificationManager.createNotification(container, this, Operation.TAKE);
                break;
            case Add:
                notificationManager.createNotification(container, this, Operation.WRITE);
                break;
        }
    }

    private NotificationManager notificationManager;
    private INotification notification;
    private NotificationMode mode;

    @Override
    public void entryOperationFinished(Notification xvsmNotification, Operation operation, List<? extends Serializable> list) {
        this.notification.dataChanged();

        if (mode == NotificationMode.Once) {
            destroy();
        }
    }

    public void destroy() {
        notificationManager.shutdown();
    }
}
