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
import java.util.LinkedList;
import java.util.List;

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

    public void updateLayout() {
        //TODO
    }

    @Override
    public void dataChanged() {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {

                    ArrayList<Part> stock = service.listStock();
                    int casingCount = 0;
                    int effectCount = 0;
                    int propellingCount = 0;
                    int stickCount = 0;

                    for(Part p : stock) {

                        if(p instanceof Casing)
                            casingCount++;

                        if(p instanceof EffectCharge)
                            effectCount++;

                        if(p instanceof PropellingChargePackage)
                            propellingCount++;

                        if(p instanceof Stick)
                            stickCount++;
                    }

                    lbCasingAmount.setText(String.valueOf(casingCount));
                    lbEffectChargeAmount.setText(String.valueOf(effectCount));
                    lbPropellingChargeAmount.setText(String.valueOf(propellingCount));
                    lbStickAmount.setText(String.valueOf(stickCount));


                    observedStockList.clear();
                    observedStockList.addAll(stock);

                    observedProducedList.clear();
                    observedProducedList.addAll(service.listPackingQueue());
                    observedProducedList.addAll(service.listQualityCheckQueue());

                    observedDeliveredList.clear();
                    observedDeliveredList.addAll(service.listDistributionStock());

                    observedDisposedList.clear();
                    observedDisposedList.addAll(service.listGarbage());

                } catch (ServiceException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
