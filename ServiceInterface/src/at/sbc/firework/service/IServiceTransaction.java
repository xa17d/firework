package at.sbc.firework.service;

import at.sbc.firework.entities.Part;
import at.sbc.firework.entities.PropellingChargePackage;
import at.sbc.firework.entities.Rocket;
import at.sbc.firework.entities.RocketPackage5;

import java.util.ArrayList;

/**
 * In ana Transaktion können Speicher verändernde Operationa durchgführt wöra.
 * Transaktiona wörn mit Hilfe vom IService erstellt. Am Schluss muss immr
 * commit() ufgruafa wöra
 */
public interface IServiceTransaction {

    void addToStock(Part part) throws ServiceException;
    ArrayList<Part> takeFromStock(Class<?> type, int count) throws ServiceException;
    PropellingChargePackage takePropellingChargePackageFromStock() throws ServiceException;

    void addToQualityCheckQueue(Rocket rocket) throws ServiceException;
    Rocket takeFromQualityCheckQueue() throws ServiceException;

    void addToPackingQueue(Rocket rocket) throws ServiceException;
    Rocket takeFromPackingQueue() throws ServiceException;

    void addToGarbage(Rocket rocket) throws ServiceException;

    void addToDistributionStock(RocketPackage5 rocket) throws ServiceException;

    void commit() throws ServiceException;
    void rollback() throws ServiceException;
}
