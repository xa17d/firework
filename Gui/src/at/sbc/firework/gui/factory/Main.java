package at.sbc.firework.gui.factory;

import at.sbc.firework.service.IFactoryService;
import at.sbc.firework.service.ServiceException;
import at.sbc.firework.service.ServiceFactory;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * starts main gui
 */
public class Main extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;
    private GridPane tableLayout;

    private IFactoryService service;

    @Override
    public void start(Stage primaryStage) {

        this.primaryStage = primaryStage;

        System.out.println("init service...");

        try {
            service = ServiceFactory.getFactory();
            service.start();
        }
        catch (ServiceException e) {
            e.printStackTrace();
        }

        System.out.println("init layout...");
        initLayout();
    }

    private void initLayout() {

        try {
            System.out.println(" - loader");
            FXMLLoader loader = new FXMLLoader();
            System.out.println(" - setLocation");
            loader.setLocation(getClass().getResource("main_frame.fxml"));

            System.out.println(" - load");
            rootLayout = (BorderPane) loader.load();
            //primaryStage.getIcons().add(new Image("file:resource/icon/icon.png"));
            System.out.println(" - title");
            primaryStage.setTitle("Firework - Factory");

            System.out.println(" - new scene");
            Scene rootScene = new Scene(rootLayout);
            System.out.println(" - set scene");
            primaryStage.setScene(rootScene);
            System.out.println(" - show");
            primaryStage.show();

            System.out.println(" - get main controller");
            MainController controller = loader.getController();
            System.out.println(" - set service");
            controller.setService(service);

        } catch(IOException e) {
            e.printStackTrace();
        }

        initTableLayout();
    }

    private void initTableLayout() {

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("table_fram.fxml"));

            tableLayout = (GridPane) loader.load();
            rootLayout.setCenter(tableLayout);

            TableController controller = loader.getController();
            controller.setService(service);

        } catch(IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
