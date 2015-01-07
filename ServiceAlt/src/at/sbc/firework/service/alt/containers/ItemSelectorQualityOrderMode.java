package at.sbc.firework.service.alt.containers;

import at.sbc.firework.entities.Order;
import at.sbc.firework.entities.OrderMode;
import at.sbc.firework.entities.Quality;
import at.sbc.firework.entities.Rocket;

/**
 * Created by daniel on 07.01.2015.
 */
public class ItemSelectorQualityOrderMode extends ItemSelector {
    public ItemSelectorQualityOrderMode(Quality quality, OrderMode orderMode) {
        this.quality = quality;
        this.orderMode = orderMode;
    }

    private Quality quality;
    private OrderMode orderMode;

    @Override
    public boolean checkItem(Object item) {
        if (item instanceof Rocket)
        {
            Rocket rocket = (Rocket)item;

            if ((orderMode == OrderMode.MustBeOrdered) && (rocket.getOrderPosition() == null)) { return true; }
            if ((orderMode == OrderMode.MustNotBeOrdered) && (rocket.getOrderPosition() != null)) { return true; }

            if (rocket.getQuality() == quality) {
                setResult(item);
                return false;
            }
            else { return true; }

        }
        else
        { return true; }
    }
}
