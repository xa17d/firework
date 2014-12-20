package at.sbc.firework;

import at.sbc.firework.actors.Utils;
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
    private double errorRate;

    public Supplier(IService service, EnumParts selectedItem, int amount, double errorRate) {

        this.service = service;
        this.selectedItem = selectedItem;
        this.amount = amount;

        if(errorRate < 0)
            this.errorRate = 0;
        else if(errorRate > 100)
            this.errorRate = 100;
        else
            this.errorRate = errorRate;
    }

    @Override
    public void run() {

        try {
            long supplierId = service.getNewId();

            if(selectedItem == EnumParts.CASING)
                for(int i = 0; i < amount; i++)
                    addToStock(new Casing(supplierId, service.getNewId()));
            if(selectedItem == EnumParts.EFFECT_CHARGE)
                for(int i = 0; i < amount; i++)
                    // TODO: d Farb vom User uswähla lo
                    addToStock(new EffectCharge(supplierId, service.getNewId(), Math.random() < (errorRate / 100.0), Color.Blue));
            if(selectedItem == EnumParts.PROPELLING_CHARGE)
                for(int i = 0; i < amount; i++)
                    addToStock(new PropellingChargePackage(supplierId, service.getNewId(), 500));
            if(selectedItem == EnumParts.STICK)
                for(int i = 0; i < amount; i++)
                    addToStock(new Stick(supplierId, service.getNewId()));

        }
        catch (ServiceException e) {
            e.printStackTrace();
        }
    }

    private void addToStock(Part part) throws ServiceException {
        IServiceTransaction t = service.startTransaction();
        t.addToStock(part);
        Utils.sleep(1000, 2000);
        t.commit();
    }
}
