package at.sbc.firework;

import at.sbc.firework.actors.Actor;
import at.sbc.firework.actors.Utils;
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
        // falls vorher ned gnuag töal do gsi sind, warta bis sich do was gändert hat
        waitForNotification();

        ArrayList<Long> excludeOrderIds = new ArrayList<Long>();

        IFactoryTransaction t = null;
        try {
            t = service.startTransaction();

            OrderPosition orderPosition = tryTakeOrderPosition(t, excludeOrderIds);

            if (orderPosition == null) {
                // es git koa Aufträg, drum mach ma einfach so a Rakete
                Console.println("no order -> build random rocket");

                try {
                    Rocket rocket = buildRocket(t, null);
                    t.commit();
                }
                catch (NotAvailableException notAvailableExcaption) {
                    // do fehlen töal also abbrecha und warta bis neue gliefert wora sind

                    Console.println("not available -> wait for new parts");

                    registerNotification(notAvailableExcaption);
                    tryRollback(t);
                    t = null;
                }
            }
            else {
                // es gibt an Auftrag
                Console.println("order present.");

                while (orderPosition != null)
                {
                    Rocket rocket;

                    Console.println("-- Rocket for "+orderPosition);

                    try {
                        rocket = buildRocket(t, orderPosition);

                        Console.println(rocket.toString());

                        // alles hat passt, also commiten und a neue transaction starta
                        t.commit();
                        t = service.startTransaction();

                        Console.println("---");

                        // schaua obs do nomol an Auftrag git
                        orderPosition = tryTakeOrderPosition(t, excludeOrderIds);
                    }
                    catch (EffectChargeNotAvailableException effectChargeNotAvailableException) {
                        // es git die erforderlicha EffectCharges numma, abr viellicht kann er jo an andra Auftrag macha
                        // drum addama dean mol zur exclude list und holan an neue OrderPosition ussa

                        Console.println("not available -> try other order");

                        excludeOrderIds.add(orderPosition.getOrderId());

                        // die Transaction zrucksetza und a neue starta
                        tryRollback(t);
                        t = service.startTransaction();

                        orderPosition = tryTakeOrderPosition(t, excludeOrderIds);

                        if (orderPosition == null) {
                            // wait for new parts
                            tryRollback(t);
                            t = null;
                            registerNotification(effectChargeNotAvailableException);
                        }
                    }
                    catch (NotAvailableException notAvailableExcaption) {
                        // do fehlen töal also abbrecha und warta bis neue gliefert wora sind

                        Console.println("not available -> wait for new parts");

                        tryRollback(t);
                        t = null;
                        orderPosition = null;
                        registerNotification(notAvailableExcaption);
                    }
                }

                // falls no was uncommited isch, mach ma besser an rollback
                tryRollback(t);
            }

        }
        catch (ServiceException e)
        {
            e.printStackTrace();
            tryRollback(t);
        }
    }

    private OrderPosition tryTakeOrderPosition(IFactoryTransaction t, ArrayList<Long> excludeIds) throws ServiceException {
        Console.print("Getting OrderPosition ");

        for (Long id : excludeIds) {
            Console.print(id+";");
        }

        Console.print("...\t");

        try {
            OrderPosition p = t.takeOrderPosition(excludeIds);
            Console.println(p);
            return p;
        }
        catch (NotAvailableException e) {
            Console.println("null");
            return null;
        }
    }

    private Rocket buildRocket(IFactoryTransaction t, OrderPosition orderPosition) throws ServiceException {
        // Töal vom Lager hola

        Console.print("Getting Stick...\t");
        Stick stick = (Stick) t.takeFromStock(Stick.class, 1).get(0);
        Console.println(stick.toString());

        Console.print("Getting Casing...\t");
        Casing casing = (Casing) t.takeFromStock(Casing.class, 1).get(0);
        Console.println(casing.toString());

        Console.println("Getting EffectCharges...\t");
        EffectCharge[] effectCharges;

        if (orderPosition == null) {
            effectCharges = (EffectCharge[]) Utils.<EffectCharge>listToArrayE(t.takeFromStock(EffectCharge.class, 3));
        }
        else {
            Order order = service.getOrder(orderPosition.getOrderId());

            Color[] colors = order.getColors();
            Console.println("try to find "+colors[0]+","+colors[1]+","+colors[2]+"");
            try {
                effectCharges = new EffectCharge[]{
                        t.takeEffectChargeFromStock(colors[0]),
                        t.takeEffectChargeFromStock(colors[1]),
                        t.takeEffectChargeFromStock(colors[2])
                };
            }
            catch (NotAvailableException e) {
                throw new EffectChargeNotAvailableException(e.getContainerId(), e);
            }
        }

        Console.println("\t" + effectCharges[0].toString());
        Console.println("\t" + effectCharges[1].toString());
        Console.println("\t" + effectCharges[2].toString());

        // PropellingCharge hola

        Console.print("Getting PropellingCharge...");

        ArrayList<PropellingCharge> propellingCharge = new ArrayList<PropellingCharge>();

        // 130g +- 15g
        int amount = Utils.randomInt(115, 145);
        int amountRemaining = amount;

        Console.println(amount + "g");

        while (amountRemaining > 0) {
            PropellingChargePackage p = t.takePropellingChargePackageFromStock();

            PropellingCharge charge = p.takeOut(amountRemaining);
            propellingCharge.add(charge);

            Console.println("\ttook " + charge.getAmount() + "g from " + p.toString());

            amountRemaining -= charge.getAmount();

            if (!p.isEmpty()) {
                t.addToStock(p);
            }
        }

        // Rocket zemmbaua

        Console.print("Building new Rocket...\t");

        long rocketId = service.getNewId();

        Rocket rocket = new Rocket(id, rocketId, orderPosition, stick, casing, effectCharges, Utils.listToArrayP(propellingCharge));

        // Ind QualityCheckQueue

        t.addToQualityCheckQueue(rocket);

        Console.println(rocket.toString());

        // Arbeitszit
        Utils.sleep(1000, 2000);

        return rocket;
    }

    private class EffectChargeNotAvailableException extends NotAvailableException {
        public EffectChargeNotAvailableException(String containerId, Exception innerException) {
            super(containerId, innerException);
        }
    }
}
