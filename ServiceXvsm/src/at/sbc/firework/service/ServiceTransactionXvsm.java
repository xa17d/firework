package at.sbc.firework.service;

import at.sbc.firework.daos.Part;
import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.capi3.Selector;
import org.mozartspaces.capi3.TypeCoordinator;
import org.mozartspaces.core.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        this.transaction = capi.createTransaction(MzsConstants.RequestTimeout.INFINITE, service.getSpaceUri());
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
    public ArrayList<Part> takeFromStock(Class<?> type, int count) throws ServiceException {

        List<Selector> selectors = new ArrayList<Selector>();
        selectors.add(TypeCoordinator.newSelector(type, count));
        ArrayList<Part> entries = null;
        try {
            entries = capi.take(service.getStockContainer(), selectors, MzsConstants.RequestTimeout.ZERO, transaction);
        } catch (MzsCoreException e) {
            throw new XvsmException(e);
        }

        return entries;
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
