package at.sbc.firework.serveralt.at.sbc.firework.serveralt.test;

import at.sbc.firework.entities.Part;
import at.sbc.firework.entities.Stick;
import at.sbc.firework.serveralt.ClientService;
import at.sbc.firework.serveralt.Server;
import at.sbc.firework.serveralt.transactions.Transaction;
import at.sbc.firework.service.IServiceTransaction;
import at.sbc.firework.service.ServiceException;

import java.util.ArrayList;

/**
 * Created by daniel on 21.11.2014.
 */
public class Test {
    public static void main(String[] args) {
        Server server = new Server();

        ClientService s = new ClientService(server);

        try {
            IServiceTransaction t = s.startTransaction();

            t.addToStock(new Stick(1,1));
            t.addToStock(new Stick(1,2));

            t.commit();

            IServiceTransaction t2 = s.startTransaction();

            ArrayList<Part> items = t2.takeFromStock(Stick.class, 1);

            t2.rollback();

        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }
}
