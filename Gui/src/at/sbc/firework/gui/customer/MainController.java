package at.sbc.firework.gui.customer;

import at.sbc.firework.service.IFactoryService;
import javafx.fxml.FXML;
/**
 * Created by daniel on 20.12.2014.
 */
public class MainController {
    private IFactoryService service;

    /**
     * called on initializing the controller by fx
     */
    @FXML
    private void initialize() {

    }

    public void setService(IFactoryService service) {
        this.service = service;
    }
}
