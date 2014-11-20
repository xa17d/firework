package at.sbc.firework.service;

import at.sbc.firework.entities.Part;
import at.sbc.firework.entities.Rocket;
import at.sbc.firework.entities.RocketPackage5;

import java.util.ArrayList;

/**
 * Created by daniel on 17.11.2014.
 */
public class Service implements IService {
    @Override
    public void start() throws ServiceException {
        System.out.println("Alternative Service");
    }

    @Override
    public void stop() throws ServiceException {

    }

    @Override
    public IServiceTransaction startTransaction() throws ServiceException {
        return null;
    }

    @Override
    public ArrayList<Part> listStock() throws ServiceException {
        return null;
    }

    @Override
    public ArrayList<Rocket> listQualityCheckQueue() throws ServiceException {
        return null;
    }

    @Override
    public ArrayList<Rocket> listPackingQueue() throws ServiceException {
        return null;
    }

    @Override
    public ArrayList<Rocket> listGarbage() throws ServiceException {
        return null;
    }

    @Override
    public ArrayList<RocketPackage5> listDistributionStock() throws ServiceException {
        return null;
    }

    @Override
    public void addChangeListener(IDataChangedListener listener) {

    }

    @Override
    public long getNewId() throws ServiceException {
        return 0;
    }
}
