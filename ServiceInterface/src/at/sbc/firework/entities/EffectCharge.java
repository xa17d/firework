package at.sbc.firework.entities;

/**
 * Created by daniel on 14.11.2014.
 */
public class EffectCharge extends Part {

    private static final long serialVersionUID = 4L;

    private boolean damaged;
    private Color color;

    public EffectCharge(long supplierId, long id, boolean damaged, Color color)
    {
        super(supplierId, id);

        this.damaged = damaged;
        this.color = color;
    }

    public boolean isDamaged() { return damaged; }
    public Color getColor() { return color; }

    @Override
    public String toString() {
        return "EffectCharge (" + getId() + " | Supplier: " + getSupplierId() + " | " + (isDamaged() ? "damaged" : "ok") + ")";
    }
}
