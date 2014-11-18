package at.sbc.firework.gui;

import at.sbc.firework.daos.Part;
import at.sbc.firework.service.IService;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by Lucas on 17.11.2014.
 */
public class TableController {

    private IService service;

    @FXML
    private ListView<Part> lvStock;
    private List<Part> stockList;
    private ObservableList<Part> observedStockList;
    @FXML
    private ListView<Part> lvProduced;
    @FXML
    private ListView<Part> lvDelivered;
    @FXML
    private ListView<Part> lvDisposed;


    /**
     * called on initializing the controller by fx
     */
    @FXML
    private void initialize() {

        stockList = new LinkedList<Part>();
        observedStockList = new ObservableListWrapper<Part>(stockList);
        lvStock.setItems(observedStockList);

        //TODO
    }

    public void setService(IService service) {
        this.service = service;
    }

    public void updateLayout() {
        //TODO
    }
}
