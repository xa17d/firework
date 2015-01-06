package at.sbc.firework.entities;

import java.io.Serializable;

/**
 * A Bestellung vo nam Auftraggeber
 */
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    public Order(long id, int count, Color[] colors, String addressShipping) {
        this.id = id;
        this.count = count;
        this.colors = colors;
        this.addressShipping = addressShipping;
    }

    private long id;
    private int count;
    private Color[] colors;
    private String addressShipping;
    private OrderStatus status = OrderStatus.New;

    @Override
    public String toString() {
        return "Order #" + id + "   { " + count + " Rockets (" + colors[0] + "|" + colors[1] + "|" + colors[2] + ") Status: " + status + " }";
    }
}
