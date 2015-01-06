package at.sbc.firework.gui.factory;

import at.sbc.firework.entities.*;
import at.sbc.firework.service.ContainerOperation;
import at.sbc.firework.service.INotification;
import at.sbc.firework.service.IFactoryService;
import at.sbc.firework.service.ServiceException;
import at.sbc.firework.utils.NotificationMode;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.util.ArrayList;

/**
 * Created by Lucas on 17.11.2014.
 */
public class TableController implements INotification {

    private IFactoryService service;

    @FXML
    private ListView<Part> lvStock;
    private ObservableList<Part> observedStockList;
    @FXML
    private ListView<Rocket> lvProduced;
    private ObservableList<Rocket> observedProducedList;
    @FXML
    private TreeView<String> tvShipped;
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

        //observedDeliveredList = new ObservableListWrapper<RocketPackage5>(new ArrayList<RocketPackage5>());
        //lvShipped.setItems(observedDeliveredList);
        //tvShipped = new TreeView<String>();

        observedDisposedList = new ObservableListWrapper<Rocket>(new ArrayList<Rocket>());
        lvDisposed.setItems(observedDisposedList);
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
            lbEffectChargeAmount.setText("B: " + String.valueOf(effectCountBlue) + " | " +
                    "G: " + String.valueOf(effectCountGreen) + " | " +
                    "R: " + String.valueOf(effectCountRed));
            lbPropellingChargeAmount.setText(String.valueOf(propellingCount));
            lbStickAmount.setText(String.valueOf(stickCount));


            observedStockList.clear();
            observedStockList.addAll(stock);

            observedProducedList.clear();
            observedProducedList.addAll(packingQueue);
            observedProducedList.addAll(qualityCheckQueue);

            //observedDeliveredList.clear();
            //observedDeliveredList.addAll(distributionPackages);

            TreeItem<String> rootItem = new TreeItem<String>("Root");
            TreeItem<String> treeItem;
            TreeItem<String> nodeItem;

            for(RocketPackage5 rp : distributionPackages) {
                treeItem = new TreeItem<String>(rp.toString());
                for(Rocket r : rp.getContent()) {
                    nodeItem = new TreeItem<String>(r.toString());
                    treeItem.getChildren().add(nodeItem);
                }
                rootItem.getChildren().add(treeItem);
            }
            tvShipped = new TreeView<String>(rootItem);

            observedDisposedList.clear();
            observedDisposedList.addAll(garbage);
        }
    }
}
