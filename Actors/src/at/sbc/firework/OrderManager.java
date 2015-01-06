package at.sbc.firework;

import at.sbc.firework.actors.Actor;
import at.sbc.firework.entities.Order;
import at.sbc.firework.entities.OrderPosition;
import at.sbc.firework.entities.OrderStatus;
import at.sbc.firework.entities.Rocket;
import at.sbc.firework.service.*;
import at.sbc.firework.utils.Notification;
import at.sbc.firework.utils.NotificationMode;

import java.util.ArrayList;

/**
 * Created by daniel on 20.12.2014.
 */
public class OrderManager extends Actor {
    public static void main(String[] args) {

        OrderManager om = new OrderManager(args);

        om.workLoop();

        om.dispose();

        System.exit(0);
    }

    public OrderManager(String[] args) {
        super("OrderManager", args);
    }

    @Override
    public void work()
    {
        try {
            ArrayList<Order> orders = service.listOrders();

            for (Order order : orders) {

                //
                // Neuer Auftrag
                //
                if (order.getStatus() == OrderStatus.New) {

                    System.out.println("== New Order " + order + " ==");

                    IFactoryTransaction t = service.startTransaction();
                    try {
                        // Order ussa taken
                        t.takeOrder(order.getId());

                        System.out.print("Creating Positions...\t");
                        // Positiona erstella
                        for (int i=0; i<order.getCount(); i++)
                        {
                            t.addOrderPosition(
                                    new OrderPosition(order.getId())
                            );
                        }
                        System.out.println("done");

                        System.out.print("Changing Status...\t");
                        // Status updaten
                        order.setStatus(OrderStatus.InProgress);

                        // die upgedatete Order wirder addn
                        t.addOrder(order);

                        t.commit();
                        System.out.println("done");
                    }
                    catch (ServiceException e) {
                        e.printStackTrace();
                        tryRollback(t);
                    }

                }
                //
                // Prüfen ob Auftrag fertig isch
                //
                else if (order.getStatus() == OrderStatus.InProgress) {

                    System.out.println("== In Progress " + order + " ==");

                    if (service.getOrderRocketCount(order.getId()) == order.getCount()) {

                        // alls produziert, denn liefra ma des mol us...

                        System.out.print("Fetching Rockets...\t");
                        ArrayList<Rocket> rockets = service.listOrderRockets(order.getId());
                        System.out.println("got "+rockets.size());

                        IFactoryTransaction t = service.startTransaction();
                        try {
                            // Connecten zum Lager vom Customer
                            ICustomerService customerService;
                            ICustomerTransaction ct = null;
                            try {
                                System.out.print("Connect to Customer "+order.getAddressShipping()+"...\t");
                                customerService = ServiceFactory.getCustomer(order.getAddressShipping());
                                System.out.println("ok");

                                System.out.print("Shipping rockets...\t");
                                ct = customerService.startTransaction();
                                for (Rocket r : rockets) {
                                    ct.addRocket(r);
                                }
                                System.out.print(rockets.size());
                                ct.commit();
                                System.out.println("done");
                            } catch (ServiceException e) {
                                tryRollback(ct);

                                System.out.print("Could not deliver...\t");
                                t.takeOrder(order.getId());
                                order.setStatus(OrderStatus.CouldNotDeliver);
                                t.addOrder(order);

                                t.commit();
                                System.out.println("status set");

                                throw e;
                            }

                            // No da Status updaten, denn simmr fertig

                            System.out.print("Update Order Status...\t");
                            t.takeOrder(order.getId());
                            order.setStatus(OrderStatus.Done);
                            t.addOrder(order);

                            t.commit();
                            System.out.println("done");
                        }
                        catch (ServiceException e) {
                            e.printStackTrace();
                            tryRollback(t);
                        }

                    }
                }

            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (ServiceException e) {
            e.printStackTrace();
        }

    }
}
