package at.sbc.firework.service.alt;

import at.sbc.firework.entities.Part;
import at.sbc.firework.entities.Rocket;
import at.sbc.firework.entities.RocketPackage5;
import at.sbc.firework.service.IDataChangedListener;
import at.sbc.firework.service.IService;
import at.sbc.firework.service.IServiceTransaction;
import at.sbc.firework.service.ServiceException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Service Interface fürs RMI
 */
public interface IServiceRmi extends Remote {
    void cancel() throws ServiceException, RemoteException;

    void start() throws ServiceException, RemoteException;
    void stop() throws ServiceException, RemoteException;

    IServiceTransactionRmi startTransaction() throws ServiceException, RemoteException;

    ArrayList<Part> listStock() throws ServiceException, RemoteException;
    ArrayList<Rocket> listQualityCheckQueue() throws ServiceException, RemoteException;
    ArrayList<Rocket> listPackingQueue() throws ServiceException, RemoteException;
    ArrayList<Rocket> listGarbage() throws ServiceException, RemoteException;
    ArrayList<RocketPackage5> listDistributionStock() throws ServiceException, RemoteException;

    void addChangeListener(IRemoteEventListener listener) throws RemoteException;

    long getNewId() throws ServiceException, RemoteException;

    void ping() throws RemoteException;
}
