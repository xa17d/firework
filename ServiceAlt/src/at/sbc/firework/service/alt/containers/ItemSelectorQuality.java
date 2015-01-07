package at.sbc.firework.service.alt.containers;

import at.sbc.firework.entities.Order;
import at.sbc.firework.entities.Quality;
import at.sbc.firework.entities.Rocket;

/**
 * Created by daniel on 07.01.2015.
 */
public class ItemSelectorQuality extends ItemSelector {
    public ItemSelectorQuality(Quality quality) {
        this.quality = quality;
    }

    private Quality quality;

    @Override
    public boolean checkItem(Object item) {
        if (item instanceof Rocket)
        {
            Rocket rocket = (Rocket)item;

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
