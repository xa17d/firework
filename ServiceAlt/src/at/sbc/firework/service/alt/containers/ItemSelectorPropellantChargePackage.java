package at.sbc.firework.service.alt.containers;

import at.sbc.firework.entities.PropellingChargePackage;

/**
 * Created by daniel on 21.11.2014.
 */
public class ItemSelectorPropellantChargePackage extends ItemSelector {

    PropellingChargePackage packageWithMinimumContent = null;

    @Override
    public boolean checkItem(Object item) {

        if (PropellingChargePackage.class.isInstance(item)) {
            PropellingChargePackage p = (PropellingChargePackage) item;

            if ((packageWithMinimumContent == null) || (p.getContent() < packageWithMinimumContent.getContent())) {
                packageWithMinimumContent = p;
                setResult(p);
            }
        }

        return true;
    }
}