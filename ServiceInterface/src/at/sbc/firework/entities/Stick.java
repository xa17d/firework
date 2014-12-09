package at.sbc.firework.entities;

/**
 * Created by daniel on 14.11.2014.
 */
public class Stick extends Part {

    private static final long serialVersionUID = 2L;

    public Stick(long supplierId, long id)
    {
        super(supplierId, id);
    }

    @Override
    public String toString() {
        return "Stick (" + getId() + " | Supplier: " + getSupplierId() + ")";
    }
}