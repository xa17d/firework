package at.sbc.firework.entities;

/**
 * d Farb vo na EffectCharge
 */
public enum Color {
    Red(0, "Red"), Green(1, "Green"), Blue(2, "Blue");

    private int id;
    private String name;

    private Color(int id, String name) {

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

    public static Color getById(int id) {

        for(Color c : Color.values()) {
            if(c.getId() == id)
                return c;
        }

        throw new NullPointerException("element not existing");
    }

    public static int size() { return Color.values().length; }
}
