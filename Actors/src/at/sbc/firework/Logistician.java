package at.sbc.firework;

import at.sbc.firework.actors.Actor;
import at.sbc.firework.entities.*;
import at.sbc.firework.service.Console;
import at.sbc.firework.service.IFactoryTransaction;
import at.sbc.firework.service.NotAvailableException;
import at.sbc.firework.service.ServiceException;
import at.sbc.firework.utils.Notification;

import java.util.ArrayList;

/**
 * Created by daniel on 17.11.2014.
 */
public class Logistician extends Actor {

    public static void main(String[] args) {

        Logistician l = new Logistician(args);

        l.workLoop();

        l.dispose();

        System.exit(0);
    }

    public Logistician(String[] args) { super("Logistician", args); }

    @Override
    public void work() {

        // falls vorher ned gnuag Raketa do gsi sind, warta bis wieder welle kond
        waitForNotification();

        boolean hasDoneSomething = false; // würd uf true gsetzt wenn der Logistican was gmacht hat,
                                          // weil wenn er nix macha künna hat müss ma nöchst mol warta
                                          // bis sich in da packingQueue was gändert hat.
        NotAvailableException notAvailableException = null; // do wird ggf. d exception gspeichert mit dera denn die Notification registriert wird.

        IFactoryTransaction t = null;

        //
        // Mol probiera zum an 5er Pack Class A Raketa macha
        //
        t = null;
        try {
            Console.print("Class A Package...\t");
            t = service.startTransaction();

            // Probiera zum 5 Rockets zöm hola, wenns zwenig sind, würd a NotAvailableException ghaut.
            // Die Raketa dürfen abr o koa bstellte si, weil dia gond jo an da Customer.
            ArrayList<Rocket> rockets = t.takeFromPackingQueue(5, Quality.ClassA, OrderMode.MustNotBeOrdered);
            RocketPackage5 rocketPackage = new RocketPackage5(id, service.getNewId(), rockets.toArray(new Rocket[1]));

            Console.println(rocketPackage);

            t.addToDistributionStock(rocketPackage);
            t.commit();
            Console.println("package put to distribution stock...");
            hasDoneSomething = true;
        }
        catch (NotAvailableException e) {
            Console.println("not enough available");
            notAvailableException = e;
            tryRollback(t);
        } catch (ServiceException e) {
            e.printStackTrace();
            tryRollback(t);
        }

        //
        // Mol probiera zum an 5er Pack Class B Raketa macha
        //
        t = null;
        try {
            Console.print("Class B Package...\t\t");
            t = service.startTransaction();

            // Probiera zum 5 Rockets zöm hola, wenns zwenig sind, würd a NotAvailableException ghaut.
            // Die Raketa künnen o bstelle si, weil es wörn jo nur ClassA Raketa an Customer gliefert.
            // In denam Fall muss ma abr die OrderPosition wieder zruckgia, damit a neue Rakete gfertigt würd
            ArrayList<Rocket> rockets = t.takeFromPackingQueue(5, Quality.ClassB, OrderMode.Indifferent);

            // OrderPosition zruckbringa
            for (Rocket r : rockets) {
                putOrderPositionBack(r, t);
            }

            // packa
            RocketPackage5 rocketPackage = new RocketPackage5(id, service.getNewId(), rockets.toArray(new Rocket[1]));

            Console.println(rocketPackage);

            t.addToDistributionStock(rocketPackage);
            t.commit();
            Console.println("package put to distribution stock...");
            hasDoneSomething = true;
        }
        catch (NotAvailableException e) {
            Console.println("not enough available");
            notAvailableException = e;
            tryRollback(t);
        } catch (ServiceException e) {
            e.printStackTrace();
            tryRollback(t);
        }

        //
        // Mol schaua obs Class B Raketa gibt, wo a OrderPosition hond. Do sött ma die OrderPositions
        // wieder zruck tua, weil sus müsst ma warta bis se in an 5er Pack vrpackt wörn
        //
        t = null;
        try {
            Console.print("Class B Freeing...\t\t");
            t = service.startTransaction();

            ArrayList<Rocket> rockets = t.takeFromPackingQueue(1, Quality.ClassB, OrderMode.MustBeOrdered);

            for (Rocket r : rockets) {

                // OrderPosition zruckbringa
                putOrderPositionBack(r, t);

                // wieder ind Queue haua
                t.addToPackingQueue(r);
            }

            t.commit();
            hasDoneSomething = true;
        }
        catch (NotAvailableException e) {
            Console.println("not enough available");
            notAvailableException = e;
            tryRollback(t);
        } catch (ServiceException e) {
            e.printStackTrace();
            tryRollback(t);
        }

        //
        // Mol schaua obs was zum usliefra git
        //
        t = null;
        try {
            Console.print("Delivering...\t");
            t = service.startTransaction();

            ArrayList<Rocket> rockets = t.takeFromPackingQueue(1, Quality.ClassA, OrderMode.MustBeOrdered);
            Rocket rocket = rockets.get(0);

            Console.println(rocket);

            t.addRocketToOrder(rocket);
            t.commit();
            Console.println("rocket put to orders stock...");
            hasDoneSomething = true;
        }
        catch (NotAvailableException e) {
            Console.println("not enough available");
            notAvailableException = e;
            tryRollback(t);
        } catch (ServiceException e) {
            e.printStackTrace();
            tryRollback(t);
        }

        //
        // Mol schaua obs was zum furtwörfa git
        //
        t = null;
        try {
            Console.print("Find Damaged...\t\t");
            t = service.startTransaction();

            ArrayList<Rocket> rockets = t.takeFromPackingQueue(1, Quality.Damaged, OrderMode.Indifferent);
            Rocket rocket = rockets.get(0);

            putOrderPositionBack(rocket, t);

            Console.println(rocket);

            t.addToGarbage(rocket);
            Console.println("putting rocket (" + rocket.getId() + ") to garbage");
            t.commit();
            hasDoneSomething = true;
        }
        catch (NotAvailableException e) {
            Console.println("not enough available");
            notAvailableException = e;
            tryRollback(t);
        } catch (ServiceException e) {
            e.printStackTrace();
            tryRollback(t);
        }

        // Falls es nix zum toa gia hat, nöchst mol ufd Notification warta
        // Falls es was zum toa gia hat, schau glei nomol ob no mehr Arbeit do isch
        if (!hasDoneSomething) {
            registerNotification(notAvailableException);
        }
        else {
            registerNotification((Notification)null);
        }
    }

    private void putOrderPositionBack(Rocket rocket, IFactoryTransaction t) throws ServiceException {
        OrderPosition orderPosition = rocket.getOrderPosition();
        if (orderPosition != null) {
            rocket.setOrderPosition(null);
            Console.println("  put OrderPosition back " + orderPosition);
            t.addOrderPosition(orderPosition);
        }
    }

    /*
    @Override
    public synchronized void dataChanged() {
        notify();
    }

    private synchronized void waitForChange(long timeout) {
        try {
            wait(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    */
}
