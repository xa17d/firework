package at.sbc.firework.gui.customer;

import at.sbc.firework.Customer;
import at.sbc.firework.gui.customer.Controller;
import at.sbc.firework.service.Console;
import at.sbc.firework.service.IFactoryService;
import at.sbc.firework.service.ServiceException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by daniel on 20.12.2014.
 */
public class Main extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;

    private Customer customer;
    private IFactoryService service;
    private Controller controller;

    @Override
    public void start(Stage primaryStage) {

        this.primaryStage = primaryStage;

        initLayout();
    }

    private void initLayout() {

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("main_fram.fxml"));

            rootLayout = (BorderPane) loader.load();
            //primaryStage.getIcons().add(new Image("file:resource/icon/icon.png"));
            primaryStage.setTitle("Firework - Customer");

            Scene rootScene = new Scene(rootLayout);
            primaryStage.setScene(rootScene);
            primaryStage.show();

            this.controller = loader.getController();

            //factory selection popup
            Stage factorySelectionStage = new Stage();

            FXMLLoader loader2 = new FXMLLoader();
            loader2.setLocation(getClass().getResource("factory_selection.fxml"));

            factorySelectionStage.setScene(new Scene((AnchorPane) loader2.load()));
            factorySelectionStage.initModality(Modality.APPLICATION_MODAL);
            factorySelectionStage.show();

            FactorySelectionController factorySelectionController = loader2.getController();
            factorySelectionController.setController(controller);
            factorySelectionController.setMain(this);

        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void createCustomer(String factoryAddress) {
        customer = new Customer(args, factoryAddress);

        try {
            service = customer.getFactoryService();
            service.start();

            Console.println("Fetching rockets...");
            customer.fetchRockets();
            Console.println("Fetching Done");

            controller.setCustomer(customer);

        }
        catch (ServiceException e) {
            e.printStackTrace();
        }
    }

    private static String[] args;
    public static void main(String[] args)
    {
        Main.args = args;
        launch(args);
    }
}
