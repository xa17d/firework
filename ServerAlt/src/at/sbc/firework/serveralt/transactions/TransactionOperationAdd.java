package at.sbc.firework.serveralt.transactions;

import at.sbc.firework.serveralt.containers.Container;

/**
 * Created by daniel on 21.11.2014.
 */
public class TransactionOperationAdd extends TransactionOperation {

    public TransactionOperationAdd(Transaction transaction, Container container, Object item) {
        super(container);
        setItem(item);
    }

    @Override
    public void instant() {

    }

    @Override
    public void commit() {
        getContainer().add(getItem());
    }

    @Override
    public void rollback() {

    }
}
