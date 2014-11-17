package at.sbc.firework.daos;

import java.io.Serializable;

/**
 * Created by daniel on 14.11.2014.
 */
public abstract class Part implements Serializable {

    private static final long serialVersionUID = 1L;

    public Part(long supplierId, long id) {
        this.supplierId = supplierId;
        this.id = id;
    }

    private long id;
    private long supplierId;

    public long getId() { return id; }
    public long getSupplierId() { return supplierId; }
}
