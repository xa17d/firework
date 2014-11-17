package at.sbc.firework.service;

/**
 * Created by daniel on 17.11.2014.
 */
public class ServiceFactory {

    private ServiceFactory() {
        // prevent public constructor
    }

    public static IService getService() {
        try {
            return (IService) Class.forName("at.sbc.firework.service.Service").newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

}
