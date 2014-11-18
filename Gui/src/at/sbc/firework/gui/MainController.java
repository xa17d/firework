package at.sbc.firework.gui;

import at.sbc.firework.daos.*;
import at.sbc.firework.service.IService;
import at.sbc.firework.service.IServiceTransaction;
import at.sbc.firework.service.ServiceException;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.TransformationList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TextField;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

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
        }
        catch (NumberFormatException e) {
            trace.add(e.getMessage());
        }

        try {
            long supplierId = service.getNewId();

            IServiceTransaction t = service.startTransaction();

            Part part = null;
            if(selectedItem == EnumParts.CASING)
                part = new Casing(supplierId, amount);
            if(selectedItem == EnumParts.EFFECT_CHARGE)
                part = new EffectCharge(supplierId, amount, Math.random() < 0.25 ? true : false);
            if(selectedItem == EnumParts.STICK)
                part = new Stick(supplierId, amount);

            if(part != null) {
                t.addToStock(part);
                t.commit();
                trace.add("added new part: " + part.toString() + " || amount: " + amount);
            }
            else {
                t.rollback();
                trace.add("no item selected - transaction rollback");
            }
        }
        catch (ServiceException e) {
            trace.add(e.getMessage());
        }
    }

    public void setService(IService service) {
        this.service = service;
    }

    public void updateLayout() {
        //TODO
    }
}