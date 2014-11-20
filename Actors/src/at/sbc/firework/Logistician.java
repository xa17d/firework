package at.sbc.firework;

import at.sbc.firework.actors.Actor;
import at.sbc.firework.entities.EffectCharge;
import at.sbc.firework.entities.PropellingCharge;
import at.sbc.firework.entities.Rocket;
import at.sbc.firework.entities.RocketPackage5;
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

        l.work();

        l.dispose();

        System.exit(0);
    }

    public Logistician(String[] args) {
        super("Logistician", args);
    }

    public void work() {

        IServiceTransaction t = null;

        try {
            t = service.startTransaction();

            //amol 5 ganze Rockets usserholla - falls ene hi isch wäckwärfa
            ArrayList<Rocket> distributionList = new ArrayList<Rocket>();
            do {
                Rocket rocket = t.takeFromPackingQueue();
                //TODO falls kene meh do isch muas ma des noch handeln
                if (rocket.isDamaged())
                    t.addToGarbage(rocket);
                else
                    distributionList.add(rocket);

            } while(distributionList.size() < 5);

            //Rockets usliefra
            t.addToDistributionStock(new RocketPackage5(id, service.getNewId(), distributionList.toArray(new Rocket[1])));
            t.commit();

        } catch (ServiceException e)
        {
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
}
