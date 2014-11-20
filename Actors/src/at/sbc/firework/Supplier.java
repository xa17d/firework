package at.sbc.firework;

import at.sbc.firework.entities.*;
import at.sbc.firework.service.IService;
import at.sbc.firework.service.IServiceTransaction;
import at.sbc.firework.service.ServiceException;

/**
 * Created by daniel on 17.11.2014.
 */
public class Supplier implements Runnable {

    private IService service;
    private EnumParts selectedItem;
    private int amount;

    public Supplier(IService service, EnumParts selectedItem, int amount) {

        this.service = service;
        this.selectedItem = selectedItem;
        this.amount = amount;
    }

    @Override
    public void run() {

        try {
            long supplierId = service.getNewId();

            IServiceTransaction t = service.startTransaction();

            if(selectedItem == EnumParts.CASING)
                for(int i = 0; i < amount; i++)
                    t.addToStock(new Casing(supplierId, service.getNewId()));
            if(selectedItem == EnumParts.EFFECT_CHARGE)
                for(int i = 0; i < amount; i++)
                    t.addToStock(new EffectCharge(supplierId, service.getNewId(), Math.random() < 0.25 ? true : false));
            if(selectedItem == EnumParts.PROPELLING_CHARGE)
                for(int i = 0; i < amount; i++)
                    t.addToStock(new PropellingChargePackage(supplierId, service.getNewId(), 500));
            if(selectedItem == EnumParts.STICK)
                for(int i = 0; i < amount; i++)
                    t.addToStock(new Stick(supplierId, service.getNewId()));

            long sleepTime = Math.round(Math.random() * 1000 * 15) + 5000;     //TODO change to 1-2sec delay (random * 1000 + 1)
            try {
                Thread.sleep(sleepTime);
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }

            t.commit();

        }
        catch (ServiceException e) {
            e.printStackTrace();
        }
    }
}
