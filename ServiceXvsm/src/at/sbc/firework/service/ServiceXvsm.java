package at.sbc.firework.service;

import at.sbc.firework.service.IService;

/**
 * Created by daniel on 14.11.2014.
 */
public class ServiceXvsm implements IService {

    @Override
    public void Start() {
        System.out.println("Starting XVSM...");
    }

    @Override
    public void Stop() {

    }
}
