package at.sbc.firework.service;

/**
 * Created by daniel on 14.11.2014.
 */
public abstract class ServiceException extends Exception {
    public ServiceException(Exception innerException)
    {
        super(innerException);
    }
}
