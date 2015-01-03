package at.sbc.firework.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by daniel on 17.11.2014.
 */
public class Rocket implements Serializable {

    private static final long serialVersionUID = 6L;

    private long manufacturerId;
    private long qualityControllerId = -1;
    private Quality quality = Quality.Undefined;

    private long id;
    private Stick stick;
    private Casing casing;
    private EffectCharge[]  effectCharges;
    private PropellingCharge[] propellingCharge;
    private OrderPosition orderPosition;

    // damit i des mit nam Query coordinator direkt abfroga kann
    private long orderId = -1;

    public Rocket(long manufacturerId, long id, OrderPosition orderPosition, Stick stick, Casing casing, EffectCharge[] effectCharges, PropellingCharge[] propellingCharge)
    {
        if (effectCharges.length != 3) {
            throw new IllegalArgumentException("there must be exactly 3 effect charges in a rocket");
        }

        this.manufacturerId = manufacturerId;
        this.id = id;
        this.orderPosition = orderPosition;
        this.stick = stick;
        this.casing = casing;
        this.effectCharges = effectCharges;
        this.propellingCharge = propellingCharge;

        if (orderPosition != null) { this.orderId = orderPosition.getOrderId(); }
    }

    public int getPropellingChargeAmount() {
        return Rocket.getPropellingChargeAmount(propellingCharge);
    }

    public static int getPropellingChargeAmount(PropellingCharge[] propellingChargeList) {
        int amount = 0;
        for (PropellingCharge c : propellingChargeList) {
            amount += c.getAmount();
        }
        return  amount;
    }

    public static int getPropellingChargeAmount(ArrayList<PropellingCharge> propellingChargeList) {
        int amount = 0;
        for (PropellingCharge c : propellingChargeList) {
            amount += c.getAmount();
        }
        return  amount;
    }

    public long getId() { return id; }
    public Stick getStick() { return stick; }
    public Casing getCasing() { return casing; }
    public EffectCharge[] getEffectCharges() { return effectCharges; }
    public PropellingCharge[] getPropellingCharge() { return propellingCharge; }

    public long getManufacturerId() { return manufacturerId; }
    public OrderPosition getOrderPosition() { return orderPosition; }

    public void setQuality(long qualityControllerId, Quality quality) {
        this.quality = quality;
        this.qualityControllerId = qualityControllerId;
    }
    public Quality getQuality() { return quality; }
    public long getQualityControllerId() { return qualityControllerId; }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append("Rocket (" + getId() + " | Manufacturer: " + getManufacturerId());
        sb.append((getQualityControllerId() == -1 ? "" : " | QualityController: " + getQualityControllerId()+ " -> " + getQuality().toString()) + ")");
        sb.append("\n * " + getCasing());

        sb.append("\n * ");
        for(EffectCharge e : getEffectCharges())
            sb.append(e + " | ");
        sb.delete(sb.length() - 2, sb.length());

        sb.append("\n * PropellingCharges: (");
        for(PropellingCharge p : getPropellingCharge())
            sb.append(p.getPackage().getId() + " | Amount: " + p.getAmount() + "g) | ");
        sb.delete(sb.length() - 2, sb.length());

        sb.append("\n * " + getStick());
        return sb.toString();
    }
}
