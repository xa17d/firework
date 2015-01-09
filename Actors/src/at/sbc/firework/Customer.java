package at.sbc.firework;

import at.sbc.firework.actors.Actor;
import at.sbc.firework.entities.Color;
import at.sbc.firework.entities.Order;
import at.sbc.firework.entities.OrderStatus;
import at.sbc.firework.entities.Rocket;
import at.sbc.firework.service.*;

import java.util.ArrayList;

/**
 * Created by daniel on 20.12.2014.
 */
public class Customer extends Actor {

    public Customer(String[] args) {
        super("Customer", args);

        factoryService = service;
        customerService = ServiceFactory.createCustomer(id);
    }

    private IFactoryService factoryService;
    private ICustomerService customerService;

    @Override
    public void work() {}

    public IFactoryService getFactoryService() { return factoryService; }
    public ICustomerService getCustomerService() { return customerService; }

    /**
     * Gibt a bestellung bi da Fabrik uf
     * @param count Anzahl vo da Raketa
     * @param colors Farba, muss genau drü iträg ha
     * @throws ServiceException
     */
    public void order(int count, Color[] colors) throws ServiceException {
        // neues Bestellungs-Objekt erzeuga
        Order order = new Order(
                factoryService.getNewId(),
                count,
                colors,
                customerService.getAddress()
        );

        // Bestellung in da Firma ufgia
        IFactoryTransaction transaction = null;
        try {
            transaction = factoryService.startTransaction();
            transaction.addOrder(order);
            transaction.commit();
        }
        catch (ServiceException e) {
            e.printStackTrace();
            tryRollback(transaction);
        }
    }

    public void fetchRockets() throws ServiceException {
        ArrayList<Order> orders = factoryService.listOrders();
        for (Order order : orders) {
            if (order.getStatus() == OrderStatus.CouldNotDeliver && customerService.getAddress().equals(order.getAddressShipping())) {

                Console.println("- NotDelivered: "+order);
                Console.print("fetching rockets...\t");
                ArrayList<Rocket> rockets = factoryService.listOrderRockets(order.getId());
                Console.println(rockets.size()+" done");
                Console.print("adding to Customer Stock...\t");

                ICustomerTransaction t = null;
                IFactoryTransaction ft = null;

                try {
                    t = customerService.startTransaction();

                    for (Rocket r : rockets) {
                        t.addRocket(r);
                    }

                    t.commit();

                    ft = factoryService.startTransaction();
                    ft.takeOrder(order.getId());
                    order.setStatus(OrderStatus.Done);
                    ft.addOrder(order);
                    ft.commit();
                }
                catch (ServiceException e) {
                    e.printStackTrace();
                    tryRollback(t);
                    tryRollback(ft);
                }


            }
        }
    }
}
