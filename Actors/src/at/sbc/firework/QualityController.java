package at.sbc.firework;

import at.sbc.firework.actors.Actor;
import at.sbc.firework.entities.EffectCharge;
import at.sbc.firework.entities.PropellingCharge;
import at.sbc.firework.entities.Quality;
import at.sbc.firework.entities.Rocket;
import at.sbc.firework.service.IFactoryTransaction;
import at.sbc.firework.service.ServiceException;

/**
 * Created by daniel on 17.11.2014.
 */
public class QualityController extends Actor {

    public static void main(String[] args) {

        QualityController qc = new QualityController(args);

        qc.workLoop();

        qc.dispose();

        System.exit(0);
    }

    public QualityController(String[] args) { super("QualityController", args); }

    @Override
    public void work() {

        IFactoryTransaction t = null;

        try {
            t = service.startTransaction();

            //amol a Rocket usserholla
            System.out.println("getting next rocket...");
            Rocket rocket = t.takeFromQualityCheckQueue();


            //d Rocket abchecka
            int damagedCount = 0;
            for (EffectCharge e : rocket.getEffectCharges()) {
                if (e.isDamaged())
                    damagedCount++;
            }

            int propellingChargeCount = 0;
            for (PropellingCharge p : rocket.getPropellingCharge()) {
                propellingChargeCount += p.getAmount();
            }


            //dr quality typ bewertet d Qualität speichert noch sine id
            // TODO: d Qualität wirklich bewerta lo, i han jetzt mol einfach uf ClassA gstellt wenn se ned damaged isch
            if (damagedCount <= 1 && propellingChargeCount >= 120)
                rocket.setQuality(id, Quality.ClassA);
            else
                rocket.setQuality(id, Quality.Damaged);

            System.out.println(rocket.toString());

            t.addToPackingQueue(rocket);
            t.commit();

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
}
