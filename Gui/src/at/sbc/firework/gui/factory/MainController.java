package at.sbc.firework.gui.factory;

import at.sbc.firework.Supplier;
import at.sbc.firework.entities.*;
import at.sbc.firework.service.IFactoryService;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TextField;

import java.util.ArrayList;

/**
 * Created by Lucas on 17.11.2014.
 */
public class MainController {

    private IFactoryService service;

    @FXML
    private ChoiceBox<EnumParts> cbSupplier;
    private ObservableList<EnumParts> supplierTypes;

    @FXML
    private ListView<String> lvTrace;
    private ObservableList<String> traceList;
    private ArrayList<String> trace;

    @FXML
    private TextField tfAmount;

    @FXML
    private TextField tfErrorRate;

    @FXML
    private ChoiceBox<Color> cbColor;
    private ObservableList<Color> colorTypes;

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
        cbSupplier.setValue(EnumParts.CASING);

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
        cbColor.setItems(colorTypes);
        cbColor.setValue(Color.Blue);

        traceList = new ObservableListWrapper<String>(trace);
        lvTrace.setItems(traceList);
    }

    /**
     * called on "Create Supplier"-Button click
     */
    @FXML
    private void createSupplier() {

        EnumParts selectedPart = cbSupplier.getValue();
        Color selectedColor = cbColor.getValue();

        int amount = 0;
        double errorRate = 0;
        try {
            amount = Integer.parseInt(tfAmount.getText());
            errorRate = Double.parseDouble(tfErrorRate.getText());

            Thread thread = new Thread(new Supplier(service, selectedPart, amount, errorRate, selectedColor));
            thread.start();

            traceList.add("added new part: " + amount + "x  " + selectedPart);
        }
        catch (NumberFormatException e) {
            traceList.add("please type in a correct number for:\n * amount (whole number)\n * error rate (0-100%)");
        }
    }

    public void setService(IFactoryService service) {
        this.service = service;
    }
}