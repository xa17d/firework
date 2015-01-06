package at.sbc.firework.gui.customer;

import at.sbc.firework.Customer;
import at.sbc.firework.entities.Color;
import at.sbc.firework.service.ServiceException;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

/**
 * Created by daniel on 20.12.2014.
 */
public class FactorySelectionController {

    private MainController mainController;

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

        mainController.setFactoryUrl(tfFactoryUrl.getText());

        Scene scene = tfFactoryUrl.getScene();
        if(scene != null)
            scene.getWindow().hide();
    }

    public void setMainController(MainController controller) {
        this.mainController = controller;
    }
}
