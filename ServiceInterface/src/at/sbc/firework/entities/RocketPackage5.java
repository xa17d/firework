package at.sbc.firework.entities;

import java.io.Serializable;

/**
 * Created by daniel on 17.11.2014.
 */
public class RocketPackage5 implements Serializable {

    private static final long serialVersionUID = 7L;

    private long logisticianId;
    private long id;
    private Rocket[] content;

    public RocketPackage5(long logisticianId, long id, Rocket[] content) {
        if (content.length != 5) {
            throw new IllegalArgumentException("content must be exactly 5 rockets");
        }

        this.logisticianId = logisticianId;
        this.id = id;
        this.content = content;
    }

    public Rocket[] getContent() { return content; }
    public long getId() { return id; }

    /*
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("RocketPackage (" +  getId() + " | Logistican: " + logisticianId + ")");

        int i = 0;
        for(Rocket r : getContent()) {
            i++;
            sb.append("\nRocket " + i + " :");
            sb.append(r.toString());
        }

        return sb.toString();
    }
    */
    @Override
    public String toString() {
        return "RocketPackage (" +  getId() + " | Logistican: " + logisticianId + ")";
    }
}
