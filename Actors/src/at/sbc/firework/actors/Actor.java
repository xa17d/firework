package at.sbc.firework.actors;

import at.sbc.firework.service.IService;
import at.sbc.firework.service.ServiceException;
import at.sbc.firework.service.ServiceFactory;

/**
 * Created by daniel on 20.11.2014.
 */
public class Actor {

    public Actor(String name, String[] args) {

        service = ServiceFactory.getService();
        try {
            service.start();
        } catch (ServiceException e) {
            e.printStackTrace();
        }

        id = Utils.getIdFromArgsOrGenerate(args, service);

        System.out.println(name + " #" + id);
    }

    public void dispose()
    {
        if (service != null)
        {
            try {
                service.stop();
            } catch (ServiceException e) {
                e.printStackTrace();
            }
            service = null;
        }
    }

    protected long id;
    protected IService service;

}
