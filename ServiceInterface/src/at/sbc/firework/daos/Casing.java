package at.sbc.firework.daos;

/**
 * Created by daniel on 14.11.2014.
 */
public class Casing extends Part {

    private static final long serialVersionUID = 3L;

    public Casing(long id)
    {
        super(id);
    };

    @Override
    public String toString() {
        return "Casing (" + getId() + ")";
    }
}
