package at.sbc.firework.daos;

import java.io.Serializable;

/**
 * Created by daniel on 14.11.2014.
 */
public class PropellingCharge implements Serializable {

    private static final long serialVersionUID = 6L;

    private int amount;
    private PropellingChargePackage sourcePackage;

    public PropellingCharge(PropellingChargePackage sourcePackage, int content)
    {
        this.sourcePackage = sourcePackage;
        this.amount = content;
    }

    public int getAmount() { return amount; }
    public PropellingChargePackage getPackage() { return sourcePackage; }
}
