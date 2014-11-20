package at.sbc.firework.entities;

/**
 * Created by Lucas on 18.11.2014.
 */
public enum EnumParts {

    CASING(0, "Casing"), EFFECT_CHARGE(1, "Effect Charge"), PROPELLING_CHARGE(2, "Propelling Charge"), STICK(3, "Stick");

    private int id;
    private String name;

    private EnumParts(int id, String name) {

        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public int getId() {
        return this.id;
    }

    public static EnumParts getById(int id) {

        for(EnumParts p : EnumParts.values()) {
            if(p.getId() == id)
                return p;
        }

        throw new NullPointerException("element not existing");
    }

    public static int size() {

        int i = 0;
        for(EnumParts p : EnumParts.values()) {
            i++;
        }
        return i;
    }
}
