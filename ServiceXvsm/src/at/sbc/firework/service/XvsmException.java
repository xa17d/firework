package at.sbc.firework.service;

import org.mozartspaces.core.MzsCoreException;

/**
 * Created by daniel on 14.11.2014.
 */
public class XvsmException extends ServiceException {
    public XvsmException(MzsCoreException innerException)
    {
        super(innerException);
    }
}
