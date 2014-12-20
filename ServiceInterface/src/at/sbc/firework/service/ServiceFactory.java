package at.sbc.firework.service;

/**
 * Created by daniel on 17.11.2014.
 */
public class ServiceFactory {

    private ServiceFactory() {
        // prevent public constructor
    }

    private static IService service = null;

    public static IService getService() {
        try {
            if (service == null) {
                service = (IService) Class.forName("at.sbc.firework.service.Service").newInstance();
            }
            return service;
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
