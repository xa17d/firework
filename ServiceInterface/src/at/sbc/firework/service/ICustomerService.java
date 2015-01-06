package at.sbc.firework.service;

import at.sbc.firework.entities.Rocket;
import at.sbc.firework.utils.NotificationMode;

import java.util.ArrayList;

/**
 * Created by daniel on 23.12.2014.
 */
public interface ICustomerService {
    void startCreate(long customerId) throws ServiceException;
    void startGet(String address) throws ServiceException;
    void stop() throws ServiceException;

    String getAddress() throws ServiceException;

    ArrayList<Rocket> listRockets() throws ServiceException;

    ICustomerTransaction startTransaction() throws ServiceException;

    void registerNotification(INotification notification, String containerId, ContainerOperation operation, NotificationMode mode) throws ServiceException;
}
