package at.sbc.firework.service;

import at.sbc.firework.entities.Rocket;
import at.sbc.firework.service.alt.ICustomerTransactionRmi;
import at.sbc.firework.service.alt.IFactoryTransactionRmi;

import java.rmi.RemoteException;

/**
 * Created by daniel on 06.01.2015.
 */
public class CustomerTransactionAlt implements ICustomerTransaction {

    public CustomerTransactionAlt(ICustomerTransactionRmi remoteTransaction) {
        this.remoteTransaction = remoteTransaction;
    }

    private ICustomerTransactionRmi remoteTransaction;

    @Override
    public void addRocket(Rocket rocket) throws ServiceException {
        try {
            remoteTransaction.addRocket(rocket);
        } catch (RemoteException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public void commit() throws ServiceException {
        try {
            remoteTransaction.commit();
        } catch (RemoteException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public void rollback() throws ServiceException {
        try {
            remoteTransaction.rollback();
        } catch (RemoteException e) {
            throw new ServiceException(e);
        }
    }
}
