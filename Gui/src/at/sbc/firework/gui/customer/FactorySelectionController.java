package at.sbc.firework.gui.customer;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TextField;

/**
 * Created by daniel on 20.12.2014.
 */
public class FactorySelectionController {

    private Controller controller;

    @FXML
    private TextField tfFactoryUrl;

    /**
     * called on initializing the controller by fx
     */
    @FXML
    private void initialize() {

    }

    @FXML
    private void saveFactoryUrl() {

        controller.setFactoryUrl(tfFactoryUrl.getText());

        Scene scene = tfFactoryUrl.getScene();
        if(scene != null)
            scene.getWindow().hide();
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }
}
