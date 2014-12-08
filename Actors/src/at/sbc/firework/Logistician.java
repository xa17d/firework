package at.sbc.firework;

import at.sbc.firework.actors.Actor;
import at.sbc.firework.actors.Utils;
import at.sbc.firework.entities.EffectCharge;
import at.sbc.firework.entities.PropellingCharge;
import at.sbc.firework.entities.Rocket;
import at.sbc.firework.entities.RocketPackage5;
import at.sbc.firework.service.IDataChangedListener;
import at.sbc.firework.service.IServiceTransaction;
import at.sbc.firework.service.ServiceException;

import java.util.ArrayList;
import java.util.Arrays;

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

        IServiceTransaction t = null;

        try {
            t = service.startTransaction();

            //amol 5 ganze Rockets usserholla - falls ene hi isch wäckwärfa
            ArrayList<Rocket> distributionList = new ArrayList<Rocket>();
            while(distributionList.size() < 5) {

                System.out.println("getting next rocket...");
                Rocket rocket = t.takeFromPackingQueue();

                if (rocket.isDamaged()) {
                    System.out.println("putting rocket (" + rocket.getId() + ") to garbage");
                    t.addToGarbage(rocket);
                }
                else {
                    System.out.println(distributionList.size()+1 + ". rocket in package: \n" + rocket.toString());
                    distributionList.add(rocket);
                }
            }

            //Rockets usliefra
            t.addToDistributionStock(new RocketPackage5(id, service.getNewId(), distributionList.toArray(new Rocket[1])));
            t.commit();
            System.out.println("package put to distribution stock...");

        } catch (ServiceException e) {
            e.printStackTrace();

            if (t != null) {
                try {
                    t.rollback();
                } catch (ServiceException e1) {
                    e1.printStackTrace();
                }
            }
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
