package at.sbc.firework;

import at.sbc.firework.actors.Actor;
import at.sbc.firework.entities.Color;
import at.sbc.firework.entities.Order;
import at.sbc.firework.service.*;

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

    /**
     * Gibt a bestellung bi da Fabrik uf
     * @param count Anzahl vo da Raketa
     * @param colors Farba, muss genau drü iträg ha
     * @throws ServiceException
     */
    public void Order(int count, Color[] colors) throws ServiceException {
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
}
