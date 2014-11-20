package at.sbc.firework.gui;

import at.sbc.firework.Supplier;
import at.sbc.firework.entities.*;
import at.sbc.firework.service.IService;
import at.sbc.firework.service.IServiceTransaction;
import at.sbc.firework.service.ServiceException;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Lucas on 17.11.2014.
 */
public class MainController {

    private IService service;

    @FXML
    private ChoiceBox<EnumParts> cbSupplier;
    private ObservableList<EnumParts> supplierTypes;

    @FXML
    private ListView<String> lvTrace;
    private ObservableList<String> traceList;
    private ArrayList<String> trace;

    @FXML
    private TextField tfAmount;

    /**
     * called on initializing the controller by fx
     */
    @FXML
    private void initialize() {

        trace = new ArrayList<String>();
        trace.add("Start Up Fireworks-App");

        supplierTypes = new ObservableListBase<EnumParts>() {
            @Override
            public EnumParts get(int index) {
                return EnumParts.getById(index);
            }

            @Override
            public int size() {
                return EnumParts.size();
            }
        };
        cbSupplier.setItems(supplierTypes);
        cbSupplier.setValue(EnumParts.getById(0));

        traceList = new ObservableListWrapper<String>(trace);
        lvTrace.setItems(traceList);
    }

    /**
     * called on "Create Supplier"-Button click
     */
    @FXML
    private void createSupplier() {

        EnumParts selectedItem = cbSupplier.getValue();

        int amount = 0;
        try {
            amount = Integer.parseInt(tfAmount.getText());

            Thread thread = new Thread(new Supplier(service, selectedItem, amount));
            thread.start();

            traceList.add("added new part: " + amount + "x  " + selectedItem);
        }
        catch (NumberFormatException e) {
            traceList.add("please type in a correct number for amount");
        }
    }

    public void setService(IService service) {
        this.service = service;
    }

    public void updateLayout() {
        //TODO
    }
}