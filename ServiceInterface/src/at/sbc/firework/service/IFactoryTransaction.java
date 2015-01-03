package at.sbc.firework.service;

import at.sbc.firework.entities.*;

import java.util.ArrayList;

/**
 * In ana Transaktion können Speicher verändernde Operationa durchgführt wöra.
 * Transaktiona wörn mit Hilfe vom IService erstellt. Am Schluss muss immr
 * commit() ufgruafa wöra
 */
public interface IFactoryTransaction {

    void addToStock(Part part) throws ServiceException;
    ArrayList<Part> takeFromStock(Class<?> type, int count) throws ServiceException;
    PropellingChargePackage takePropellingChargePackageFromStock() throws ServiceException;

    void addToQualityCheckQueue(Rocket rocket) throws ServiceException;
    Rocket takeFromQualityCheckQueue() throws ServiceException;

    void addToPackingQueue(Rocket rocket) throws ServiceException;
    Rocket takeFromPackingQueue() throws ServiceException;

    void addToGarbage(Rocket rocket) throws ServiceException;

    void addToDistributionStock(RocketPackage5 rocketPackage) throws ServiceException;

    void addOrder(Order order) throws ServiceException;
    void addOrderPosition(OrderPosition orderPosition) throws ServiceException;
    OrderPosition takeOrderPosition() throws ServiceException;
    EffectCharge takeEffectChargeFromStock(Color color) throws ServiceException;

    void addRocketToOrder(Rocket rocket) throws ServiceException;

    void commit() throws ServiceException;
    void rollback() throws ServiceException;
}
