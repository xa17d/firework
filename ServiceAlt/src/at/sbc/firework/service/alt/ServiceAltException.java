package at.sbc.firework.service.alt;

import at.sbc.firework.service.ServiceException;

/**
 * Created by daniel on 21.11.2014.
 */
public class ServiceAltException extends ServiceException {
    public ServiceAltException(Exception innerException) {
        super(innerException);
    }

    public ServiceAltException(String message) {
        super(new Exception(message));
    }
}
