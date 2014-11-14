package at.sbc.firework.service;

import at.sbc.firework.daos.Part;

import java.util.ArrayList;

/**
 * Stellt d Verbindung zum gemeinsama Speicher her und stellt Methoda zur
 * verfügung um der aktuelle Status uszumleasa. Für Ändernde Operationa
 * muss a neue Transaktion erstellt wöra.
 */
public interface IService {
    void start() throws ServiceException;
    void stop() throws ServiceException;

    IServiceTransaction startTransaction() throws ServiceException;
    ArrayList<Part> getStock() throws ServiceException;
}
