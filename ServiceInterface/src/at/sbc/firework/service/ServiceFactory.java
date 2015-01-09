package at.sbc.firework.service;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Created by daniel on 17.11.2014.
 */
public class ServiceFactory {

    private ServiceFactory() {
        // prevent public constructor
    }

    public static IFactoryService getFactory() {
        try {
            IFactoryService factoryService = (IFactoryService) Class.forName("at.sbc.firework.service.FactoryService").newInstance();
            return factoryService;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Bietet a nam Factory Mitarbeiter da Zugriff ufs lafer vo nam Customer
     * @param address Adress vom Customer
     * @return Customer-Service-Interface
     */
    public static ICustomerService getCustomer(String address) throws ServiceException {
        try {
            ICustomerService customerService = (ICustomerService) Class.forName("at.sbc.firework.service.CustomerService").newInstance();
            customerService.startGet(address);
            return customerService;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Erstellt an neua Customer a nar bestimmta Adress
     * @param customerId customer id
     * @return Customer-Service-Interface
     */
    public static ICustomerService createCustomer(long customerId) {
        try {
            ICustomerService customerService = (ICustomerService) Class.forName("at.sbc.firework.service.CustomerService").newInstance();
            customerService.startCreate(customerId);
            return customerService;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            e.printStackTrace();
        }

        return null;
    }
}
