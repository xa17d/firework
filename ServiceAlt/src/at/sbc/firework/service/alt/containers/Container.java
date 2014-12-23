package at.sbc.firework.service.alt.containers;

import at.sbc.firework.service.INotification;

import java.util.ArrayList;

/**
 * Created by daniel on 21.11.2014.
 */


public class Container {

    private ArrayList<Object> items = new ArrayList<Object>();

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
            changed();
        }

        return item;
    }

    public void add(Object item) {
        synchronized (listLock) {
            items.add(item);
        }

        changed();
    }

    public void addFirst(Object item) {
        synchronized (listLock) {
            items.add(0, item);
        }
        changed();
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
    private Object changeLock = new Object();

    private void changed() {
        if (dataChangedListener != null) {
            dataChangedListener.dataChanged();
        }
        synchronized (changeLock) {
            changeLock.notifyAll();
        }

        System.out.println("done");
    }

    public void waitForChange(int timeout) {
        synchronized (changeLock)
        {
            try {
                changeLock.wait(timeout);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private INotification dataChangedListener = null;
    public void setDataChangedListener(INotification listener) {
        this.dataChangedListener = listener;
    }
}
