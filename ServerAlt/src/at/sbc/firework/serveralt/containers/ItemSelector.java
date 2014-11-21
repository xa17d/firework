package at.sbc.firework.serveralt.containers;

/**
 * Created by daniel on 21.11.2014.
 */
public abstract class ItemSelector {

    private Object result;

    public Object getResult() { return result; }
    public void setResult(Object item) { result = item; }

    public abstract boolean checkItem(Object item);

}
