package at.sbc.firework.service;

import at.sbc.firework.utils.Notification;

/**
 * Created by daniel on 23.12.2014.
 */
public class NotAvailableException extends ServiceException {
    public NotAvailableException(String containerId, Exception innerException) {
        super("Requested Entry not available in Container \""+containerId+"\"");

        this.containerId = containerId;
    }

    private String containerId;

    public String getContainerId() { return containerId; }
}
