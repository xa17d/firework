package at.sbc.firework.gui.customer;

import at.sbc.firework.Customer;
import at.sbc.firework.OrderManager;
import at.sbc.firework.Supplier;
import at.sbc.firework.entities.Color;
import at.sbc.firework.entities.Order;
import at.sbc.firework.entities.Part;
import at.sbc.firework.service.ContainerOperation;
import at.sbc.firework.service.IFactoryService;
import at.sbc.firework.service.INotification;
import at.sbc.firework.service.ServiceException;
import at.sbc.firework.utils.NotificationMode;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by daniel on 20.12.2014.
 */
public class MainController implements INotification {

    private Customer customer;
    private String factoryUrl;

    private IFactoryService service;

    @FXML
    private TextField tfFactoryUrl;

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

    @FXML
    private ListView<Order> lvTrace;
    private ObservableList<Order> observableOrderList;

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

        observableOrderList = new ObservableListWrapper<Order>(new ArrayList<Order>());
        lvTrace.setItems(observableOrderList);
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
        }
        catch (ServiceException e) {
            e.printStackTrace();
            //TODO haendln
        }
    }

    public void setCustomer(Customer customer) { this.customer = customer; }
    public void setFactoryUrl(String url) {
        this.factoryUrl = url;
        tfFactoryUrl.setText(factoryUrl);
    }

    public void setService(IFactoryService service) {

        this.service = service;
        //TODO register notification
        /*
        try {
            service.registerNotification(this, "*", ContainerOperation.All, NotificationMode.Permanent);
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        */
        dataChanged();
    }

    @Override
    public void dataChanged() {

        ArrayList<Order> orderList = null;
        try {
            orderList = service.listOrders();
        } catch (ServiceException e) {
            e.printStackTrace();
        }

        Platform.runLater(new UpdateListRunnable(orderList));
    }

    private class UpdateListRunnable implements Runnable {

        private ArrayList<Order> orderList;

        public UpdateListRunnable(ArrayList<Order> orderList) {
            this.orderList = orderList;
        }

        @Override
        public void run() {

            observableOrderList.clear();
            observableOrderList.addAll(orderList);
        }
    }
}
