package at.sbc.firework.service.alt;

import java.rmi.RemoteException;

/**
 * An Pinger pingt in nam extra Thread jede Sekund der Service ufm Serva a, damit er woas dass der Client no leabt.
 */
public class Pinger extends Thread {

    public Pinger(IServiceRmi serviceRmi) {

        this.serviceRmi = serviceRmi;
    }

    public void run() {
        while (true) {
            try {
                serviceRmi.ping();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) { }
        }
    }

    private IServiceRmi serviceRmi;
}
