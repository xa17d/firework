package at.sbc.firework.service.alt.transactions;

import at.sbc.firework.service.ContainerOperation;
import at.sbc.firework.service.ITransaction;
import at.sbc.firework.service.NotAvailableException;
import at.sbc.firework.service.alt.FactoryTransaction;
import at.sbc.firework.service.alt.containers.Container;
import at.sbc.firework.service.alt.containers.ItemSelector;

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
    public void instant() throws NotAvailableException {

        Object item = null;
        Container container = getContainer();

        if (transaction.isActive()) {
            item = container.take(selector);
            if (item == null) {
                throw new NotAvailableException(container.getId(), null);
            }
        }

        setItem(item);
    }

    @Override
    public void commit() {
        getContainer().change(ContainerOperation.Take);
    }

    @Override
    public void rollback() {
        Object item = getItem();
        if (item != null) {
            getContainer().addFirst(item);
        }
    }
}