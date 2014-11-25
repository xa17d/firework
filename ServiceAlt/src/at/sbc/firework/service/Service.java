package at.sbc.firework.service;

import at.sbc.firework.entities.Part;
import at.sbc.firework.entities.Rocket;
import at.sbc.firework.entities.RocketPackage5;
import at.sbc.firework.service.alt.ClientService;
import at.sbc.firework.service.alt.Server;
import at.sbc.firework.service.alt.ServiceAltException;

import java.rmi.Naming;
import java.util.ArrayList;

/**
 * Created by daniel on 17.11.2014.
 */
public class Service implements IService {

    private static final String HOST = "localhost";
    private static final int PORT = 9876;

    private Server server;
    private ClientService clientService;

    @Override
    public void start() throws ServiceException {
        System.out.println("--- Alternative Service ---");

        System.setProperty("java.security.policy","file:./firework.policy");

        try {
            if(System.getSecurityManager() == null)
                System.setSecurityManager(new SecurityManager());

            server = (Server) Naming.lookup("rmi://" + HOST + ":" + PORT + "/" + Server.SERVER_NAME);
            clientService = new ClientService(server);

            System.out.println("connected to server: " + "rmi://" + HOST + ":" + PORT + "/" + Server.SERVER_NAME);

        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public void stop() throws ServiceException {
        if(server != null)
            server.disconnectClient(clientService);
    }

    @Override
    public IServiceTransaction startTransaction() throws ServiceException {
        return getClientService().startTransaction();
    }

    @Override
    public ArrayList<Part> listStock() throws ServiceException {
        return getClientService().listStock();
    }

    @Override
    public ArrayList<Rocket> listQualityCheckQueue() throws ServiceException {
        return getClientService().listQualityCheckQueue();
    }

    @Override
    public ArrayList<Rocket> listPackingQueue() throws ServiceException {
        return getClientService().listPackingQueue();
    }

    @Override
    public ArrayList<Rocket> listGarbage() throws ServiceException {
        return getClientService().listGarbage();
    }

    @Override
    public ArrayList<RocketPackage5> listDistributionStock() throws ServiceException {
        return getClientService().listDistributionStock();
    }

    @Override
    public void setChangeListener(IDataChangedListener listener) {
        getClientService().setChangeListener(listener);
    }

    @Override
    public long getNewId() throws ServiceException {
        return getClientService().getNewId();
    }

    private ClientService getClientService() /* throws ServiceException */ {
        //if(clientService == null)
        //    throw new ServiceAltException("no client service existing");

        return clientService;
    }
}
