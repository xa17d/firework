package at.sbc.firework.gui;

import at.sbc.firework.entities.Part;
import at.sbc.firework.service.IDataChangedListener;
import at.sbc.firework.service.IService;
import at.sbc.firework.service.ServiceException;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
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

        observedStockList = new ObservableListWrapper<Part>(new ArrayList<Part>());

        lvStock.setItems(observedStockList);

        dataChanged();
        //TODO
    }

    public void setService(IService service) {
        this.service = service;
        service.addChangeListener(this);
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
                    observedStockList.clear();
                    observedStockList.addAll(service.listStock());
                } catch (ServiceException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
