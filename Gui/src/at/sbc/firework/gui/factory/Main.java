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

        try {
            service = ServiceFactory.getFactory();
            service.start();
        }
        catch (ServiceException e) {
            e.printStackTrace();
        }

        initLayout();
    }

    private void initLayout() {

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("main_frame.fxml"));

            rootLayout = (BorderPane) loader.load();
            //primaryStage.getIcons().add(new Image("file:resource/icon/icon.png"));
            primaryStage.setTitle("Firework");

            Scene rootScene = new Scene(rootLayout);
            primaryStage.setScene(rootScene);
            primaryStage.show();

            MainController controller = loader.getController();
            controller.setService(service);

        } catch(IOException e) {
            e.printStackTrace();
        }

        initTableLayout();
    }

    private void initTableLayout() {

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("table_frame.fxml"));

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
