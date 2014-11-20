package at.sbc.firework.entities;

/**
 * Created by daniel on 14.11.2014.
 */
public class PropellingChargePackage extends Part {

    private static final long serialVersionUID = 5L;

    private int content;

    public PropellingChargePackage(long supplierId, long id, int content)
    {
        super(supplierId, id);

        this.content = content;
    }

    public int getContent() { return content; }

    public boolean isEmpty() {
        return (content == 0);
    }

    public PropellingCharge takeOut(int amount)
    {
        amount = Math.min(amount, content);

        content -= amount;

        return new PropellingCharge(this, amount);
    }

    @Override
    public String toString() {
        return "PropellingChargePackage (" + getId() + ", content: " + getContent() + ")";
    }
}
