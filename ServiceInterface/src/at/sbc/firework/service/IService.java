package at.sbc.firework.service;

/**
 * Created by daniel on 14.11.2014.
 */
public interface IService {
    void Start() throws ServiceException;
    void Stop() throws ServiceException;
}
