package at.sbc.firework.gui;

import at.sbc.firework.daos.*;
import at.sbc.firework.service.IService;
import at.sbc.firework.service.IServiceTransaction;
import at.sbc.firework.service.ServiceException;
import at.sbc.firework.service.ServiceXvsm;

import java.util.ArrayList;

/**
 * Created by daniel on 14.11.2014.
 */
public class HelloGui {
    public static void main(String[] args)
    {
        System.out.println("Hello Gui");

        IService service = new ServiceXvsm();

        try {
            System.out.println("START");
            service.start();

            System.out.println("ADD");

            long supplierId1 = service.getNewId();
            long supplierId2 = service.getNewId();

            IServiceTransaction t = service.startTransaction();
            t.addToStock(new EffectCharge(supplierId1, 12, true));
            t.addToStock(new Casing(supplierId1, service.getNewId()));
            t.addToStock(new Casing(supplierId2, service.getNewId()));
            t.addToStock(new Casing(supplierId2, service.getNewId()));
            t.commit();

            System.out.println("LIST");

            for (Part p: service.listStock()) {
                System.out.println(" - " + p.toString());
            }

            System.out.println("TAKE OUT");

            IServiceTransaction t2 = service.startTransaction();
            for (Part p: t2.takeFromStock(Casing.class, 2)) {
                System.out.println(" Take - " + p.toString());
            }
            t2.commit();

            System.out.println("LIST 2");

            for (Part p: service.listStock()) {
                System.out.println(" - " + p.toString());
            }

            System.out.println("STOP");
            service.stop();
        } catch (ServiceException e) {
            e.printStackTrace();
        }

        System.out.println("EXIT");
    }
}
