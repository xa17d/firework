package at.sbc.firework.service;

import at.sbc.firework.entities.Part;
import at.sbc.firework.entities.Rocket;
import at.sbc.firework.entities.RocketPackage5;

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

    ArrayList<Part> listStock() throws ServiceException;
    ArrayList<Rocket> listQualityCheckQueue() throws ServiceException;
    ArrayList<Rocket> listPackingQueue() throws ServiceException;
    ArrayList<Rocket> listGarbage() throws ServiceException;
    ArrayList<RocketPackage5> listDistributionStock() throws ServiceException;

    void addChangeListener(IDataChangedListener listener);

    long getNewId() throws ServiceException;
}
