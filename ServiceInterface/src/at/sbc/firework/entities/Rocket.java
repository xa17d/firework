package at.sbc.firework.entities;

import java.io.Serializable;

/**
 * Created by daniel on 17.11.2014.
 */
public class Rocket implements Serializable {

    private static final long serialVersionUID = 6L;

    private long manufacturerId;
    private long id;
    private Stick stick;
    private Casing casing;
    private EffectCharge[]  effectCharges;
    private PropellingCharge[] propellingCharge;

    private long qualityControllerId;

    public Rocket(long manufacturerId, long id, Stick stick, Casing casing, EffectCharge[] effectCharges, PropellingCharge[] propellingCharge)
    {
        if (effectCharges.length != 3) {
            throw new IllegalArgumentException("there must be exactly 3 effect charges in a rocket");
        }

        this.manufacturerId = manufacturerId;
        this.id = id;
        this.stick = stick;
        this.casing = casing;
        this.effectCharges = effectCharges;
        this.propellingCharge = propellingCharge;
    }

    public int getPropellingChargeAmount() {
        int amount = 0;
        for (PropellingCharge c : propellingCharge) {
            amount += c.getAmount();
        }
        return  amount;
    }

    public long getManufacturerId() { return manufacturerId; }
    public long getId() { return id; }
    public Stick getStick() { return stick; }
    public Casing getCasing() { return casing; }
    public EffectCharge[] getEffectCharges() { return effectCharges; }
    public PropellingCharge[] getPropellingCharge() { return propellingCharge; }

    public long getQualityControllerId() { return qualityControllerId; }
    public void setQualityControllerId(long id) { qualityControllerId = id; }
}
