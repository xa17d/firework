package at.sbc.firework.service;

import at.sbc.firework.daos.Part;
import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.capi3.Selector;
import org.mozartspaces.core.*;

import java.util.ArrayList;

/**
 * Created by daniel on 14.11.2014.
 */
public class ServiceTransactionXvsm implements IServiceTransaction {

    private Capi capi;
    private TransactionReference transaction;
    private ServiceXvsm service;

    public ServiceTransactionXvsm(ServiceXvsm service) throws MzsCoreException {
        this.service = service;
        this.capi = service.getCapi();
        this.transaction = capi.createTransaction(ServiceXvsm.DEFAULT_TIMEOUT, service.getSpaceUri());
    }


    @Override
    public void addToStock(Part part) throws ServiceException
    {
        Entry entry = new Entry(part);

        try {
            capi.write(entry, service.getStockContainer(), ServiceXvsm.DEFAULT_TIMEOUT, transaction);

        } catch (MzsCoreException e) {
            throw new XvsmException(e);
        }
    }

    @Override
    public void commit() throws ServiceException {
        try {
            capi.commitTransaction(transaction);
        } catch (MzsCoreException e) {
            throw new XvsmException(e);
        }
    }
}
