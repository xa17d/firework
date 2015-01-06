package at.sbc.firework.service.alt.containers;

import at.sbc.firework.entities.Color;
import at.sbc.firework.entities.EffectCharge;
import at.sbc.firework.entities.OrderPosition;

import java.util.ArrayList;

/**
 * Created by daniel on 06.01.2015.
 */
public class ItemSelectorColorOrderPosition extends ItemSelector {

    public ItemSelectorColor(ArrayList<Long> excludeIds) {
        this.excludeIds = excludeIds;
    }

    private ArrayList<Long> excludeIds;

    @Override
    public boolean checkItem(Object item) {
        if (item instanceof OrderPosition)
        {
            OrderPosition op = (OrderPosition)item;

            if (!excludeIds.contains(op.getOrderId())) {
                setResult(item);
                return false;
            }
            else { return true; }

        }
        else
        { return true; }
    }
}
