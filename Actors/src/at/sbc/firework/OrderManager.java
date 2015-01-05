package at.sbc.firework;

import at.sbc.firework.actors.Actor;

/**
 * Created by daniel on 20.12.2014.
 */
public class OrderManager extends Actor {
    public static void main(String[] args) {

        OrderManager om = new OrderManager(args);

        om.workLoop();

        om.dispose();

        System.exit(0);
    }

    public OrderManager(String[] args) {
        super("OrderManager", args);
    }

    @Override
    public void work()
    {
        // TODO:
        // Prüfen ob order mit status new vorhanden ist
        // OrderPositions erzeugen und Status auf inProgress setzen
        // Wenn alle Raketen erzeugt sind -> ausleifern, status auf done/couldNotDeliver setzen
    }
}
