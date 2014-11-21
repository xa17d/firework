package at.sbc.firework.serveralt.containers;

/**
 * Created by daniel on 21.11.2014.
 */
public class ItemSelectorFirst extends ItemSelector {
    @Override
    public boolean checkItem(Object item) {
        setResult(item);
        return false;
    }
}
