package at.sbc.firework.service;

import at.sbc.firework.daos.Part;

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

    void commit() throws ServiceException;
}
