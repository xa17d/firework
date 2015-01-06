package at.sbc.firework.service.alt.transactions;

import at.sbc.firework.service.ITransaction;
import at.sbc.firework.service.ServiceException;

import java.util.ArrayList;

/**
 * Created by daniel on 06.01.2015.
 */
public class TransactionManager {

    private ArrayList<ITransaction> transactions = new ArrayList<ITransaction>();

    public void cancel() throws ServiceException {
        synchronized (transactions)
        {
            while (!transactions.isEmpty()) {
                ITransaction t = transactions.get(0);
                t.rollback();
                transactions.remove(t);
            }
        }
    }

    public void startTransaction(ITransaction t) {
        synchronized (transactions)
        {
            transactions.add(t);
        }
    }

    public void endTransaction(ITransaction t) {
        synchronized (transactions)
        {
            transactions.remove(t);
        }
    }
}
