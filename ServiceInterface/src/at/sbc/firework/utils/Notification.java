package at.sbc.firework.utils;

import at.sbc.firework.service.*;

/**
 * Created by daniel on 23.12.2014.
 */
public class Notification implements INotification {
    public Notification(IFactoryService factoryService, String containerId, ContainerOperation operation, NotificationMode mode) throws ServiceException {
        factoryService.registerNotification(this, containerId, operation, mode);
    }

    public Notification(IFactoryService factoryService, NotAvailableException notAvailableException) throws ServiceException {
        factoryService.registerNotification(this, notAvailableException.getContainerId(), ContainerOperation.Add, NotificationMode.Once);
    }

    public void waitForNotification(long timeout) {
        synchronized (lockObject) {
            if (count > 0) {
                count--;
                return;
            }
            else {
                try {
                    lockObject.wait(timeout);
                } catch (InterruptedException e) {

                }

                return;
            }
        }
    }

    private int count = 0;
    private Object lockObject = new Object();

    @Override
    public void dataChanged() {
        synchronized (lockObject) {
            count++;
            lockObject.notifyAll();
        }
    }
}
