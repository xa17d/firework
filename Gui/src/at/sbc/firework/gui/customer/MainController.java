package at.sbc.firework.gui.customer;

import at.sbc.firework.Customer;
import at.sbc.firework.OrderManager;
import at.sbc.firework.Supplier;
import at.sbc.firework.entities.Color;
import at.sbc.firework.entities.Order;
import at.sbc.firework.service.IFactoryService;
import at.sbc.firework.service.ServiceException;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

/**
 * Created by daniel on 20.12.2014.
 */
public class MainController {

    private Customer customer;

    @FXML
    private Button btCreateOrder;

    @FXML
    private TextField tfAmount;

    @FXML
    private TextField tfAddress;

    private ObservableList<Color> colorTypes;
    @FXML
    private ChoiceBox<Color> cbColor1;
    @FXML
    private ChoiceBox<Color> cbColor2;
    @FXML
    private ChoiceBox<Color> cbColor3;

    /**
     * called on initializing the controller by fx
     */
    @FXML
    private void initialize() {

        colorTypes = new ObservableListBase<Color>() {
            @Override
            public Color get(int index) {
                return Color.getById(index);
            }

            @Override
            public int size() {
                return Color.size();
            }
        };
        cbColor1.setItems(colorTypes);
        cbColor1.setValue(Color.Blue);

        cbColor2.setItems(colorTypes);
        cbColor2.setValue(Color.Green);

        cbColor3.setItems(colorTypes);
        cbColor3.setValue(Color.Red);

    }

    @FXML
    private void createOrder() {

        Color[] colors = new Color[3];
        colors[0] = cbColor1.getValue();
        colors[1] = cbColor2.getValue();
        colors[2] = cbColor3.getValue();

        String address = tfAddress.getText();

        int amount = 0;
        try {
            amount = Integer.parseInt(tfAmount.getText());
        }
        catch (NumberFormatException e) {
            e.printStackTrace();
            //TODO haendln
        }


        try {
            customer.order(amount, colors);
            //TODO imma neua thread???
        }
        catch (ServiceException e) {
            e.printStackTrace();
            //TODO haendln
        }

        btCreateOrder.setDisable(true);
        btCreateOrder.setText("on order");

        //TODO set on "received"
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
