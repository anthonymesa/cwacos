import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.ArrayList;
import javafx.geometry.Pos;
import javafx.geometry.Insets;

public class CwacosView extends Application {

    CwacosUI UI = new CwacosUI();

    public static void beginUI(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        ArrayList<Button> topButtons = new ArrayList<Button>();

        primaryStage.setTitle("Hello World!");

        //Button that saves the active ticker data
        Button saveBtn = new Button();
        saveBtn.setText("HelloWorld");
        topButtons.add(saveBtn);
        //saveBtn.setGraphic(new ImageView(
        //new Image(getClass().getResourceAsStream("C:/Users/jackf/JavaProjects/cwacos/Cwacos/res/icons/saveicon.jpeg"))));

        //Button that loads the active ticker data
        Button loadTickerDataBtn = new Button();
        loadTickerDataBtn.setText("Load");
        topButtons.add(loadTickerDataBtn);

        //Button that updates the active ticket data
        Button updateTickerDataBtn = new Button();
        updateTickerDataBtn.setText("Update");
        topButtons.add(updateTickerDataBtn);
        
        //Button that updates all ticker data
        Button updateAllTickersBtn = new Button();
        updateAllTickersBtn.setText("Update All");
        topButtons.add(updateAllTickersBtn);

        //Button that runs the max profit algorithm
        Button maxProfitBtn = new Button();
        maxProfitBtn.setText("MAX PROFIT!!!!!");
        maxProfitBtn.setAlignment(Pos.BOTTOM_CENTER);

        //Top bar of buttons
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(15, 12, 15, 12));
        topBar.setSpacing(10);
        topBar.getChildren().addAll(topButtons);

        //Parents VBox layout
        VBox root = new VBox();
        root.getChildren().add(topBar);
        root.getChildren().add(maxProfitBtn);
        
        primaryStage.setScene(new Scene(root, 1280, 720));
        primaryStage.show();
    }
}