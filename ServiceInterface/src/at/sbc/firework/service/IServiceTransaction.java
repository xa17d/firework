package at.sbc.firework.service;

import at.sbc.firework.daos.Part;

import java.util.ArrayList;

/**
 * Created by daniel on 14.11.2014.
 */
public interface IServiceTransaction {

    void addToStock(Part part) throws ServiceException;

    void commit() throws ServiceException;
}
