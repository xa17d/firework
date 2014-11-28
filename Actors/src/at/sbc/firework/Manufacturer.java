package at.sbc.firework;

import at.sbc.firework.actors.Actor;
import at.sbc.firework.actors.Utils;
import at.sbc.firework.entities.*;
import at.sbc.firework.service.IService;
import at.sbc.firework.service.IServiceTransaction;
import at.sbc.firework.service.ServiceException;
import at.sbc.firework.service.ServiceFactory;

import javax.rmi.CORBA.Util;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by daniel on 17.11.2014.
 */
public class Manufacturer extends Actor {
    public static void main(String[] args) {

        Manufacturer m = new Manufacturer(args);

        m.workLoop();

        m.dispose();

        System.exit(0);
    }

    public Manufacturer(String[] args) {
        super("Manufacturer", args);
    }

    @Override
    public void work()
    {
        IServiceTransaction t = null;
        try {
            t = service.startTransaction();

            // Töal vom Lager hola

            System.out.print("Getting Stick...\t");
            Stick stick = (Stick) t.takeFromStock(Stick.class, 1).get(0);
            System.out.println(stick.toString());

            System.out.print("Getting Casing...\t");
            Casing casing = (Casing) t.takeFromStock(Casing.class, 1).get(0);
            System.out.println(casing.toString());

            System.out.println("Getting EffectCharges...\t");
            EffectCharge[] effectCharges = (EffectCharge[])Utils.<EffectCharge>listToArrayE(t.takeFromStock(EffectCharge.class, 3));

            System.out.println("\t"+effectCharges[0].toString());
            System.out.println("\t"+effectCharges[1].toString());
            System.out.println("\t"+effectCharges[2].toString());

            // PropellingCharge hola

            System.out.print("Getting PropellingCharge...");

            ArrayList<PropellingCharge> propellingCharge = new ArrayList<PropellingCharge>();

            // 130g +- 15g
            int amount = Utils.randomInt(115, 145);
            int amountRemaining = amount;

            System.out.println(amount+"g");

            while (amountRemaining > 0)
            {
                PropellingChargePackage p = t.takePropellingChargePackageFromStock();

                PropellingCharge charge = p.takeOut(amountRemaining);
                propellingCharge.add(charge);

                System.out.println("\ttook " + charge.getAmount() + "g from " + p.toString());

                amountRemaining -= charge.getAmount();

                if (!p.isEmpty()) {
                    t.addToStock(p);
                }
            }

            // Rocket zemmbaua

            System.out.print("Building new Rocket...\t");

            long rocketId = service.getNewId();

            Rocket rocket = new Rocket(id, rocketId, stick, casing, effectCharges, Utils.listToArrayP(propellingCharge));

            // Ind QualityCheckQueue

            t.addToQualityCheckQueue(rocket);

            // Arbeitszit

            Utils.sleep(1000, 2000);

            // Commit

            t.commit();

            System.out.println(rocket.toString());
        }
        catch (ServiceException e)
        {
            e.printStackTrace();

            if (t!=null)
            {
                try {
                    t.rollback();
                } catch (ServiceException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

}
