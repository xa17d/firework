package at.sbc.firework.service.alt.transactions;

import at.sbc.firework.service.alt.containers.Container;

/**
 * Created by daniel on 21.11.2014.
 */
public abstract class TransactionOperation {

    public TransactionOperation(Container container) {
        this.container = container;
    }

    private Object item;
    private Container container;

    protected void setItem(Object item) { this.item = item; }
    protected Container getContainer() { return container; }

    public Object getItem() { return item; }

    public abstract void instant();

    public abstract void commit();

    public abstract void rollback();
}
