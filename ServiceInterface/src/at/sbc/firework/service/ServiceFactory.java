package at.sbc.firework.service;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Created by daniel on 17.11.2014.
 */
public class ServiceFactory {

    private ServiceFactory() {
        // prevent public constructor
    }

    private static IFactoryService factoryService = null;

    public static IFactoryService getFactory() {
        try {
            if (factoryService == null) {
                factoryService = (IFactoryService) Class.forName("at.sbc.firework.service.FactoryService").newInstance();
            }
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

    private static ICustomerService customerService = null;

    public static ICustomerService getCustomer(String address) {
        // TODO: implement
        throw new NotImplementedException();
    }

    public static ICustomerService createCustomer(String address) {
        // TODO: implement
        throw new NotImplementedException();
    }
}
