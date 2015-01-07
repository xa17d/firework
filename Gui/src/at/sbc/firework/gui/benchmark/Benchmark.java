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
            final IFactoryService service = ServiceFactory.getFactory();
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

            Thread m2 = new Thread(new Runnable() {
                @Override
                public void run() {
                    new Manufacturer(new String[]{"102"}).workLoop();
                }
            });

            Thread q1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    new QualityController(new String[]{"103"}).workLoop();
                }
            });

            Thread l1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    new Logistician(new String[]{"104"}).workLoop();
                }
            });

            m1.start();
            System.out.println("Manufacturer 1 started");

            m2.start();
            System.out.println("Manufacturer 2 started");

            q1.start();
            System.out.println("QualityController started");

            l1.start();
            System.out.println("Logistician started");

            System.out.println("Building Rockets for 60 seconds");

            Thread infoThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            displayInfo(service);
                            System.out.println("---");
                        } catch (ServiceException e) {
                            e.printStackTrace();
                        }

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            infoThread.start();

            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            m1.stop();
            m2.stop();
            q1.stop();
            l1.stop();
            infoThread.stop();

            System.out.println("Benchmark ended\n\n ++++ Result: ++++");
            displayInfo(service);

            System.console().readLine();
        }
        catch (ServiceException e) {
            e.printStackTrace();
        }

    }

    private static void displayInfo(IFactoryService service) throws ServiceException {
        int distStock = service.listDistributionStock().size() * 5;
        int garbage = service.listGarbage().size();
        int packingQueue = service.listPackingQueue().size();
        System.out.println("DistributionStock|Garbage|PackingQueue|Sum " + distStock + "\t" + garbage + "\t" + packingQueue + "\t = "+(distStock+garbage+packingQueue));
    }
}
