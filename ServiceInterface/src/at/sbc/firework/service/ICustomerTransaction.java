package at.sbc.firework.service;

import at.sbc.firework.entities.Rocket;

/**
 * Created by daniel on 23.12.2014.
 */
public interface ICustomerTransaction extends ITransaction {
    void addRocket(Rocket rocket) throws ServiceException;
}
