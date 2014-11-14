package at.sbc.firework.daos;

/**
 * Created by daniel on 14.11.2014.
 */
public class EffectCharge extends Part {

    private static final long serialVersionUID = 4L;

    private boolean damaged;

    public EffectCharge(long id, boolean damaged)
    {
        super(id);

        this.damaged = damaged;
    }

    public boolean isDamaged() { return damaged; }

    @Override
    public String toString() {
        return "EffectCharge (" + getId() + ", " + (isDamaged() ? "damaged" : "ok") + ")";
    }
}
