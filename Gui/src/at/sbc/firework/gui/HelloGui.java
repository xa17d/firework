package at.sbc.firework.gui;

import at.sbc.firework.daos.*;
import at.sbc.firework.service.IService;
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
            service.addToStock(new EffectCharge(12, true));
            service.addToStock(new Casing(13));

            System.out.println("LIST");
            for (Part p: service.getStock())
            {
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
