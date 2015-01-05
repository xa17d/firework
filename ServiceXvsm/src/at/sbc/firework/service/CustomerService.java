package at.sbc.firework.service;

import at.sbc.firework.entities.Rocket;
import org.mozartspaces.capi3.*;
import org.mozartspaces.core.*;

import java.net.URI;
import java.util.ArrayList;

/**
 * Created by daniel on 04.01.2015.
 */
public class CustomerService implements ICustomerService {

    private void init(String address, boolean isHost) throws XvsmException {
        this.spaceUri = URI.create(address);
        this.isHost = true;

        System.out.println("create MzsCore " + address);
        MzsCore core = DefaultMzsCore.newInstance(spaceUri.getPort());
        this.capi = new Capi(core);

        this.utils = new XvsmUtils(capi, spaceUri);

        // Container erstella
        try {
            stockContainer = utils.findOrCreateContainer(CONTAINER_NAME_STOCK, false, false, true, false);
        } catch (MzsCoreException e) {
            throw new XvsmException(e);
        }

    }

    @Override
    public void startCreate(long customerId) throws ServiceException {
        init("xvsm://localhost:"+customerId, true);
    }

    @Override
    public void startGet(String address) throws ServiceException {
        init(address, false);
    }

    private URI spaceUri;
    private Capi capi;
    private boolean isHost;
    private XvsmUtils utils;

    private static final String CONTAINER_NAME_STOCK = "stock";
    private ContainerReference stockContainer;

    public ContainerReference getStockContainer() { return stockContainer; }

    public XvsmUtils getXvsmUtils() { return utils; }

    @Override
    public void stop() throws ServiceException {
        if (isHost) {
            try {
                capi.shutdown(spaceUri);
            } catch (MzsCoreException e) {
                throw new ServiceException(e);
            }
        }
    }

    @Override
    public String getAddress() throws ServiceException {
        return spaceUri.toString();
    }

    @Override
    public ArrayList<Rocket> listRockets() throws ServiceException {
        return utils.listQueryContainer(getStockContainer());
    }

    @Override
    public ICustomerTransaction startTransaction() throws ServiceException {
        try {
            return new CustomerTransactionXvsm(this);
        } catch (MzsCoreException e) {
            throw new XvsmException(e);
        }
    }
}
