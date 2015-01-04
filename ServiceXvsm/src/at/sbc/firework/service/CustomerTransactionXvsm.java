package at.sbc.firework.service;

import at.sbc.firework.entities.Rocket;
import org.mozartspaces.core.Capi;
import org.mozartspaces.core.MzsConstants;
import org.mozartspaces.core.MzsCoreException;
import org.mozartspaces.core.TransactionReference;

/**
 * Created by daniel on 04.01.2015.
 */
public class CustomerTransactionXvsm implements ICustomerTransaction {

    private XvsmUtils utils;
    private TransactionReference transaction;
    private CustomerService service;

    public CustomerTransactionXvsm(CustomerService service) throws MzsCoreException {
        this.service = service;
        this.utils = service.getXvsmUtils();
        this.transaction = utils.createTransaction();
    }


    @Override
    public void addRocket(Rocket rocket) throws ServiceException {

    }

    @Override
    public void commit() throws ServiceException {

    }

    @Override
    public void rollback() throws ServiceException {

    }
}
