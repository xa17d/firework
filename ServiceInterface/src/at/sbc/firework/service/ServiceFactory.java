package at.sbc.firework.service;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by daniel on 17.11.2014.
 */
public class ServiceFactory {

    private ServiceFactory() {
        // prevent public constructor
    }

    public static IFactoryService getFactory(String address) {
        try {
            IFactoryService factoryService = (IFactoryService) Class.forName("at.sbc.firework.service.FactoryService").newInstance();
            factoryService.setAddress(address);
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

    public static String getDefaultFactoryAddress() {
        try {
            Class<?> c = Class.forName("at.sbc.firework.service.FactoryService");
            Method m = c.getMethod("getDefaultFactoryAddress");
            Object result = m.invoke(null);
            return result.toString();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return "error "+e.getMessage();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return "error "+e.getMessage();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            return "error "+e.getMessage();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return "error "+e.getMessage();
        }
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
