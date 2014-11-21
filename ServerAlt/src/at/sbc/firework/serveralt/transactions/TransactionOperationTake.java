package at.sbc.firework.serveralt.transactions;

import at.sbc.firework.serveralt.containers.Container;
import at.sbc.firework.serveralt.containers.ItemSelector;

/**
 * Created by daniel on 21.11.2014.
 */
public class TransactionOperationTake extends TransactionOperation {

    public TransactionOperationTake(Transaction transaction, Container container, ItemSelector selector) {
        super(container);
        this.transaction = transaction;
        this.selector = selector;
    }

    private Transaction transaction;
    private ItemSelector selector;

    @Override
    public void instant() {

        Object item = null;
        Container container = getContainer();

        while (transaction.isActive())
        {
            item = container.take(selector);
            if (item == null) {
                container.waitForChange(1000);
            }
            else
            { break; }
        }

        setItem(item);
    }

    @Override
    public void commit() {

    }

    @Override
    public void rollback() {
        Object item = getItem();
        if (item != null) {
            getContainer().addFirst(item);
        }
    }
}
