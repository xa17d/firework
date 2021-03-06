package at.sbc.firework.actors;

import at.sbc.firework.entities.EffectCharge;
import at.sbc.firework.entities.PropellingCharge;
import at.sbc.firework.service.IFactoryService;
import at.sbc.firework.service.ServiceException;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by daniel on 20.11.2014.
 */
public class Utils {

    private static final int DEFAULT_MIN_SLEEP_TIME = 400;
    private static final int DEFAULT_MAX_SLEEP_TIME = 800;

    public static long getIdFromArgsOrGenerate(String[] args, IFactoryService service) {
        if (args.length > 0) {
            return Long.parseLong(args[0]);
        }
        else
        {
            try {
                return service.getNewId();
            } catch (ServiceException e) {
                e.printStackTrace();
                return -1;
            }
        }
    }

    public static int randomInt(int min, int max)
    {
        return min + (int)(new Random().nextDouble() * (max-min));
    }

    public static PropellingCharge[] listToArrayP(ArrayList<?> list) {
        PropellingCharge[] array = list.toArray(new PropellingCharge[list.size()]);
        return array;
    }

    public static EffectCharge[] listToArrayE(ArrayList<?> list) {
        EffectCharge[] array = list.toArray(new EffectCharge[list.size()]);
        return array;
    }

    public static void sleep(int minMilliseconds, int maxMilliseconds)
    {
        long time = randomInt(minMilliseconds, maxMilliseconds);
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void sleep() {
        sleep(DEFAULT_MIN_SLEEP_TIME, DEFAULT_MAX_SLEEP_TIME);
    }

}
