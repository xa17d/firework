package at.sbc.firework.gui.benchmark;

import at.sbc.firework.Logistician;
import at.sbc.firework.Manufacturer;
import at.sbc.firework.QualityController;
import at.sbc.firework.Supplier;
import at.sbc.firework.actors.Utils;
import at.sbc.firework.entities.Color;
import at.sbc.firework.entities.EnumParts;
import at.sbc.firework.service.FactoryService;
import at.sbc.firework.service.IFactoryService;
import at.sbc.firework.service.ServiceException;
import at.sbc.firework.service.ServiceFactory;

import java.io.Console;
import java.io.IOException;

/**
 * Created by Lucas on 07.01.2015.
 */
public class Benchmark {



    public static void main(String[] args) {

        System.out.println("Starting Benchmark");

        try {
            IFactoryService service = ServiceFactory.getFactory();
            service.start();

            int rocketAmount = 1500;
            System.out.println("Creating Parts for " + rocketAmount + " Rockets...");

            new Thread(new Supplier(service, EnumParts.CASING, rocketAmount, 0, null)).start();
            new Thread(new Supplier(service, EnumParts.EFFECT_CHARGE, rocketAmount, 5, Color.Blue)).start();
            new Thread(new Supplier(service, EnumParts.EFFECT_CHARGE, rocketAmount, 5, Color.Green)).start();
            new Thread(new Supplier(service, EnumParts.EFFECT_CHARGE, rocketAmount, 5, Color.Red)).start();
            new Thread(new Supplier(service, EnumParts.PROPELLING_CHARGE, rocketAmount, 0, null)).start();
            new Thread(new Supplier(service, EnumParts.STICK, rocketAmount, 0, null)).start();

            while(service.listStock().size() < rocketAmount * 6) {
                System.out.println(service.listStock().size() + " of " + rocketAmount*6 + "Parts created..");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("Parts created\nPress Enter to start Benchmark!");
            System.console().readLine();

            Thread m1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    new Manufacturer(new String[]{"101"}).workLoop();
                }
            });
            m1.start();
            System.out.println("Manufacturer 1 started");

            Thread m2 = new Thread(new Runnable() {
                @Override
                public void run() {
                    new Manufacturer(new String[]{"102"}).workLoop();
                }
            });
            m2.start();
            System.out.println("Manufacturer 2 started");

            Thread q1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    new QualityController(new String[]{"103"}).workLoop();
                }
            });
            q1.start();
            System.out.println("QualityController started");

            Thread l1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    new Logistician(new String[]{"104"}).workLoop();
                }
            });
            l1.start();
            System.out.println("Logistician started");

            System.out.println("Building Rockets for 60 seconds");

            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            m1.stop();
            m2.stop();
            q1.stop();
            l1.stop();

            System.out.println("Benchmark ended\n\n ++++ Result: ++++");
            System.out.println("Rockets in DistributionStock: " + service.listDistributionStock().size() * 5);
            System.out.println("Rockets in Garbage: " + service.listGarbage().size());

            System.console().readLine();
        }
        catch (ServiceException e) {
            e.printStackTrace();
        }

    }
}
