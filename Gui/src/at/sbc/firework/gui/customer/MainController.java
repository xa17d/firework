package at.sbc.firework.gui.customer;

import at.sbc.firework.service.IService;
import javafx.fxml.FXML;
/**
 * Created by daniel on 20.12.2014.
 */
public class MainController {
    private IService service;

    /**
     * called on initializing the controller by fx
     */
    @FXML
    private void initialize() {

    }

    public void setService(IService service) {
        this.service = service;
    }
}
