package at.sbc.firework.service;

import at.sbc.firework.entities.Order;
import at.sbc.firework.entities.Part;
import at.sbc.firework.entities.Rocket;
import at.sbc.firework.entities.RocketPackage5;

import java.util.ArrayList;

/**
 * Stellt d Verbindung zum gemeinsama Speicher her und stellt Methoda zur
 * verfügung um der aktuelle Status uszumleasa. Für Ändernde Operationa
 * muss a neue Transaktion erstellt wöra.
 */
public interface IFactoryService {
    void start() throws ServiceException;
    void stop() throws ServiceException;

    IFactoryTransaction startTransaction() throws ServiceException;

    ArrayList<Part> listStock() throws ServiceException;
    ArrayList<Rocket> listQualityCheckQueue() throws ServiceException;
    ArrayList<Rocket> listPackingQueue() throws ServiceException;
    ArrayList<Rocket> listGarbage() throws ServiceException;
    ArrayList<RocketPackage5> listDistributionStock() throws ServiceException;

    ArrayList<Order> listOrders() throws ServiceException;
    ArrayList<Rocket> listOrderRockets(long orderId) throws ServiceException;
    int getOrderRocketCount(long orderId) throws ServiceException;

    void addChangeListener(IDataChangedListener listener);

    long getNewId() throws ServiceException;
}
