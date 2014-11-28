package at.sbc.firework.service.alt.containers;

import at.sbc.firework.service.IDataChangedListener;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by daniel on 21.11.2014.
 */
public class Container {

    private ArrayList<Object> items = new ArrayList<Object>();

    public synchronized Object take(ItemSelector selector) {
        for (Object o : items) {
            if (!selector.checkItem(o)) { break; }
        }

        Object item = selector.getResult();
        if (item != null) {
            items.remove(item);
            changed();
        }

        return item;
    }

    public synchronized void add(Object item) {
        items.add(item);
        changed();
    }

    public synchronized void addFirst(Object item) {
        items.add(0, item);
        changed();
    }

    public synchronized <T> ArrayList<T> list() {
        ArrayList<T> copy = new ArrayList<T>(items.size());
        for (Object o : items) {
            copy.add((T)o);
        }
        return copy;
    }

    private void changed()
    {
        if (dataChangedListener != null) {
            dataChangedListener.dataChanged();
        }
    }

    public void waitForChange(int timeout) {

    }

    private IDataChangedListener dataChangedListener = null;
    public void setDataChangedListener(IDataChangedListener listener) {
        this.dataChangedListener = listener;
    }
}
