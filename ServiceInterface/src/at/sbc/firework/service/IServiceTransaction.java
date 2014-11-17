package at.sbc.firework.service;

import at.sbc.firework.daos.Part;
import at.sbc.firework.daos.Rocket;
import at.sbc.firework.daos.RocketPackage5;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * In ana Transaktion können Speicher verändernde Operationa durchgführt wöra.
 * Transaktiona wörn mit Hilfe vom IService erstellt. Am Schluss muss immr
 * commit() ufgruafa wöra
 */
public interface IServiceTransaction {

    void addToStock(Part part) throws ServiceException;
    ArrayList<Part> takeFromStock(Class<?> type, int count) throws ServiceException;

    void addToOpenStock(Part part) throws ServiceException;
    ArrayList<Part> takeFromOpenStock(Class<?> type, int count) throws ServiceException;

    void addToQualityCheckQueue(Rocket rocket) throws ServiceException;
    Rocket takeFromQualityCheckQueue() throws ServiceException;

    void addToPackingQueue(Rocket rocket) throws ServiceException;
    Rocket takeFromPackingQueue() throws ServiceException;

    void addToGarbage(Rocket rocket) throws ServiceException;

    void addToDistributionStock(RocketPackage5 rocket) throws ServiceException;

    void commit() throws ServiceException;
    void rollback() throws ServiceException;
}
