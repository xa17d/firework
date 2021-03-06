package at.sbc.firework.gui.customer;

import at.sbc.firework.Customer;
import at.sbc.firework.OrderManager;
import at.sbc.firework.Supplier;
import at.sbc.firework.entities.*;
import at.sbc.firework.service.*;
import at.sbc.firework.utils.NotificationMode;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by daniel on 20.12.2014.
 */
public class Controller implements INotification {

    private Customer customer;
    private String factoryUrl;

    private IFactoryService factoryService;
    private ICustomerService customerService;

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
    private ListView<String> lvTrace;
    private ObservableList<String> observableOrderList;

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

        observableOrderList = new ObservableListWrapper<String>(new ArrayList<String>());
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
        }

        try {
            customer.order(amount, colors);
        }
        catch (ServiceException e) {
            e.printStackTrace();
        }
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        this.factoryService = customer.getFactoryService();
        this.customerService = customer.getCustomerService();

        try {
            tfAddress.setEditable(false);
            tfAddress.setText(customerService.getAddress());
            factoryService.registerNotification(this, "*", ContainerOperation.All, NotificationMode.Permanent);
        } catch (ServiceException e) {
            e.printStackTrace();
        }

        dataChanged();
    }

    public void setFactoryUrl(String url) {
        this.factoryUrl = url;
        tfFactoryUrl.setText(factoryUrl);
    }

    @Override
    public void dataChanged() {

        ArrayList<Order> orderList = null;
        try {
            orderList = factoryService.listOrders();
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
            for(Order order : orderList) {
                String orderString = order.toString();

                if(order.getStatus() == OrderStatus.InProgress) {
                    try {
                        orderString += " - " + factoryService.getOrderRocketCount(order.getId()) + "/" + order.getCount();
                    } catch (ServiceException e) {
                        e.printStackTrace();
                    }
                }

                observableOrderList.add(orderString);
            }
        }
    }

    @FXML
    private void showOrder() {

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("order_frame.fxml"));

            Stage orderStage = new Stage();
            orderStage.setScene(new Scene((AnchorPane) loader.load()));
            OrderController controller = (OrderController) loader.getController();
            controller.setCustomer(customer);
            orderStage.initModality(Modality.APPLICATION_MODAL);
            orderStage.show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
