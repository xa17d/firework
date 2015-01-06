package at.sbc.firework.service.alt.containers;

import at.sbc.firework.entities.Color;
import at.sbc.firework.entities.EffectCharge;
import at.sbc.firework.entities.Order;

/**
 * Created by daniel on 06.01.2015.
 */
public class ItemSelectorOrderId extends ItemSelector {

    public ItemSelectorOrderId(long orderId) {
        this.orderId = orderId;
    }

    private long orderId;

    @Override
    public boolean checkItem(Object item) {
        if (item instanceof Order)
        {
            Order order = (Order)item;

            if (order.getId() == orderId) {
                setResult(item);
                return false;
            }
            else { return true; }

        }
        else
        { return true; }
    }
}
