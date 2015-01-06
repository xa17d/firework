package at.sbc.firework.service;

import at.sbc.firework.entities.Part;
import org.mozartspaces.capi3.*;
import org.mozartspaces.core.*;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;

/**
 * Created by daniel on 04.01.2015.
 */
public class XvsmUtils {
    public XvsmUtils(Capi capi, URI spaceUri) {
        this.capi = capi;
        this.spaceUri = spaceUri;
    }

    private Capi capi;
    private URI spaceUri;

    public Capi getCapi() { return capi; }

    public static final long DEFAULT_TIMEOUT = 5000;

    public <T extends Serializable> ArrayList<T> listFifoContainer(ContainerReference container) throws ServiceException {
        ArrayList<T> result;

        try {
            ArrayList<Selector> selectors = new ArrayList<Selector>();
            selectors.add(FifoCoordinator.newSelector(MzsConstants.Selecting.COUNT_ALL));
            result = capi.read(container, selectors, XvsmUtils.DEFAULT_TIMEOUT, null);
        } catch (MzsCoreException e) {
            throw new XvsmException(e);
        }

        return result;
    }

    public <T extends Serializable> ArrayList<T> listQueryContainer(ContainerReference container) throws ServiceException {
        ArrayList<T> result;

        try {
            ArrayList<Selector> selectors = new ArrayList<Selector>();

            Query query = new Query().sortup(Property.forName("id"));
            selectors.add(QueryCoordinator.newSelector(query, MzsConstants.Selecting.COUNT_ALL));
            result = capi.read(container, selectors, XvsmUtils.DEFAULT_TIMEOUT, null);
        } catch (MzsCoreException e) {
            throw new XvsmException(e);
        }

        return result;
    }

    public ContainerReference findOrCreateContainer(String name, boolean fifo, boolean type, boolean query, boolean key) throws MzsCoreException {
        ContainerReference container;

        try {
            container = capi.lookupContainer(name, spaceUri, MzsConstants.RequestTimeout.TRY_ONCE, null);
        } catch (MzsCoreException e) {
            System.out.println(name + " not found and will be created.");
            ArrayList<Coordinator> obligatoryCoords = new ArrayList<Coordinator>();
            if (fifo) { obligatoryCoords.add(new FifoCoordinator()); }
            if (type) { obligatoryCoords.add(new TypeCoordinator()); }
            if (query) { obligatoryCoords.add(new QueryCoordinator()); }
            if (key) { obligatoryCoords.add(new KeyCoordinator()); }

            ArrayList<Coordinator> optionalCoords = new ArrayList<Coordinator>();

            container = capi.createContainer(name, spaceUri, MzsConstants.Container.UNBOUNDED, obligatoryCoords, optionalCoords, null);
        }

        return container;
    }

    public TransactionReference createTransaction() throws MzsCoreException {
        return capi.createTransaction(XvsmUtils.DEFAULT_TIMEOUT, spaceUri);
    }

    public ArrayList<Part> takeFromStock(TransactionReference transaction, ContainerReference container, Class<?> type, int count) throws ServiceException
    {
        ArrayList<Selector> selectors = new ArrayList<Selector>();
        selectors.add(TypeCoordinator.newSelector(type, count));

        ArrayList<Part> entries = null;
        try {
            entries = capi.take(container, selectors, MzsConstants.RequestTimeout.TRY_ONCE, transaction);
        } catch (CountNotMetException e) {
            throw new NotAvailableException(container.getId(), e);
        }
        catch (MzsCoreException e) {
            throw new XvsmException(e);
        }

        return entries;
    }

    public ArrayList<Serializable> takeFromQueue(TransactionReference transaction, ContainerReference container, int count) throws ServiceException
    {
        ArrayList<Selector> selectors = new ArrayList<Selector>();
        selectors.add(FifoCoordinator.newSelector(count));
        ArrayList<Serializable> entries = null;
        try {
            entries = capi.take(container, selectors, MzsConstants.RequestTimeout.TRY_ONCE, transaction);
        } catch (CountNotMetException e) {
            throw new NotAvailableException(container.getId(), e);
        } catch (MzsCoreException e) {
            throw new XvsmException(e);
        }

        return entries;
    }

    public  <T> T take1FromQueue(TransactionReference transaction, ContainerReference container) throws ServiceException
    {
        ArrayList<Serializable> items = takeFromQueue(transaction, container, 1);
        if (items.isEmpty()) {
            return null;
        }
        else {
            return (T)items.get(0);
        }
    }

    public void addToContainer(TransactionReference transaction, ContainerReference container, Serializable item) throws ServiceException
    {
        Entry entry = new Entry(item);

        try {
            capi.write(entry, container, XvsmUtils.DEFAULT_TIMEOUT, transaction);

        } catch (MzsCoreException e) {
            throw new XvsmException(e);
        }
    }

    public Serializable takeById(TransactionReference transaction, ContainerReference container, long id) throws ServiceException {
        ArrayList<Serializable> items;

        try {
            ArrayList<Selector> selectors = new ArrayList<Selector>();

            Query query = new Query().filter(Property.forName("id").equalTo(id)).cnt(1);
            selectors.add(QueryCoordinator.newSelector(query, 1));
            items = capi.read(container, selectors, XvsmUtils.DEFAULT_TIMEOUT, transaction);
        } catch (MzsCoreException e) {
            throw new XvsmException(e);
        }

        if (items.isEmpty()) {
            return null;
        }
        else {
            return items.get(0);
        }
    }
}
