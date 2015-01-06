package at.sbc.firework.service.alt.transactions;

import at.sbc.firework.service.ITransaction;
import at.sbc.firework.service.ServiceException;
import at.sbc.firework.service.alt.ServiceAltException;
import at.sbc.firework.service.alt.containers.Container;
import at.sbc.firework.service.alt.containers.ItemSelector;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

/**
 * Created by daniel on 06.01.2015.
 */
public class Transaction extends UnicastRemoteObject implements ITransaction {

    public Transaction(TransactionManager transactionManager) throws RemoteException {
        this.transactionManager = transactionManager;
        this.active = true;
        transactionManager.startTransaction(this);
    }

    private TransactionManager transactionManager;
    private boolean active;
    public boolean isActive() { return active; }

    private ArrayList<TransactionOperation> operations = new ArrayList<TransactionOperation>();

    private void addOperation(TransactionOperation operation) throws ServiceException{

        synchronized (operations) {
            // druf schaua, dass koa neue Operationa dazua kommt, wenn grad
            // a commit odr an rollback durchgführt wörn, odr scho durchgführt
            // wora sind. Damit nix vrgeassa wird.
            if (!isActive()) {
                throw new ServiceAltException("Transaction is not active anymore");
            }

            operations.add(operation);
            operation.instant();
        }
    }

    protected Object containerTake(Container container, ItemSelector selector) throws ServiceException {
        TransactionOperation operation =
                new TransactionOperationTake(
                        this,
                        container,
                        selector
                );
        addOperation(operation);
        return operation.getItem();
    }

    protected void containerAdd(Container container, Object item) throws ServiceException {
        addOperation(
                new TransactionOperationAdd(
                        this,
                        container,
                        item
                )
        );
    }

    @Override
    public void commit() throws ServiceException {
        active = false;

        synchronized (operations) {
            for (TransactionOperation o : operations) {
                o.commit();
            }
        }

        transactionManager.endTransaction(this);
    }

    @Override
    public void rollback() throws ServiceException {
        active = false;

        synchronized (operations) {
            for (TransactionOperation o : operations) {
                o.rollback();
            }
        }

        transactionManager.endTransaction(this);
    }
}
