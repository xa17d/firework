package at.sbc.firework.gui;

import at.sbc.firework.entities.*;
import at.sbc.firework.service.IDataChangedListener;
import at.sbc.firework.service.IService;
import at.sbc.firework.service.ServiceException;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.util.ArrayList;

/**
 * Created by Lucas on 17.11.2014.
 */
public class TableController implements IDataChangedListener {

    private IService service;

    @FXML
    private ListView<Part> lvStock;
    private ObservableList<Part> observedStockList;
    @FXML
    private ListView<Rocket> lvProduced;
    private ObservableList<Rocket> observedProducedList;
    @FXML
    private ListView<RocketPackage5> lvShipped;
    private ObservableList<RocketPackage5> observedDeliveredList;
    @FXML
    private ListView<Rocket> lvDisposed;
    private ObservableList<Rocket> observedDisposedList;

    @FXML
    private Label lbCasingAmount;
    @FXML
    private Label lbEffectChargeAmount;
    @FXML
    private Label lbPropellingChargeAmount;
    @FXML
    private Label lbStickAmount;


    /**
     * called on initializing the controller by fx
     */
    @FXML
    private void initialize() {

        observedStockList = new ObservableListWrapper<Part>(new ArrayList<Part>());
        lvStock.setItems(observedStockList);

        observedProducedList = new ObservableListWrapper<Rocket>(new ArrayList<Rocket>());
        lvProduced.setItems(observedProducedList);

        observedDeliveredList = new ObservableListWrapper<RocketPackage5>(new ArrayList<RocketPackage5>());
        lvShipped.setItems(observedDeliveredList);

        observedDisposedList = new ObservableListWrapper<Rocket>(new ArrayList<Rocket>());
        lvDisposed.setItems(observedDisposedList);
    }

    public void setService(IService service) {

        this.service = service;
        service.addChangeListener(this);
        dataChanged();
    }

    @Override
    public void dataChanged() {

        ArrayList<Part> stock = null;
        ArrayList<Rocket> packingQueue = null;
        ArrayList<Rocket> qualityCheckQueue = null;
        ArrayList<RocketPackage5> distributionPackages = null;
        ArrayList<Rocket> garbage = null;

        try {
            stock = service.listStock();
            packingQueue = service.listPackingQueue();
            qualityCheckQueue = service.listQualityCheckQueue();
            distributionPackages = service.listDistributionStock();
            garbage = service.listGarbage();
        } catch (ServiceException e) {
            e.printStackTrace();
        }

        Platform.runLater(new UpdateTableRunnable(stock, packingQueue, qualityCheckQueue, distributionPackages, garbage));
    }

    private class UpdateTableRunnable implements Runnable {

        private ArrayList<Part> stock;
        private ArrayList<Rocket> packingQueue;
        private ArrayList<Rocket> qualityCheckQueue;
        private ArrayList<RocketPackage5> distributionPackages;
        private ArrayList<Rocket> garbage;


        public UpdateTableRunnable(ArrayList<Part> stock, ArrayList<Rocket> packingQueue, ArrayList<Rocket> qualityCheckQueue, ArrayList<RocketPackage5> distributionPackages, ArrayList<Rocket> garbage) {
            this.stock = stock;
            this.packingQueue = packingQueue;
            this.qualityCheckQueue = qualityCheckQueue;
            this.distributionPackages = distributionPackages;
            this.garbage = garbage;
        }

        @Override
        public void run() {

            int casingCount = 0;
            int effectCount = 0;
            int propellingCount = 0;
            int stickCount = 0;

            for (Part p : stock) {

                if (p instanceof Casing)
                    casingCount++;

                if (p instanceof EffectCharge)
                    effectCount++;

                if (p instanceof PropellingChargePackage)
                    propellingCount++;

                if (p instanceof Stick)
                    stickCount++;
            }

            lbCasingAmount.setText(String.valueOf(casingCount));
            lbEffectChargeAmount.setText(String.valueOf(effectCount));
            lbPropellingChargeAmount.setText(String.valueOf(propellingCount));
            lbStickAmount.setText(String.valueOf(stickCount));


            observedStockList.clear();
            observedStockList.addAll(stock);

            observedProducedList.clear();
            observedProducedList.addAll(packingQueue);
            observedProducedList.addAll(qualityCheckQueue);

            observedDeliveredList.clear();
            observedDeliveredList.addAll(distributionPackages);

            observedDisposedList.clear();
            observedDisposedList.addAll(garbage);
        }
    }
}
