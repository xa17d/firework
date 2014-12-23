package at.sbc.firework.service.alt.transactions;

import at.sbc.firework.service.alt.containers.Container;

/**
 * Created by daniel on 21.11.2014.
 */
public class TransactionOperationAdd extends TransactionOperation {

    public TransactionOperationAdd(FactoryTransaction transaction, Container container, Object item) {
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
