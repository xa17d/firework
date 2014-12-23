package at.sbc.firework.service;

import at.sbc.firework.entities.Rocket;

/**
 * Created by daniel on 23.12.2014.
 */
public interface ICustomerTransaction {
    void addRocket(Rocket rocket) throws ServiceException;

    void commit() throws ServiceException;
    void rollback() throws ServiceException;
}
