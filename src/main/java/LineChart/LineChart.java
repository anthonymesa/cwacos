package LineChart;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.*;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;


class LineCharts extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        init(stage);
    }
    private void init(Stage stage){
        HBox root= new HBox();
        Scene scene = new Scene(root,450,330);

        CategoryAxis xAxis= new CategoryAxis();
        xAxis.setLabel("interval");

        NumberAxis yAxis= new NumberAxis();
        yAxis.setLabel("Stock Price");

        javafx.scene.chart.LineChart<String,Number> lineChart = new javafx.scene.chart.LineChart<String,Number>(xAxis,yAxis);
        lineChart.setTitle("Stocks");

        XYChart.Series<String,Number> series = new XYChart.Series<>();
        series.setName("$ amount");

        series.getData().add(new XYChart.Data<>("1995", 18000));
        series.getData().add(new XYChart.Data<>("1999", 19000));
        series.getData().add(new XYChart.Data<>("2003", 21500));
        series.getData().add(new XYChart.Data<>("2007", 23000));
        series.getData().add(new XYChart.Data<>("2011", 25000));
        series.getData().add(new XYChart.Data<>("2013", 26000));

        lineChart.getData().add(series);
        root.getChildren().add(lineChart);

        stage.setTitle("Qwacos Line chart");
        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}



