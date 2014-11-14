package at.sbc.firework.service;

import at.sbc.firework.daos.Part;

/**
 * Created by daniel on 14.11.2014.
 */
public interface IService {
    void start() throws ServiceException;
    void stop() throws ServiceException;

    void addToStock(Part part) throws ServiceException;
}
