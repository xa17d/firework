package at.sbc.firework.entities;

/**
 * Quasi dr Auftrag für oane oanzige Rakete
 */
public class OrderPosition {
    public OrderPosition(long orderId) {
        this.orderId = orderId;
    }

    private long orderId;

    public long getOrderId() { return orderId; }
}
