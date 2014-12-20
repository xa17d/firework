package at.sbc.firework.entities;

/**
 * A Bestellung vo nam Auftraggeber
 */
public class Order {
    public Order(long id, int count, Color[] colors, String addressShipping, String addressFactory) {
        this.id = id;
        this.count = count;
        this.colors = colors;
        this.addressShipping = addressShipping;
        this.addressFactory = addressFactory;
    }

    private long id;
    private int count;
    private Color[] colors;
    private String addressShipping;
    private String addressFactory;
    private OrderStatus status = OrderStatus.New;
}
