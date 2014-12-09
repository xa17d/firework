package at.sbc.firework.actors;

import at.sbc.firework.service.IService;
import at.sbc.firework.service.ServiceException;
import at.sbc.firework.service.ServiceFactory;

/**
 * Created by daniel on 20.11.2014.
 */
public abstract class Actor {

    private static final int WAITING_TIME = 500;

    public Actor(String name, String[] args) {

        service = ServiceFactory.getService();
        try {
            service.start();
        } catch (ServiceException e) {
            e.printStackTrace();
        }

        id = Utils.getIdFromArgsOrGenerate(args, service);

        System.out.println(name + " #" + id + " ready for work...");
    }

    public abstract void work();

    public void workLoop() {

        while (true) {
            work();
            System.out.println("--------------");
        }
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
