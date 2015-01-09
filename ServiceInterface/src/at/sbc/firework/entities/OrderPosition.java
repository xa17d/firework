package at.sbc.firework.entities;

import java.io.Serializable;

/**
 * Quasi dr Auftrag für oane oanzige Rakete
 */
public class OrderPosition implements Serializable {

    private static final long serialVersionUID = 1L;

    public OrderPosition(long orderId) {
        this.orderId = orderId;
    }

    private long orderId;

    public long getOrderId() { return orderId; }

    @Override
    public String toString() {
        return "Order #"+orderId;
    }
}
