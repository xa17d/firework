package at.sbc.firework.service;

import at.sbc.firework.entities.Rocket;

import java.util.ArrayList;

/**
 * Created by daniel on 23.12.2014.
 */
public interface ICustomerService {
    ArrayList<Rocket> listRockets() throws ServiceException;

    ICustomerTransaction startTransaction() throws ServiceException;
}
