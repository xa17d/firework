package at.sbc.firework.service;

/**
 * Created by daniel on 06.01.2015.
 */
public interface ITransaction {
    void commit() throws ServiceException;
    void rollback() throws ServiceException;
}
