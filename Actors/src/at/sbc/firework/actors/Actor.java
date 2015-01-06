package at.sbc.firework.actors;

import at.sbc.firework.service.*;
import at.sbc.firework.utils.Notification;
import at.sbc.firework.utils.NotificationMode;

/**
 * Created by daniel on 20.11.2014.
 */
public abstract class Actor {

    private static final int WAITING_TIME = 500;

    public Actor(String name, String[] args) {

        service = ServiceFactory.getFactory();
        try {
            service.start();
        } catch (ServiceException e) {
            e.printStackTrace();
        }

        id = Utils.getIdFromArgsOrGenerate(args, service);

        System.out.println(name + " #" + id + " init...");
        init();
        System.out.println(name + " #" + id + " ready for work...");
    }

    public void init() { }

    public abstract void work();

    protected void registerNotification(NotAvailableException e) {
        try {
            registerNotification(new Notification(
                    service,
                    e.getContainerId(),
                    ContainerOperation.Add,
                    NotificationMode.Once
            ));
        } catch (ServiceException e1) {
            e1.printStackTrace();
        }
    }

    protected void registerNotification(Notification notification) {
        this.notification = notification;
    }

    protected void tryRollback(ITransaction t) {
        if (t!=null)
        {
            try {
                t.rollback();
            } catch (ServiceException e1) {
                e1.printStackTrace();
            }
        }
    }

    private Notification notification = null;
    protected boolean resetNotification = true;

    protected void waitForNotification() {
        if (notification != null) {
            notification.waitForNotification(5000);

            if (resetNotification) {
                notification = null;
            }
        }
    }


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
    protected IFactoryService service;

}
