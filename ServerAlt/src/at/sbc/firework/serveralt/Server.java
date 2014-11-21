package at.sbc.firework.serveralt;

import at.sbc.firework.serveralt.containers.Container;

/**
 * Created by daniel on 21.11.2014.
 */
public class Server {

    private Container stockContainer = new Container();
    private Container qualityCheckQueueContainer = new Container();
    private Container packingQueueContainer = new Container();
    private Container garbageContainer = new Container();
    private Container distributionStockContainer = new Container();

    public Container getStockContainer() { return stockContainer; }
    public Container getQualityCheckQueueContainer() { return qualityCheckQueueContainer; }
    public Container getPackingQueueContainer() { return packingQueueContainer; }
    public Container getGarbageContainer() { return garbageContainer; }
    public Container getDistributionStockContainer() { return distributionStockContainer; }

    private long idCounter = 0;
    public synchronized long getNewId() {
        return idCounter++;
    }
}
