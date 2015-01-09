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

                    Console.println("== New Order " + order + " ==");

                    IFactoryTransaction t = service.startTransaction();
                    try {
                        // Order ussa taken
                        t.takeOrder(order.getId());

                        Console.print("Creating Positions...\t");
                        // Positiona erstella
                        for (int i=0; i<order.getCount(); i++)
                        {
                            t.addOrderPosition(
                                    new OrderPosition(order.getId())
                            );
                        }
                        Console.println("done");

                        Console.print("Changing Status...\t");
                        // Status updaten
                        order.setStatus(OrderStatus.InProgress);

                        // die upgedatete Order wirder addn
                        t.addOrder(order);

                        t.commit();
                        Console.println("done");
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

                    Console.print("== In Progress " + order + " | ");

                    int progress = service.getOrderRocketCount(order.getId());
                    Console.println(progress+"/"+order.getCount()+" ==");

                    if (progress == order.getCount()) {

                        // alls produziert, denn liefra ma des mol us...

                        Console.print("Fetching Rockets...\t");
                        ArrayList<Rocket> rockets = service.listOrderRockets(order.getId());
                        Console.println("got "+rockets.size());

                        // Connecten zum Lager vom Customer
                        ICustomerService customerService;
                        ICustomerTransaction ct = null;
                        try {
                            Console.print("Connect to Customer "+order.getAddressShipping()+"...\t");
                            customerService = ServiceFactory.getCustomer(order.getAddressShipping());
                            Console.println("ok");

                            Console.print("Shipping rockets...\t");
                            ct = customerService.startTransaction();
                            for (Rocket r : rockets) {
                                ct.addRocket(r);
                            }
                            Console.print(rockets.size());
                            ct.commit();
                            Console.println("done");

                            Console.print("Update Order Status...\t");
                            updateOrderStatus(order, OrderStatus.Done);
                            Console.println("done");

                        } catch (ServiceException e) {
                            tryRollback(ct);

                            Console.print("Could not deliver...\t");
                            updateOrderStatus(order, OrderStatus.CouldNotDeliver);
                            Console.println("status set");

                            Console.println("reason: "+e.getMessage());
                        }

                    }
                }
                //
                // Could Not Deliver - do müss ma nix tua
                //
                else if (order.getStatus() == OrderStatus.CouldNotDeliver) {
                    Console.println("== CouldNotDeliver " + order + " ==");
                }
                //
                // Done - do müss ma nix tua
                //
                else if (order.getStatus() == OrderStatus.Done) {
                    Console.println("== Done " + order + " ==");
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

    private void updateOrderStatus(Order order, OrderStatus newStatus) throws ServiceException {

        IFactoryTransaction t = service.startTransaction();
        try {
            t = service.startTransaction();
            t.takeOrder(order.getId());
            order.setStatus(newStatus);
            t.addOrder(order);
            t.commit();
        } catch (ServiceException e) {
            tryRollback(t);
            throw e;
        }

    }
}
