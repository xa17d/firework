package at.sbc.firework.service.alt.containers;

import at.sbc.firework.entities.Color;
import at.sbc.firework.entities.EffectCharge;

/**
 * Created by daniel on 06.01.2015.
 */
public class ItemSelectorColor extends ItemSelector {

    public ItemSelectorColor(Color color) {
        this.color = color;
    }

    private Color color;

    @Override
    public boolean checkItem(Object item) {
        if (item instanceof EffectCharge)
        {
            EffectCharge ec = (EffectCharge)item;

            if (ec.getColor() == color) {
                setResult(item);
                return false;
            }
            else { return true; }

        }
        else
        { return true; }
    }
}
