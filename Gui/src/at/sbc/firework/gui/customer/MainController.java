package at.sbc.firework.gui.customer;

import at.sbc.firework.Customer;
import at.sbc.firework.service.IFactoryService;
import javafx.fxml.FXML;
/**
 * Created by daniel on 20.12.2014.
 */
public class MainController {
    private Customer customer;

    /**
     * called on initializing the controller by fx
     */
    @FXML
    private void initialize() {

    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
