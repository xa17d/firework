package at.sbc.firework.gui.factory;

import at.sbc.firework.Supplier;
import at.sbc.firework.entities.*;
import at.sbc.firework.service.ContainerOperation;
import at.sbc.firework.service.INotification;
import at.sbc.firework.service.IFactoryService;
import at.sbc.firework.service.ServiceException;
import at.sbc.firework.utils.NotificationMode;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.util.ArrayList;

/**
 * Created by Lucas on 17.11.2014.
 */
public class Controller implements INotification {

    private IFactoryService service;

    /*
     * MAIN
     */
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

    /*
     * TABLE
     */
    @FXML
    private ListView<Part> lvStock;
    private ObservableList<Part> observedStockList;
    @FXML
    private ListView<Rocket> lvProduced;
    private ObservableList<Rocket> observedProducedList;
    @FXML
    private ListView<RocketPackage5> lvShipped;
    private ObservableList<RocketPackage5> observedShippedList;
    @FXML
    private ListView<Rocket> lvDisposed;
    private ObservableList<Rocket> observedDisposedList;
    @FXML
    private ListView<Rocket> lvDelivered;
    private ObservableList<Rocket> observedDeliveredList;

    @FXML
    private Label lbCasingAmount;
    @FXML
    private Label lbECBlue;
    @FXML
    private Label lbECGreen;
    @FXML
    private Label lbECRed;
    @FXML
    private Label lbPropellingChargeAmount;
    @FXML
    private Label lbStickAmount;

    @FXML
    private Label lbShippedAmount;
    @FXML
    private Label lbDisposedAmount;
    @FXML
    private Label lbOrderedAmount;
    @FXML
    private Label lbDeliveredAmount;

    /**
     * called on initializing the controller by fx
     */
    @FXML
    private void initialize() {
        //do nothing
    }

    public void initializeLayout() {

        /*
         * MAIN
         */
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

        /*
         * TABLE
         */
        observedStockList = new ObservableListWrapper<Part>(new ArrayList<Part>());
        lvStock.setItems(observedStockList);

        observedProducedList = new ObservableListWrapper<Rocket>(new ArrayList<Rocket>());
        lvProduced.setItems(observedProducedList);

        observedShippedList = new ObservableListWrapper<RocketPackage5>(new ArrayList<RocketPackage5>());
        lvShipped.setItems(observedShippedList);

        observedDisposedList = new ObservableListWrapper<Rocket>(new ArrayList<Rocket>());
        lvDisposed.setItems(observedDisposedList);

        observedDeliveredList = new ObservableListWrapper<Rocket>(new ArrayList<Rocket>());
        lvDelivered.setItems(observedDeliveredList);

        cbSupplier.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                supplierChanged(newValue.intValue());
            }
        });

        supplierChanged(0);
    }

    public void setService(IFactoryService service) {

        this.service = service;
        try {
            service.registerNotification(this, "*", ContainerOperation.All, NotificationMode.Permanent);
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        dataChanged();
    }

    @Override
    public void dataChanged() {

        ArrayList<Part> stock = null;
        ArrayList<Rocket> packingQueue = null;
        ArrayList<Rocket> qualityCheckQueue = null;
        ArrayList<RocketPackage5> distributionPackages = null;
        ArrayList<Rocket> garbage = null;
        ArrayList<Rocket> orderRocketsDone = new ArrayList<Rocket>();
        int amountOrderedRockets = 0;

        try {
            stock = service.listStock();
            packingQueue = service.listPackingQueue();
            qualityCheckQueue = service.listQualityCheckQueue();
            distributionPackages = service.listDistributionStock();
            garbage = service.listGarbage();

            for(Order order : service.listOrders()) {
                amountOrderedRockets += order.getCount();
                if(order.getStatus() == OrderStatus.Done)
                    orderRocketsDone.addAll(service.listOrderRockets(order.getId()));
            }

        } catch (ServiceException e) {
            e.printStackTrace();
        }

        Platform.runLater(new UpdateTableRunnable(stock, packingQueue, qualityCheckQueue, distributionPackages, garbage, amountOrderedRockets, orderRocketsDone));
    }

    private class UpdateTableRunnable implements Runnable {

        private ArrayList<Part> stock;
        private ArrayList<Rocket> packingQueue;
        private ArrayList<Rocket> qualityCheckQueue;
        private ArrayList<RocketPackage5> distributionPackages;
        private ArrayList<Rocket> garbage;
        private int amountOrderedRockets;
        private ArrayList<Rocket> orderRockets;

        public UpdateTableRunnable(ArrayList<Part> stock, ArrayList<Rocket> packingQueue, ArrayList<Rocket> qualityCheckQueue, ArrayList<RocketPackage5> distributionPackages, ArrayList<Rocket> garbage, int amountOrderedRockets, ArrayList<Rocket> orderRockets) {
            this.stock = stock;
            this.packingQueue = packingQueue;
            this.qualityCheckQueue = qualityCheckQueue;
            this.distributionPackages = distributionPackages;
            this.garbage = garbage;
            this.amountOrderedRockets = amountOrderedRockets;
            this.orderRockets = orderRockets;
        }

        @Override
        public void run() {

            int casingCount = 0;
            int effectCountBlue = 0;
            int effectCountGreen = 0;
            int effectCountRed = 0;
            int propellingCount = 0;
            int stickCount = 0;

            for (Part p : stock) {

                if (p instanceof Casing)
                    casingCount++;

                if (p instanceof EffectCharge) {
                    EffectCharge charge = (EffectCharge) p;
                    Color color = charge.getColor();

                    if(color == Color.Blue)
                        effectCountBlue++;
                    else if(color == Color.Green)
                        effectCountGreen++;
                    else
                        effectCountRed++;
                }

                if (p instanceof PropellingChargePackage)
                    propellingCount++;

                if (p instanceof Stick)
                    stickCount++;
            }

            lbCasingAmount.setText(String.valueOf(casingCount));
            lbECBlue.setText(String.valueOf(effectCountBlue));
            lbECGreen.setText(String.valueOf(effectCountGreen));
            lbECRed.setText(String.valueOf(effectCountRed));
            lbPropellingChargeAmount.setText(String.valueOf(propellingCount));
            lbStickAmount.setText(String.valueOf(stickCount));


            observedStockList.clear();
            observedStockList.addAll(stock);

            observedProducedList.clear();
            observedProducedList.addAll(packingQueue);
            observedProducedList.addAll(qualityCheckQueue);

            observedShippedList.clear();
            observedShippedList.addAll(distributionPackages);

            observedDisposedList.clear();
            observedDisposedList.addAll(garbage);

            observedDeliveredList.clear();
            observedDeliveredList.addAll(orderRockets);

            lbShippedAmount.setText(String.valueOf(observedShippedList.size() * 5));
            lbDisposedAmount.setText(String.valueOf(observedDisposedList.size()));
            lbOrderedAmount.setText(String.valueOf(amountOrderedRockets));
            lbDeliveredAmount.setText(String.valueOf(observedDeliveredList.size()));

        }
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

    @FXML
    private HBox hbError;
    @FXML
    private HBox hbColor;

    @FXML
    private void supplierChanged(int index) {

        //boolean isEC = cbSupplier.getValue() == EnumParts.EFFECT_CHARGE;
        boolean isEC = index == 1;

        hbError.setVisible(isEC);
        hbColor.setVisible(isEC);
    }
}