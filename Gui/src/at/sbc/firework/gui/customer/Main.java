package at.sbc.firework.gui.customer;

import at.sbc.firework.Customer;
import at.sbc.firework.gui.customer.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by daniel on 20.12.2014.
 */
public class Main extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;
    private Customer customer;

    @Override
    public void start(Stage primaryStage) {

        this.primaryStage = primaryStage;

        customer = new Customer(args);

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
            controller.setCustomer(customer);

        } catch(IOException e) {
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
