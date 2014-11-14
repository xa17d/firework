package at.sbc.firework.daos;

/**
 * Created by daniel on 14.11.2014.
 */
public class Stick extends Part {

    private static final long serialVersionUID = 2L;

    public Stick(long id)
    {
        super(id);
    }

    @Override
    public String toString() {
        return "Stick (" + getId() + ")";
    }
}