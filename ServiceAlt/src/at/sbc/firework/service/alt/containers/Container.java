package at.sbc.firework.service.alt.containers;

import at.sbc.firework.service.AltNotification;
import at.sbc.firework.service.ContainerOperation;

import java.util.ArrayList;

/**
 * Created by daniel on 21.11.2014.
 */


public class Container {

    public Container(String id) {
        this.id = id;
    }

    private String id;
    public String getId() { return id; }

    private ArrayList<Object> items = new ArrayList<Object>();
    private ArrayList<AltNotification> notifications = new ArrayList<AltNotification>();

    public Object take(ItemSelector selector) {
        boolean listChanged = false;
        Object item;

        synchronized (listLock) {
            for (Object o : items) {
                if (!selector.checkItem(o)) {
                    break;
                }
            }

            item = selector.getResult();
            if (item != null) {
                items.remove(item);
                listChanged = true;
            }
        }

        if (listChanged) {
            internChange(ContainerOperation.Take);
        }

        return item;
    }

    public void add(Object item) {
        synchronized (listLock) {
            items.add(item);
        }

        internChange(ContainerOperation.Add);
    }

    public void addFirst(Object item) {
        synchronized (listLock) {
            items.add(0, item);
        }
        internChange(ContainerOperation.Add);
    }

    public <T> ArrayList<T> list() {
        ArrayList<T> copy;
        synchronized (listLock) {
            copy = new ArrayList<T>(items.size());
            for (Object o : items) {
                copy.add((T) o);
            }
        }
        return copy;
    }

    private Object listLock = new Object();
    private Object notificationsLock = new Object();

    public void registerNotification(AltNotification notification) {
        synchronized (notificationsLock) {
            notifications.add(notification);
        }
    }

    public void internChange(ContainerOperation performedOperation) { }

    public void change(ContainerOperation performedOperation) {
        ArrayList<AltNotification> ns;

        // copy notifications
        synchronized (notificationsLock) {
            ns = new ArrayList<AltNotification>(notifications);
        }

        // invoke notifications
        for (AltNotification n : ns) {
            n.dataChanged(performedOperation);
        }

        // delete onetime notifications
        synchronized (notificationsLock) {
            for (AltNotification n : ns) {
                if (n.isDone()) {
                    notifications.remove(n);
                }
            }
        }
    }
}
