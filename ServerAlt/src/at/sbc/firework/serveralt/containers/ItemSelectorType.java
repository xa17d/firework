package at.sbc.firework.serveralt.containers;

/**
 * Created by daniel on 21.11.2014.
 */
public class ItemSelectorType extends ItemSelector {

    public ItemSelectorType(Class<?> type) {
        this.type = type;
    }

    private Class<?> type;

    @Override
    public boolean checkItem(Object item) {
        if (type.isInstance(item))
        {
            setResult(item);
            return false;
        }
        else
        { return true; }
    }
}
