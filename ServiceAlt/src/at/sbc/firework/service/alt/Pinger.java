package at.sbc.firework.service.alt;

import java.rmi.RemoteException;

/**
 * Created by daniel on 28.11.2014.
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
