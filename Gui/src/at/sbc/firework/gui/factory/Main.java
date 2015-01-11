package at.sbc.firework.gui.factory;

import at.sbc.firework.service.Console;
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

        Console.println("init service...");

        try {
            service = ServiceFactory.getFactory(ServiceFactory.getDefaultFactoryAddress());
            service.start();
        }
        catch (ServiceException e) {
            e.printStackTrace();
        }

        Console.println("init layout...");
        initLayout();
    }

    private void initLayout() {

        try {
            /*
             * MAIN
             */
            Console.println(" - loader");
            FXMLLoader loaderMain = new FXMLLoader();
            Console.println(" - setLocation");
            loaderMain.setLocation(getClass().getResource("main_fram.fxml"));

            Console.println(" - load");
            rootLayout = (BorderPane) loaderMain.load();
            //primaryStage.getIcons().add(new Image("file:resource/icon/icon.png"));
            Console.println(" - title");
            primaryStage.setTitle("Firework - Factory");

            Console.println(" - new scene");
            Scene rootScene = new Scene(rootLayout);
            Console.println(" - set scene");
            primaryStage.setScene(rootScene);
            Console.println(" - show");
            primaryStage.show();

            /*
             * set service
             */
            Console.println(" - get controller");
            Controller controller = loaderMain.getController();

            Console.println(" - initialize layout");
            controller.initializeLayout();

            Console.println(" - set service");
            controller.setService(service);

        } catch(IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}