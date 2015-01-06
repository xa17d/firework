package at.sbc.firework;

import at.sbc.firework.actors.Actor;
import at.sbc.firework.entities.Order;
import at.sbc.firework.entities.OrderPosition;
import at.sbc.firework.entities.OrderStatus;
import at.sbc.firework.service.ContainerOperation;
import at.sbc.firework.service.IFactoryTransaction;
import at.sbc.firework.service.ServiceException;
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

                    IFactoryTransaction t = service.startTransaction();
                    try {
                        // Order ussa taken
                        t.takeOrder(order.getId());

                        // Positiona erstella
                        for (int i=0; i<order.getCount(); i++)
                        {
                            t.addOrderPosition(
                                    new OrderPosition(order.getId())
                            );
                        }

                        // Status updaten
                        order.setStatus(OrderStatus.InProgress);

                        // die upgedatete Order wirder addn
                        t.addOrder(order);

                        t.commit();
                    }
                    catch (ServiceException e) {
                        e.printStackTrace();
                        tryRollback(t);
                    }

                }
                else if (order.getStatus() == OrderStatus.InProgress) {

                    // TODO: implement
                    // OrderPositions erzeugen und Status auf inProgress setzen
                    // Wenn alle Raketen erzeugt sind -> ausleifern, status auf done/couldNotDeliver setzen

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
