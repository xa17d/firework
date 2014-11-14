package at.sbc.firework.service;

import at.sbc.firework.daos.Part;

import java.util.ArrayList;

/**
 * In ana Transaktion können Speicher verändernde Operationa durchgführt wöra.
 * Transaktiona wörn mit Hilfe vom IService erstellt. Am Schluss muss immr
 * commit() ufgruafa wöra
 */
public interface IServiceTransaction {

    void addToStock(Part part) throws ServiceException;

    void commit() throws ServiceException;
}
