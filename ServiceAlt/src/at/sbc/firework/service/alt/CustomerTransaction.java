package at.sbc.firework.service.alt;

import at.sbc.firework.entities.Rocket;
import at.sbc.firework.service.ICustomerTransaction;
import at.sbc.firework.service.ServiceException;
import at.sbc.firework.service.alt.transactions.Transaction;
import at.sbc.firework.service.alt.transactions.TransactionManager;

import java.rmi.RemoteException;

/**
 * Created by daniel on 06.01.2015.
 */
public class CustomerTransaction extends Transaction implements ICustomerTransaction, ICustomerTransactionRmi {
    public CustomerTransaction(CustomerServer customerServer, TransactionManager transactionManager) throws RemoteException {
        super(transactionManager);
        this.customerServer = customerServer;

        transactionManager.startTransaction(this);
    }

    private CustomerServer customerServer;

    private void Log(String msg) {
        customerServer.Log("T#"+hashCode()+": "+msg);
    }

    @Override
    public void addRocket(Rocket rocket) throws ServiceException {
        containerAdd(customerServer.getStockContainer(), rocket);
    }

    @Override
    public void commit() throws ServiceException {
        Log("commit");
        super.commit();
    }

    @Override
    public void rollback() throws ServiceException {
        Log("rollback");
        super.rollback();
    }
}
