package at.sbc.firework.gui.benchmark;

import at.sbc.firework.Logistician;
import at.sbc.firework.Manufacturer;
import at.sbc.firework.QualityController;
import at.sbc.firework.Supplier;
import at.sbc.firework.actors.Utils;
import at.sbc.firework.entities.*;
import at.sbc.firework.service.FactoryService;
import at.sbc.firework.service.IFactoryService;
import at.sbc.firework.service.ServiceException;
import at.sbc.firework.service.ServiceFactory;

import java.io.Console;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

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

            double errorRate = 5;

            new Thread(new Supplier(service, EnumParts.CASING, rocketAmount, 0, null)).start();
            new Thread(new Supplier(service, EnumParts.EFFECT_CHARGE, rocketAmount, errorRate, Color.Blue)).start();
            new Thread(new Supplier(service, EnumParts.EFFECT_CHARGE, rocketAmount, errorRate, Color.Green)).start();
            new Thread(new Supplier(service, EnumParts.EFFECT_CHARGE, rocketAmount, errorRate, Color.Red)).start();
            new Thread(new Supplier(service, EnumParts.PROPELLING_CHARGE, rocketAmount, 0, null)).start();
            new Thread(new Supplier(service, EnumParts.STICK, rocketAmount, 0, null)).start();

            while(service.listStock().size() < rocketAmount * 6) {
                System.out.println(service.listStock().size() + " of " + rocketAmount*6 + "Parts created..");
                try {
                    Thread.sleep(5000);
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

            System.out.println("Building Rockets for 60 seconds");

            /*PrintStream printStreamOriginal=System.out;
            System.setOut(new PrintStream(new OutputStream() {
                public void write(int b) {
                    // NO-OP
                }
            }));*/

            Date startTime = new Date();
            m1.start();
            System.out.println("Manufacturer 1 started");
            m2.start();
            System.out.println("Manufacturer 2 started");
            q1.start();
            System.out.println("QualityController started");
            l1.start();
            System.out.println("Logistician started");

            /*for(int i = 0; i < 60; i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if(service.listDistributionStock().size() * 5 + service.listGarbage().size() >= rocketAmount - 10) {
                    break;
                }
            }
            */

            System.out.println("time|DistributionStock|Garbage|PackingQueue|Sum");

            double t = (new Date().getTime() - startTime.getTime()) / 1000.0;
            while (t < 60.0) {

                int distStock = service.listDistributionStock().size() * 5;
                int garbage = service.listGarbage().size();
                int packingQueue = service.listPackingQueue().size();
                int sum = (distStock + garbage + packingQueue);

                System.out.println(t+"s\t" + distStock + "\t" + garbage + "\t" + packingQueue + "\t = " + sum);

                if (sum == rocketAmount) { break; }

                try {
                    Thread.sleep(333);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                t = (new Date().getTime() - startTime.getTime()) / 1000.0;
            }

            Date stopTime = new Date();

            m1.stop();
            m2.stop();
            q1.stop();
            l1.stop();


            //System.setOut(printStreamOriginal);

            System.out.println("\nBenchmark ended\n\n ++++ Result: ++++");
            System.out.println("needed Time: " + (stopTime.getTime() - startTime.getTime()) / 1000.0 + " seconds");
            System.out.println("Rockets in DistributionStock: " + service.listDistributionStock().size() * 5);
            System.out.println("Rockets in Garbage: " + service.listGarbage().size());
            System.out.println("------");

            int sCount = 0;
            int eCount = 0;
            int cCount = 0;
            int pCount = 0;
            for(Part p : service.listStock()) {
                if(p instanceof Stick)
                    sCount++;

                if(p instanceof EffectCharge)
                    eCount++;

                if(p instanceof Casing)
                    cCount++;

                if(p instanceof PropellingChargePackage)
                    pCount++;
            }

            System.out.println("Stick-Stock: " + sCount);
            System.out.println("Casing-Stock: " + sCount);
            System.out.println("EC-Stock: " + sCount);
            System.out.println("PC-Stock: " + pCount);
            System.out.println("QC-Queue: " + service.listQualityCheckQueue().size());
            System.out.println("PK-Queue: " + service.listPackingQueue().size());

            System.console().readLine();
        }
        catch (ServiceException e) {
            e.printStackTrace();
        }

    }
}