package at.sbc.firework.gui.customer;

import at.sbc.firework.service.ServiceFactory;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TextField;

/**
 * Created by daniel on 20.12.2014.
 */
public class FactorySelectionController {

    private Controller controller;
    private Main main;

    @FXML
    private TextField tfFactoryUrl;

    /**
     * called on initializing the controller by fx
     */
    @FXML
    private void initialize() {
        tfFactoryUrl.setText(ServiceFactory.getDefaultFactoryAddress());
    }

    @FXML
    private void saveFactoryUrl() {

        controller.setFactoryUrl(tfFactoryUrl.getText());

        Scene scene = tfFactoryUrl.getScene();
        if(scene != null)
            scene.getWindow().hide();

        main.createCustomer(tfFactoryUrl.getText());
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }
    public void setMain(Main main) {
        this.main = main;
    }
}
