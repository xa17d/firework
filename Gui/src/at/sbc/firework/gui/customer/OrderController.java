package at.sbc.firework.gui.customer;

import at.sbc.firework.Customer;
import at.sbc.firework.entities.Part;
import at.sbc.firework.entities.Rocket;
import at.sbc.firework.service.*;
import at.sbc.firework.utils.NotificationMode;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.util.ArrayList;

/**
 * Created by Lucas on 09.01.2015.
 */
public class OrderController implements INotification {

    private Customer customer;
    private IFactoryService factoryService;
    private ICustomerService customerService;

    @FXML
    private ListView<Rocket> lvOrder;
    private ObservableList<Rocket> observedOrderList;

    /**
     * called on initializing the controller by fx
     */
    @FXML
    private void initialize() {

        observedOrderList = new ObservableListWrapper<Rocket>(new ArrayList<Rocket>());
        lvOrder.setItems(observedOrderList);
    }

    public void setCustomer(Customer customer) {

        try {
            this.customer = customer;
            this.factoryService = customer.getFactoryService();
            this.customerService = customer.getCustomerService();

            customerService.registerNotification(this, "*", ContainerOperation.All, NotificationMode.Permanent);
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        dataChanged();
    }

    @Override
    public void dataChanged() {

        ArrayList<Rocket> list = null;
        try {
            list = customerService.listRockets();
        } catch (ServiceException e) {
            e.printStackTrace();
        }

        Platform.runLater(new UpdateTableRunnable(list));
    }

    private class UpdateTableRunnable implements Runnable {

        private ArrayList<Rocket> list;

        public UpdateTableRunnable(ArrayList<Rocket> list) {
            this.list = list;
        }

        @Override
        public void run() {
            observedOrderList.clear();
            observedOrderList.addAll(list);
        }
    }
}
